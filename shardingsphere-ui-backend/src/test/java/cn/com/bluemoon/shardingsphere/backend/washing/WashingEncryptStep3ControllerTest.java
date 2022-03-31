package cn.com.bluemoon.shardingsphere.backend.washing;

import cn.com.bluemoon.shardingsphere.backend.util.BaseTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.web.controller.SchemaEncryptStep3Controller;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Jarod.Kong
 */
@Slf4j
public class WashingEncryptStep3ControllerTest extends BaseTest {
    @Autowired
    private SchemaEncryptStep3Controller controller;

    @Test
    public void renamePlainField() {
        ResponseResult<String> ec_order = controller.renamePlainField("washingservice");
        System.out.println(ec_order);
    }

}
