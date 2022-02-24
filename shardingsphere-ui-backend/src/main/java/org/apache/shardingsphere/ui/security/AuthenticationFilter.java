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

package org.apache.shardingsphere.ui.security;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.ui.web.response.ResponseResultUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Authentication filter.
 */
@Slf4j
public final class AuthenticationFilter implements Filter {

    private static final String LOGIN_URI = "/api/login";

    private final Gson gson = new Gson();

    private static final Set<String> openApi = new HashSet<String >(){{
        add("/api/kms-center/pull");
    }};

    @Setter
    private UserAuthenticationService userAuthenticationService;

    @Override
    public void init(final FilterConfig filterConfig) {
    }

    @Override
    public void doFilter(final ServletRequest servletRequest, final ServletResponse servletResponse, final FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpResponse = (HttpServletResponse) servletResponse;
        if (LOGIN_URI.equals(httpRequest.getRequestURI())) {
            handleLogin(httpRequest, httpResponse);
        } else {
            String accessToken = httpRequest.getHeader("Access-Token");
            if (!Strings.isNullOrEmpty(accessToken) && accessToken.equals(userAuthenticationService.getToken())) {
                filterChain.doFilter(httpRequest, httpResponse);
            } else if (openApi.contains(httpRequest.getRequestURI())){
                log.info("open api {} 放行，请求ip={}", httpRequest.getRequestURI(), getIP(httpRequest));
                filterChain.doFilter(httpRequest, httpResponse);
            } else {
                respondWithUnauthorized(httpResponse);
            }
        }
    }

    @Override
    public void destroy() {
    }

    private String getIP(HttpServletRequest request) {
        try {
            String ip = request.getHeader("x-forwarded-for");
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("X-Real-IP");
            }
            if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            return ip;
        } catch (Exception e) {
            // nothing
        }
        return null;
    }

    private void handleLogin(final HttpServletRequest httpRequest, final HttpServletResponse httpResponse) {
        try {
            UserAccount user = gson.fromJson(httpRequest.getReader(), UserAccount.class);
            if (userAuthenticationService.checkUser(user)) {
                httpResponse.setContentType("application/json");
                httpResponse.setCharacterEncoding("UTF-8");
                Map<String, Object> result = new HashMap<>();
                result.put("username", userAuthenticationService.getUsername());
                result.put("accessToken", userAuthenticationService.getToken());
                httpResponse.getWriter().write(gson.toJson(ResponseResultUtil.build(result)));
            } else {
                respondWithUnauthorized(httpResponse);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void respondWithUnauthorized(final HttpServletResponse httpResponse) throws IOException {
        httpResponse.setContentType("application/json");
        httpResponse.setCharacterEncoding("UTF-8");
        httpResponse.getWriter().write(new Gson().toJson(ResponseResultUtil.handleUnauthorizedException("Unauthorized.")));
    }
}
