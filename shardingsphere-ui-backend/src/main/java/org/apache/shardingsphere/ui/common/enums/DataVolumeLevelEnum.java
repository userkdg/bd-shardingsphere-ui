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
    DEFAULT(2, "十万级别", 50),
    WAN(1, "万级别", 10),
    SHI_WAN(2, "十万级别", 50),
    BAI_WAN(3, "百万级别", 200),
    QIAN_WAN(4, "千万级别", 1000),
    YI_PLUS(5, "亿级别", 2000);

    private final int code;
    private final String name;
    /**
     * 定义每个级别的分片数
     */
    private final int adviceNumberPartition;

    DataVolumeLevelEnum(int code, String name, int adviceNumberPartition) {
        this.code = code;
        this.name = name;
        this.adviceNumberPartition = adviceNumberPartition;
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
