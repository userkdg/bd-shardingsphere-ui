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

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepository;
import org.apache.shardingsphere.mode.repository.cluster.ClusterPersistRepositoryConfiguration;
import org.apache.shardingsphere.mode.repository.cluster.zookeeper.CuratorZookeeperRepository;
import org.apache.shardingsphere.ui.common.constant.InstanceType;
import org.apache.shardingsphere.ui.common.domain.CenterConfig;

import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Center factory.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class CenterRepositoryFactory {

    private static final ConcurrentHashMap<String, ClusterPersistRepository> CONFIG_CENTER_MAP = new ConcurrentHashMap<>();


    /**
     * Create config center instance
     *
     * @param config config center config
     * @return config center
     */
    public static ClusterPersistRepository createConfigCenter(final CenterConfig config) {
        if (config.getProps() == null || config.getProps().isEmpty()){
            Properties props = new Properties();
            props.put("retryIntervalMilliseconds",10000);
            props.put("timeToLiveSeconds",60);
            props.put("maxRetries",3);
            props.put("operationTimeoutMilliseconds",70000);
            config.setProps(props);
        }
        ClusterPersistRepository result = CONFIG_CENTER_MAP.get(config.getName());
        if (null != result) {
            return result;
        }
        InstanceType instanceType = InstanceType.nameOf(config.getInstanceType());
        switch (instanceType) {
            case ZOOKEEPER:
                result = new CuratorZookeeperRepository();
                result.setProps(config.getProps());
                break;
            default:
                throw new UnsupportedOperationException(config.getName());
        }
        result.init(convert(config));
        CONFIG_CENTER_MAP.put(config.getName(), result);
        return result;
    }


    private static ClusterPersistRepositoryConfiguration convert(final CenterConfig config) {
        ClusterPersistRepositoryConfiguration result = new ClusterPersistRepositoryConfiguration(config.getInstanceType(),
                config.getNamespace(), config.getServerLists(), config.getProps());
        return result;
    }

}
