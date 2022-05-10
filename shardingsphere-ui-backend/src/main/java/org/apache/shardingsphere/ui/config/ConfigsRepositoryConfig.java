package org.apache.shardingsphere.ui.config;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.repository.impl.YamlCenterConfigsRepositoryImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.ClassUtils;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

import static org.springframework.util.ResourceUtils.CLASSPATH_URL_PREFIX;

@Configuration
@Slf4j
public class ConfigsRepositoryConfig {
    @Value("${spring.profiles.active:test}")
    private String springProfileActive;

    @Bean
    public YamlCenterConfigsRepositoryImpl getYamlCenterConfigsRepositoryImpl() {

        String filePath;
        if ("test".equalsIgnoreCase(springProfileActive)) {
            filePath = "config/shardingsphere-ui-configs-test.yaml";
        }else {
            filePath = "config/shardingsphere-ui-configs-prod.yaml";
        }
        try {
            InputStream input = Objects.requireNonNull(ClassUtils.getDefaultClassLoader()).getResourceAsStream(filePath);
            File file = new File(new File(System.getProperty("user.dir")), "shardingsphere-ui-configs.yaml");
            assert input != null;
            FileUtils.copyToFile(input, file);
            log.info("加载配置文件：{}", file.getAbsolutePath());
            return new YamlCenterConfigsRepositoryImpl(file);
        } catch (Exception e) {
            throw new ShardingSphereUIException(500, "获取配置配置失败");
        }
    }

}
