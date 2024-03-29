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

package org.apache.shardingsphere.ui.web.response;

import lombok.*;

import java.io.Serializable;

/**
 * Restful Response result.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public final class ResponseResult<T> implements Serializable {
    
    private static final long serialVersionUID = 8144393142115317354L;
    
    private boolean success = true;
    
    private int errorCode;
    
    private String errorMsg;
    
    private T model;

    private ResponseResult(int code, String msg) {
        this.success = false;
        this.errorCode = code;
        this.errorMsg = msg;
    }

    private ResponseResult(T model) {
        this.model = model;
    }

    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult(500, msg);
    }

    public static <T> ResponseResult<T> ok(T data) {
        return new ResponseResult(data);
    }
}
