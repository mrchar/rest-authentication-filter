package com.example.samples.config;

import net.mrchar.security.web.authentication.RestAccessDeniedHandler;
import net.mrchar.security.web.authentication.RestFormLoginConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.logout.HttpStatusReturningLogoutSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> {
            configurer.requestMatchers("/api/login").permitAll();
            configurer.anyRequest().authenticated();
        });
        http.csrf(configurer -> {
            configurer.ignoringRequestMatchers("/api/login");
            configurer.csrfTokenRepository(new CookieCsrfTokenRepository());
        });

        http.formLogin().disable();
        http.apply(new RestFormLoginConfigurer<>());
        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());

        http.exceptionHandling(configurer -> {
            configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
            configurer.accessDeniedHandler(new RestAccessDeniedHandler());
        });

        return http.build();
    }
}
