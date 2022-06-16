package org.apache.shardingsphere.ui.common.dto;

import cn.com.bluemoon.shardingsphere.custom.shuffle.base.ExtractMode;
import lombok.*;

/**
 * @author Jarod.Kong
 */
@Setter
@Getter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TableExtractDefine {
    private String tableName;
    private ExtractMode extractMode;
    private String incrFieldName;
    private String customExtractWhereSql;
    /**
     * {@link ExtractMode#WithPersistStateCustomWhere}下有意义，用于重置作业历史刷数状态，重新洗数
     */
    private boolean resetExtractState;

    /**
     * 建议表抽取的分片数，对应表主键
     */
    private Integer adviceNumberPartition;

    /**
     * 重置表历史洗数状态
     */
    public TableExtractDefine(String tableName, boolean resetExtractState) {
        this.tableName = tableName;
        this.resetExtractState = resetExtractState;
    }
}
