package net.mrchar.security.demo;

import net.mrchar.security.web.authentication.RestAuthenticationFilterTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RestAuthenticationFilterApplicationTests {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    SecurityProperties securityProperties;

    @Test
    void contextLoads() {
    }

    @Test
    void login() throws Exception {
        new RestAuthenticationFilterTest(mockMvc, securityProperties).login();
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
