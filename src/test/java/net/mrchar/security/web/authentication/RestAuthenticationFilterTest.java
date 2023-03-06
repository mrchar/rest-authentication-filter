package net.mrchar.security.web.authentication;

import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RestAuthenticationFilterTest {
    private MockMvc mockMvc;
    private SecurityProperties securityProperties;

    public RestAuthenticationFilterTest(MockMvc mockMvc, SecurityProperties securityProperties) {
        this.mockMvc = mockMvc;
        this.securityProperties = securityProperties;
    }

    public void login() throws Exception {
        this.securityProperties.getUser().setName("username");
        this.securityProperties.getUser().setPassword("password");

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"password\"}"))
                .andDo(print())
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"invalid\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/needAuthenticated"))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}