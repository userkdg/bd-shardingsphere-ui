package cn.com.bluemoon.shardingsphere.backend.ec.oms;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.mode.metadata.persist.service.SchemaMetaDataPersistService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;


@Slf4j
public class SchemaMetadataTest extends BaseTest {
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
