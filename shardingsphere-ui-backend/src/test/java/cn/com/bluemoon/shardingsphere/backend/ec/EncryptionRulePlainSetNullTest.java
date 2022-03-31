package cn.com.bluemoon.shardingsphere.backend.ec;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
public class EncryptionRulePlainSetNullTest extends BaseTest {

    @Autowired
    private ConfigCenterService configCenterService;

    private MetaDataPersistService metaDataPersistService;

    private String schema;

    @Before
    public void setUp() throws Exception {
        this.metaDataPersistService = configCenterService.getActivatedMetadataService();
        this.schema = "ec_order_db";
    }

    @Test
    public void testReadRule() {
        readRule();
    }

    private List<EncryptRuleConfiguration> readRule() {
        Collection<RuleConfiguration> collection = metaDataPersistService.getSchemaRuleService().load(schema);
        Assert.assertFalse(collection.isEmpty());
        List<EncryptRuleConfiguration> encrypts = collection.stream().map(r -> (EncryptRuleConfiguration) r).collect(Collectors.toList());
        Assert.assertFalse(encrypts.isEmpty());
        return encrypts;
    }


    @Test
    public void testReadWriteRule() throws IOException {
        List<EncryptRuleConfiguration> encryptRuleConfigurations = readRule();
        // bak
        jsonBak("sharding-encrypt-rule", encryptRuleConfigurations);
        List<RuleConfiguration> encryptRuleConfigurationsNew = cleanPlainCols(encryptRuleConfigurations);
        // bak
        jsonBak("sharding-encrypt-rule-new", encryptRuleConfigurationsNew);
        writeRule(encryptRuleConfigurationsNew);
    }

    private <T> void jsonBak(String tmpPrefix, List<T> encryptRuleConfigurations) throws IOException {
        Path tempFile = Files.createTempFile(tmpPrefix, ".json");
        Files.write(tempFile, JSON.toJSONBytes(encryptRuleConfigurations));
    }

    private void writeRule(List<RuleConfiguration> encryptRuleConfigurations) {
        metaDataPersistService.getSchemaRuleService()
                .persist(schema, encryptRuleConfigurations, true);

    }

    private List<RuleConfiguration> cleanPlainCols(List<EncryptRuleConfiguration> encryptRuleConfigurations) {
        return encryptRuleConfigurations.stream()
                .map(e -> {
                    List<EncryptTableRuleConfiguration> tables = e.getTables().stream().map(t -> {
                        List<EncryptColumnRuleConfiguration> newCols = t.getColumns().stream().map(col ->{
                                return new EncryptColumnRuleConfiguration(col.getLogicColumn(), col.getCipherColumn(), col.getAssistedQueryColumn(), null, col.getEncryptorName());
                        }).collect(Collectors.toList());
                        return new EncryptTableRuleConfiguration(t.getName(), newCols, t.getQueryWithCipherColumn());
                    }).collect(Collectors.toList());
                    return new EncryptRuleConfiguration(tables, e.getEncryptors());
                }).collect(Collectors.toList());
    }


}
