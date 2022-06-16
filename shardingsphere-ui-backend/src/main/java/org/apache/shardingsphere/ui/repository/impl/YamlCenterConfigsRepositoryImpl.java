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

package org.apache.shardingsphere.ui.repository.impl;

import cn.hutool.core.io.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.shardingsphere.ui.common.domain.CenterConfigs;
import org.apache.shardingsphere.ui.common.exception.ShardingSphereUIException;
import org.apache.shardingsphere.ui.repository.CenterConfigsRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.ResourceUtils;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

/**
 * Implementation of Center configs repository.
 */
@Slf4j
public final class YamlCenterConfigsRepositoryImpl implements CenterConfigsRepository {
    
    private final File file;
    
    public YamlCenterConfigsRepositoryImpl(final File file) {
        this.file = file;
    }
    
    @Override
    public CenterConfigs load() {
        if (!file.exists()) {
            return new CenterConfigs();
        }
        
        try (FileInputStream fileInputStream = new FileInputStream(file);
             InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, StandardCharsets.UTF_8)) {
            return new Yaml(new Constructor(CenterConfigs.class)).loadAs(inputStreamReader, CenterConfigs.class);
        } catch (IOException e) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "load center config error");
        }
        
    }
    
    @Override
    public void save(final CenterConfigs centerConfigs) {
        Yaml yaml = new Yaml();
        try (BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(file))) {
            bufferedOutputStream.write(yaml.dumpAsMap(centerConfigs).getBytes());
            bufferedOutputStream.flush();
        } catch (IOException e) {
            throw new ShardingSphereUIException(ShardingSphereUIException.SERVER_ERROR, "save center config error");
        }
    }
    
}
