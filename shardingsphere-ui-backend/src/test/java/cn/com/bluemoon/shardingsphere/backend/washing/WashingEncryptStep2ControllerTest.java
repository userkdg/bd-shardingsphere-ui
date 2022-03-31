package cn.com.bluemoon.shardingsphere.backend.washing;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep2Controller;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

/**
 * 洗数上线启动类
 */
public class WashingEncryptStep2ControllerTest extends BaseTest {
    String schemaName = "washingservice";

    @Autowired
    private SchemaEncryptStep2Controller controller;


    /**
     * 生成脚本-创建加密字段
     */
    @Test
    public void createCipherField() {
        ResponseResult<String> ec_order = controller.createCipherField(schemaName);
        System.out.println(ec_order);

    }

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
    }

}
