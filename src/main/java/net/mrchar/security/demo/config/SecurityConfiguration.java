package net.mrchar.security.demo.config;

import net.mrchar.security.web.authentication.RestLoginConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
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
        http.apply(new RestLoginConfigurer());
        http.logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(new HttpStatusReturningLogoutSuccessHandler());
        return http.build();
    }
}
