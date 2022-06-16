package org.apache.shardingsphere.ui.common.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Set;

/**
 * @author Jarod.Kong
 */
@Setter
@Getter
@ToString
public class EncryptShuffleV2BaseDto {
    private String schema;

    private Set<String> shuffleTableNames;
}
