package cn.com.bluemoon.shardingsphere.backend;

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
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig.Tuple2;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfigSwapper;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.platform.commons.util.StringUtils;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class SparkSubmitShardingSphereTest extends BaseTest {

    private String sourceUrl;

    private List<TableInfo> tableInfos;
    @Autowired
    private ConfigCenterService configCenterService;
    private MetaDataPersistService metaDataPersistService;
    private String schema;
    private List<EncryptRuleConfiguration> rules;
    private Map<String, Object> dataSourceProps;
    @Mock
    private DbMetaDataService dbMetaDataService;
    private Map<String, String> tableAndIncField;

    private List<EncryptRuleConfiguration> readRule() {
        Collection<RuleConfiguration> collection = metaDataPersistService.getSchemaRuleService().load(schema);
        Assert.assertFalse(collection.isEmpty());
        List<EncryptRuleConfiguration> encrypts = collection.stream().map(r -> (EncryptRuleConfiguration) r).collect(Collectors.toList());
        Assert.assertFalse(encrypts.isEmpty());
        return encrypts;
    }

    @Before
    public void setUp() throws Exception {
        this.metaDataPersistService = configCenterService.getActivatedMetadataService();
        this.schema = "ec_order_test";
        Map<String, DataSourceConfiguration> dataSource = metaDataPersistService.getDataSourceService().load(schema);
        Map<String, Object> dataSourceProps = dataSource.values().stream().findFirst()
                .map(DataSourceConfiguration::getAllProps).orElseThrow(() -> new RuntimeException("ERROR"));
        this.dataSourceProps = dataSourceProps;
        this.sourceUrl = dataSourceProps.get("jdbcUrl").toString() + "&user=" + dataSourceProps.get("username").toString() + "&password=" + dataSourceProps.get("password");
        // 读取配置中心的规则的表和明文列
        this.rules = readRule();
        List<TableInfo> tableInfos0 = rules.stream().flatMap(e -> e.getTables().stream())
                .map(t -> {
                    List<String> cols = t.getColumns().stream().map(c -> Optional.ofNullable(c.getPlainColumn()).orElse(c.getLogicColumn())).collect(Collectors.toList());
                    return new TableInfo(t.getName(), cols);
                }).collect(Collectors.toList());
        assert !tableInfos0.isEmpty();
        this.tableInfos = tableInfos0;
        // 获取表与增量字段映射
        // TODO: 2021/12/18 dynamic
        Map<String, String> tableAndIncField = new HashMap<>();
        tableAndIncField.put("ec_oms_order", "last_update_time");
        tableAndIncField.put("sys_user", "op_time");
        this.tableAndIncField = tableAndIncField;
    }

    @Test
    public void submit() {

        QueryMetaDataRequest metaDataRequest = new QueryMetaDataRequest();
        metaDataRequest.setDbType(DbTypeEnum.MYSQL);
        metaDataRequest.setPort(String.valueOf(dataSourceProps.get("port")));
        metaDataRequest.setIp(String.valueOf(dataSourceProps.get("host")));
        metaDataRequest.setPassword(String.valueOf(dataSourceProps.get("password")));
        metaDataRequest.setUsername(String.valueOf(dataSourceProps.get("user")));
        metaDataRequest.setDbName(String.valueOf(dataSourceProps.get("database")));
        metaDataRequest.setSchemaName(String.valueOf(dataSourceProps.get("database")));
        metaDataRequest.setTableNames(tableInfos.stream().map(TableInfo::getName).collect(Collectors.joining(",")));
        ResultBean<SchemaInfoVO> metaData = dbMetaDataService.queryMetaData(metaDataRequest);
        SchemaInfoVO metaDataContent = metaData.getContent();
        Map<String, List<ColumnInfoVO>> tableAndPrimaryCols = metaDataContent.getTables().stream().flatMap(t -> t.getColumns().stream())
                .filter(c -> c.getIsPrimary().equals(JudgeEnum.YES))
                .collect(Collectors.groupingBy(ColumnInfoVO::getTableName));

        List<GlobalConfig> configs = new ArrayList<>();
        for (EncryptRuleConfiguration rule : rules) {
            Map<String, ShardingSphereAlgorithmConfiguration> encryptorMaps = rule.getEncryptors().entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            for (EncryptTableRuleConfiguration table : rule.getTables()) {
                GlobalConfig config = new GlobalConfig();
                config.setSourceUrl(sourceUrl);
                config.setTargetUrl(sourceUrl);
                config.setRuleTableName(table.getName());
                // 主键列
                List<ColumnInfoVO> primaryCols = tableAndPrimaryCols.get(table.getName());
                List<FieldInfo> fieldInfos = primaryCols.stream().map(ColumnInfoVO::getName).map(c -> new FieldInfo(c)).collect(Collectors.toList());
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
                config.setJobName("bd-spark-kms-shuffle-" + table.getName());
                // 明文列加密
                List<Tuple2<FieldInfo>> shuffleCols = new ArrayList<>();
                for (EncryptColumnRuleConfiguration column : table.getColumns()) {
                    String encryptorName = column.getEncryptorName();
                    ShardingSphereAlgorithmConfiguration aglo = encryptorMaps.get(encryptorName);
                    FieldInfo extractCol = new FieldInfo(Optional.ofNullable(column.getPlainColumn()).orElse(column.getLogicColumn()), new GlobalConfig.EncryptRule(aglo.getType(), aglo.getProps()));
                    FieldInfo targetCol = new FieldInfo(column.getCipherColumn());
                    shuffleCols.add(new Tuple2<>(extractCol, targetCol));
                }
                config.setShuffleCols(shuffleCols);
                config.setMultiBatchUrlConfig(true);
                configs.add(config);
            }
        }
        // submit
        Optional.of(configs)
                .filter(c -> !c.isEmpty())
                .ifPresent(cs -> {
                    for (GlobalConfig c : cs) {
                        String json = GlobalConfigSwapper.gson.toJson(c);
                        SparkSubmitEncryptShuffleMain.main(new String[]{json, c.getRuleTableName()});
                    }
                });
    }

    @RequiredArgsConstructor
    @Getter
    private static class TableInfo {
        private final String name;
        private final List<String> cleanFields;
    }


}
