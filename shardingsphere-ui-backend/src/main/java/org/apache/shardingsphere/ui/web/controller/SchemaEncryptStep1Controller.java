package org.apache.shardingsphere.ui.web.controller;


import cn.com.bluemoon.daps.api.sys.RemoteSystemDatasourceService;
import cn.com.bluemoon.daps.common.domain.ResultBean;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.daps.system.entity.DapSystemSchema;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.util.IOUtils;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.*;
import org.apache.shardingsphere.ui.util.ImportEncryptionRuleUtils;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 业务系统上报入口-步骤1
 * 1.提供上报模板
 * 2.提供上报入口：解析->转换->入库（db、zk）
 * 3.获取schema列表
 */
@RestController
@RequestMapping("/api/config-center")
@Slf4j
public class SchemaEncryptStep1Controller {

    @Autowired
    ConfigCenterService configCenterService;
    @Autowired
    ShardingSchemaService shardingSchemaService;
    @Autowired
    private RemoteSystemDatasourceService remoteSystemDatasourceService;
    @Resource
    private ExcelShardingSchemaService excelShardingSchemaService;
    @Autowired
    DsSySensitiveInfoService dsSySensitiveInfoService;
    @Autowired
    CreateCipherService createCipherService;

    /**
     * 模板下载
     *
     * @throws IOException
     */
    @GetMapping(value = "download")
    public void downloadRuleMould(HttpServletResponse response) throws IOException {

        InputStream inputStream = this.getClass().getResourceAsStream("/template/系统敏感信息采集表模板.xlsx");
        byte[] buffer = IOUtils.toByteArray(inputStream);
        response.setContentType("application/force-download;" + "charset = UTF-8");
        String name = "系统敏感信息采集表模板";
        name = new String(name.getBytes(), "ISO-8859-1");
        response.setHeader("Content-Disposition", "attachment; filename=" + name + ".xlsx");
        OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
        toClient.write(buffer);
        toClient.flush();
        toClient.close();
    }

    /**
     * 模板导入
     */
    @PostMapping(value = "import")
    public ResponseResult<String> importRule(@RequestBody MultipartFile file, @RequestParam("id") String schemaId) {

        // 获取schema下的环境列表
        ResultBean<Map<String, List<DapSystemDatasourceEnvironment>>> resultBean = remoteSystemDatasourceService.getSchemaDatasourceList(schemaId);
        if (resultBean.getCode() == 500 || resultBean.getContent() == null) {
            return ResponseResult.error("获取schema失败");
        }
        // 判断schema是否存在 以及数据源连接信息
        Map<String, List<DapSystemDatasourceEnvironment>> map = resultBean.getContent();
        ResponseResult<Boolean> result = excelShardingSchemaService.CheckShardingSchemaRule(map);
        if (result.isSuccess()) {
            // 封装数据源
            String schemaName = map.keySet().stream().findFirst().get();
            Map<String, DataSourceConfiguration> maps = ConnectionProxyUtils.transToDatasourceString(map.get(schemaName));
            List<SensitiveInformation> list = EasyExcel.read(ImportEncryptionRuleUtils.transferToFile(file)).head(SensitiveInformation.class).sheet().doReadSync();
            Map<QueryMetaDataRequest, List<TableInfoVO>> voMap = new HashMap<>();
            String names = list.stream().map(SensitiveInformation::getTableName).collect(Collectors.joining(","));
            createCipherService.getMetaInfo(maps.values().stream().findFirst().get(), names, voMap);
            // 解析excel规则数据
            if(voMap.isEmpty()){
                return ResponseResult.error("数据库不存在excel表信息");
            }
            ResponseResult<List<SensitiveInformation>>  data = ImportEncryptionRuleUtils.getData(file,voMap.values().stream().findFirst().get());
            if (data.isSuccess()) {
                // 规则入库
                excelShardingSchemaService.ruleImport(schemaName, data.getModel(), maps);
                // 数据入库
                dsSySensitiveInfoService.insertRuleConfig(data.getModel(),schemaName);
                return ResponseResult.ok("导入成功!");
            }
            return ResponseResult.error(data.getErrorMsg());
        }
        return ResponseResult.error(result.getErrorMsg());
    }

    /**
     * schema列表
     */
        @GetMapping("schema/List")
        public ResponseResult<List<DapSystemSchema>> getSchemaList() {

            ResultBean<List<DapSystemSchema>> schemaList = remoteSystemDatasourceService.getSchemaList();
            if (schemaList.getCode() == 200 && schemaList.getContent() != null) {
                return ResponseResult.ok(schemaList.getContent());
            }
            return ResponseResult.error(schemaList.getMsg());
        }
}
