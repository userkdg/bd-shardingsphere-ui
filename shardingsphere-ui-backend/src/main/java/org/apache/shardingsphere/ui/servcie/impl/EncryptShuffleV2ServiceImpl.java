package org.apache.shardingsphere.ui.servcie.impl;

import cn.com.bluemoon.daps.common.toolkit.BmAssetUtils;
import cn.com.bluemoon.encrypt.shuffle.cli.SparkSubmitEncryptShuffleMain;
import cn.com.bluemoon.metadata.common.ResultBean;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.common.enums.JudgeEnum;
import cn.com.bluemoon.metadata.inter.DbMetaDataService;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.FieldInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SchemaInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SqlIndexInfoVO;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.ExtractMode;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig.FieldInfo;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfigSwapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveShuffleInfo;
import org.apache.shardingsphere.ui.common.dto.TableExtractDefine;
import org.apache.shardingsphere.ui.common.enums.DataVolumeLevelEnum;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveShuffleInfoService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleV2Service;
import org.apache.shardingsphere.ui.util.sql.MysqlFieldFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import javax.annotation.Nullable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author Jarod.Kong
 */
@Slf4j
@Service("EncryptShuffleServiceV2")
@Scope(value = "prototype")
public class EncryptShuffleV2ServiceImpl implements EncryptShuffleV2Service {
    private static final String JOB_NAME_PREFIX = "bd-spark-kms-shuffle-";
    @Autowired
    private ConfigCenterService configCenterService;

    @Autowired
    private DbMetaDataService dbMetaDataService;

    @Autowired
    private DsSySensitiveShuffleInfoService dsSySensitiveShuffleInfoService;

    @Value("${spark.job.env:test}")
    private String sparkJobEnv;

    private String sourceUrl;

    private List<TableInfo> encryptTablesByRule;

    private MetaDataPersistService metaDataPersistService;

    private String schema;

    private List<EncryptRuleConfiguration> encryptRules;

    private final List<String> preSubmitErrors = new ArrayList<>();

    private Map<String, Object> dataSourceProps;

    private Map<String, DsSysSensitiveShuffleInfo> tableAndShuffleInfo;

    private Map<String, List<ColumnInfoVO>> tableAndPrimaryCols;

    private String dbType;

    private Set<String> customTableNames;

    private Map<String, List<SqlIndexInfoVO>> tableAndIndexes;

    private Map<String, TableExtractDefine> tableExtractDefineMap;

    @Override
    public void submitJob(String schema,
                          String dbType,
                          @Nullable Set<String> tableNames,
                          @Nullable final List<TableExtractDefine> tableExtractDefines) {
        BmAssetUtils.isTrue(schema != null, "数据库名不为空");
        BmAssetUtils.isTrue(dbType != null, "数据库类型不为空");
        this.schema = schema;
        this.dbType = dbType;
        this.customTableNames = Optional.ofNullable(tableNames).orElse(Collections.emptySet());
        if (tableExtractDefines != null) {
            this.tableExtractDefineMap = tableExtractDefines.stream().collect(Collectors.toMap(TableExtractDefine::getTableName, t -> t, (a, b) -> b));
        }
        log.info("初始化作业配置信息开始");
        initBaseInfo();
        List<GlobalConfig> globalConfigs = buildJobConfigs();
        if (globalConfigs.isEmpty()) {
            throw new RuntimeException("获取洗数作业为空");
        }
        log.info("初始化作业配置信息完成");
        if (!preSubmitErrors.isEmpty()){
            throw new ShardingSphereUIException(500, "提交作业前错误：\n" + String.join(";\n", preSubmitErrors));
        }
        globalConfigs.stream()
                .filter(this::selectTables)
                .peek(this::prePrintJobCommand)
                .forEachOrdered(c -> {
                    String json = GlobalConfigSwapper.swapToJsonStr(c);
                    log.info("提交作业入参：表：{}，params：{}", c.getRuleTableName(), json);
                    SparkSubmitEncryptShuffleMain.main(new String[]{json, c.getRuleTableName(), sparkJobEnv});
                });
        log.info("提交作业完成");
    }

