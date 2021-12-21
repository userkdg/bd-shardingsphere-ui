package org.apache.shardingsphere.ui.common.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class SensitiveInformation {

    public final static List<String> ALGORITHM_LIST = Arrays.asList("AES","MD5","RC4");

    @ExcelProperty("数据库名称")
    private String databaseName;

    @ExcelProperty("模式名称")
    private String schemaName;

    @ExcelProperty("数据库表名")
    private String tableName;

    @ExcelProperty("数据库列名")
    private String fieldName;

    @ExcelProperty("数据库字段类型")
    private String datatype;

    @ExcelProperty("表增量字段(默认为否)")
    private String tableIncrField;

    @ExcelProperty("算法类型(默认AES)")
    private String algorithmType;

    @ExcelProperty("密钥Key(默认随机生成)")
    private String cipherKey;

}
