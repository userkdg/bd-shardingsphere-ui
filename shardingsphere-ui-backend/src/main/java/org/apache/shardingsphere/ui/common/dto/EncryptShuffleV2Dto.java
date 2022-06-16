package org.apache.shardingsphere.ui.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * @author Jarod.Kong
 */
@Setter
@Getter
@ToString
public class EncryptShuffleV2Dto extends EncryptShuffleV2BaseDto{
    private String dbType;

    private List<TableExtractDefine> tableExtractDefines;
}
