package com.tweats.config.security;

import com.tweats.service.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {


    private UserPrincipalService userPrincipalService;

    @Autowired
    public SecurityConfig(UserPrincipalService userPrincipalService) {
        this.userPrincipalService = userPrincipalService;
    }

    @Bean
    public PasswordEncoder getPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http)
            throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class)
                .userDetailsService(userPrincipalService)
                .passwordEncoder(getPasswordEncoder())
                .and()
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors()
                .and()
                .authorizeRequests()
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers("/cart/**").hasAnyRole("CUSTOMER", "ADMIN")
                .antMatchers("/order/active", "/order/complete").hasRole("VENDOR")
                .antMatchers("/order/completed").hasAnyRole("ADMIN", "VENDOR")
                .antMatchers("/order/place", "/order/buy/*").hasAnyRole("ADMIN", "CUSTOMER")
                .antMatchers(HttpMethod.POST, "/category").hasRole("ADMIN")
                .antMatchers(HttpMethod.GET, "/category").hasAnyRole("CUSTOMER", "ADMIN")
                .antMatchers(HttpMethod.GET, "/category/vendor").hasRole("VENDOR")
                .antMatchers(HttpMethod.PUT, "/category/status").hasRole("VENDOR")
                .antMatchers(HttpMethod.POST, "/item").hasRole("VENDOR")
                .antMatchers(HttpMethod.PUT, "/item/*").hasRole("VENDOR")
                .anyRequest().authenticated()
                .and()
                .httpBasic()
                .and()
                .csrf().disable();

        return http.build();

    }

}
