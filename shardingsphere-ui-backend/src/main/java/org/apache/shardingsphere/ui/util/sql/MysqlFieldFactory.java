package org.apache.shardingsphere.ui.util.sql;

import cn.com.bluemoon.metadata.inter.dto.out.ColumnInfoVO;
import cn.hutool.core.util.StrUtil;
import com.alibaba.excel.util.StringUtils;
import org.apache.shardingsphere.encrypt.spi.EncryptAlgorithm;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmConfiguration;
import org.apache.shardingsphere.infra.config.algorithm.ShardingSphereAlgorithmFactory;
import org.apache.shardingsphere.spi.ShardingSphereServiceLoader;
import org.apache.shardingsphere.ui.common.dto.FiledEncryptionInfo;

import java.util.*;
import java.util.stream.Collectors;

public class MysqlFieldFactory extends FieldFactory{

    private final List<String> charList = Arrays.asList("char", "varchar", "varbinary", "binary","text","tinytext","mediumtext","longtext");
    private final List<String> intList = Arrays.asList("int", "bigint", "tinyint", "smallint", "mediumint", "float", "double", "decimal");

    @Override
    public Integer getFieldLength(String algorithmType, Properties props, String length) {

        EncryptAlgorithm algorithm = ShardingSphereAlgorithmFactory.createAlgorithm
                (new ShardingSphereAlgorithmConfiguration(algorithmType, props), EncryptAlgorithm.class);
        String join = StrUtil.repeat("中", Integer.parseInt(length));
        return algorithm.encrypt(join).length();
    }

    @Override
    public String createFieldSql(FiledEncryptionInfo info) {

        String fieldSql = null;
        ColumnInfoVO vo = info.getColumnInfoVO();
        // 获取密文字段的长度
        if(charList.contains(vo.getSqlSimpleType())){
            // 有长度的字符串
            if(StringUtils.isNotBlank(vo.getLength())){
                Integer fieldLength = getFieldLength(info.algorithmType, info.props, vo.getLength());
                // 字段脚本
                fieldSql = String.format("alter table %s add %s_cipher %s(%s) comment'%s';", vo.getTableName(), vo.getName(), vo.getSqlSimpleType(), fieldLength, vo.getComment());
            // 无长度字符串
            }else {
                fieldSql = String.format("alter table %s add %s_cipher %s comment'%s';", vo.getTableName(), vo.getName(), vo.getSqlSimpleType(), vo.getComment());
            }
        }else if(intList.contains(vo.getSqlSimpleType())){
            fieldSql = String.format("alter table %s add %s_cipher %s(%s) comment'%s';", vo.getTableName(), vo.getName(), "varchar", "512", vo.getComment());
        }else{
            String testFormat = "alter table %s add %s_cipher %s comment'%s';";
            fieldSql = String.format(testFormat, vo.getTableName(), vo.getName(), "text", vo.getComment());
        }
        return fieldSql;
    }
}
