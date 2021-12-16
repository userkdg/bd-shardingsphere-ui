package org.apache.shardingsphere.ui.servcie.impl;

import cn.com.bluemoon.metadata.common.ResultBean;
import cn.com.bluemoon.metadata.common.enums.DbTypeEnum;
import cn.com.bluemoon.metadata.inter.DbMetaDataService;
import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.SchemaInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
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
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CreateCipherServiceImpl implements CreateCipherService {

    @Autowired
    ConfigCenterService configCenterService;

    @Autowired
    DbMetaDataService dbMetaDataService;

    @Override
    public ResponseResult<String> createCipherField(String schemaName) {

        MetaDataPersistService activatedMetadataService = configCenterService.getActivatedMetadataService();
        Collection<RuleConfiguration> ruleConfiguration = activatedMetadataService.getSchemaRuleService().load(schemaName);// 规则信息
        if(ruleConfiguration.isEmpty()){
            return ResponseResult.error("规则为空!");
        }
        // 加载数据源
        Map<String, DataSourceConfiguration> load = activatedMetadataService.getDataSourceService().load(schemaName);
        // 转换成规则实体
        List<EncryptRuleConfiguration> encryptionList = ruleConfiguration.stream().map(e -> (EncryptRuleConfiguration) e).collect(Collectors.toList());
        // 获取加密规则
        List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors = encryptionList.stream().map(EncryptRuleConfiguration::getEncryptors).collect(Collectors.toList());
        List<Collection<EncryptTableRuleConfiguration>> configList = encryptionList.stream().map(EncryptRuleConfiguration::getTables).collect(Collectors.toList());
        List<EncryptTableRuleConfiguration> tables = configList.stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
        String names = tables.stream().map(e -> e.getName()).collect(Collectors.joining(","));
        // 获取数据源名称
        Collection<DataSourceConfiguration> values = load.values();
        Map<QueryMetaDataRequest, List<TableInfoVO>> map = new HashMap<>();
        for (DataSourceConfiguration config : values) {
            getMetaInfo(config, names, map);
        }
        // 筛选表存在的密文字段
        if(!map.isEmpty()){
            List<TableInfoVO> voList = map.values().stream().collect(ArrayList::new, ArrayList::addAll, ArrayList::addAll);
            // 获取表名
            List<String> getNames = voList.stream().map(TableInfoVO::getName).collect(Collectors.toList());
            // 存在的表规则
            List<EncryptTableRuleConfiguration> ruleTable = tables.stream().filter(e -> getNames.contains(e.getName())).collect(Collectors.toList());
            // 已经配置的字段信息
            Map<String, Collection<EncryptColumnRuleConfiguration>> collect = ruleTable.stream().collect(Collectors.toMap(EncryptTableRuleConfiguration::getName, EncryptTableRuleConfiguration::getColumns));
            // 获取配置字段信息的源字段
            Map<String, List<ColumnInfoVO>> tableMap = voList.stream().collect(Collectors.toMap(TableInfoVO::getName, TableInfoVO::getColumns));
            List<ColumnInfoVO> cipherFiled = Lists.newArrayList();
            for (Map.Entry<String, Collection<EncryptColumnRuleConfiguration>> encrypt : collect.entrySet()) {
                if(tableMap.containsKey(encrypt.getKey())){
                    // 明文列字段
                    List<String> plain = encrypt.getValue().stream().map(e -> e.getPlainColumn()).collect(Collectors.toList());
                    // 密文列字段
                    List<String> cipher = encrypt.getValue().stream().map(e -> e.getCipherColumn()).collect(Collectors.toList());
                    List<ColumnInfoVO> columnInfoVOS = tableMap.get(encrypt.getKey());
                    // 筛选两者共有的字段数据
                    List<ColumnInfoVO> samePlainField = columnInfoVOS.stream().filter(c -> plain.contains(c.getName())).collect(Collectors.toList());
                    // 筛选出已经创建的密文字段
                    List<String> sameCipherField = columnInfoVOS.stream()
                            .filter(c -> cipher.contains(c.getName())).map(ColumnInfoVO::getName)
                            .collect(Collectors.toList());
                    List<ColumnInfoVO> sameField = samePlainField.stream().filter(s -> !sameCipherField.contains(s.getName() + "_cipher")).collect(Collectors.toList());
                    cipherFiled.addAll(samePlainField);
                    /*if(!sameField.isEmpty()){
                        cipherFiled.addAll(sameField);
                    }*/
                }
            }
            List<QueryMetaDataRequest> requests = map.keySet().stream().collect(Collectors.toList());
            // 字段处理
            Map<QueryMetaDataRequest, List<FiledEncryptionInfo>> cipherInfo = ImportEncryptionRuleUtils.getCipherInfo(cipherFiled, encryptors, requests);
            // 创建sql
            Map<QueryMetaDataRequest, String> cipherFieldSql = ImportEncryptionRuleUtils.createCipherFieldSql(cipherInfo);
        }
        return ResponseResult.error(String.format("数据库不存在%s表，无法创建密文字段", names));
    }

    /**
     * 获取数据库表的元信息
     * @param dataSourceConfiguration
     * @return
     */
    private void getMetaInfo(DataSourceConfiguration dataSourceConfiguration, String names, Map<QueryMetaDataRequest, List<TableInfoVO>> map){

        QueryMetaDataRequest queryMetaDataRequest = new QueryMetaDataRequest();
        queryMetaDataRequest.setTableNames(names);
        Map<String, Object> allProps = dataSourceConfiguration.getAllProps();
        String url = (String) allProps.get("jdbcUrl");
        getRequest(url, queryMetaDataRequest);
        queryMetaDataRequest.setUsername((String)allProps.get("username"));
        queryMetaDataRequest.setPassword((String)allProps.get("password"));
        ResultBean<SchemaInfoVO> bean = dbMetaDataService.queryMetaData(queryMetaDataRequest);
        if(bean.getContent() != null && !bean.getContent().getTables().isEmpty()){
            String nameList = bean.getContent().getTables().stream().map(TableInfoVO::getName).collect(Collectors.joining(","));
            queryMetaDataRequest.setTableNames(nameList);
            map.put(queryMetaDataRequest, bean.getContent().getTables());
        }
    }

    private void getRequest(String url,QueryMetaDataRequest queryMetaDataRequest){

        String[] split = url.split("\\?");
        String simpleUrl = split[0];
        String[] simpleUrlSplit = simpleUrl.split("://");
        String jdbc = simpleUrlSplit[0];
        String[] jdbcSplit = jdbc.split(":");
        String dbType = jdbcSplit[jdbcSplit.length - 1];
        String urlPortDbname = simpleUrlSplit[simpleUrlSplit.length - 1];
        String[] urlPortDbnameSplit = urlPortDbname.split("/");
        String dbname = urlPortDbnameSplit[urlPortDbnameSplit.length - 1];
        String[] urlAndPort = urlPortDbnameSplit[0].split(":");
        String ip = urlAndPort[0];
        String port = urlAndPort[urlAndPort.length-1];
        queryMetaDataRequest.setIp(ip);
        queryMetaDataRequest.setPort(port);
        queryMetaDataRequest.setDbName(dbname);
        queryMetaDataRequest.setSchemaName(dbname);
        queryMetaDataRequest.setDbType(dbType.equals("mysql") ? DbTypeEnum.MYSQL : DbTypeEnum.PGSQL);
    }
}
