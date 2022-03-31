package org.apache.shardingsphere.ui.servcie.impl;

import cn.com.bluemoon.daps.common.toolkit.BmAssetUtils;
import cn.com.bluemoon.encrypt.shuffle.cli.SparkSubmitEncryptShuffleMain;
import cn.com.bluemoon.metadata.common.ResultBean;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.common.enums.JudgeEnum;
import cn.com.bluemoon.metadata.inter.DbMetaDataService;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SchemaInfoVO;
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
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveInfoService;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveShuffleInfoService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
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
@Service
@Scope(value = "prototype")
public class EncryptShuffleServiceImpl implements EncryptShuffleService {
    private static final String JOB_NAME_PREFIX = "bd-spark-kms-shuffle-";
    @Autowired
    private ConfigCenterService configCenterService;

    @Autowired
    private DbMetaDataService dbMetaDataService;

    @Autowired
    private DsSySensitiveInfoService dsSySensitiveInfoService;

    @Autowired
    private DsSySensitiveShuffleInfoService dsSySensitiveShuffleInfoService;

    @Value("${spark.job.env:test}")
    private String sparkJobEnv;

    private String sourceUrl;

    private List<TableInfo> encryptTablesByRule;

    private MetaDataPersistService metaDataPersistService;

    private String schema;

    private List<EncryptRuleConfiguration> encryptRules;

    private Map<String, Object> dataSourceProps;

    private Map<String, String> tableAndIncField;

    private Map<String, DsSysSensitiveShuffleInfo> tableAndShuffleInfo;

    private Map<String, List<ColumnInfoVO>> tableAndPrimaryCols;

    private List<GlobalConfig> globalConfigs;

    private Set<String> customTableNames;

    private Map<String, String> tableNameAndIncrFieldPreVal;

    private Set<String> customIgnoreTableNames;

    private String dbType;

    private boolean withIncrFieldExtractOnce;

    @Override
    public void submitJob(String schema,
                          String dbType,
                          @Nullable Set<String> ignoreTableNames,
                          @Nullable Set<String> tableNames,
                          @Nullable Map<String, String> tableNameAndIncrFieldPreVal,
                          boolean withIncrFieldExtractOnce) {
        log.info("初始化作业配置信息开始");
        init(schema, ignoreTableNames, tableNames, tableNameAndIncrFieldPreVal);
        BmAssetUtils.isTrue(dbType != null, "数据库类型不为空");
        this.dbType = dbType;
        this.withIncrFieldExtractOnce = withIncrFieldExtractOnce;
        buildJobConfigs();
        log.info("初始化作业配置信息完成");
        customTables();
        log.info("开始提交作业");
        doSubmit();
        log.info("提交作业完成");
    }

    private void customTables() {
        Objects.requireNonNull(globalConfigs);
        if (!customTableNames.isEmpty()) {
            this.globalConfigs = globalConfigs.stream().filter(c -> {
                if (customTableNames.contains(c.getRuleTableName())) {
                    log.info("指定洗数表{}", c.getRuleTableName());
                    return true;
                }
                return false;
            }).collect(Collectors.toList());
        }
        if (!customIgnoreTableNames.isEmpty()) {
            this.globalConfigs = globalConfigs.stream().filter(c -> {
                if (customIgnoreTableNames.contains(c.getRuleTableName())) {
                    log.info("指定忽略洗数表{}", c.getRuleTableName());
                    return false;
                }
                return true;
            }).collect(Collectors.toList());
        }
    }

    private void doSubmit() {
        Objects.requireNonNull(globalConfigs);
        for (GlobalConfig c : globalConfigs) {
            String json = GlobalConfigSwapper.swapToJsonStr(c);
            log.info("提交作业入参：表：{}，params：{}", c.getRuleTableName(), json);
            //  2022/2/25 增加部署环境对应作业提交
            SparkSubmitEncryptShuffleMain.main(new String[]{json, c.getRuleTableName(), sparkJobEnv});
        }
    }

