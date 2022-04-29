package cn.com.bluemoon.shardingsphere.backend.ec.oms;

import cn.com.bluemoon.encrypt.shuffle.cli.SparkSubmitEncryptShuffleMain;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Jarod.Kong
 */
@Slf4j
public class SparkSubmitEcOrderShuffleTest2 {
    private String tableName;
    private String jsonParam;

    @Before
    public void setUp() throws Exception {
        tableName = "ec_oms_invoice";
        jsonParam = "{\"sourceUrl\":\"jdbc:mysql://192.168.234.7:3306/ec_order?useUnicode\\u003dtrue\\u0026characterEncoding\\u003dutf8\\u0026rewriteBatchedStatements\\u003dtrue\\u0026useSSL\\u003dfalse\\u0026user\\u003dshproxy_morder\\u0026password\\u003d9kD6sN4qMIwN\",\"targetUrl\":\"jdbc:mysql://192.168.234.7:3306/ec_order?useUnicode\\u003dtrue\\u0026characterEncoding\\u003dutf8\\u0026rewriteBatchedStatements\\u003dtrue\\u0026useSSL\\u003dfalse\\u0026user\\u003dshproxy_morder\\u0026password\\u003d9kD6sN4qMIwN\",\"ruleTableName\":\"ec_oms_invoice\",\"primaryCols\":[{\"name\":\"id\"}],\"partitionCol\":{\"name\":\"id\"},\"incrTimestampCol\":\"last_update_time\",\"onYarn\":true,\"jobName\":\"bd-spark-kms-shuffle-ec_oms_invoice\",\"shuffleCols\":[{\"t1\":{\"name\":\"buyer_bank_account\"},\"t2\":{\"name\":\"buyer_bank_account_cipher\",\"encryptRule\":{\"type\":\"AES\",\"props\":{\"aes-key-value\":\"wlf1d5mmal2xsttr\"}}}},{\"t1\":{\"name\":\"buyer_address\"},\"t2\":{\"name\":\"buyer_address_cipher\",\"encryptRule\":{\"type\":\"AES\",\"props\":{\"aes-key-value\":\"wlf1d5mmal2xsttr\"}}}},{\"t1\":{\"name\":\"invoice_receiver_phone\"},\"t2\":{\"name\":\"invoice_receiver_phone_cipher\",\"encryptRule\":{\"type\":\"AES\",\"props\":{\"aes-key-value\":\"wlf1d5mmal2xsttr\"}}}},{\"t1\":{\"name\":\"invoice_receiver_address\"},\"t2\":{\"name\":\"invoice_receiver_address_cipher\",\"encryptRule\":{\"type\":\"AES\",\"props\":{\"aes-key-value\":\"wlf1d5mmal2xsttr\"}}}},{\"t1\":{\"name\":\"invoice_receiver\"},\"t2\":{\"name\":\"invoice_receiver_cipher\",\"encryptRule\":{\"type\":\"AES\",\"props\":{\"aes-key-value\":\"wlf1d5mmal2xsttr\"}}}}],\"extractMode\":\"WithIncField\",\"multiBatchUrlConfig\":true,\"shuffleMode\":\"ENCRYPT\"}";
    }

    @Test
    public void name() {
        log.info("提交作业入参：表：{}，params：{}", tableName, jsonParam);
        SparkSubmitEncryptShuffleMain.main(new String[]{jsonParam, tableName});
    }
}
