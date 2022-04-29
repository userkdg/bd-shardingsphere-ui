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
            add("mall_wash_appointment_order");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }

    /**
     * 全量
     */
    @Test
    public void shuffleEncrypt() {
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
        shuffleVo.setWithIncrFieldExtractOnce(true);
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }
    @Test
    public void shuffleEncrypt2() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema(schemaName);
        shuffleVo.setDbType(GlobalConfig.MYSQL);
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{
            add("bm_ucm_customer_1");
//            add("bm_ucm_customer_2");
//            add("bm_ucm_customer_3");
//            add("bm_ucm_customer_address_1");
//            add("bm_ucm_customer_address_2");
//            add("bm_ucm_customer_address_3");
//            add("bm_ucm_customer_buy_info_1");
//            add("bm_ucm_customer_buy_info_2");
//            add("bm_ucm_customer_buy_info_3");
//            add("bm_ucm_customer_buy_order_info_1");
//            add("bm_ucm_customer_buy_order_info_2");
//            add("bm_ucm_customer_buy_order_info_3");
//            add("bm_ucm_customer_call_info");
//            add("bm_ucm_customer_import");
//            add("bm_ucm_customer_oper_log_1");
//            add("bm_ucm_customer_oper_log_2");
//            add("bm_ucm_customer_oper_log_3");
//            add("bm_ucm_jd_membership_info");
//            add("bm_ucm_member_cy_customer");
//            add("bm_ucm_wxwork_customer");
//            add("bm_ucm_wxwork_customer_follow_user");
//            add("bm_ucm_wxwork_import_record");
//            add("bm_ucm_wxwork_mobile");
//            add("bm_ucm_wxwork_mobile_history");
//            add("bm_ucm_yz_customer");
//            add("bm_ucm_yz_customer_import");
//            add("scrm_crowd_data");
//            add("scrm_msg_send_record");
//            add("scrm_white_list_import");
//            add("bm_ucm_member_wx_user");
//            add("scrm_marketing_activity_user");

        }});
        // FIXME: 2022/4/21  setWithIncrFieldExtractOnce=true刷库失败！
//        shuffleVo.setWithIncrFieldExtractOnce(true);
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }
}
