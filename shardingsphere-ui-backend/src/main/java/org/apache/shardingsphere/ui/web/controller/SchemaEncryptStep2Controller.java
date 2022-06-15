package org.apache.shardingsphere.ui.web.controller;

import cn.com.bluemoon.daps.common.toolkit.WebDownloadUtils;
import cn.com.bluemoon.shardingsphere.custom.shuffle.base.GlobalConfig;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.common.dto.EncryptShuffleV2BaseDto;
import org.apache.shardingsphere.ui.common.dto.EncryptShuffleV2Dto;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.servcie.CreateCipherService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleService;
import org.apache.shardingsphere.ui.servcie.EncryptShuffleV2Service;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * 业务系统接入加密-步骤2
 * 对系统上报的敏感字段信息做处理
 * 1.基于上报数据源、加密规则等，对明文列进行分析后创建密文列字段-接口1
 * 2.基于接口1成功后，基于报数据源、加密规则等分别对加密表进行洗数作业-接口2
 */
@RestController
@RequestMapping("/api/config-center")
@Slf4j
public class SchemaEncryptStep2Controller {

    @Autowired
    CreateCipherService createCipherService;

    @Autowired
    EncryptShuffleService encryptShuffleService;

    @Autowired
    EncryptShuffleV2Service encryptShuffleV2Service;

    /**
     * 根据schema名称创建加密字段
     */
    @GetMapping("create/cipher")
    public ResponseResult<String> createCipherField(@RequestParam("schema") String schemaName) {
        return createCipherService.operateDbField(schemaName, true);
    }

    /**
     * 根据schema名称创建加密字段
     */
    @GetMapping("encrypt/download")
    public void downloadScript(@RequestParam("schema") String schemaName, HttpServletResponse response) throws IOException {
        List<Path> creatCipherScript = createCipherService.createIntegrationScriptPath(schemaName, true);
        List<Path> otherScript = createCipherService.createIntegrationScriptPath(schemaName, false);
        List<Path> allScript = new ArrayList<>(creatCipherScript);
        allScript.addAll(otherScript);
        if (allScript.isEmpty()){
            throw new ShardingSphereUIException(500, schemaName+"生成脚本失败");
        }
        File parentFile = allScript.get(0).toFile().getParentFile();
        File zip = ZipUtil.zip(parentFile);
        log.info("zip:{}", zip);
        WebDownloadUtils.commonDownload(response, zip);
    }

    @PostMapping("encrypt/shuffle")
    public ResponseResult<String> encryptShuffle(@RequestBody EncryptShuffleVo encryptShuffleVo) {
        encryptShuffleService.submitJob(encryptShuffleVo.getSchema(), encryptShuffleVo.getDbType(),
                encryptShuffleVo.getUnSelectedTableNames(),
                encryptShuffleVo.getSelectedTableNames(),
                encryptShuffleVo.getTableNameAndIncrFieldPreVal(),
                encryptShuffleVo.isWithIncrFieldExtractOnce());
        return ResponseResult.ok("提交作业成功");
    }


    @PostMapping("encrypt/shuffleV2")
    public ResponseResult<String> simpleEncryptShuffleV2(@RequestBody EncryptShuffleV2BaseDto encryptShuffleVo) {
        encryptShuffleV2Service.submitJob(encryptShuffleVo.getSchema(), GlobalConfig.MYSQL, encryptShuffleVo.getShuffleTableNames(), Collections.emptyList());
        return ResponseResult.ok("提交作业成功");
    }

    @PostMapping("encrypt/shuffleV2/custom")
    public ResponseResult<String> customEncryptShuffleV2(@RequestBody EncryptShuffleV2Dto encryptShuffleVo) {
        encryptShuffleV2Service.submitJob(encryptShuffleVo.getSchema(), encryptShuffleVo.getDbType(),
                encryptShuffleVo.getShuffleTableNames(), encryptShuffleVo.getTableExtractDefines());
        return ResponseResult.ok("提交作业成功");
    }

    @Data
    public static class EncryptShuffleVo {
        private String schema;
        private String dbType;
        private Set<String> unSelectedTableNames;
        private Set<String> selectedTableNames;

        /**
         * 刷库中抽取源数据的方式
         */
        private boolean withIncrFieldExtractOnce;

        /**
         * 表名
         * 表对应的增量字段值
         */
        @Nullable
        private Map<String, String> tableNameAndIncrFieldPreVal;

    }

}
