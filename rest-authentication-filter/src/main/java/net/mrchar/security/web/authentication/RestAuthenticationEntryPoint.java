package net.mrchar.security.web.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {
    private static final ObjectWriter objectWriter = new ObjectMapper().writer();

    @Data
    @AllArgsConstructor
    public static class AuthenticationEntryPointResponse {
        private String message;
        private String error;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8).toString());
        String resString = objectWriter.writeValueAsString(
                new AuthenticationEntryPointResponse(
                        "当前用户无权执行该操作",
                        authException.getMessage()
                )
        );
        response.getWriter().write(resString);
        response.getWriter().flush();
    }
}
