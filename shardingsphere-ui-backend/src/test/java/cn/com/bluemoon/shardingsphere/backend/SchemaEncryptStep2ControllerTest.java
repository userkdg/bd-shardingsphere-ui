package cn.com.bluemoon.shardingsphere.backend;

import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep2Controller;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashMap;
import java.util.HashSet;

public class SchemaEncryptStep2ControllerTest extends BaseTest {
    @Autowired
    private SchemaEncryptStep2Controller controller;

    @Test
    public void createCipherField() {
        ResponseResult<String> ec_order = controller.createCipherField("ec_order");
        System.out.println(ec_order);

    }

    @Test
    public void name() {
        SchemaEncryptStep2Controller.EncryptShuffleVo shuffleVo = new SchemaEncryptStep2Controller.EncryptShuffleVo();
        shuffleVo.setSchema("ec_order");
        shuffleVo.setUnSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setSelectedTableNames(new HashSet<String>() {{

        }});
        shuffleVo.setTableNameAndIncrFieldPreVal(new HashMap<String, String>() {{

        }});
        ResponseResult<String> res = controller.encryptShuffle(shuffleVo);
        System.out.println(res);
    }
}
