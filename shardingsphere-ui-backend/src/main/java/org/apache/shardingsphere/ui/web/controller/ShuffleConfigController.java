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

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.shardingsphere.ui.common.domain.DsSysSensitiveShuffleInfo;
import org.apache.shardingsphere.ui.common.domain.ShuffleConfig;
import org.apache.shardingsphere.ui.servcie.DsSySensitiveShuffleInfoService;
import org.apache.shardingsphere.ui.web.response.ResponseResult;
import org.apache.shardingsphere.ui.web.response.ResponseResultUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * RESTful API of shuffle center configuration.
 */
@RestController
@RequestMapping("/api/shuffle-center")
public final class ShuffleConfigController {

    @Autowired
    private DsSySensitiveShuffleInfoService sensitiveShuffleInfoService;

    @SuppressWarnings("unchecked")
    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseResult<List<ShuffleConfig>> loadConfigs(@RequestParam(value = "keyword", required = false) String keyword) {
        LambdaQueryWrapper<DsSysSensitiveShuffleInfo> wrapper = new LambdaQueryWrapper<>();
        wrapper.and(StringUtils.isNotBlank(keyword), w -> {
            return w.like(DsSysSensitiveShuffleInfo::getSchemaName, keyword)
                    .or()
                    .like(DsSysSensitiveShuffleInfo::getTableName, keyword)
                    .or()
                    .like(DsSysSensitiveShuffleInfo::getIncrFieldName, keyword)
                    .or()
                    .like(DsSysSensitiveShuffleInfo::getOnUpdateTimestampFields, keyword);
        });
        wrapper.orderByDesc(DsSysSensitiveShuffleInfo::getUpdateTime);
        wrapper.orderByAsc(DsSysSensitiveShuffleInfo::getSchemaName, DsSysSensitiveShuffleInfo::getTableName);
        return ResponseResultUtil.build(sensitiveShuffleInfoService.list(wrapper).stream()
                .map(this::toShuffleConfig)
                .collect(Collectors.toList()));
    }

    private ShuffleConfig toShuffleConfig(DsSysSensitiveShuffleInfo d) {
        ShuffleConfig vo = new ShuffleConfig();
        BeanUtils.copyProperties(d, vo);
        return vo;
    }

    @RequestMapping(value = "", method = RequestMethod.POST)
    public ResponseResult<Integer> add(@RequestBody final ShuffleConfig config) {
        config.setId(null);
        DsSysSensitiveShuffleInfo model = toDsSysSensitiveShuffleInfo(config);
        // 补充 sensitiveId
        if (model.getSensitiveId() == null) {
            String sensitiveId = getSensitiveIdBySchemaAndTableName(model);
            model.setSensitiveId(sensitiveId);
        }
        model.setCreateTime(LocalDateTime.now());
        model.setUpdateTime(LocalDateTime.now());
        sensitiveShuffleInfoService.save(model);
        return ResponseResultUtil.build(model.getId());
    }

    @Nullable
    private String getSensitiveIdBySchemaAndTableName(DsSysSensitiveShuffleInfo model) {
        LambdaQueryWrapper<DsSysSensitiveShuffleInfo> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DsSysSensitiveShuffleInfo::getSchemaName, model.getSchemaName());
        queryWrapper.eq(DsSysSensitiveShuffleInfo::getTableName, model.getTableName());
        queryWrapper.last("limit 1");
        String sensitiveId = Optional.ofNullable(sensitiveShuffleInfoService.getOne(queryWrapper)).map(DsSysSensitiveShuffleInfo::getSensitiveId).orElse(null);
        return sensitiveId;
    }

    private DsSysSensitiveShuffleInfo toDsSysSensitiveShuffleInfo(ShuffleConfig config) {
        DsSysSensitiveShuffleInfo model = new DsSysSensitiveShuffleInfo();
        BeanUtils.copyProperties(config, model);
        return model;
    }

    @RequestMapping(value = "", method = RequestMethod.DELETE)
    public ResponseResult delete(@RequestParam("id") final Integer id) {
        sensitiveShuffleInfoService.removeById(id);
        return ResponseResultUtil.success();
    }

    @RequestMapping(value = "/update", method = RequestMethod.POST)
    public ResponseResult update(@RequestBody final ShuffleConfig config) {
        if (config.getId() == null) {
            throw new RuntimeException("更新id为空");
        }
        DsSysSensitiveShuffleInfo model = toDsSysSensitiveShuffleInfo(config);
        // 补充 sensitiveId
        if (model.getSensitiveId() == null) {
            String sensitiveId = getSensitiveIdBySchemaAndTableName(model);
            model.setSensitiveId(sensitiveId);
        }
        model.setUpdateTime(LocalDateTime.now());
        sensitiveShuffleInfoService.saveOrUpdate(model);
        return ResponseResultUtil.success();
    }
}
