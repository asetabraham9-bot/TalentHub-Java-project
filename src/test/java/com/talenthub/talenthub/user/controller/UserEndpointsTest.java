package com.talenthub.talenthub.user.controller;

import com.talenthub.talenthub.user.repository.InMemoryUserRepository;
import com.talenthub.talenthub.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class UserEndpointsTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() throws Exception {
        UserService service = new UserService(new InMemoryUserRepository(), new BCryptPasswordEncoder());
        mockMvc = MockMvcBuilders
                .standaloneSetup(new AuthController(service), new UserController(service))
                .build();

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {"fullName":"Abel Tesfaye","email":"abel@mail.com","password":"secret123","role":"TALENT"}
                        """));
    }

    @Test
    void getUserReturns200() throws Exception {
        mockMvc.perform(get("/api/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("abel@mail.com"));
    }

    @Test
    void getUnknownUserReturns404() throws Exception {
        mockMvc.perform(get("/api/users/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void loginWithCorrectPasswordReturns200() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"abel@mail.com\",\"password\":\"secret123\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void loginWithWrongPasswordReturns401() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"abel@mail.com\",\"password\":\"nope\"}"))
                .andExpect(status().isUnauthorized());
    }
}