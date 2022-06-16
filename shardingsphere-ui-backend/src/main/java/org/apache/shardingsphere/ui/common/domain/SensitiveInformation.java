package org.apache.shardingsphere.ui.common.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class SensitiveInformation {

    public final static List<String> ALGORITHM_LIST = Arrays.asList("AES", "MYSQL-AES");

    @ExcelProperty("数据库名称")
    private String databaseName;

    @ExcelProperty("数据库表名")
    private String tableName;

    @ExcelProperty("数据库列名")
    private String fieldName;

    @ExcelProperty("数据库字段类型")
    private String datatype;

    @ExcelProperty("算法类型(默认AES)")
    private String algorithmType;

    @ExcelProperty("密钥Key(默认随机生成)")
    private String cipherKey;

    @ExcelProperty("说明")
    private String remark;

}
