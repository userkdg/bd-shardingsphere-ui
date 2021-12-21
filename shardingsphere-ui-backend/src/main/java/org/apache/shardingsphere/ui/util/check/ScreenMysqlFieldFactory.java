package org.apache.shardingsphere.ui.util.check;

import cn.com.bluemoon.metadata.inter.dto.in.QueryMetaDataRequest;
import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.com.bluemoon.metadata.inter.dto.out.TableInfoVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.Lists;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptColumnRuleConfiguration;
import org.apache.shardingsphere.encrypt.api.config.rule.EncryptTableRuleConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;
import org.apache.shardingsphere.ui.util.jdbc.ConnectionProxyUtils;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class ScreenMysqlFieldFactory extends AbsScreenTableFactory{


    /**
     * 筛选密文字段
     * @param collection
     * @param tableMap
     * @param tableName
     * @return
     */
    @Override
    public List<ColumnInfoVO> screenCipherField(Collection<EncryptColumnRuleConfiguration> collection,
                                                Map<String, List<ColumnInfoVO>> tableMap, String tableName) {

        // 明文列字段
        List<String> plain = collection.stream().map(e -> e.getPlainColumn()).collect(Collectors.toList());
        // 密文列字段
        List<String> cipher = collection.stream().map(e -> e.getCipherColumn()).collect(Collectors.toList());

        List<ColumnInfoVO> columnInfoVOS = tableMap.get(tableName);
        // 筛选两者共有的字段数据
        List<ColumnInfoVO> samePlainField = columnInfoVOS.stream().filter(c -> plain.contains(c.getName())).collect(Collectors.toList());
        // 筛选出已经创建的密文字段
        List<String> sameCipherField = columnInfoVOS.stream()
                .filter(c -> cipher.contains(c.getName())).map(ColumnInfoVO::getName)
                .collect(Collectors.toList());
        log.info("表{}密文字段已经创建{}", tableName, sameCipherField);
        List<ColumnInfoVO> sameField = samePlainField.stream().filter(s -> !sameCipherField.contains(s.getName() + "_cipher")).collect(Collectors.toList());
        return sameField;
    }

    @Override
    public List<ColumnInfoVO> screenPlainField(Collection<EncryptColumnRuleConfiguration> collection, Map<String, List<ColumnInfoVO>> tableMap, String tableName) {

        // 明文列字段
        List<String> plain = collection.stream().map(e -> e.getPlainColumn()).collect(Collectors.toList());
        List<ColumnInfoVO> columnInfoVOS = tableMap.get(tableName);
        // 筛选两者共有的字段数据
        List<ColumnInfoVO> samePlainField = columnInfoVOS.stream().filter(c -> plain.contains(c.getName())).collect(Collectors.toList());
       return samePlainField;
    }

    @Override
    public List<FiledEncryptionInfo> getCipherInfo(List<ColumnInfoVO> list, List<Map<String, ShardingSphereAlgorithmConfiguration>> encryptors) {

        Map<String, ColumnInfoVO> tableMap = list.stream().collect(Collectors.toMap(c -> String.format("%s_%s", c.getTableName(), c.getName()), ColumnInfoVO -> ColumnInfoVO));
        Map<String, ShardingSphereAlgorithmConfiguration> ruleMap = encryptors.stream().collect(HashMap::new, HashMap::putAll, HashMap::putAll);
        List<FiledEncryptionInfo> fieldList = Lists.newArrayList();
        for (Map.Entry<String, ShardingSphereAlgorithmConfiguration> rule : ruleMap.entrySet()){
            if(tableMap.containsKey(rule.getKey())){
                FiledEncryptionInfo filedEncryptionInfo = new FiledEncryptionInfo();
                filedEncryptionInfo.setProps(rule.getValue().getProps());
                filedEncryptionInfo.setAlgorithmType(rule.getValue().getType());
                filedEncryptionInfo.setColumnInfoVO(tableMap.get(rule.getKey()));
                fieldList.add(filedEncryptionInfo);
            }
        }
        return fieldList;
    }
}
