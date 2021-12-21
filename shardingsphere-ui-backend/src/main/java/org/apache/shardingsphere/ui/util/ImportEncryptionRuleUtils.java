package org.apache.shardingsphere.ui.util;


import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.annotation.DbType;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmFactory;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.common.domain.SensitiveInformation;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;
import org.apache.shardingsphere.ui.util.excel.ExcelHeadDataListener;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;
import org.apache.shardingsphere.ui.util.sql.FieldFactory;
import org.apache.shardingsphere.ui.util.sql.MysqlFieldFactory;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
