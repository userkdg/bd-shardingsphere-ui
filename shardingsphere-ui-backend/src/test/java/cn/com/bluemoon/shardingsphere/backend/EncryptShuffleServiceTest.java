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
                    put("ec_oms_order", "2022-01-03 10:00:00");
                    put("ec_oms_plat_order_encrypt_data", "2022-01-03 10:00:00");
                }});
    }
}