    private void buildJobConfigs() {
        List<GlobalConfig> configs = new ArrayList<>();
        for (EncryptRuleConfiguration rule : encryptRules) {
            Map<String, ShardingSphereAlgorithmConfiguration> encryptorMaps = rule.getEncryptors().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            for (EncryptTableRuleConfiguration table : rule.getTables()) {
                GlobalConfig config = new GlobalConfig();
                config.setSourceUrl(sourceUrl);
                config.setTargetUrl(sourceUrl);
                // 增加库名
                config.setDbName(schema);
                config.setDbType(dbType);
                config.setRuleTableName(table.getName());
                // 主键列
                List<ColumnInfoVO> primaryCols = tableAndPrimaryCols.get(table.getName());
                for (ColumnInfoVO p : primaryCols) {
                    if (dbType.equalsIgnoreCase("mysql")) {
                        if (MysqlFieldFactory.intList.contains(p.getSqlSimpleType())) {
                            log.info("表{}主键{}类型为{}数值类", table.getName(), p.getName(), p.getSqlSimpleType());
                        }else {
                            log.warn("表{}主键{}类型为{}非数值类", table.getName(), p.getName(), p.getSqlSimpleType());
                        }
                    }
                }
                List<FieldInfo> fieldInfos = primaryCols.stream().map(ColumnInfoVO::getName).map(FieldInfo::new).collect(Collectors.toList());
                config.setPrimaryCols(fieldInfos);
                FieldInfo partitionCol = primaryCols.stream().filter(c -> JudgeEnum.YES.equals(c.getIsAutoIncrement()))
                        .map(c -> new FieldInfo(c.getName())).findFirst().orElseGet(()->{
                            try {
                                return primaryCols.stream().max(Comparator.comparing(ColumnInfoVO::getLength))
                                        .map(c -> new FieldInfo(c.getName())).orElse(fieldInfos.get(0));
                            } catch (Exception e) {
                                log.error("对比字段长度失败", e);
                                return fieldInfos.get(0);
                            }
                        });
                config.setPartitionCol(partitionCol);
                // 获取表->增量字段
//                String incrFieldName = tableAndIncField.get(table.getName());
                DsSysSensitiveShuffleInfo shuffleInfo = tableAndShuffleInfo.get(table.getName());
                String incrFieldName = shuffleInfo != null ? shuffleInfo.getIncrFieldName() : null;
//                incrFieldName=null;// 2022/3/10 临时改为全量跑 不走count统计
                if (StringUtils.isBlank(incrFieldName)) {
                    config.setExtractMode(ExtractMode.All);
                    log.info("全量一次抽取数据进行洗数");
                } else {
                    config.setExtractMode(ExtractMode.WithIncField);
                    config.setIncrTimestampCol(incrFieldName);
                    log.info("增量抽取数据进行洗数, 表{}，增量字段为{}", table.getName(), incrFieldName);
                    String incrFieldPreVal = tableNameAndIncrFieldPreVal.getOrDefault(table.getName(), null);
                    if (incrFieldPreVal != null) {
                        config.setIncrTimestampColPreVal(incrFieldPreVal);
                        log.info("增量抽取数据中指定了表{}只对增量字段{}大于{}的数据进行洗数", table.getName(), incrFieldName, incrFieldPreVal);
                    }
                }
                if (ExtractMode.WithIncField.equals(config.getExtractMode()) && withIncrFieldExtractOnce) {
                    log.warn("接口入参指定了抽取模式{}，默认为根据具体业务逻辑进行设置抽取模式，由最初{}=>{}",
                            ExtractMode.WithIncFieldOnce, config.getExtractMode(), ExtractMode.WithIncFieldOnce);
                    config.setExtractMode(ExtractMode.WithIncFieldOnce);
                }
                if (shuffleInfo != null) {
                    // 增加避免刷库更新SQL中timestamp自动更新问题，会拿该原值数据回填
                    // TODO: 2022/2/25  DsSySensitiveInfo库表（导入文件数据）增加一列，onUpdateCurrentTimestamps列字段，eg: sys_user的op_time
                    List<String> onUpdateTimestampFields = Arrays.stream(shuffleInfo.getOnUpdateTimestampFields().split(",")).filter(StringUtils::isNotBlank).collect(Collectors.toList());
                    config.setOnUpdateCurrentTimestamps(onUpdateTimestampFields);
                }

                config.setCustomExtractWhereSql(null);
                config.setOnYarn(true);
                config.setJobName(JOB_NAME_PREFIX + table.getName());
                // 明文列加密
                List<GlobalConfig.Tuple2<FieldInfo>> shuffleCols = new ArrayList<>();
                for (EncryptColumnRuleConfiguration column : table.getColumns()) {
                    String encryptorName = column.getEncryptorName();
                    ShardingSphereAlgorithmConfiguration aglo = encryptorMaps.get(encryptorName);
                    FieldInfo extractCol = new FieldInfo(Optional.ofNullable(column.getPlainColumn()).orElse(column.getLogicColumn()));
                    FieldInfo targetCol = new FieldInfo(column.getCipherColumn(), new GlobalConfig.EncryptRule(aglo.getType(), aglo.getProps()));
                    shuffleCols.add(new GlobalConfig.Tuple2<>(extractCol, targetCol));
                }
                config.setShuffleCols(shuffleCols);
                config.setMultiBatchUrlConfig(true);
                configs.add(config);
            }
        }
        this.globalConfigs = configs;
    }

