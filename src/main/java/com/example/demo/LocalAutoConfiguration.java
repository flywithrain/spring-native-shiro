package com.example.demo;

import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author wxy
 */
@Configuration
public class LocalAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(SecurityManager securityManager, Map<String, Filter> filterMap) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);

        shiroFilterFactoryBean.setFilters(filterMap);
        shiroFilterFactoryBean.setUnauthorizedUrl("/403");

        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("/login", "anon");
        map.put("/error", "anon");
        map.put("/403", "anon");
        map.put("/logout", "logout");
        map.put("/unauthorized", "anon");
        map.put("/**", "authc");

        shiroFilterFactoryBean.setFilterChainDefinitionMap(map);
        return shiroFilterFactoryBean;
    }

}
