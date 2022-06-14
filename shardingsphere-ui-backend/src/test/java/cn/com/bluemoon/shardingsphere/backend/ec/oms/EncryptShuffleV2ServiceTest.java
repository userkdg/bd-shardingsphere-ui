package cn.com.bluemoon.shardingsphere.backend.ec.oms;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import org.apache.shardingsphere.ui.common.dto.TableExtractDefine;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleV2Service;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Jarod.Kong
 */
public class EncryptShuffleV2ServiceTest extends BaseTest {
    private final Set<String> tableNames = new HashSet<>();
    @Autowired
    private EncryptShuffleV2Service encryptShuffleService;
    private String schema;
    private final List<TableExtractDefine> tableExtractDefines = new ArrayList<>();
    private String dbType;

    @Before
    public void setUp() throws Exception {
        schema = "ec_order_sandbox";
        dbType = GlobalConfig.MYSQL;
        tableNames.add("ec_oms_order");
//        tableExtractDefines.add(new TableExtractDefine("ec_oms_order",true));
    }

    @Test
    public void TestSubmitJob() {
        encryptShuffleService.submitJob(schema, dbType, tableNames, tableExtractDefines);
    }

}
