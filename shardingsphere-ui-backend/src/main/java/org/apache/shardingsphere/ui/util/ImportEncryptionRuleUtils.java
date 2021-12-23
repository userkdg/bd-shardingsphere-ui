package org.apache.shardingsphere.ui.util;


import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import com.alibaba.excel.EasyExcel;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.util.excel.ExcelHeadDataListener;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
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

    public static ResponseResult<List<SensitiveInformation>> getData(MultipartFile file, List<TableInfoVO> voList){

        // 文件格式校验
        if(file == null || !isExcel(file.getOriginalFilename())){
            return ResponseResult.error("文件为空或格式不正确");
        }
        ExcelHeadDataListener excelHeadDataListener = new ExcelHeadDataListener();
        EasyExcel.read(transferToFile(file), SensitiveInformation.class, excelHeadDataListener).sheet().doRead();
        if(!excelHeadDataListener.errorList.isEmpty()){
            return ResponseResult.error(excelHeadDataListener.errorList.toString());
        }
        List<SensitiveInformation> cachedDataList = excelHeadDataListener.cachedDataList;
        List<String> collect = voList.stream().map(TableInfoVO::getName).collect(Collectors.toList());
        List<String> unExistTable = cachedDataList.stream().filter(c -> !collect.contains(c.getTableName()))
                .map(SensitiveInformation::getTableName).collect(Collectors.toList());
        if(unExistTable.isEmpty()){
            // 字段校验
            Map<String, List<SensitiveInformation>> map = cachedDataList.stream().collect(Collectors.groupingBy(SensitiveInformation::getTableName));
            voList.forEach( v -> {
                if(map.containsKey(v.getName())){
                    List<String> voField = v.getColumns().stream().map(ColumnInfoVO::getName).collect(Collectors.toList());
                    String unExistField = map.get(v.getName()).stream().filter(m -> !voField.contains(m.getFieldName()))
                            .map(SensitiveInformation::getFieldName).collect(Collectors.joining(","));
                    if(!unExistField.isEmpty()){
                        String error = String.format("表%s字段%s不存在", v.getName(), unExistField);
                        log.info(error);
                        excelHeadDataListener.errorList.add(error);
                    }
                }
            });
            return excelHeadDataListener.errorList.isEmpty() ? ResponseResult.ok(excelHeadDataListener.cachedDataList)
                    : ResponseResult.error(excelHeadDataListener.errorList.toString());
        }else {
            String format = String.format("表%不存在", unExistTable);
            log.info(format);
            return ResponseResult.error(format);
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
                properties.setProperty("aes-key-value", information.getCipherKey());
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

    public static InputStream transferToFile(MultipartFile multipartFile) {

        InputStream file = null;
        try {
             file = multipartFile.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, e.getMessage());
        }
        return file;
    }
}
