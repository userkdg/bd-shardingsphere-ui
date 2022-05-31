package cn.com.bluemoon.shardingsphere.backend.ec.crm;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep2Controller;
import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep3Controller;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 洗数上线启动类
 */
public class BmUCMEncryptTest extends BaseTest {
    /**
     * 配置中心、当前应用对应的schema（库名）
     */
    String schemaName = "bm_ucm";
    @Autowired
    private SchemaEncryptStep2Controller controller;
    @Autowired
    private SchemaEncryptStep3Controller step3Controller;

    /**
     * 生成脚本1
     * xxx_1-1_创建密文列_.sql
     */
    @Test
    public void createCipherField() {
        ResponseResult<String> ec_order = controller.createCipherField(schemaName);
        System.out.println(ec_order);
        Assert.assertTrue(ec_order.isSuccess());
    }

    /**
     * 生成脚本2
     * xxx_明文列改为备份列_.sql
     * xxx_密文列改为明文列_.sql
     * xxx_备份列数据清空_.sql
     * xxx_创建备份列_.sql
     */
    @Test
    public void renamePlainField() {
        ResponseResult<String> ec_order = step3Controller.renamePlainField(schemaName);
        System.out.println(ec_order);
        Assert.assertTrue(ec_order.isSuccess());
    }

    /**
     * 单独跑一个小表验证是否正常
     */
    @Test
    public void forTest() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_customer_call_info");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }

    /**
     * 为空，首次全量刷数
     * 不为空，>该值，增量刷数
     */
//    private final String preTimestampValue = "";
//    private final String preTimestampValue = "2022-05-17 09:00:00";

