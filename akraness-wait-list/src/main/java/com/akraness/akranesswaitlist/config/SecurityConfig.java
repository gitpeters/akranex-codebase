package com.akraness.akranesswaitlist.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) {
        web.ignoring()
                .antMatchers("/users/**")
                .antMatchers("/actuator/**")
                .antMatchers("/swagger-ui/**", "/v3/api-docs/**");
    }
}