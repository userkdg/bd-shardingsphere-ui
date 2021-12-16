package org.apache.shardingsphere.ui.web.controller;


import cn.com.bluemoon.daps.api.sys.RemoteSystemDatasourceService;
import cn.com.bluemoon.daps.common.domain.ResultBean;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.daps.system.entity.DapSystemSchema;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.google.gson.JsonArray;
import org.apache.poi.util.IOUtils;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.ui.common.domain.DatasourceInfo;
import org.apache.shardingsphere.ui.common.domain.FormWorkConstant;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ExcelShardingSchemaService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.util.ImportEncryptionRuleUtils;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.apache.shardingsphere.ui.web.response.ResponseResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/config-center")
public class ImportEncryptionRuleController {

    @Autowired
    private RemoteSystemDatasourceService remoteSystemDatasourceService;

    @Resource
    private ExcelShardingSchemaService excelShardingSchemaService;

    @Autowired
    ConfigCenterService configCenterService;

    @Autowired
    ShardingSchemaService shardingSchemaService;

    /**
     * 模板下载
     * @param response
     * @throws IOException
     */
    @GetMapping(value = "download")
    public void downloadRuleMould(HttpServletResponse response) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        String fileName = URLEncoder.encode("系统敏感信息采集表模板", "UTF-8").replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream()).sheet("系统敏感信息采集表").doWrite(FormWorkConstant.getData());
    }

    /**
     * 模板导入
     * @param file
     * @return
     */
    @PostMapping(value = "import")
    public ResponseResult<String> importRule(@RequestBody MultipartFile file, @RequestParam("id") String schemaId ) {

        // 获取schema下的环境列表
        ResultBean<Map<String, List<DapSystemDatasourceEnvironment>>> resultBean = remoteSystemDatasourceService.getSchemaDatasourceList(schemaId);
        if(resultBean.getCode() == 500 || resultBean.getContent() == null){
            return ResponseResult.error("获取schema失败");
        }
        // 解析excel规则数据
        ResponseResult<List<SensitiveInformation>> data = ImportEncryptionRuleUtils.getData(file);
        if(!data.isSuccess()){
            return ResponseResult.error(data.getErrorMsg());
        }
        Map<String, List<DapSystemDatasourceEnvironment>> map = resultBean.getContent();
        ResponseResult<Boolean> result = excelShardingSchemaService.CheckShardingSchemaRule(map);
        if (result.isSuccess()){
            String schemaName = map.keySet().stream().findFirst().get();
            // 封装数据源
            Map<String, DataSourceConfiguration> maps = ImportEncryptionRuleUtils.transToDatasourceString(map.get(schemaName));
            // 封装规则结果集
            List<RuleConfiguration> ruleConfigurations = ImportEncryptionRuleUtils.transToRuleConfiguration(data.getModel());
            // 规则写入配置文件
            configCenterService.getActivatedMetadataService().getSchemaRuleService().persist(schemaName, ruleConfigurations, true);
            configCenterService.getActivatedMetadataService().getDataSourceService().persist(schemaName, maps);
            configCenterService.getActivatedMetadataService().getSchemaMetaDataService().persist(schemaName, new ShardingSphereSchema());
            return ResponseResult.ok("导入成功!");
        }
        // 获取schema列表接口
        return ResponseResult.error(result.getErrorMsg());
    }

    /**
     * schema列表
     * @return
     */
    @GetMapping("schema/List")
    public ResponseResult<List<DapSystemSchema>> getSchemaList(){

        ResultBean<List<DapSystemSchema>> schemaList = remoteSystemDatasourceService.getSchemaList();
        if(schemaList.getCode() == 200 && schemaList.getContent() != null){
            return ResponseResult.ok(schemaList.getContent());
        }
        return ResponseResult.error(schemaList.getMsg());
    }
}
