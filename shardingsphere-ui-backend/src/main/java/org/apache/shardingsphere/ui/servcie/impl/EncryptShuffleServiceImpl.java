package org.apache.shardingsphere.ui.servcie.impl;

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
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.springframework.beans.factory.annotation.Autowired;
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

    private String sourceUrl;

    private List<TableInfo> encryptTablesByRule;
    @Autowired
    private ConfigCenterService configCenterService;

    private MetaDataPersistService metaDataPersistService;

    private String schema;

    private List<EncryptRuleConfiguration> encryptRules;

    private Map<String, Object> dataSourceProps;

    @Autowired
    private DbMetaDataService dbMetaDataService;

    private Map<String, String> tableAndIncField;

    private Map<String, List<ColumnInfoVO>> tableAndPrimaryCols;

    private List<GlobalConfig> globalConfigs;

    private Set<String> customTableNames;

    @Override
    public void submitJob(String schema, @Nullable Set<String> tableNames) {
        log.info("初始化作业配置信息开始");
        init(schema, tableNames);
        buildJobConfigs();
        log.info("初始化作业配置信息完成");
        customTables();
        log.info("开始提交作业");
        doSubmit();
        log.info("提交作业完成");
    }

    private void customTables() {
        Objects.requireNonNull(globalConfigs);
        if (customTableNames != null && !customTableNames.isEmpty()) {
            this.globalConfigs = globalConfigs.stream().filter(c -> customTableNames.contains(c.getRuleTableName())).collect(Collectors.toList());
        }
    }

    private void doSubmit() {
        Objects.requireNonNull(globalConfigs);
        for (GlobalConfig c : globalConfigs) {
            String json = GlobalConfigSwapper.gson.toJson(c);
            log.info("提交作业入参：表：{}，params：{}", c.getRuleTableName(), json);
            SparkSubmitEncryptShuffleMain.main(new String[]{json, c.getRuleTableName()});
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
                config.setRuleTableName(table.getName());
                // 主键列
                List<ColumnInfoVO> primaryCols = tableAndPrimaryCols.get(table.getName());
                List<FieldInfo> fieldInfos = primaryCols.stream().map(ColumnInfoVO::getName).map(FieldInfo::new).collect(Collectors.toList());
                config.setPrimaryCols(fieldInfos);
                FieldInfo partitionCol = primaryCols.stream().filter(c -> c.getSqlSimpleType().equalsIgnoreCase("int")).map(c -> new FieldInfo(c.getName())).findFirst().orElse(fieldInfos.get(0));
                config.setPartitionCol(partitionCol);

                // 获取表->增量字段
                String incFieldName = tableAndIncField.get(table.getName());
                if (StringUtils.isBlank(incFieldName)) {
                    config.setExtractMode(ExtractMode.All);
                } else {
                    config.setExtractMode(ExtractMode.WithIncField);
                    config.setIncrTimestampCol(incFieldName);
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

    private void init(String schema, Set<String> tableNames) {
        this.metaDataPersistService = configCenterService.getActivatedMetadataService();
        this.schema = schema;
        this.customTableNames = tableNames;
        Map<String, DataSourceConfiguration> dataSource = metaDataPersistService.getDataSourceService().load(schema);
        Map<String, Object> dataSourceProps = dataSource.values().stream().findFirst().map(DataSourceConfiguration::getAllProps).orElseThrow(() -> new RuntimeException("ERROR"));
        this.dataSourceProps = dataSourceProps;
        this.sourceUrl = dataSourceProps.get("jdbcUrl").toString() + "&user=" + dataSourceProps.get("username").toString() + "&password=" + dataSourceProps.get("password");
        // 读取配置中心的规则的表和明文列
        this.encryptRules = readRule();
        this.encryptTablesByRule = findEncryptTablesByRule(encryptRules);
        this.tableAndPrimaryCols = tableAndPrimaryCols();
        // TODO: 2021/12/21 获取导入的表与增量字段关系
        this.tableAndIncField = null;
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
        Map<String, List<ColumnInfoVO>> tableAndPrimaryCols = metaDataContent.getTables().stream().flatMap(t -> t.getColumns().stream())
                .filter(c -> c.getIsPrimary().equals(JudgeEnum.YES))
                .collect(Collectors.groupingBy(ColumnInfoVO::getTableName));
        return tableAndPrimaryCols;

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
