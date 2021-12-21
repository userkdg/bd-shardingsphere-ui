package cn.com.bluemoon.shardingsphere.backend;

import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;

/**
 * @author Jarod.Kong
 */
public class EncryptShuffleServiceTest extends BaseTest {
    @Autowired
    private EncryptShuffleService encryptShuffleService;

    @Test
    public void testSubmit() {
        encryptShuffleService.submitJob("ec_order_test", Collections.singleton("ec_oms_invoice"));
    }
}
