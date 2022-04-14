package cn.com.bluemoon.shardingsphere.backend.util;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.shardingsphere.ui.Bootstrap;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Random;

/**
 * @author Jarod.Kong
 */
/*@SpringBootTest(classes = Bootstrap.class)
@RunWith(value = SpringRunner.class)*/
public class BaseTest {

    @Test
    public void test(){

        String filename = RandomStringUtils.randomAlphanumeric(16);
        System.out.println(filename);
    }
}
