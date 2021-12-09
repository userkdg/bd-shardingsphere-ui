package org.apache.shardingsphere.ui.config;

import org.apache.ibatis.lang.UsesJava8;
import org.apache.shardingsphere.ui.repository.impl.YamlCenterConfigsRepositoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.core.type.AnnotationMetadata;

@Configuration
public class ConfigsRepositoryConfig {

    @Bean
    public YamlCenterConfigsRepositoryImpl getYamlCenterConfigsRepositoryImpl(){
        return new YamlCenterConfigsRepositoryImpl();
    }

}
