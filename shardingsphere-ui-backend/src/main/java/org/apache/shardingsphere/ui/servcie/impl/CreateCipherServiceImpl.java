package org.apache.shardingsphere.ui.servcie.impl;

import cn.com.bluemoon.metadata.common.ResultBean;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.DbMetaDataService;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SchemaInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import com.baomidou.mybatisplus.annotation.DbType;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.CreateCipherService;
import org.apache.shardingsphere.ui.util.ImportEncryptionRuleUtils;
import org.apache.shardingsphere.ui.util.check.AbsScreenTableFactory;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;
import org.apache.shardingsphere.ui.util.sql.FieldFactory;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class CreateCipherServiceImpl implements CreateCipherService {

    @Autowired
    ConfigCenterService configCenterService;

    @Autowired
    DbMetaDataService dbMetaDataService;

    @Value("${spark.job.env:test}")
    private String sparkJobEnv;

    @Override
    public ResponseResult<String> operateDbField(String schemaName, Boolean isCipher) {

        MetaDataPersistService activatedMetadataService = configCenterService.getActivatedMetadataService();
        Collection<RuleConfiguration> ruleConfiguration = activatedMetadataService.getSchemaRuleService().load(schemaName);// 规则信息
        if(ruleConfiguration.isEmpty()){
            return ResponseResult.error("schema不存在或规则为空!");
        }
        // 加载数据源
        Map<String, DataSourceConfiguration> load = activatedMetadataService.getDataSourceService().load(schemaName);
        // 转换成规则实体
        List<EncryptRuleConfiguration> encryptionList = ruleConfiguration.stream().map(e -> (EncryptRuleConfiguration) e).collect(Collectors.toList());
        // 获取加密规则
        List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors = encryptionList.stream().map(EncryptRuleConfiguration::getEncryptors).collect(Collectors.toList());
        List<Collection<EncryptTableRuleConfiguration>> configList = encryptionList.stream().map(EncryptRuleConfiguration::getTables).collect(Collectors.toList());
        List<EncryptTableRuleConfiguration> tables = configList.stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        // 所有表名
        String names = tables.stream().map(e -> e.getName()).collect(Collectors.joining(","));
        // 获取数据源名称
        Collection<DataSourceConfiguration> values = load.values();
        // 只需获取第一个数据源
        Optional<DataSourceConfiguration> first = values.stream().findFirst();
        Map<QueryMetaDataRequest, List<TableInfoVO>> map = new HashMap<>();
        if(first.isPresent()){
            getMetaInfo(first.get(), names, map);
        }else {
            return ResponseResult.error("数据源不存在");
        }
        // 筛选表字段
        if(!map.isEmpty()){
            AbsScreenTableFactory absScreenTableFactory = AbsScreenTableFactory.screenMysqlFieldFactory();
            List<ColumnInfoVO> fieldList = absScreenTableFactory.screenSameField(map, tables, isCipher);
            QueryMetaDataRequest request = map.keySet().stream().findFirst().get();
            FieldFactory mysqlFieldFactory = FieldFactory.mysqlFieldFactory(DbType.MYSQL);
            List<String> sqlList = null;
            // 密文字段处理
            if(isCipher && !fieldList.isEmpty()){
                List<FiledEncryptionInfo> cipherInfo = absScreenTableFactory.getCipherInfo(fieldList, encryptors);
                // 创建sql
                sqlList = cipherInfo.stream().map(l -> mysqlFieldFactory.createCipherFieldSql(l)).collect(Collectors.toList());
                generateTempFile(schemaName + "_1-1_创建密文列_", sqlList);
            }else if(!isCipher && !fieldList.isEmpty()) {
                // 明文列改为明文备份列、密文列改为明文列
                sqlList = fieldList.stream().map(f -> mysqlFieldFactory.renamePlainFieldSql(f)).collect(Collectors.toList());
                for (String sql : sqlList) {
                    log.info("gen logic Column to plain sql=>{}", sql);
                }
                generateTempFile(schemaName + "_2-1_明文列改为备份列_", sqlList);
                List<FiledEncryptionInfo> cipherInfo = absScreenTableFactory.getCipherInfo(fieldList, encryptors);
                List<String> renameCipherSqls = cipherInfo.stream().map(l -> mysqlFieldFactory.renameCipherFieldSql(l)).collect(Collectors.toList());
                for (String sql : renameCipherSqls) {
                    log.info("gen cipher  Column to logic sql=>{}", sql);
                }
                generateTempFile(schemaName + "_2-2_密文列改为明文列_", renameCipherSqls);
                sqlList.addAll(renameCipherSqls);
                // 生成备份列创建sql
                List<String> createPlainBakSqls = cipherInfo.stream().map(l -> mysqlFieldFactory.createPlainBakFieldSql(l)).collect(Collectors.toList());
                for (String sql : createPlainBakSqls) {
                    log.info("gen plain Column bak to logic sql=>{}", sql);
                }
                generateTempFile(schemaName+"_3_创建备份列_", createPlainBakSqls);
                // 生成清空备份列数据sql（考虑字段是否存在默认值）
                List<String> cleanPlainFieldDataSql = cipherInfo.stream().map(l -> mysqlFieldFactory.deletePlainFieldData(l)).collect(Collectors.toList());
                for (String sql : cleanPlainFieldDataSql) {
                    log.info("gen clean bak_plain sql=>{}", sql);
                }
                generateTempFile(schemaName + "_2-3_备份列数据清空_", cleanPlainFieldDataSql);
            }else {
                return ResponseResult.error("字段已创建或不存在");
            }
//            if ("prod".equals(sparkJobEnv)){
//                log.error("生产环境程序不执行SQL，提供SQL语句给DBA负责执行");
//                return null;
//            }
            log.error("测试或生产环境，程序不执行SQL，提供SQL语句给DBA负责执行");
            return ResponseResult.ok("执行成功");
//            ResponseResult<String> result = ConnectionProxyUtils.connectionDatabase(request, sqlList);
//            return result.isSuccess() ? ResponseResult.ok("执行成功") : ResponseResult.error("执行失败");
        }
        return ResponseResult.error(String.format("数据库不存在%s表，无法创建密文字段", names));
    }

    @SneakyThrows
    private void generateTempFile(String fileNamePrefix, List<String> sqlList) {
        String userDir = System.getProperty("user.dir");
        Path sqlPath = Files.createDirectories(Paths.get(userDir, "/kms-sql"));
        File file = new File(sqlPath.toAbsolutePath() + "/" + fileNamePrefix + ".sql");
        Files.deleteIfExists(file.toPath());
        Path tempFile = Files.createFile(file.toPath());
        Files.write(tempFile, sqlList);
        log.info("生成SQL文件：{}", tempFile);
    }

    /**
     * 获取数据库表的元信息
     * @param dataSourceConfiguration
     * @return
     */
    @Override
    public void getMetaInfo(DataSourceConfiguration dataSourceConfiguration, String names, Map<QueryMetaDataRequest, List<TableInfoVO>> map){

        QueryMetaDataRequest queryMetaDataRequest = new QueryMetaDataRequest();
        queryMetaDataRequest.setTableNames(names);
        Map<String, Object> allProps = dataSourceConfiguration.getAllProps();
        String url = (String) allProps.get("jdbcUrl");
        ConnectionProxyUtils.getRequest(url, queryMetaDataRequest);
        queryMetaDataRequest.setUsername((String)allProps.get("username"));
        queryMetaDataRequest.setPassword((String)allProps.get("password"));
        ResultBean<SchemaInfoVO> bean = dbMetaDataService.queryMetaData(queryMetaDataRequest);
        if(bean.getContent() != null && !bean.getContent().getTables().isEmpty()){
            String nameList = bean.getContent().getTables().stream().map(TableInfoVO::getName).collect(Collectors.joining(","));
            queryMetaDataRequest.setTableNames(nameList);
            map.put(queryMetaDataRequest, bean.getContent().getTables());
        }
    }
}
