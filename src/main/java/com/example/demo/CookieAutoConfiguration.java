package com.example.demo;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author wxy
 */
@Configuration
public class CookieAutoConfiguration {

    @Bean
    public Realm cookieRealm() {
        return new SimpleAccountRealm();
    }

    @Bean
    public DefaultWebSecurityManager webSecurityManager(Realm realm) {
        DefaultWebSecurityManager webSecurityManager = new DefaultWebSecurityManager();
        webSecurityManager.setRealm(realm);
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionIdUrlRewritingEnabled(false);
        webSecurityManager.setSessionManager(sessionManager);
        return webSecurityManager;
    }

}
