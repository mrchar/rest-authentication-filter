package net.mrchar.security.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.MapSession;
import org.springframework.session.MapSessionRepository;
import org.springframework.session.Session;
import org.springframework.session.SessionRepository;
import org.springframework.session.config.annotation.web.http.EnableSpringHttpSession;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableSpringHttpSession
public class HttpSessionConfiguration {
    private final Map<String, Session> sessionMap = new HashMap<>();

    @Bean
    public SessionRepository<MapSession> sessionRepository() {
        return new MapSessionRepository(sessionMap);
    }

    @Bean
    public HeaderHttpSessionIdResolver headerHttpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }
}
