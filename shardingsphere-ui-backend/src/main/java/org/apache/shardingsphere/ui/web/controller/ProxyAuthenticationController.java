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

package org.apache.shardingsphere.ui.web.controller;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.servcie.ProxyAuthenticationService;
import org.apache.shardingsphere.ui.servcie.ShardingSchemaService;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.apache.shardingsphere.ui.web.response.ResponseResultUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

/**
 * RESTful API of sharding proxy authentication.
 */
@RestController
@RequestMapping("/api/authentication")
@Slf4j
public final class ProxyAuthenticationController {
    
    @Autowired
    private ProxyAuthenticationService proxyAuthenticationService;

    @Autowired
    private ShardingSchemaService shardingSchemaService;
    
    /**
     * Load authentication.
     *
     * @return response result
     */
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseResult<String> loadAuthentication() {
        return ResponseResultUtil.build(proxyAuthenticationService.getAuthentication());
    }

    /**
     * Update authentication.
     *
     * @param configMap config map
     * @return response result
     */
    @RequestMapping(value = "", method = RequestMethod.PUT)
    public ResponseResult updateAuthentication(@RequestBody final Map<String, String> configMap) {
        proxyAuthenticationService.updateAuthentication(configMap.get("authentication"));
        shardingSchemaService.asyncRefreshAllSchemaDataSources();
        return ResponseResultUtil.success();
    }

}
