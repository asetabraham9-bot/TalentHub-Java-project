package com.talenthub.talenthub.user.controller;

import com.talenthub.talenthub.user.repository.InMemoryUserRepository;
import com.talenthub.talenthub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        UserService service = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder());
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthController(service)).build();
    }

    @Test
    void registerReturns201AndHidesPassword() throws Exception {
        String body = """
                {"fullName":"Abel Tesfaye","email":"abel@mail.com","password":"secret123","role":"TALENT"}
                """;

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.email").value("abel@mail.com"))
                .andExpect(jsonPath("$.status").value("ACTIVE"))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }
}