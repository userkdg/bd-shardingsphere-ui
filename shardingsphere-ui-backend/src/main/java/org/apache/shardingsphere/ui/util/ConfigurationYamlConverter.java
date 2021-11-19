/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shardingsphere.ui.util;

import com.google.common.base.Preconditions;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.encrypt.api.config.EncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.yaml.config.YamlEncryptRuleConfiguration;
import org.apache.shardingsphere.encrypt.yaml.swapper.EncryptRuleConfigurationYamlSwapper;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.yaml.config.swapper.YamlRuleConfigurationSwapperEngine;
import org.apache.shardingsphere.infra.yaml.engine.YamlEngine;
import org.apache.shardingsphere.readwritesplitting.api.ReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.yaml.config.YamlReadwriteSplittingRuleConfiguration;
import org.apache.shardingsphere.readwritesplitting.yaml.swapper.ReadwriteSplittingRuleConfigurationYamlSwapper;

import java.util.Collection;
import java.util.Map;
import java.util.Properties;

/**
 * YAML converter for configuration.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConfigurationYamlConverter {

    /**
     * Load data source configurations.
     *
     * @param data data
     * @return data source configurations
     */
    @SuppressWarnings("unchecked")
    public static Map<String, DataSourceConfiguration> loadDataSourceConfigurations(final String data) {
        Map<String, DataSourceConfiguration> result = YamlEngine.unmarshal(data, Map.class);
        Preconditions.checkState(null != result && !result.isEmpty(), "No available data sources to load for orchestration.");
        return result;
    }

    /**
     * Load master-slave rule configuration.
     *
     * @param data data
     * @return master-slave rule configuration
     */
    public static ReadwriteSplittingRuleConfiguration loadMasterSlaveRuleConfiguration(final String data) {
        return new ReadwriteSplittingRuleConfigurationYamlSwapper().swapToObject(YamlEngine.unmarshal(data, YamlReadwriteSplittingRuleConfiguration.class));
    }

    /**
     * Load authentication.
     *
     * @param data data
     * @return authentication
     */
    public static Collection<RuleConfiguration> loadAuthentication(final String data) {
        return createRuleConfigurations(data);
    }

    @SuppressWarnings("unchecked")
    private static Collection<RuleConfiguration> createRuleConfigurations(final String data) {
        return new YamlRuleConfigurationSwapperEngine().swapToRuleConfigurations(YamlEngine.unmarshal(data, Collection.class));
    }
    
    /**
     * Load properties configuration.
     *
     * @param data data
     * @return properties
     */
    public static Properties loadProperties(final String data) {
        return YamlEngine.unmarshal(data, Properties.class);
    }
    
    /**
     * Load encrypt rule configuration.
     *
     * @param data data
     * @return encrypt rule configuration
     */
    public static EncryptRuleConfiguration loadEncryptRuleConfiguration(final String data) {
        return new EncryptRuleConfigurationYamlSwapper().swapToObject(YamlEngine.unmarshal(data, YamlEncryptRuleConfiguration.class));
    }
}
