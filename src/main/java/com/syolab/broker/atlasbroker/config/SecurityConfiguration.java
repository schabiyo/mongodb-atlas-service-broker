package com.syolab.broker.atlasbroker.config;


import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;


@Configuration
@EnableWebSecurity
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {



    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/v2/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers(EndpointRequest.to("info", "health")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).hasAuthority("ROLE_ADMIN")
                .and()
                .httpBasic();
        // @formatter:on
    }

    @Bean
    public InMemoryUserDetailsManager userDetailsService() {
        return new InMemoryUserDetailsManager(adminUser());
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    private UserDetails adminUser() {
        return User
                .withUsername("admin")
                .password(new BCryptPasswordEncoder().encode("supersecret"))
                .roles("ADMIN")
                .build();
    }

}