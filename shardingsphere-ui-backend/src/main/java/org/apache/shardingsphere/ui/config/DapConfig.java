package org.apache.shardingsphere.ui.config;

import cn.com.bluemoon.metadata.inter.factory.DbMetaDataServiceFallbackFactory;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Jarod.Kong
 */

@Configuration
public class DapConfig {
    @Bean
    public DbMetaDataServiceFallbackFactory dbDbMetaDataServiceFallbackFactory() {
        return new DbMetaDataServiceFallbackFactory();
    }
}
