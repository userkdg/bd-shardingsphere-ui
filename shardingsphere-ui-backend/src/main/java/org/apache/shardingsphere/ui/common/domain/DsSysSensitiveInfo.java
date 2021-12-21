package org.apache.shardingsphere.ui.common.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DsSysSensitiveInfo {

    private Integer id;

    private String dbName;

    private String schema;

    private String schemaId;

    private String tableName;

    private String fieldName;

    private String datatype;

    private Integer tableIncrField;

    private String algorithmType;

    private String cipherKey;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime updateTime;
}
