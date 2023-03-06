package net.mrchar.security.web.authentication;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.RememberMeServices;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

public final class RestLoginConfigurer extends AbstractHttpConfigurer<RestLoginConfigurer, HttpSecurity> {
    private final RestAuthenticationFilter authFilter = new RestAuthenticationFilter();
    private AuthenticationSuccessHandler successHandler = new RestAuthenticationSuccessHandler();
    private AuthenticationFailureHandler failureHandler = new RestAuthenticationFailureHandler();
    private String loginProcessingUrl;
    private boolean permitAll;


    public RestLoginConfigurer() {
        this.loginParamsType(DefaultRestLoginParamsImpl.class);
        this.loginProcessingUrl("/api/login");
    }

    public <T extends RestLoginParams> RestLoginConfigurer(Class<T> loginParamsType, String defaultLoginProcessingUrl) {
        this();
        this.loginParamsType(loginParamsType);
        if (defaultLoginProcessingUrl != null) {
            this.loginProcessingUrl(defaultLoginProcessingUrl);
        }
    }

    public RestLoginConfigurer loginProcessingUrl(String loginProcessingUrl) {
        this.loginProcessingUrl = loginProcessingUrl;
        this.authFilter.setRequiresAuthenticationRequestMatcher(createLoginProcessingUrlMatcher(loginProcessingUrl));
        return this;
    }

    public RestLoginConfigurer successHandler(AuthenticationSuccessHandler successHandler) {
        this.successHandler = successHandler;
        return this;
    }

    public RestLoginConfigurer failureHandler(AuthenticationFailureHandler authenticationFailureHandler) {
        this.failureHandler = authenticationFailureHandler;
        return this;
    }

    public RestLoginConfigurer permitAll() {
        return permitAll(true);
    }

    public RestLoginConfigurer permitAll(boolean permitAll) {
        this.permitAll = permitAll;
        return this;
    }

    @Override
    public void init(HttpSecurity http) throws Exception {
        if (this.permitAll && this.loginProcessingUrl != null) {
            http.authorizeHttpRequests(configurer -> {
                configurer.requestMatchers(this.loginProcessingUrl).permitAll();
            });
            http.csrf(configurer -> {
                configurer.ignoringRequestMatchers(this.loginProcessingUrl);
            });
        }
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        this.authFilter.setAuthenticationManager(http.getSharedObject(AuthenticationManager.class));
        if (this.successHandler != null) {
            this.authFilter.setAuthenticationSuccessHandler(this.successHandler);
        }
        if (this.failureHandler != null) {
            this.authFilter.setAuthenticationFailureHandler(this.failureHandler);
        }

        SessionAuthenticationStrategy sessionAuthenticationStrategy = http
                .getSharedObject(SessionAuthenticationStrategy.class);
        if (sessionAuthenticationStrategy != null) {
            this.authFilter.setSessionAuthenticationStrategy(sessionAuthenticationStrategy);
        }
        RememberMeServices rememberMeServices = http.getSharedObject(RememberMeServices.class);
        if (rememberMeServices != null) {
            this.authFilter.setRememberMeServices(rememberMeServices);
        }
        this.authFilter.setSecurityContextHolderStrategy(getSecurityContextHolderStrategy());
        RestAuthenticationFilter filter = postProcess(this.authFilter);
        http.addFilterAt(filter, UsernamePasswordAuthenticationFilter.class);
    }


    public RestLoginConfigurer loginParamsType(Class<? extends RestLoginParams> loginParamsType) {
        this.authFilter.setLoginParamsType(loginParamsType);
        return this;
    }


    private RequestMatcher createLoginProcessingUrlMatcher(String loginProcessingUrl) {
        return new AntPathRequestMatcher(loginProcessingUrl, "POST");
    }
}
