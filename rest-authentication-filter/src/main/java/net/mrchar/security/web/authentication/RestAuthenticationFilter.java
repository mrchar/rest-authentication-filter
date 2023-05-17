package net.mrchar.security.web.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.BufferedReader;
import java.io.IOException;

public class RestAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private static final AntPathRequestMatcher DEFAULT_ANT_PATH_REQUEST_MATCHER = new AntPathRequestMatcher("/api/login", "POST");
    private static final Class<? extends RestLoginParams> DEFAULT_LOGON_PARAMS_TYPE = DefaultRestLoginParamsImpl.class;

    private ObjectMapper mapper = new ObjectMapper();
    private Class<? extends RestLoginParams> loginParamsType = DEFAULT_LOGON_PARAMS_TYPE;

    public RestAuthenticationFilter() {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER);
        this.setAuthenticationSuccessHandler(new RestAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new RestAuthenticationFailureHandler());
    }

    public RestAuthenticationFilter(AuthenticationManager authenticationManager) {
        super(DEFAULT_ANT_PATH_REQUEST_MATCHER, authenticationManager);
        this.setAuthenticationSuccessHandler(new RestAuthenticationSuccessHandler());
        this.setAuthenticationFailureHandler(new RestAuthenticationFailureHandler());
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        if (!request.getContentType().startsWith(MediaType.APPLICATION_JSON_VALUE)) {
            throw new AuthenticationServiceException("Authentication content type not supported:" + request.getContentType());
        }

        String username = "";
        String password = "";
        try {
            BufferedReader reader = request.getReader();
            RestLoginParams restLoginParams = mapper.readValue(reader, loginParamsType);
            if (restLoginParams != null) {
                username = restLoginParams.getUsername().trim();
                password = restLoginParams.getPassword();
            }
        } catch (IOException e) {
            throw new AuthenticationServiceException("Authentication request payload is empty");
        }

        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        setDetails(request, authRequest);
        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    public Class<? extends RestLoginParams> loginParamsType() {
        return this.loginParamsType;
    }

    public void setLoginParamsType(Class<? extends RestLoginParams> loginParamsType) {
        this.loginParamsType = loginParamsType;
    }

    public void setMapper(ObjectMapper mapper) {
        this.mapper = mapper;
    }
}
