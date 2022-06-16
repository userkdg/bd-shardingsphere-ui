package org.apache.shardingsphere.ui.common.enums;

import lombok.Getter;

import java.util.Arrays;

/**
 * 定义量级
 * 需要业务系统大概明确表的数据量级
 * 如：万级别表示0-9.9w数据量的表，其他类似
 * 原因：用于刷数程序，更合理的评估刷数的分片数
 *
 * @author Jarod.Kong
 */
@Getter
public enum DataVolumeLevelEnum {
    DEFAULT(2, "十万级别", 50, false),
    WAN(1, "万级别", 10, false),
    SHI_WAN(2, "十万级别", 50, false),
    BAI_WAN(3, "百万级别", 200, true),
    QIAN_WAN(4, "千万级别", 1000, true),
    YI_PLUS(5, "亿级别", 2000, true);

    private final int code;
    private final String name;
    /**
     * 定义每个级别的分片数
     */
    private final int adviceNumberPartition;
    /**
     * 定义级别的增量字段是否要有索引！
     */
    private final boolean needIncrFieldIndex;

    DataVolumeLevelEnum(int code, String name, int adviceNumberPartition, boolean needIncrFieldIndex) {
        this.code = code;
        this.name = name;
        this.adviceNumberPartition = adviceNumberPartition;
        this.needIncrFieldIndex = needIncrFieldIndex;
    }

    public static DataVolumeLevelEnum from(String name, DataVolumeLevelEnum defaultVal) {
        return Arrays.stream(values()).filter(d -> d.getName().equals(name))
                .findFirst().orElse(defaultVal);
    }

    public static DataVolumeLevelEnum from(Integer code, DataVolumeLevelEnum defaultVal) {
        return Arrays.stream(values()).filter(d -> code != null && d.getCode() == code)
                .findFirst().orElse(defaultVal);
    }
}