    private void init(String schema, Set<String> ignoreTableNames, Set<String> tableNames, Map<String, String> tableNameAndIncrFieldPreVal) {
        this.metaDataPersistService = configCenterService.getActivatedMetadataService();
        this.schema = schema;
        this.customTableNames = Optional.ofNullable(tableNames).orElse(Collections.emptySet());
        this.customIgnoreTableNames = Optional.ofNullable(ignoreTableNames).orElse(Collections.emptySet());
        this.tableNameAndIncrFieldPreVal = Optional.ofNullable(tableNameAndIncrFieldPreVal).orElse(Collections.emptyMap());
        Map<String, DataSourceConfiguration> dataSource = metaDataPersistService.getDataSourceService().load(schema);
        Map<String, Object> dataSourceProps = dataSource.values().stream().findFirst().map(DataSourceConfiguration::getAllProps).orElseThrow(() -> new RuntimeException("ERROR"));
        this.dataSourceProps = dataSourceProps;
        this.sourceUrl = dataSourceProps.get("jdbcUrl").toString() + "&user=" + dataSourceProps.get("username").toString() + "&password=" + dataSourceProps.get("password");
        // 读取配置中心的规则的表和明文列
        this.encryptRules = readRule();
        this.encryptTablesByRule = findEncryptTablesByRule(encryptRules);
        this.tableAndPrimaryCols = tableAndPrimaryCols();
        //获取导入的表与增量字段关系
        LambdaQueryWrapper<DsSysSensitiveShuffleInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(DsSysSensitiveShuffleInfo::getSchemaName, schema);
        List<DsSysSensitiveShuffleInfo> sensitiveShuffleInfos = dsSySensitiveShuffleInfoService.list(wrapper);
        this.tableAndShuffleInfo = sensitiveShuffleInfos.stream().collect(Collectors.toMap(DsSysSensitiveShuffleInfo::getTableName, d -> d, (a, b) -> b));
        this.tableAndIncField = dsSySensitiveInfoService.getTableNameAndIncFieldMap(schema);
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

    private Map<String, List<ColumnInfoVO>> tableAndPrimaryCols() {
        Map<String, Object> connectInfo = findConnectInfo();
        Assert.isTrue(connectInfo != null && !connectInfo.isEmpty(), "schema下数据源不存在");
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
        return metaDataContent.getTables().stream().flatMap(t -> t.getColumns().stream())
                .filter(c -> c.getIsPrimary().equals(JudgeEnum.YES))
                .collect(Collectors.groupingBy(ColumnInfoVO::getTableName));

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
