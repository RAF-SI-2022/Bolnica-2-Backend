package com.raf.si.userservice.integration;

import com.google.gson.Gson;
import com.raf.si.userservice.dto.request.LoginUserRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

@SpringBootTest
@RunWith(SpringRunner.class)
@AutoConfigureMockMvc
public class AuthControllerITest {

    @Autowired
    private MockMvc mvc;
    private final Gson gson = new Gson();

    @Test
    public void login() throws Exception {
        LoginUserRequest loginUserRequest = new LoginUserRequest();
        loginUserRequest.setUsername("admin");
        loginUserRequest.setPassword("admin");

        mvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON).content(gson.toJson(loginUserRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }
}
