package cn.com.bluemoon.shardingsphere.backend.util;

import cn.hutool.core.util.RandomUtil;
import org.junit.Test;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.junit.Assert.assertFalse;

/**
 * @author Jarod.Kong
 */
public class JBaseTest {


    @Test
    public void secureKey() {
        String s = RandomUtil.randomString(16);
        System.out.println(s);
    }

    @Test
    public void findConnectInfo() {
        String url = "jdbc:mysql://192.168.xx.x:3306/xxxx?useUnicode=true&characterEncoding=utf8&rewriteBatchedStatements=true&useSSL=false";
        Matcher matcher = Pattern.compile("jdbc:(.*)://(.*):([0-9]+)/(.*)\\?(.*)").matcher(url);
        HashMap<String, Object> res = new HashMap<>();
        if (matcher.find()) {
            res.put("dbType", matcher.group(1));
            res.put("host", matcher.group(2));
            res.put("port", matcher.group(3));
            res.put("dbname", matcher.group(4));
            res.put("configs", matcher.group(5));
        }
        assertFalse(res.isEmpty());
    }
}
