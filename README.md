# Rest Authentication Filter

这个项目实现了一个简单的Rest风格的SecurityFilter，适合用于只有Rest服务的后端程序。

借助RestLoginConfigurer可以实现快捷的配置。

## 快速开始

配置方法请参考`net.mrchar.security.demo`中的`SecurityConfiguration`。

```java

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

        http.exceptionHandling(configurer -> {
            configurer.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED));
            configurer.accessDeniedHandler(new RestAccessDeniedHandler());
        });

        return http.build();
    }
}
```

您也可以自定义`RestLoginParams`配置登录请求的参数。

如果需要自定义响应内容，请自行实现`AuthenticationSuccessHandler` 和`AuthenticationFailureHandler`
，并通过`RestLoginConfigurer`进行配置。

## 开发进度

```catalpa
Planing:
发布到Maven仓库

Done:
实现RestAuthenticationFilter
实现RestLoginConfigurer
实现RestAccessDeniedHandler
```