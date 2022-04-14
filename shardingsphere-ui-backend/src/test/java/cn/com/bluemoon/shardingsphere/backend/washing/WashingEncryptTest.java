package cn.com.bluemoon.shardingsphere.backend.washing;

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
public class WashingEncryptTest extends BaseTest {
    /**
     * 配置中心、当前应用对应的schema（库名）
     */
    String schemaName = "mh_smc";

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
            add("mall_wash_order_info");
            add("mall_wash_appointment_order");
            add("mall_wash_collect_info");
            add("mall_wash_back_order");
            add("mall_wash_back_order_log");
            add("mall_wash_back_order_operation_log");
            add("mall_wash_order_back_address");
            add("mall_wash_other_back_ways");
            add("mall_wash_carriage");
            add("mall_wash_carriage_address");
            add("mall_wash_clothes_unique_id");
        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
        Assert.assertTrue(res.isSuccess());
    }

}
