package org.apache.shardingsphere.ui.web.controller;

import org.apache.shardingsphere.ui.servcie.CreateCipherService;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务系统接入加密-步骤3
 * 明确业务系统在步骤2中没问题后，可执行该接口
 * 1.对加密表明文列修改名称
 */
@RestController
@RequestMapping("/api/config-center")
public class SchemaEncryptStep3Controller {

    @Autowired
    CreateCipherService createCipherService;

    @GetMapping("rename/plain")
    public ResponseResult<String> renamePlainField(@RequestParam("schema") String schemaName) {

        return createCipherService.createCipherPlainField(schemaName, false);
    }
}
