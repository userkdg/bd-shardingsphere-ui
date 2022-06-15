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

package org.apache.shardingsphere.ui.common.domain;

import lombok.Getter;
import lombok.Setter;
import org.apache.shardingsphere.ui.common.enums.DataVolumeLevelEnum;

import java.util.Properties;

/**
 * shuffle config.
 */
@Getter
@Setter
public final class ShuffleConfig {
    private Integer id;

    private String sensitiveId;

    private String schemaName;

    private String tableName;

    private String incrFieldName;

    private String onUpdateTimestampFields;

    /**
     * 数据量级别
     * 1-5：万（包含或以下）、十万、百万、千万、亿级别
     * {@link DataVolumeLevelEnum#getCode()}
     */
    private Integer dataVolumeLevel;

}
