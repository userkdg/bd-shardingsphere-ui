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

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.infra.config.RuleConfiguration;
import org.apache.shardingsphere.infra.config.datasource.DataSourceConfiguration;
import org.apache.shardingsphere.mode.metadata.persist.MetaDataPersistService;
import org.apache.shardingsphere.mode.metadata.persist.node.GlobalNode;
import org.apache.shardingsphere.mode.persist.PersistRepository;
import org.apache.shardingsphere.ui.servcie.ConfigCenterService;
import org.apache.shardingsphere.ui.servcie.ProxyAuthenticationService;
import org.apache.shardingsphere.ui.util.ConfigurationYamlConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Map;

/**
 * Implementation of sharding proxy authentication service.
 */
@Service
@Slf4j
public final class ProxyAuthenticationServiceImpl implements ProxyAuthenticationService {
    @Autowired
    ConfigCenterService configCenterService;

    @Override
    public String getAuthentication() {
        PersistRepository repository = configCenterService.getActivatedMetadataService().getRepository();
        return repository.get(GlobalNode.getGlobalRuleNode());
    }

    @Override
    public void updateAuthentication(final String authentication) {
        Collection<RuleConfiguration> ruleConfigurations = checkAuthenticationConfiguration(authentication);
        MetaDataPersistService activatedMetadataService = configCenterService.getActivatedMetadataService();
        activatedMetadataService.getGlobalRuleService().persist(ruleConfigurations, true);
        Collection<String> schemas = activatedMetadataService.getSchemaMetaDataService().loadAllNames();
        for (String schemaName : schemas) {
            refreshSchemaDataSourceConfiguration(schemaName);
        }
    }

    private void refreshSchemaDataSourceConfiguration(String schemaName) {
        log.info("刷新schema={}，数据源配置开始", schemaName);
        Map<String, DataSourceConfiguration> dsMap = configCenterService.getActivatedMetadataService().getDataSourceService().load(schemaName);
        configCenterService.getActivatedMetadataService().getDataSourceService().persist(schemaName, dsMap);
        log.info("刷新schema={}，数据源配置结束", schemaName);
    }

    private Collection<RuleConfiguration> checkAuthenticationConfiguration(final String data) {
        try {
            return ConfigurationYamlConverter.loadAuthentication(data);
            // CHECKSTYLE:OFF
        } catch (final Exception ex) {
            // CHECKSTYLE:ON
            throw new IllegalArgumentException("authentication configuration is invalid.");
        }
    }
}
