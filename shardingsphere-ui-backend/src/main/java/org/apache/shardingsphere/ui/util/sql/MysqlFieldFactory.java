package org.apache.shardingsphere.ui.util.sql;

import com.alibaba.excel.util.StringUtils;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmFactory;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlFieldFactory extends FieldFactory{

    private final List<String> charList = Arrays.asList("char", "varchar", "varbinary", "binary");
    private final List<String> intList = Arrays.asList("int", "bigint", "tinyint", "s");

    @Override
    public Integer getFieldLength(String algorithmType, Properties props, String length) {

        EncryptAlgorithm algorithm = ShardingSphereAlgorithmFactory.createAlgorithm
                (new ShardingSphereAlgorithmConfiguration(algorithmType, props), EncryptAlgorithm.class);
        List<String> list = Collections.nCopies(Integer.getInteger(length), "中");
        String join = String.join("", list);
        return algorithm.encrypt(join).length();
    }

    @Override
    public String createFieldSql(FiledEncryptionInfo info) {

        // 获取密文字段的长度
        if(charList.contains(info.columnInfoVO.getSqlSimpleType()) && StringUtils.isNotBlank(info.columnInfoVO.getLength())){
            Integer fieldLength = getFieldLength(info.algorithmType, info.props, info.columnInfoVO.getLength());
        }else{

        }

        return null;
    }
}
