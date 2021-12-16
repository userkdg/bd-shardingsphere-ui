package org.apache.shardingsphere.ui.util;


import cn.com.bluemoon.daps.system.entity.DapSystemDatasourceEnvironment;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.crypto.symmetric.AES;
import com.alibaba.excel.EasyExcel;
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
import org.apache.shardingsphere.ui.web.response.ResponseResult;
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

    public static Map<QueryMetaDataRequest, List<FiledEncryptionInfo>> getCipherInfo( List<ColumnInfoVO> list, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors,
                                                          List<QueryMetaDataRequest> requests){
        // 获取键值对
        Map<String, ColumnInfoVO> tableMap = list.stream().collect(Collectors.toMap(c -> String.format("%s_%s", c.getTableName(), c.getName()), ColumnInfoVO -> ColumnInfoVO));
        Map<String, ShardingSphereAlgorithmConfiguration> ruleMap = encryptors.stream().collect(HashMap::new, HashMap::putAll, HashMap::putAll);
        Map<String, List<FiledEncryptionInfo>> infoMap = new HashMap<>();
        for (Map.Entry<String, ShardingSphereAlgorithmConfiguration> rule : ruleMap.entrySet()){
            List<FiledEncryptionInfo> fieldList = Lists.newArrayList();
            if(tableMap.containsKey(rule.getKey())){
                FiledEncryptionInfo filedEncryptionInfo = new FiledEncryptionInfo();
                filedEncryptionInfo.setProps(rule.getValue().getProps());
                filedEncryptionInfo.setAlgorithmType(rule.getValue().getType());
                filedEncryptionInfo.setColumnInfoVO(tableMap.get(rule.getKey()));
                fieldList.add(filedEncryptionInfo);
            }
            infoMap.put(rule.getKey(), fieldList);
        }
        //
        Map<QueryMetaDataRequest, List<FiledEncryptionInfo>> cipherMap = new HashMap<>();
        for (Map.Entry<String, List<FiledEncryptionInfo>> info : infoMap.entrySet()){
            List<QueryMetaDataRequest> collect = requests.stream().filter(r -> r.getTableNames().contains(info.getKey())).collect(Collectors.toList());
            if(!collect.isEmpty()){
                cipherMap.put(collect.stream().findFirst().get(), info.getValue());
            }
        }
        return cipherMap;
    }

    /**
     * 创建密文字段脚本
     * @param cipherMap
     * @return
     */
    public static List<String> createCipherFieldSql(Map<QueryMetaDataRequest, List<FiledEncryptionInfo>> cipherMap){


        for (Map.Entry<QueryMetaDataRequest,List<FiledEncryptionInfo>> entry: cipherMap.entrySet()) {
            QueryMetaDataRequest key = entry.getKey();
            DbTypeEnum dbType = key.getDbType();
            if(dbType.equals(DbTypeEnum.MYSQL)){
                for (FiledEncryptionInfo info : entry.getValue()) {
                    // 获取密文字段的长度
                    /*if(info.getColumnInfoVO().getSqlSimpleType())
                    getEncryptionFieldLength(info.algorithmType, info.props, info.columnInfoVO.getLength());*/
                }

            }
        }
        return null;

    }
    /**
     * 计算加密后的字段长度
     * @return
     */
    public static Integer getEncryptionFieldLength(String algorithmType, Properties props, String length){

        EncryptAlgorithm algorithm = ShardingSphereAlgorithmFactory
                .createAlgorithm(new ShardingSphereAlgorithmConfiguration(algorithmType, props),
                        EncryptAlgorithm.class);
        List<String> list = Collections.nCopies(Integer.getInteger(length), "中");
        String join = String.join("", list);
        String encrypt = algorithm.encrypt(join);
        return encrypt.length();
    }
}
