package cn.com.bluemoon.shardingsphere.backend;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.mode.metadata.persist.service.SchemaMetaDataPersistService;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.CenterConfigService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class SchemaMetadataTest extends BaseTest{
    @Autowired
    private ShardingSchemaService schemaService;

    @Autowired
    private ConfigCenterService configCenterService;

    @Test
    public void name() {
        MetaDataPersistService metadataService = configCenterService.getActivatedMetadataService();
        SchemaMetaDataPersistService schemaMetaDataService = metadataService.getSchemaMetaDataService();
        Optional<ShardingSphereSchema> sphereSchema = schemaMetaDataService.load("ec_order_db");
        ShardingSphereSchema shardingSphereSchema = sphereSchema.get();
        Collection<String> allSchemaNames = schemaService.getAllSchemaNames();

    }
}
