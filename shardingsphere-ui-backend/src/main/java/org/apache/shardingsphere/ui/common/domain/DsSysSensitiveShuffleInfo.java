package org.apache.shardingsphere.ui.common.domain;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.shardingsphere.ui.common.enums.DataVolumeLevelEnum;

import java.time.LocalDateTime;

@Data
public class DsSysSensitiveShuffleInfo {

    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;

    private String sensitiveId;

    private String schemaName;

    private String tableName;

    private String incrFieldName;

    private String onUpdateTimestampFields;

    /**
     * 数据量级别
     * 1-5：万（包含或以下）、十万、百万、千万、亿级别
     * {@link DataVolumeLevelEnum#getCode()}
     */
    private Integer dataVolumeLevel;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;
}
