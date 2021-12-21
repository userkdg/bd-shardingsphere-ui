package org.apache.shardingsphere.ui.web.controller;

import org.apache.shardingsphere.ui.servcie.CreateCipherService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/config-center")
public class CreateCipherController {

    @Autowired
    CreateCipherService createCipherService;

    /**
     * 根据schema名称创建加密字段
     * @param schemaName
     */
    @GetMapping("create/cipher")
    public ResponseResult<String> createCipherField(@RequestParam("schema") String schemaName){

        return createCipherService.createCipherPlainField(schemaName, true);
    }

    @GetMapping("rename/plain")
    public ResponseResult<String> renamePlainField(@RequestParam("schema") String schemaName){

        return createCipherService.createCipherPlainField(schemaName, false);
    }
}
