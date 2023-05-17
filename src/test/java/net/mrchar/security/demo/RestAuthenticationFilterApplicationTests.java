package net.mrchar.security.demo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestAuthenticationFilterApplicationTests {
    private static final String SESSION_ID_TOKEN = "X-Auth-Token";

    @Autowired
    MockMvc mockMvc;
    @Autowired
    SecurityProperties securityProperties;

    @Test
    void contextLoads() {
    }

    @Test
    void login() throws Exception {
        this.securityProperties.getUser().setName("username");
        this.securityProperties.getUser().setPassword("password");

        MvcResult loginFailedResponse = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"invalid\"}"))
                .andDo(print())
                .andExpect(status().isUnauthorized())
                .andReturn();

        Assertions.assertFalse(loginFailedResponse.getResponse()
                .containsHeader(SESSION_ID_TOKEN));

        MvcResult loginSuccessResponse = mockMvc.perform(post("/api/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"username\": \"username\", \"password\": \"password\"}"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();

        Assertions.assertTrue(loginSuccessResponse.getResponse()
                .containsHeader(SESSION_ID_TOKEN));

        String sessionId = loginSuccessResponse.getResponse().getHeader(SESSION_ID_TOKEN);

        mockMvc.perform(get("/api/needAuthenticated")
                        .header(SESSION_ID_TOKEN, "invalidSessionId"))
                .andDo(print())
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/needAuthenticated")
                        .header(SESSION_ID_TOKEN, sessionId))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    void logout() throws Exception {
        mockMvc.perform(
                        post("/api/logout")
                                .with(csrf())
                )
                .andExpect(status().isOk());
    }
}
