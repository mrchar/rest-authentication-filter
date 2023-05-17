package net.mrchar.security.web.authentication;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class DefaultRestLoginParamsImpl implements RestLoginParams {
    @JsonProperty("username")
    private String username;
    
    @JsonProperty("password")
    private String password;

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }
}
