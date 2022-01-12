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

package org.apache.shardingsphere.ui.servcie.impl;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.infra.metadata.schema.ShardingSphereSchema;
import org.apache.shardingsphere.mode.metadata.persist.node.SchemaMetaDataNode;
import org.apache.shardingsphere.mode.persist.PersistRepository;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.util.ConfigurationYamlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * Implementation of sharding schema service.
 */
@Service
@Slf4j
public final class ShardingSchemaServiceImpl implements ShardingSchemaService {

    private static final ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor();
    @Autowired
    ConfigCenterService configCenterService;

    @Override
    public Collection<String> getAllSchemaNames() {
        return configCenterService.getActivatedMetadataService().getSchemaMetaDataService().loadAllNames();
    }

    @Override
    public String getRuleConfiguration(final String schemaName) {
        PersistRepository repository = configCenterService.getActivatedMetadataService().getRepository();
        return repository.get(SchemaMetaDataNode.getRulePath(schemaName));
    }

    @Override
    public String getDataSourceConfiguration(final String schemaName) {
        PersistRepository repository = configCenterService.getActivatedMetadataService().getRepository();
        return repository.get(SchemaMetaDataNode.getMetaDataDataSourcePath(schemaName));
    }

    @Override
    public void updateRuleConfiguration(final String schemaName, final String configData) {
        Collection<RuleConfiguration> ruleConfigurations = checkRuleConfiguration(configData);
        persistRuleConfiguration(schemaName, ruleConfigurations);
    }

    @Override
    public void updateDataSourceConfiguration(final String schemaName, final String configData) {
        Map<String, DataSourceConfiguration> sourceConfigurationMap = checkDataSourceConfiguration(configData);
        persistDataSourceConfiguration(schemaName, sourceConfigurationMap);
    }

    @Override
    public void addSchemaConfiguration(final String schemaName, final String ruleConfiguration, final String dataSourceConfiguration) {
        checkSchemaName(schemaName, getAllSchemaNames());
        Collection<RuleConfiguration> ruleConfigurations = checkRuleConfiguration(ruleConfiguration);
        Map<String, DataSourceConfiguration> dataSourceConfigurationMap = checkDataSourceConfiguration(dataSourceConfiguration);
        persistRuleConfiguration(schemaName, ruleConfigurations);
        persistDataSourceConfiguration(schemaName, dataSourceConfigurationMap);
        persistSchemaName(schemaName);
    }

    @SneakyThrows
    @Override
    public void refreshAllSchemaDataSources() {
        Collection<String> schemaNames = getAllSchemaNames();
        if (schemaNames == null) {
            log.info("schema为空不刷新");
            return;
        }
        for (String schemaName : schemaNames) {
            refreshSchemaDataSourceConfiguration(schemaName);
            TimeUnit.SECONDS.sleep(5);
        }
    }

    @Override
    public void asyncRefreshAllSchemaDataSources() {
        singleThreadExecutor.submit(() -> {
            log.info("刷新开始");
            try {
                TimeUnit.SECONDS.sleep(30);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            refreshAllSchemaDataSources();
            log.info("刷新完成");
        });
    }

    private void refreshSchemaDataSourceConfiguration(String schemaName) {
        log.info("刷新schema={}，数据源配置开始", schemaName);
        String dsConfig = getDataSourceConfiguration(schemaName);
        log.info("refresh dsConfig=" + dsConfig);
        updateDataSourceConfiguration(schemaName, dsConfig);
        log.info("刷新schema={}，数据源配置结束", schemaName);
    }


    private Collection<RuleConfiguration> checkRuleConfiguration(final String configData) {
        try {
            if (configData.contains("encryptors:\n")) {
                return ConfigurationYamlConverter.loadEncryptRuleConfiguration(configData);
            } else {
                return ConfigurationYamlConverter.loadMasterSlaveRuleConfiguration(configData);
            }
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("rule configuration is invalid.");
        }
    }

    private void persistRuleConfiguration(final String schemaName, final Collection<RuleConfiguration> ruleConfiguration) {
        configCenterService.getActivatedMetadataService().getSchemaRuleService()
                .persist(schemaName, ruleConfiguration, true);
    }

    private Map<String, DataSourceConfiguration> checkDataSourceConfiguration(final String configData) {
        try {
            Map<String, DataSourceConfiguration> dataSourceConfigs = ConfigurationYamlConverter.loadDataSourceConfigurations(configData);
            Preconditions.checkState(!dataSourceConfigs.isEmpty(), "data source configuration is invalid.");
            return dataSourceConfigs;
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("data source configuration is invalid.");
        }
    }

    private void persistDataSourceConfiguration(final String schemaName, final Map<String, DataSourceConfiguration> dataSourceConfiguration) {
        configCenterService.getActivatedMetadataService().getDataSourceService().persist(schemaName, dataSourceConfiguration);
    }

    private void checkSchemaName(final String schemaName, final Collection<String> existedSchemaNames) {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(schemaName), "schema name is invalid.");
        Preconditions.checkArgument(!existedSchemaNames.contains(schemaName), "schema name already exists.");
    }

    private void persistSchemaName(final String schemaName) {
        configCenterService.getActivatedMetadataService().getSchemaMetaDataService().persist(schemaName, new ShardingSphereSchema());
    }
}
