package org.apache.shardingsphere.ui.config;

import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Optional;
import java.util.Set;

/**
 * @author Jarod.Kong
 */
@ConfigurationProperties(prefix = "auth.api")
@Configuration
@ToString
public class AuthProperties {
    private Set<String> excludes;

    public Set<String> getExcludes() {
        return Optional.ofNullable(excludes).orElse(Collections.emptySet());
    }

    public void setExcludes(Set<String> excludes) {
        this.excludes = excludes;
    }
}
