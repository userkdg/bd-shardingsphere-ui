package org.apache.shardingsphere.ui.web.controller;

import lombok.Data;
import org.apache.shardingsphere.ui.servcie.CreateCipherService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Set;

/**
 * 业务系统接入加密-步骤2
 * 对系统上报的敏感字段信息做处理
 * 1.基于上报数据源、加密规则等，对明文列进行分析后创建密文列字段-接口1
 * 2.基于接口1成功后，基于报数据源、加密规则等分别对加密表进行洗数作业-接口2
 */
@RestController
@RequestMapping("/api/config-center")
public class SchemaEncryptStep2Controller {

    @Autowired
    CreateCipherService createCipherService;

    @Autowired
    EncryptShuffleService encryptShuffleService;

    /**
     * 根据schema名称创建加密字段
     */
    @GetMapping("create/cipher")
    public ResponseResult<String> createCipherField(@RequestParam("schema") String schemaName) {

        return createCipherService.createCipherPlainField(schemaName, true);
    }

    @PostMapping("encrypt/shuffle")
    public ResponseResult<String> encryptShuffle(@RequestBody EncryptShuffleVo encryptShuffleVo) {
        encryptShuffleService.submitJob(encryptShuffleVo.getSchema(), encryptShuffleVo.getTableNames(), encryptShuffleVo.getTableNameAndIncrFieldPreVal());
        return ResponseResult.ok("提交作业成功");
    }

    @Data
    public static class EncryptShuffleVo {
        private String schema;
        private Set<String> tableNames;
        /**
         * 表名
         * 表对应的增量字段值
         */
        @Nullable
        private Map<String, String> tableNameAndIncrFieldPreVal;

    }

}
