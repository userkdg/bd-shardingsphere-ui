package cn.com.bluemoon.shardingsphere.backend.ec;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep2Controller;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 电商上线启动类
 */
public class EcOrderSchemaEncryptStep2ControllerTest extends BaseTest {
//    String preTimestamp = "2022-03-10 14:00:00";
//    String preTimestamp = "2022-03-14 14:00:00";
//    String preTimestamp = "2022-03-15 09:00:00";
//    String preTimestamp = "2022-03-15 21:00:00";
    String preTimestamp = "2022-03-15 23:00:00";

    @Autowired
    private SchemaEncryptStep2Controller controller;

    /**
     * 生成脚本
     */
    @Test
    public void createCipherField() {
        ResponseResult<String> ec_order = controller.createCipherField("ec_order");
        System.out.println(ec_order);

    }

    /**
     * 小表-全量
     */
    @Test
    public void sumbitSmallTable() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema("ec_order");
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        // "2022-03-10 14:00:00";
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("ec_oms_plat_address_modify_record");
            add("ec_oms_exc_reissue_order");
            add("ec_oms_address_modify_record");
            add("ec_oms_address_clean_record");
            add("oms_b2b_oper_client_base");
            add("oms_b2b_oper_client_account");
            add("ec_oms_self_help_query_log");
            add("sys_user");
            add("oms_b2b_client_storehouse");
            add("ec_oms_exc_offline_refund_order");
            add("ec_oms_channel_shop_base");
            add("oms_b2b_client_distri_channel_charge");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
    }

    /**
     * 大表-全量（应用考虑到生产canal的同步效率问题，单独对大表进行跑）
     */
    @Test
    public void sumbitBigTableStep1() {

        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema("ec_order");
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("ec_oms_order");
            add("ec_oms_plat_order_encrypt_data");
            add("ec_oms_order_import");
            add("ec_oms_invoice");
            add("ec_oms_plat_order_decrypt_data");
            add("ec_oms_sms_management_sub");
            add("ec_oms_plat_tmall_presale_order");
            add("ec_oms_sms_management");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
    }

    /**
     * 增量（步骤）执行
     */
    @Test
    public void submitBigTableIncr() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema("ec_order");
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("ec_oms_order");
            add("ec_oms_plat_order_encrypt_data");
            add("ec_oms_order_import");
            add("ec_oms_invoice");
            add("ec_oms_plat_order_decrypt_data");
            add("ec_oms_sms_management_sub");
            add("ec_oms_plat_tmall_presale_order");
            add("ec_oms_sms_management");

            // 小表
            add("ec_oms_plat_address_modify_record");
            add("ec_oms_exc_reissue_order");
            add("ec_oms_address_modify_record");
            add("ec_oms_address_clean_record");
            add("oms_b2b_oper_client_base");
            add("oms_b2b_oper_client_account");
            add("ec_oms_self_help_query_log");
            add("sys_user");
            add("oms_b2b_client_storehouse");
            add("ec_oms_exc_offline_refund_order");
            add("ec_oms_channel_shop_base");
            add("oms_b2b_client_distri_channel_charge");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{
            put("ec_oms_order", preTimestamp);
            put("ec_oms_plat_order_encrypt_data", preTimestamp);
            put("ec_oms_order_import", preTimestamp);
            put("ec_oms_invoice", preTimestamp);
            put("ec_oms_plat_order_decrypt_data", preTimestamp);
            put("ec_oms_sms_management_sub", preTimestamp);
            put("ec_oms_plat_tmall_presale_order", preTimestamp);
            put("ec_oms_sms_management", preTimestamp);

            // 小表
            put("ec_oms_plat_address_modify_record", preTimestamp);
            put("ec_oms_exc_reissue_order", preTimestamp);
            put("ec_oms_address_modify_record", preTimestamp);
            put("ec_oms_address_clean_record", preTimestamp);
            put("oms_b2b_oper_client_base", preTimestamp);
            put("oms_b2b_oper_client_account", preTimestamp);
            put("ec_oms_self_help_query_log", preTimestamp);
            put("sys_user", preTimestamp);
            put("oms_b2b_client_storehouse", preTimestamp);
            put("ec_oms_exc_offline_refund_order", preTimestamp);
            put("ec_oms_channel_shop_base", preTimestamp);
            put("oms_b2b_client_distri_channel_charge", preTimestamp);
        }});
        shuffleVo.setWithIncrFieldExtractOnce(true);
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
    }
}