    @Test
    public void shuffleSmallTables() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_customer_import");
            add("scrm_white_list_import");
            add("bm_ucm_member_wx_user");
            add("scrm_marketing_activity_user");
            add("bm_ucm_wxwork_mobile_history");
            add("bm_ucm_yz_customer_import");
            add("bm_ucm_yz_customer");
            add("bm_ucm_wxwork_mobile");
            add("bm_ucm_customer_call_info");
            add("scrm_crowd_data");
            add("bm_ucm_wxwork_customer");
            add("bm_ucm_wxwork_customer_follow_user");
            add("bm_ucm_wxwork_import_record");
            add("bm_ucm_member_cy_customer");
            add("scrm_msg_send_record");
            add("bm_ucm_customer_oper_log_2");
            add("bm_ucm_customer_oper_log_3");
            add("bm_ucm_customer_oper_log_1");

        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{
            put("bm_ucm_customer_1", preTimestampValue);
            put("bm_ucm_customer_2", preTimestampValue);
            put("bm_ucm_customer_3", preTimestampValue);
            put("bm_ucm_customer_address_1", preTimestampValue);
            put("bm_ucm_customer_address_2", preTimestampValue);
            put("bm_ucm_customer_address_3", preTimestampValue);
            put("bm_ucm_customer_buy_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_info_3", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_3", preTimestampValue);
            put("bm_ucm_customer_call_info", preTimestampValue);
            put("bm_ucm_customer_import", preTimestampValue);
            put("bm_ucm_customer_oper_log_1", preTimestampValue);
            put("bm_ucm_customer_oper_log_2", preTimestampValue);
            put("bm_ucm_customer_oper_log_3", preTimestampValue);
            put("bm_ucm_jd_membership_info", preTimestampValue);
            put("bm_ucm_member_cy_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer_follow_user", preTimestampValue);
            put("bm_ucm_wxwork_import_record", preTimestampValue);
            put("bm_ucm_wxwork_mobile", preTimestampValue);
            put("bm_ucm_wxwork_mobile_history", preTimestampValue);
            put("bm_ucm_yz_customer", preTimestampValue);
            put("bm_ucm_yz_customer_import", preTimestampValue);
            put("scrm_crowd_data", preTimestampValue);
            put("scrm_msg_send_record", preTimestampValue);
            put("scrm_white_list_import", preTimestampValue);
            put("bm_ucm_member_wx_user", preTimestampValue);
//            put("scrm_marketing_activity_user", preTimestampValue);

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }

    @Test
    public void shuffleBigTables() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_jd_membership_info");
            add("bm_ucm_customer_3");
            add("bm_ucm_customer_2");
            add("bm_ucm_customer_1");
            add("bm_ucm_customer_buy_info_1");
            add("bm_ucm_customer_buy_info_2");
//            add("bm_ucm_customer_buy_info_3"); doing
            add("bm_ucm_customer_address_1");

        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{
            put("bm_ucm_customer_1", preTimestampValue);
            put("bm_ucm_customer_2", preTimestampValue);
            put("bm_ucm_customer_3", preTimestampValue);
            put("bm_ucm_customer_address_1", preTimestampValue);
            put("bm_ucm_customer_address_2", preTimestampValue);
            put("bm_ucm_customer_address_3", preTimestampValue);
            put("bm_ucm_customer_buy_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_info_3", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_3", preTimestampValue);
            put("bm_ucm_customer_call_info", preTimestampValue);
            put("bm_ucm_customer_import", preTimestampValue);
            put("bm_ucm_customer_oper_log_1", preTimestampValue);
            put("bm_ucm_customer_oper_log_2", preTimestampValue);
            put("bm_ucm_customer_oper_log_3", preTimestampValue);
            put("bm_ucm_jd_membership_info", preTimestampValue);
            put("bm_ucm_member_cy_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer_follow_user", preTimestampValue);
            put("bm_ucm_wxwork_import_record", preTimestampValue);
            put("bm_ucm_wxwork_mobile", preTimestampValue);
            put("bm_ucm_wxwork_mobile_history", preTimestampValue);
            put("bm_ucm_yz_customer", preTimestampValue);
            put("bm_ucm_yz_customer_import", preTimestampValue);
            put("scrm_crowd_data", preTimestampValue);
            put("scrm_msg_send_record", preTimestampValue);
            put("scrm_white_list_import", preTimestampValue);
            put("bm_ucm_member_wx_user", preTimestampValue);
//            put("scrm_marketing_activity_user", preTimestampValue);

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }


    @Test
    public void shuffleAllOrIncr() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_customer_1");
            add("bm_ucm_customer_2");
            add("bm_ucm_customer_3");
            add("bm_ucm_customer_address_1");
            add("bm_ucm_customer_address_2");
            add("bm_ucm_customer_address_3");
            add("bm_ucm_customer_buy_info_1");
            add("bm_ucm_customer_buy_info_2");
            add("bm_ucm_customer_buy_info_3");
            add("bm_ucm_customer_buy_order_info_1");
            add("bm_ucm_customer_buy_order_info_2");
            add("bm_ucm_customer_buy_order_info_3");
            add("bm_ucm_customer_call_info");
            add("bm_ucm_customer_import");
            add("bm_ucm_customer_oper_log_1");
            add("bm_ucm_customer_oper_log_2");
            add("bm_ucm_customer_oper_log_3");
            add("bm_ucm_jd_membership_info");
            add("bm_ucm_member_cy_customer");
            add("bm_ucm_wxwork_customer");
            add("bm_ucm_wxwork_customer_follow_user");
            add("bm_ucm_wxwork_import_record");
            add("bm_ucm_wxwork_mobile");
            add("bm_ucm_wxwork_mobile_history");
            add("bm_ucm_yz_customer");
            add("bm_ucm_yz_customer_import");
            add("scrm_crowd_data");
            add("scrm_msg_send_record");
            add("scrm_white_list_import");
            add("bm_ucm_member_wx_user");
            add("scrm_marketing_activity_user");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{
            put("bm_ucm_customer_1", preTimestampValue);
            put("bm_ucm_customer_2", preTimestampValue);
            put("bm_ucm_customer_3", preTimestampValue);
            put("bm_ucm_customer_address_1", preTimestampValue);
            put("bm_ucm_customer_address_2", preTimestampValue);
            put("bm_ucm_customer_address_3", preTimestampValue);
            put("bm_ucm_customer_buy_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_info_3", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_1", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_2", preTimestampValue);
            put("bm_ucm_customer_buy_order_info_3", preTimestampValue);
            put("bm_ucm_customer_call_info", preTimestampValue);
            put("bm_ucm_customer_import", preTimestampValue);
            put("bm_ucm_customer_oper_log_1", preTimestampValue);
            put("bm_ucm_customer_oper_log_2", preTimestampValue);
            put("bm_ucm_customer_oper_log_3", preTimestampValue);
            put("bm_ucm_jd_membership_info", preTimestampValue);
            put("bm_ucm_member_cy_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer", preTimestampValue);
            put("bm_ucm_wxwork_customer_follow_user", preTimestampValue);
            put("bm_ucm_wxwork_import_record", preTimestampValue);
            put("bm_ucm_wxwork_mobile", preTimestampValue);
            put("bm_ucm_wxwork_mobile_history", preTimestampValue);
            put("bm_ucm_yz_customer", preTimestampValue);
            put("bm_ucm_yz_customer_import", preTimestampValue);
            put("scrm_crowd_data", preTimestampValue);
            put("scrm_msg_send_record", preTimestampValue);
            put("scrm_white_list_import", preTimestampValue);
            put("bm_ucm_member_wx_user", preTimestampValue);
//            put("scrm_marketing_activity_user", preTimestampValue);

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }

    private final String preTimestampValue = "2022-05-24 12:00:00";
    /**
     * 增量表的跑数（可以不用，取决于全量刷库完后的增量数据量是否很大，若大可以考虑）
     */
    @Test
    public void shuffleIncr() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_customer_1");
            add("bm_ucm_customer_2");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{
            put("bm_ucm_customer_1", preTimestampValue);

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }
}
