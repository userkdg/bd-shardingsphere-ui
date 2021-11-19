//package org.apache.shardingsphere.ui.config;
//
//import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
//import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author Jarod.Kong
// */
//@Configuration
//public class MetaDataPersistServiceConfig {
//    @Bean
//    public MetaDataPersistService metaDataPersistService(ConfigCenterService configCenterService) {
//        return new MetaDataPersistService(configCenterService.getActivatedConfigCenter());
//    }
//}
