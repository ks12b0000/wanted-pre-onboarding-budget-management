package com.wanted.budgetmanagement.api.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wanted.budgetmanagement.api.user.dto.UserSignUpRequest;
import com.wanted.budgetmanagement.domain.user.entity.User;
import com.wanted.budgetmanagement.domain.user.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class UserControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder encoder;

    @DisplayName("테스트에 필요한 User 저장")
    void before() {
        userRepository.save(User.builder()
                .email("email@gmail.com")
                .password(encoder.encode("password123"))
                .build());
    }

    @DisplayName("유저 회원가입 성공")
    @Test
    void userSignUp() throws Exception {
        // given
        UserSignUpRequest request = new UserSignUpRequest("email@gmail.com", "password123");
        String content = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions resultActions = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        );

        // then
        resultActions.andExpect(status().isCreated())
                .andExpect(jsonPath("code").value("201"))
                .andExpect(jsonPath("message").value("유저 회원가입에 성공했습니다."));
    }

    @DisplayName("유저 회원가입 실패")
    @Test
    void userSignUpFail() throws Exception {
        // 테스트에 필요한 유저 저장
        before();
        // given
        UserSignUpRequest request = new UserSignUpRequest("email@gmail.com", "password123");
        String content = new ObjectMapper().writeValueAsString(request);

        // when
        ResultActions resultActions = mvc.perform(post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON)
                .characterEncoding("UTF-8")
        );

        // then
        resultActions.andExpect(status().isConflict())
                .andExpect(jsonPath("code").value("409"))
                .andExpect(jsonPath("message").value("중복된 이메일이 있습니다."));
    }
}