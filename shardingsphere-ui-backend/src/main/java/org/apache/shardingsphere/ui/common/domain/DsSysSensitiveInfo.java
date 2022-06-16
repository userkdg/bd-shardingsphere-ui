package org.apache.shardingsphere.ui.common.domain;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DsSysSensitiveInfo {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    /**
     * 导出唯一id
     */
    private String sensitiveId;

    private String dbname;

    private String schemaName;

    private String schemaId;

    private String tableName;

    private String fieldName;

    private String dataType;

    /**
     * @Deprecated 增量字段管理移到 {@link DsSysSensitiveShuffleInfo#getIncrFieldName()}管理
     *
     */
    @Deprecated
    private Boolean incrField;

    private String algorithmType;

    private String cipherKey;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
