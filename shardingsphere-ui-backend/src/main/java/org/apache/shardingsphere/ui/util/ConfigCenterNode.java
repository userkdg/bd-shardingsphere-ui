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

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Configuration node.
 */
@RequiredArgsConstructor
public final class ConfigCenterNode {
    
    @Deprecated
    private static final String ROOT = "config";
    
    private static final String SCHEMA_NODE = "schema";
    
    private static final String DATA_SOURCE_NODE = "datasource";
    
    private static final String RULE_NODE = "rule";
    
    private static final String AUTHENTICATION_NODE = "rules";
    
    private static final String PROPS_NODE = "props";
    
    private static final String COMMA_SEPARATOR = ",";
    
    private static final String PATH_SEPARATOR = "/";
    
    private final String name;
    
    /**
     * Get schema path.
     *
     * @return schema path
     */
    public String getSchemaPath() {
        return Joiner.on(PATH_SEPARATOR).join("", SCHEMA_NODE);
    }
    
    /**
     * Get schema name path.
     * 
     * @param schemaName schema name
     * @return schema name path
     */
    public String getSchemaNamePath(final String schemaName) {
        return Joiner.on(PATH_SEPARATOR).join("",  SCHEMA_NODE, schemaName);
    }
    
    /**
     * Get data source path.
     *
     * @param schemaName schema name
     * @return data source path
     */
    public String getDataSourcePath(final String schemaName) {
        return getFullPath(schemaName, DATA_SOURCE_NODE);
    }
    
    /**
     * Get rule path.
     * 
     * @param schemaName schema name
     * @return rule path
     */
    public String getRulePath(final String schemaName) {
        return getFullPath(schemaName, RULE_NODE);
    }
    
    /**
     * Get authentication path.
     *
     * @return authentication path
     */
    public String getAuthenticationPath() {
        return getFullPath(AUTHENTICATION_NODE);
    }
    
    /**
     * Get props path.
     *
     * @return props path
     */
    public String getPropsPath() {
        return getFullPath(PROPS_NODE);
    }
    
    private String getFullPath(final String schemaName, final String node) {
        return Joiner.on(PATH_SEPARATOR).join("",  SCHEMA_NODE, schemaName, node);
    }
    
    private String getFullPath(final String node) {
        return Joiner.on(PATH_SEPARATOR).join("",  node);
    }
    
    /**
     * Get schema name.
     * 
     * @param configurationNodeFullPath configuration node full path
     * @return schema name
     */
    public String getSchemaName(final String configurationNodeFullPath) {
        Pattern pattern = Pattern.compile(getSchemaPath() + "/(\\w+)" + "(/datasource|/rule)?", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(configurationNodeFullPath);
        return matcher.find() ? matcher.group(1) : "";
    }
    
    /**
     * Split sharding schema name.
     * 
     * @param shardingSchemaNames sharding schema names
     * @return sharding schema names
     */
    public Collection<String> splitShardingSchemaName(final String shardingSchemaNames) {
        return Strings.isNullOrEmpty(shardingSchemaNames) ? Collections.emptyList() : Splitter.on(COMMA_SEPARATOR).splitToList(shardingSchemaNames);
    }
    
    /**
     * Get all schema config paths.
     * 
     * @param schemaNames schema names.
     * @return config paths list.
     */
    public Collection<String> getAllSchemaConfigPaths(final Collection<String> schemaNames) {
        Collection<String> result = new ArrayList<>(Arrays.asList(getSchemaPath()));
        for (String schemaName : schemaNames) {
            result.add(getSchemaNamePath(schemaName));
            result.add(getRulePath(schemaName));
            result.add(getDataSourcePath(schemaName));
        }
        return result;
    }
}
