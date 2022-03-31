package cn.com.bluemoon.shardingsphere.backend.ec;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class EncryptionRuleSourceTablePlainSetNullTest extends BaseTest {

    private String sourceUrl;

    private List<TableInfo> tableInfos;
    @Autowired
    private ConfigCenterService configCenterService;
    private MetaDataPersistService metaDataPersistService;
    private String schema;

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
        this.schema = "ec_order_db";
        Map<String, DataSourceConfiguration> dataSource = metaDataPersistService.getDataSourceService().load(schema);
        Map<String, Object> dataSourceProps = dataSource.values().stream().findFirst()
                .map(DataSourceConfiguration::getAllProps).orElseThrow(() -> new RuntimeException("ERROR"));
        this.sourceUrl = dataSourceProps.get("jdbcUrl").toString() + "&user=" + dataSourceProps.get("username").toString() + "&password=" + dataSourceProps.get("password");
        // 读取配置中心的规则的表和明文列
        List<EncryptRuleConfiguration> encrypts = readRule();
        List<TableInfo> tableInfos0 = encrypts.stream().flatMap(e -> e.getTables().stream())
                .map(t -> {
                    List<String> cols = t.getColumns().stream().map(c -> Optional.ofNullable(c.getPlainColumn()).orElse(c.getLogicColumn())).collect(Collectors.toList());
                    return new TableInfo(t.getName(), cols);
                }).collect(Collectors.toList());
        assert !tableInfos0.isEmpty();
        this.tableInfos = tableInfos0;
    }

    @Test
    public void testSourceTablePlainSetNull() {
        // update table_xx set field_1=null, field_2 = null where 1=1
        // alter table xx drop column col1, drop column col2 ..;
        List<String> updateSqls = getSqlMode(FieldMode.DROP);
        updateSqls.parallelStream()
                .forEach(sql -> {
                    try (Connection conn = DriverManager.getConnection(sourceUrl)) {
                        try (PreparedStatement ps = conn.prepareStatement(sql)) {
                            boolean execute = ps.execute();
                            log.info("sql={},status:{}", sql, execute);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                });
        log.info("sqls:{}", updateSqls);
    }

    private List<String> getSqlMode(FieldMode mode) {
        if (FieldMode.DROP.equals(mode)) {
            return getDropSqls();
        }
        return getUpdateSqls();
    }

    private List<String> getDropSqls() {
        List<String> updateSqls = new ArrayList<>(tableInfos.size());
        for (TableInfo tableInfo : tableInfos) {
            StringBuilder sb = new StringBuilder();
            sb.append("alter table ").append(tableInfo.getName());
            String setFieldNulls = tableInfo.getCleanFields().stream()
                    .map(f -> " drop column " + f).collect(Collectors.joining(" , "));
            sb.append(setFieldNulls);
            updateSqls.add(sb.toString());
        }
        return updateSqls;
    }

    private List<String> getUpdateSqls() {
        List<String> updateSqls = new ArrayList<>(tableInfos.size());
        for (TableInfo tableInfo : tableInfos) {
            StringBuilder sb = new StringBuilder();
            sb.append("update ").append(tableInfo.getName()).append(" set ");
            String setFieldNulls = tableInfo.getCleanFields().stream().map(f -> f + "=0").collect(Collectors.joining(" , "));
            sb.append(setFieldNulls);
            updateSqls.add(sb.toString());
        }
        return updateSqls;
    }

    public enum FieldMode {
        UPDATE, DROP;
    }

    @RequiredArgsConstructor
    @Getter
    private static class TableInfo {
        private final String name;
        private final List<String> cleanFields;
    }


}
