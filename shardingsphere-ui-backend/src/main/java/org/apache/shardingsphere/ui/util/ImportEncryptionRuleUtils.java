package org.apache.shardingsphere.ui.util;


import cn.com.bluemoon.daps.common.enums.DatabaseType;
import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import groovy.util.Factory;
import org.apache.commons.compress.utils.Lists;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.servcie.impl.ShardingSchemaServiceImpl;
import org.apache.shardingsphere.ui.util.excel.ExcelHeadDataListener;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class ImportEncryptionRuleUtils {

    public static final List<String> EXCEL_SUFFER = Arrays.asList("xls","xlsx","xlsm");

    /**
     * 是否为excel文档格式
     * @return
     */
    public static Boolean isExcel(String name){

        String[] split = name.split("\\.");
        return EXCEL_SUFFER.contains(split[split.length-1]);
    }

    public static ResponseResult<List<SensitiveInformation>> getData(MultipartFile file){

        // 文件格式校验
        if(file == null || !isExcel(file.getOriginalFilename())){
            return ResponseResult.error("文件为空或格式不正确");
        }
        ExcelHeadDataListener excelHeadDataListener = new ExcelHeadDataListener();
        EasyExcel.read(transferToFile(file), SensitiveInformation.class, excelHeadDataListener).sheet().doRead();
        if(!excelHeadDataListener.errorList.isEmpty()){
            return ResponseResult.error(excelHeadDataListener.errorList.toString());
        }else {
            return ResponseResult.ok(excelHeadDataListener.cachedDataList);
        }
    }

    public static List<RuleConfiguration> transToRuleConfiguration(List<SensitiveInformation> list){

        Map<String, List<SensitiveInformation>> collect = list.stream().collect(Collectors.groupingBy(SensitiveInformation::getTableName));
        List<EncryptTableRuleConfiguration> tableRuleConfigurations = new ArrayList<>();
        Map<String, ShardingSphereAlgorithmConfiguration> map = new HashMap<>();
        for (Map.Entry<String, List<SensitiveInformation>> entry : collect.entrySet()) {
            List<SensitiveInformation> value = entry.getValue();
            List<EncryptColumnRuleConfiguration> configurations = new ArrayList<>();
            for (SensitiveInformation information : value){
                Properties properties = new Properties();
                properties.setProperty("aes-key-value",   "wlf1d5mmal2xsttr");
                String algorithmType = SensitiveInformation.ALGORITHM_LIST.contains(information.getAlgorithmType()) ? information.getAlgorithmType() : "AES";
                ShardingSphereAlgorithmConfiguration shardingSphereAlgorithmConfiguration = new ShardingSphereAlgorithmConfiguration(algorithmType, properties);
                EncryptColumnRuleConfiguration encrypt = new EncryptColumnRuleConfiguration
                        (information.getFieldName(),information.getFieldName()+"_cipher",
                                null, information.getFieldName(), entry.getKey()+"_"+information.getFieldName());
                configurations.add(encrypt);
                map.put(entry.getKey()+"_"+information.getFieldName(), shardingSphereAlgorithmConfiguration);
            }
            EncryptTableRuleConfiguration encryptTableRuleConfiguration = new EncryptTableRuleConfiguration(entry.getKey(), configurations, true);
            tableRuleConfigurations.add(encryptTableRuleConfiguration);
        };
        EncryptRuleConfiguration encryptRuleConfiguration = new EncryptRuleConfiguration(tableRuleConfigurations, map, true);
        List<RuleConfiguration> encryptRuleConfigurations = Arrays.asList(encryptRuleConfiguration);
        return encryptRuleConfigurations;
    }

    public static Map<String, DataSourceConfiguration> transToDatasourceString(List<DapSystemDatasourceEnvironment> list){

        // 生成
        Map<String, DataSourceConfiguration> dataSourceConfigurations = new HashMap<>();
        for (DapSystemDatasourceEnvironment environment:list) {
            DataSourceConfiguration dataSourceConfiguration = new DataSourceConfiguration("com.zaxxer.hikari.HikariDataSource");
            Properties customPoolProps = dataSourceConfiguration.getCustomPoolProps();
            String url = ConnectionProxyUtils.getUrl(environment);
            customPoolProps.setProperty("connectionTimeoutMilliseconds", "3000");
            customPoolProps.setProperty("idleTimeoutMilliseconds", "60000");
            customPoolProps.setProperty("maxPoolSize", "50");
            customPoolProps.setProperty("minPoolSize", "1");
            customPoolProps.setProperty("maxLifetimeMilliseconds", "1800000");
            Map<String, Object> props = dataSourceConfiguration.getProps();
            props.put("customPoolProps", customPoolProps);
            props.put("readOnly", false);
            props.put("maxLifetime", 1800000);
            props.put("minimumIdle", 1);
            props.put("password", environment.getPassword());
            props.put("minPoolSize", 1);
            props.put("idleTimeout", 60000);
            props.put("jdbcUrl",url);
            props.put("dataSourceClassName", dataSourceConfiguration.getDataSourceClassName());
            props.put("maximumPoolSize", 50);
            props.put("connectionTimeout", 30000);
            props.put("maxPoolSize", 50);
            props.put("username", environment.getUsername());
            dataSourceConfigurations.put(environment.getDatabaseName()+RandomUtil.randomString(3), dataSourceConfiguration);
        }
        return dataSourceConfigurations;
    }


    private static File transferToFile(MultipartFile multipartFile) {

        File file = null;
        try {
            String originalFilename = multipartFile.getOriginalFilename();
            String[] filename = originalFilename.split("\\.");
            file=File.createTempFile(filename[0], filename[1]);
            multipartFile.transferTo(file);
            file.deleteOnExit();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }
}
