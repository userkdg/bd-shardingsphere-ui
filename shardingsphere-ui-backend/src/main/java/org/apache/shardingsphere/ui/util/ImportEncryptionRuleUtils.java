package org.apache.shardingsphere.ui.util;


import cn.hutool.core.util.RandomUtil;
import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.JsonArray;
import groovy.util.Factory;
import org.apache.log4j.Logger;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.servcie.impl.ShardingSchemaServiceImpl;
import org.apache.shardingsphere.ui.util.excel.ExcelHeadDataListener;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

public class ImportEncryptionRuleUtils {

    public static final List<String> EXCEL_SUFFER = Arrays.asList("xls","xlsx","xlsm");

    /**
     * 是否为excel文档格式
     * @return
     */
    public static Boolean isExcel(String name){

        String[] split = name.split(".");
        return EXCEL_SUFFER.contains(split[split.length-1]);
    }

    public static ResponseResult<List<SensitiveInformation>> getData(MultipartFile file){

        String name = file.getName();
        // 文件格式校验
        if(file == null || !isExcel(file.getName())){
            return ResponseResult.error("文件为空或格式不正确");
        }
        ExcelHeadDataListener excelHeadDataListener = new ExcelHeadDataListener();
        EasyExcel.read(name, SensitiveInformation.class, excelHeadDataListener).sheet().doRead();
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
                properties.setProperty("aes-key-value",   "");
                ShardingSphereAlgorithmConfiguration shardingSphereAlgorithmConfiguration = new ShardingSphereAlgorithmConfiguration(information.getAlgorithmType(), properties);
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

    public static List<RuleConfiguration> transToDatasourceMap(List<SensitiveInformation> list){


        return null;
    }
}
