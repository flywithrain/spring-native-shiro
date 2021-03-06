/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.shiro.spring.web.config;

import org.apache.shiro.mgt.RememberMeManager;
import org.apache.shiro.mgt.SessionStorageEvaluator;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.config.AbstractShiroConfiguration;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.mgt.DefaultWebSessionStorageEvaluator;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.servlet.SimpleCookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.apache.shiro.web.session.mgt.ServletContainerSessionManager;
import org.springframework.beans.factory.annotation.Value;

/**
 * @since 1.4.0
 */
public class AbstractShiroWebConfiguration extends AbstractShiroConfiguration {

    @Value("#{ @environment['shiro.sessionManager.sessionIdCookieEnabled'] ?: true }")
    public boolean sessionIdCookieEnabled;

    @Value("#{ @environment['shiro.sessionManager.sessionIdUrlRewritingEnabled'] ?: true }")
    public boolean sessionIdUrlRewritingEnabled;

    @Value("#{ @environment['shiro.userNativeSessionManager'] ?: false }")
    public boolean useNativeSessionManager;


    // Session Cookie info

    @Value("#{ @environment['shiro.sessionManager.cookie.name'] ?: T(org.apache.shiro.web.servlet.ShiroHttpSession).DEFAULT_SESSION_ID_NAME }")
    public String sessionIdCookieName;

    @Value("#{ @environment['shiro.sessionManager.cookie.maxAge'] ?: T(org.apache.shiro.web.servlet.SimpleCookie).DEFAULT_MAX_AGE }")
    public int sessionIdCookieMaxAge;

    @Value("#{ @environment['shiro.sessionManager.cookie.domain'] ?: null }")
    public String sessionIdCookieDomain;

    @Value("#{ @environment['shiro.sessionManager.cookie.path'] ?: null }")
    public String sessionIdCookiePath;

    @Value("#{ @environment['shiro.sessionManager.cookie.secure'] ?: false }")
    public boolean sessionIdCookieSecure;


    // RememberMe Cookie info

    @Value("#{ @environment['shiro.rememberMeManager.cookie.name'] ?: T(org.apache.shiro.web.mgt.CookieRememberMeManager).DEFAULT_REMEMBER_ME_COOKIE_NAME }")
    public String rememberMeCookieName;

    @Value("#{ @environment['shiro.rememberMeManager.cookie.maxAge'] ?: T(org.apache.shiro.web.servlet.Cookie).ONE_YEAR }")
    public int rememberMeCookieMaxAge;

    @Value("#{ @environment['shiro.rememberMeManager.cookie.domain'] ?: null }")
    public String rememberMeCookieDomain;

    @Value("#{ @environment['shiro.rememberMeManager.cookie.path'] ?: null }")
    public String rememberMeCookiePath;

    @Value("#{ @environment['shiro.rememberMeManager.cookie.secure'] ?: false }")
    public boolean rememberMeCookieSecure;


    public SessionManager nativeSessionManager() {
        DefaultWebSessionManager webSessionManager = new DefaultWebSessionManager();
        webSessionManager.setSessionIdCookieEnabled(sessionIdCookieEnabled);
        webSessionManager.setSessionIdUrlRewritingEnabled(sessionIdUrlRewritingEnabled);
        webSessionManager.setSessionIdCookie(sessionCookieTemplate());

        webSessionManager.setSessionFactory(sessionFactory());
        webSessionManager.setSessionDAO(sessionDAO());
        webSessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);

        return webSessionManager;
    }

    public Cookie sessionCookieTemplate() {
        return buildCookie(
                sessionIdCookieName,
                sessionIdCookieMaxAge,
                sessionIdCookiePath,
                sessionIdCookieDomain,
                sessionIdCookieSecure);
    }

    public Cookie rememberMeCookieTemplate() {
        return buildCookie(
                rememberMeCookieName,
                rememberMeCookieMaxAge,
                rememberMeCookiePath,
                rememberMeCookieDomain,
                rememberMeCookieSecure);
    }

    public Cookie buildCookie(String name, int maxAge, String path, String domain, boolean secure) {
        Cookie cookie = new SimpleCookie(name);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(maxAge);
        cookie.setPath(path);
        cookie.setDomain(domain);
        cookie.setSecure(secure);

        return cookie;
    }

    @Override
    public SessionManager sessionManager() {
        if (useNativeSessionManager) {
            return nativeSessionManager();
        }
        return new ServletContainerSessionManager();
    }

    public RememberMeManager rememberMeManager() {
        CookieRememberMeManager cookieRememberMeManager = new CookieRememberMeManager();
        cookieRememberMeManager.setCookie(rememberMeCookieTemplate());
        return cookieRememberMeManager;
    }

    @Override
    public SubjectFactory subjectFactory() {
        return new DefaultWebSubjectFactory();
    }

    @Override
    public SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultWebSessionStorageEvaluator();
    }

    public SessionsSecurityManager createSecurityManager() {

        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setSubjectDAO(subjectDAO());
        securityManager.setSubjectFactory(subjectFactory());
        securityManager.setRememberMeManager(rememberMeManager());

        return securityManager;
    }

    public ShiroFilterChainDefinition shiroFilterChainDefinition() {
        DefaultShiroFilterChainDefinition chainDefinition = new DefaultShiroFilterChainDefinition();
        chainDefinition.addPathDefinition("/**", "authc");
        return chainDefinition;
    }
}
