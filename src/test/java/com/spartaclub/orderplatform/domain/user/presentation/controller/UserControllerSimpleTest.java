package com.spartaclub.orderplatform.domain.user.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.user.application.service.UserService;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserInfoDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.global.auth.jwt.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * UserController 간단한 테스트 - 인증이 필요없는 기본 API들만 테스트
 */
@SpringBootTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerSimpleTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtUtil jwtUtil;

    private UserSignupRequestDto signupRequestDto;
    private UserLoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        signupRequestDto = new UserSignupRequestDto();
        signupRequestDto.setUsername("testuser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setPassword("Password123!");
        signupRequestDto.setNickname("testnick");
        signupRequestDto.setPhoneNumber("01012345678");
        signupRequestDto.setRole(UserRole.CUSTOMER);

        loginRequestDto = new UserLoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("Password123!");
    }

    @Test
    @DisplayName("회원가입 API 성공")
    void signup_success() throws Exception {
        // given
        UserSignupResponseDto responseDto = new UserSignupResponseDto("회원가입이 완료되었습니다.", 1L);
        given(userService.signup(any(UserSignupRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
            .andDo(print())
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.message").value("회원가입이 완료되었습니다."))
            .andExpect(jsonPath("$.data.userId").value(1));
    }

    @Test
    @DisplayName("로그인 API 성공")
    void login_success() throws Exception {
        // given
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setUserId(1L);
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");

        UserLoginResponseDto responseDto = new UserLoginResponseDto(
            "accessToken", "refreshToken", 900L, userInfo
        );
        given(userService.login(any(UserLoginRequestDto.class))).willReturn(responseDto);

        // when & then
        mockMvc.perform(post("/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestDto)))
            .andDo(print())
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.accessToken").value("accessToken"))
            .andExpect(jsonPath("$.data.refreshToken").value("refreshToken"));
    }

    @Test
    @DisplayName("회원가입 API 실패 - 유효성 검증 오류")
    void signup_validation_fail() throws Exception {
        // given
        signupRequestDto.setEmail("invalid-email"); // 잘못된 이메일 형식

        // when & then
        mockMvc.perform(post("/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(signupRequestDto)))
            .andDo(print())
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("UserController가 올바르게 로드되는지 확인")
    void contextLoads() {
        // UserController가 Spring 컨텍스트에서 제대로 로드되는지만 확인
    }
}