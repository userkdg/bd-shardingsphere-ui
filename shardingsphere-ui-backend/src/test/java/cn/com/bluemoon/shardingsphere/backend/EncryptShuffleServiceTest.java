package cn.com.bluemoon.shardingsphere.backend;

import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.HashMap;

/**
 * @author Jarod.Kong
 */
public class EncryptShuffleServiceTest extends BaseTest {
    @Autowired
    private EncryptShuffleService encryptShuffleService;

    @Test
    public void testSubmit() {
//        encryptShuffleService.submitJob("ec_order", null, null);
//        encryptShuffleService.submitJob("ec_order",null, null);
        encryptShuffleService.submitJob("ec_order", Collections.singleton("sys_user"), new HashMap<String, String>(){{
            put("sys_user", "2021-12-20 11:19:33");
        }});
    }
}
