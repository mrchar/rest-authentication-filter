package net.mrchar.security.demo.config;

import net.mrchar.security.web.authentication.RestAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.DefaultSecurityFilterChain;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
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
        RestAuthenticationFilter restAuthenticationFilter = new RestAuthenticationFilter();
        http.addFilterAt(restAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        DefaultSecurityFilterChain securityFilterChain = http.build();

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class);
        if (authenticationManager != null) {
            restAuthenticationFilter.setAuthenticationManager(authenticationManager);
        }

        return securityFilterChain;
    }
}