    @Override
    public void submitJob(GlobalConfig c) {
        String json = GlobalConfigSwapper.swapToJsonStr(c);
        log.info("提交作业入参：表：{}，params：{}", c.getRuleTableName(), json);
        SparkSubmitEncryptShuffleMain.main(new String[]{json, c.getRuleTableName(), sparkJobEnv});
    }

    private void prePrintJobCommand(GlobalConfig c) {
        String jsonParam = GlobalConfigSwapper.swapToJsonStr(c);
        String jobName = c.getRuleTableName();
        log.info("提交作业入参：表：{}，params：{}", jobName, jsonParam);
        String command = String.format("sh /home/data_tool/bd-spark/bd-spark-encrypt-shuffle/runSparkEncryptShuffleJob.sh '-c %s' '%s'", jsonParam, jobName);
        System.out.println("预生产命令，如下：");
        System.out.println(command);
    }

    private boolean selectTables(GlobalConfig c) {
        // 没有指定就全部提交作业
        if (customTableNames.isEmpty()) {
            return true;
        }
        if (customTableNames.contains(c.getRuleTableName())) {
            log.info("指定洗数表{}", c.getRuleTableName());
            return true;
        }
        return false;
    }

    private void initBaseInfo() {
        this.metaDataPersistService = configCenterService.getActivatedMetadataService();
        Map<String, DataSourceConfiguration> dataSource = metaDataPersistService.getDataSourceService().load(schema);
        Map<String, Object> dataSourceProps = dataSource.values().stream().findFirst().map(DataSourceConfiguration::getAllProps).orElseThrow(() -> new RuntimeException("数据库名"+schema+"获取数据源信息为空"));
        this.dataSourceProps = dataSourceProps;
        this.sourceUrl = dataSourceProps.get("jdbcUrl").toString() + "&user=" + dataSourceProps.get("username").toString() + "&password=" + dataSourceProps.get("password");
        // 读取配置中心的规则的表和明文列
        this.encryptRules = readRule();
        this.encryptTablesByRule = findEncryptTablesByRule(encryptRules);
        // 元数据信息
        Map<String, Object> connectInfo = findConnectInfo();
        Assert.isTrue(connectInfo != null && !connectInfo.isEmpty(), "schema下数据源不存在");
        SchemaInfoVO schemaInfoVO = getSchemaInfoVO(connectInfo);
        this.tableAndPrimaryCols = getTableAndPrimaryCols(schemaInfoVO);
        this.tableAndIndexes = getTableAndIndexes(schemaInfoVO);
        //获取导入的表与增量字段关系
        LambdaQueryWrapper<DsSysSensitiveShuffleInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DsSysSensitiveShuffleInfo::getSchemaName, schema);
        List<DsSysSensitiveShuffleInfo> sensitiveShuffleInfos = dsSySensitiveShuffleInfoService.list(wrapper);
        this.tableAndShuffleInfo = sensitiveShuffleInfos.stream().collect(Collectors.toMap(DsSysSensitiveShuffleInfo::getTableName, d -> d, (a, b) -> b));
    }

    private Map<String, List<SqlIndexInfoVO>> getTableAndIndexes(SchemaInfoVO schemaInfoVO) {
        return schemaInfoVO.getTables().stream().flatMap(t ->
                t.getSqlIndexes().stream())
                .collect(Collectors.groupingBy(SqlIndexInfoVO::getTableName));
    }

    private List<GlobalConfig> buildJobConfigs() {
        List<GlobalConfig> configs = new ArrayList<>();
        for (EncryptRuleConfiguration rule : encryptRules) {
            Map<String, ShardingSphereAlgorithmConfiguration> encryptorMaps = rule.getEncryptors().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            for (EncryptTableRuleConfiguration table : rule.getTables()) {
                final String tableName = table.getName();
                GlobalConfig config = new GlobalConfig();
                config.setSourceUrl(sourceUrl);
                config.setTargetUrl(sourceUrl);
                // 增加库名
                config.setDbName(schema);
                config.setDbType(dbType);
                config.setRuleTableName(tableName);
                // 主键列
                List<ColumnInfoVO> primaryCols = getPkColumns(tableName);
                List<FieldInfo> fieldInfos = primaryCols.stream().map(ColumnInfoVO::getName).map(FieldInfo::new).collect(Collectors.toList());
                config.setPrimaryCols(fieldInfos);
                FieldInfo partitionCol = getPartitionFieldInfo(primaryCols, fieldInfos);
                config.setPartitionCol(partitionCol);

                // 获取表->增量字段
                DsSysSensitiveShuffleInfo shuffleInfo = tableAndShuffleInfo.get(tableName);
                String incrFieldName = shuffleInfo != null ? shuffleInfo.getIncrFieldName() : null;
                // 判断自定义增量字段是否有索引，没索引不允许提交作业
                predicateIncrFieldHadIndex(tableName, primaryCols, incrFieldName);

                // 结合基础信息设置抽取方式
                if (StringUtils.isNotBlank(incrFieldName)) {
                    config.setIncrTimestampCol(incrFieldName);
                    config.setExtractMode(ExtractMode.WithPersistStateCustomWhere);
                } else {
                    config.setExtractMode(ExtractMode.All);
                }

                if (shuffleInfo != null) {
                    if (StringUtils.isNotBlank(shuffleInfo.getOnUpdateTimestampFields())){
                        // 增加避免刷库更新SQL中timestamp自动更新问题，会拿该原值数据回填，onUpdateCurrentTimestamps列字段，eg: sys_user的op_time
                        List<String> onUpdateTimestampFields = Arrays.stream(shuffleInfo.getOnUpdateTimestampFields().split(",")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                        config.setOnUpdateCurrentTimestamps(onUpdateTimestampFields);
                    }
                    DataVolumeLevelEnum dataVolumeLevelEnum = DataVolumeLevelEnum.from(shuffleInfo.getDataVolumeLevel(), DataVolumeLevelEnum.DEFAULT);
                    config.setAdviceNumberPartition(dataVolumeLevelEnum.getAdviceNumberPartition());
                }

                // 外部自定义抽取（最后设置，会覆盖基础逻辑设置的）
                TableExtractDefine tableExtractDefine = tableExtractDefineMap.get(tableName);
                if (tableExtractDefine != null){
                    log.warn("外部指定抽取方式：{}", tableExtractDefine);
                    if (tableExtractDefine.getExtractMode() != null) {
                        config.setExtractMode(tableExtractDefine.getExtractMode());
                    }
                    if (StringUtils.isNotBlank(tableExtractDefine.getCustomExtractWhereSql())) {
                        config.setCustomExtractWhereSql(tableExtractDefine.getCustomExtractWhereSql());
                    }
                    if (StringUtils.isNotBlank(tableExtractDefine.getIncrFieldName())){
                        // 判断自定义增量字段是否有索引，没索引不允许提交作业
                        predicateIncrFieldHadIndex(tableName, primaryCols, tableExtractDefine.getIncrFieldName());
                        config.setIncrTimestampCol(tableExtractDefine.getIncrFieldName());
                    }
                    if (config.getExtractMode().equals(ExtractMode.WithPersistStateCustomWhere) && tableExtractDefine.isResetExtractState()) {
                        log.warn("注：该表{}设置了重置抽取状态{}，表示清除历史抽取的状态信息（上一次增量位置），然后进行重新刷数", tableName, config.getExtractMode());
                        config.setResetJobState(true);
                    }
                    if (tableExtractDefine.getAdviceNumberPartition() != null && tableExtractDefine.getAdviceNumberPartition() >= 1){
                        config.setAdviceNumberPartition(tableExtractDefine.getAdviceNumberPartition());
                        log.warn("注：表{}自定义抽取分片数{}", tableName, tableExtractDefine.getAdviceNumberPartition());
                    }
                }
                config.setOnYarn(true);
                config.setJobName(JOB_NAME_PREFIX + tableName);
                // 明文列加密
                List<GlobalConfig.Tuple2<FieldInfo>> shuffleCols = getShuffleColumnFromShardingsphere(encryptorMaps, table);
                if (shuffleCols.isEmpty()) {
                    throw new ShardingSphereUIException(500, "刷数字段为空，不可刷数！");
                }
                config.setShuffleCols(shuffleCols);
                config.setMultiBatchUrlConfig(true);
                configs.add(config);
            }
        }
        return configs;
    }

    private List<ColumnInfoVO> getPkColumns(String tableName) {
        List<ColumnInfoVO> primaryCols = tableAndPrimaryCols.get(tableName);
        for (ColumnInfoVO p : primaryCols) {
            if (dbType.equalsIgnoreCase("mysql")) {
                if (MysqlFieldFactory.intList.contains(p.getSqlSimpleType())) {
                    log.info("表{}主键{}类型为{}数值类", tableName, p.getName(), p.getSqlSimpleType());
                } else {
                    log.warn("表{}主键{}类型为{}非数值类", tableName, p.getName(), p.getSqlSimpleType());
                }
            }
        }
        return primaryCols;
    }

    private FieldInfo getPartitionFieldInfo(List<ColumnInfoVO> primaryCols, List<FieldInfo> fieldInfos) {
        FieldInfo partitionCol = primaryCols.stream().filter(c -> JudgeEnum.YES.equals(c.getIsAutoIncrement()))
                .map(c -> new FieldInfo(c.getName())).findFirst().orElseGet(() -> {
                    try {
                        return primaryCols.stream().max(Comparator.comparing(ColumnInfoVO::getLength))
                                .map(c -> new FieldInfo(c.getName())).orElse(fieldInfos.get(0));
                    } catch (Exception e) {
                        log.error("对比字段长度失败", e);
                        return fieldInfos.get(0);
                    }
                });
        return partitionCol;
    }

    private List<GlobalConfig.Tuple2<FieldInfo>> getShuffleColumnFromShardingsphere(Map<String, ShardingSphereAlgorithmConfiguration> encryptorMaps, EncryptTableRuleConfiguration table) {
        List<GlobalConfig.Tuple2<FieldInfo>> shuffleCols = new ArrayList<>();
        for (EncryptColumnRuleConfiguration column : table.getColumns()) {
            String encryptorName = column.getEncryptorName();
            ShardingSphereAlgorithmConfiguration aglo = encryptorMaps.get(encryptorName);
            FieldInfo extractCol = new FieldInfo(Optional.ofNullable(column.getPlainColumn()).orElse(column.getLogicColumn()));
            FieldInfo targetCol = new FieldInfo(column.getCipherColumn(), new GlobalConfig.EncryptRule(aglo.getType(), aglo.getProps()));
            shuffleCols.add(new GlobalConfig.Tuple2<>(extractCol, targetCol));
        }
        return shuffleCols;
    }

    private void predicateIncrFieldHadIndex(String tableName, List<ColumnInfoVO> primaryCols, String incrFieldName) {
        if (StringUtils.isNotBlank(incrFieldName)) {
            boolean isPk = primaryCols.stream().anyMatch(p -> p.getName().equals(incrFieldName));
            if (!isPk) {
                List<SqlIndexInfoVO> sqlIndexInfoVOS = tableAndIndexes.get(tableName);
                if (!hadIndexIncrField(incrFieldName, sqlIndexInfoVOS)) {
                    preSubmitErrors.add(String.format("刷数增量字段非主键且没有索引，不可刷数！提示：建议首选增量主键字段或者联系DBA添加表字段%s.%s索引", tableName, incrFieldName));
                }
            }
        }
    }

    private boolean hadIndexIncrField(String incrFieldName, List<SqlIndexInfoVO> sqlIndexInfoVOS) {
        if (sqlIndexInfoVOS == null || sqlIndexInfoVOS.isEmpty()) {
            return false;
        }
        return sqlIndexInfoVOS.stream().anyMatch(i -> {
            List<FieldInfoVO> fields = i.getFields();
            for (FieldInfoVO field : fields) {
                if (field.getName().equals(incrFieldName)) {
                    return true;
                }
            }
            return false;
        });
    }

    private List<TableInfo> findEncryptTablesByRule(List<EncryptRuleConfiguration> encryptRules) {
        List<TableInfo> encryptTablesByRule0 = encryptRules.stream().flatMap(e -> e.getTables().stream())
                .map(t -> {
                    List<String> cols = t.getColumns().stream().map(c -> Optional.ofNullable(c.getPlainColumn()).orElse(c.getLogicColumn())).collect(Collectors.toList());
                    return new TableInfo(t.getName(), cols);
                }).collect(Collectors.toList());
        assert !encryptTablesByRule0.isEmpty();
        return encryptTablesByRule0;
    }

    private Map<String, List<ColumnInfoVO>> getTableAndPrimaryCols(SchemaInfoVO schemaInfoVO) {
        return schemaInfoVO.getTables().stream().flatMap(t -> t.getColumns().stream())
                .filter(c -> c.getIsPrimary().equals(JudgeEnum.YES))
                .collect(Collectors.groupingBy(ColumnInfoVO::getTableName));

    }

    private SchemaInfoVO getSchemaInfoVO(Map<String, Object> connectInfo) {
        QueryMetaDataRequest metaDataRequest = new QueryMetaDataRequest();
        metaDataRequest.setDbType(DbTypeEnum.getByCode(connectInfo.get("dbtype").toString()));
        metaDataRequest.setPort(String.valueOf(connectInfo.get("port")));
        metaDataRequest.setIp(String.valueOf(connectInfo.get("host")));
        metaDataRequest.setPassword(String.valueOf(connectInfo.get("password")));
        metaDataRequest.setUsername(String.valueOf(connectInfo.get("user")));
        metaDataRequest.setDbName(String.valueOf(connectInfo.get("dbname")));
        metaDataRequest.setSchemaName(String.valueOf(connectInfo.get("dbname")));
        metaDataRequest.setTableNames(encryptTablesByRule.stream().map(TableInfo::getName).collect(Collectors.joining(",")));
        ResultBean<SchemaInfoVO> metaData = dbMetaDataService.queryMetaData(metaDataRequest);
        SchemaInfoVO metaDataContent = metaData.getContent();
        return metaDataContent;
    }


    private List<EncryptRuleConfiguration> readRule() {
        Collection<RuleConfiguration> collection = metaDataPersistService.getSchemaRuleService().load(schema);
        Assert.isTrue(!collection.isEmpty(), "schema下规则为空");
        List<EncryptRuleConfiguration> encrypts = collection.stream().map(r -> (EncryptRuleConfiguration) r).collect(Collectors.toList());
        Assert.isTrue(!encrypts.isEmpty(), "schema下加密规则为空");
        return encrypts;
    }


    private Map<String, Object> findConnectInfo() {
        Matcher matcher = Pattern.compile("jdbc:(.*)://(.*):([0-9]+)/(.*)\\?(.*)").matcher(dataSourceProps.get("jdbcUrl").toString());
        if (matcher.find()) {
            HashMap<String, Object> res = new HashMap<>();
            res.put("dbtype", matcher.group(1));
            res.put("host", matcher.group(2));
            res.put("port", matcher.group(3));
            res.put("dbname", matcher.group(4));
            res.put("user", dataSourceProps.get("username"));
            res.put("password", dataSourceProps.get("password"));
            return res;
        }
        return null;
    }


    @RequiredArgsConstructor
    @Getter
    private static class TableInfo {
        private final String name;
        private final List<String> cleanFields;
    }
}
