package org.apache.shardingsphere.ui.util.check;

import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;
import org.apache.shardingsphere.ui.web.response.ResponseResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public abstract class AbsScreenTableFactory {

    public static AbsScreenTableFactory screenMysqlFieldFactory(){

        return new ScreenMysqlFieldFactory();
    }

    /**
     * 筛选出相同字段
     * @param map
     * @param tables
     * @param isCipher
     * @return
     */
    public List<ColumnInfoVO> screenSameField(Map<QueryMetaDataRequest, List<TableInfoVO>> map,
                                                  List<EncryptTableRuleConfiguration> tables, Boolean isCipher){

        List<TableInfoVO> voList = map.values().stream().findFirst().get();
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
            cipherFiled.addAll(isCipher ? screenCipherField(encrypt.getValue(), tableMap, encrypt.getKey()) :
                    screenPlainField(encrypt.getValue(), tableMap, encrypt.getKey()));
        }
        return cipherFiled;

    }

    public abstract List<ColumnInfoVO> screenCipherField(Collection<EncryptColumnRuleConfiguration> collection,
                                                         Map<String, List<ColumnInfoVO>> tableMap, String tableName);

    public abstract List<ColumnInfoVO> screenPlainField(Collection<EncryptColumnRuleConfiguration> collection, Map<String, List<ColumnInfoVO>> tableMap, String tableName);


    public abstract List<FiledEncryptionInfo> getCipherInfo(List<ColumnInfoVO> list, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors);


}
