package cn.com.bluemoon.shardingsphere.backend;

import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

/**
 * @author Jarod.Kong
 */
public class EncryptShuffleServiceTest extends BaseTest {
    @Autowired
    private EncryptShuffleService encryptShuffleService;

    @Test
    public void testNewApi() {

        encryptShuffleService.submitJob("ec_order_test", null, new HashSet<String>() {{
                    add("oms_b2b_client_storehouse");
                }},
                new HashMap<String, String>() {{

                }});
    }

    /**
     * 刷数据时候，要保证本地配置文件
     * eg: C:\Users\Administrator\shardingsphere-ui-configs.yaml
     * 中激活的配置中心对应需要的配置中心命名空间
     * <pre>
     *     centerConfigs:
     *       - activated: true
     *         digest: ''
     *         instanceType: Zookeeper
     *         name: data_security_ns_test
     *         namespace: data_security_ns_test
     *         orchestrationName: data_security_ns_test
     *         orchestrationType: config_center
     *         props: {}
     *         serverLists: 192.168.243.34:12181
     * </pre>
     */
    @Test
    public void testSubmit() {
        encryptShuffleService.submitJob("ec_order", new HashSet<String>() {{
                    add("ec_oms_order");
                    add("ec_oms_plat_order_encrypt_data");
                }},
                null, null);
        encryptShuffleService.submitJob("ec_order", null, new HashSet<String>() {{
                    add("ec_oms_order");
                    add("ec_oms_plat_order_encrypt_data");
                }},
                new HashMap<String, String>() {{
                    put("ec_oms_order", "2022-01-16 10:00:00");
                    put("ec_oms_plat_order_encrypt_data", "2022-01-16 10:00:00");
                }});
    }
}
