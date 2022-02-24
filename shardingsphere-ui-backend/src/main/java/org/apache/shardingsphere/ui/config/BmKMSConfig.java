package org.apache.shardingsphere.ui.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Jarod.Kong
 */
@Configuration
@ConfigurationProperties(prefix = "bluemoon.kms.secure")
@Data
public class BmKMSConfig {
    private List<AlgorithmType> aes;

    private List<AlgorithmType> md5;

    private List<AlgorithmType> sm3;

    private List<AlgorithmType> sm4;

    private List<AlgorithmType> rc4;

    @Data
    public static class AlgorithmType {
        private String sys;

        private String type;

        private String key;
    }

}
