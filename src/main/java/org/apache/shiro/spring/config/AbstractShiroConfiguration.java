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
package org.apache.shiro.spring.config;

import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authc.pam.AtLeastOneSuccessfulStrategy;
import org.apache.shiro.authc.pam.AuthenticationStrategy;
import org.apache.shiro.authc.pam.ModularRealmAuthenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.authz.permission.PermissionResolver;
import org.apache.shiro.authz.permission.RolePermissionResolver;
import org.apache.shiro.cache.CacheManager;
import org.apache.shiro.config.Ini;
import org.apache.shiro.event.EventBus;
import org.apache.shiro.mgt.*;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.session.mgt.DefaultSessionManager;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.session.mgt.SimpleSessionFactory;
import org.apache.shiro.session.mgt.eis.MemorySessionDAO;
import org.apache.shiro.session.mgt.eis.SessionDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

/**
 * @since 1.4.0
 */
public class AbstractShiroConfiguration {

    @Autowired(required = false)
    public CacheManager cacheManager;

    @Autowired(required = false)
    public RolePermissionResolver rolePermissionResolver;

    @Autowired(required = false)
    public PermissionResolver permissionResolver;

    @Autowired
    public EventBus eventBus;

    @Value("#{ @environment['shiro.sessionManager.deleteInvalidSessions'] ?: true }")
    public boolean sessionManagerDeleteInvalidSessions;


    public SessionsSecurityManager securityManager(List<Realm> realms) {
        SessionsSecurityManager securityManager = createSecurityManager();
        securityManager.setAuthenticator(authenticator());
        securityManager.setAuthorizer(authorizer());
        securityManager.setRealms(realms);
        securityManager.setSessionManager(sessionManager());
        securityManager.setEventBus(eventBus);

        if (cacheManager != null) {
            securityManager.setCacheManager(cacheManager);
        }

        return securityManager;
    }

    public SessionManager sessionManager() {
        DefaultSessionManager sessionManager = new DefaultSessionManager();
        sessionManager.setSessionDAO(sessionDAO());
        sessionManager.setSessionFactory(sessionFactory());
        sessionManager.setDeleteInvalidSessions(sessionManagerDeleteInvalidSessions);
        return sessionManager;
    }


    public SessionsSecurityManager createSecurityManager() {
        DefaultSecurityManager securityManager = new DefaultSecurityManager();
        securityManager.setSubjectDAO(subjectDAO());
        securityManager.setSubjectFactory(subjectFactory());

        RememberMeManager rememberMeManager = rememberMeManager();
        if (rememberMeManager != null) {
            securityManager.setRememberMeManager(rememberMeManager);
        }

        return securityManager;
    }

    public RememberMeManager rememberMeManager() {
        return null;
    }

    public SubjectDAO subjectDAO() {
        DefaultSubjectDAO subjectDAO = new DefaultSubjectDAO();
        subjectDAO.setSessionStorageEvaluator(sessionStorageEvaluator());
        return subjectDAO;
    }

    public SessionStorageEvaluator sessionStorageEvaluator() {
        return new DefaultSessionStorageEvaluator();
    }

    public SubjectFactory subjectFactory() {
        return new DefaultSubjectFactory();
    }


    public SessionFactory sessionFactory() {
        return new SimpleSessionFactory();
    }

    public SessionDAO sessionDAO() {
        return new MemorySessionDAO();
    }

    public Authorizer authorizer() {
        ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();

        if (permissionResolver != null) {
            authorizer.setPermissionResolver(permissionResolver);
        }

        if (rolePermissionResolver != null) {
            authorizer.setRolePermissionResolver(rolePermissionResolver);
        }

        return authorizer;
    }

    public AuthenticationStrategy authenticationStrategy() {
        return new AtLeastOneSuccessfulStrategy();
    }

    public Authenticator authenticator() {
        ModularRealmAuthenticator authenticator = new ModularRealmAuthenticator();
        authenticator.setAuthenticationStrategy(authenticationStrategy());
        return authenticator;
    }

    public Realm iniRealmFromLocation(String iniLocation) {
        Ini ini = Ini.fromResourcePath(iniLocation);
        return new IniRealm( ini );
    }
}
