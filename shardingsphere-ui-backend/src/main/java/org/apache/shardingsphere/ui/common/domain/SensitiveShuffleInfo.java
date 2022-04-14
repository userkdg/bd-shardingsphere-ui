package org.apache.shardingsphere.ui.common.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class SensitiveShuffleInfo {

    @ExcelProperty("数据库名称")
    private String databaseName;

    @ExcelProperty("数据库表名")
    private String tableName;

    @ExcelProperty("增量字段名")
    private String incrFieldName;

    @ExcelProperty("表on_update_timestamp字段名")
    private String onUpdateTimestampFields;

}
