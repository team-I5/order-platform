package com.spartaclub.orderplatform.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.spartaclub.orderplatform.domain.user.application.mapper.UserMapper;
import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.RefreshTokenRepository;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import com.spartaclub.orderplatform.domain.user.exception.UserErrorCode;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserInfoDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserProfileResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.global.auth.jwt.JwtUtil;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * UserService 단위 테스트
 * - Mock을 활용한 Service 계층 비즈니스 로직 테스트
 * - 의존성 격리를 통한 순수 비즈니스 로직 검증
 * - 예외 상황 및 경계값 테스트
 */
@ExtendWith(MockitoExtension.class)
class UserServiceUnitTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private UserSignupRequestDto signupRequestDto;
    private UserLoginRequestDto loginRequestDto;

    @BeforeEach
    void setUp() {
        testUser = User.createUser(
            "testuser",
            "test@example.com",
            "encodedPassword",
            "testnick",
            "01012345678",
            UserRole.CUSTOMER
        );

        signupRequestDto = new UserSignupRequestDto();
        signupRequestDto.setUsername("testuser");
        signupRequestDto.setEmail("test@example.com");
        signupRequestDto.setPassword("password123");
        signupRequestDto.setNickname("testnick");
        signupRequestDto.setPhoneNumber("01012345678");
        signupRequestDto.setRole(UserRole.CUSTOMER);

        loginRequestDto = new UserLoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password123");
    }

    @Test
    @DisplayName("회원가입 성공 - 일반 사용자")
    void signup_customer_success() {
        // given
        given(userRepository.existsActiveByEmail(anyString())).willReturn(false);
        given(userRepository.existsActiveByUsername(anyString())).willReturn(false);
        given(userRepository.existsActiveByNickname(anyString())).willReturn(false);
        given(userRepository.existsActiveByPhoneNumber(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        UserSignupResponseDto response = userService.signup(signupRequestDto);

        // then
        assertThat(response.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 이메일")
    void signup_duplicateEmail_fail() {
        // given
        given(userRepository.existsActiveByEmail(anyString())).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.signup(signupRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_EMAIL);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 실패 - 중복 사용자명")
    void signup_duplicateUsername_fail() {
        // given
        given(userRepository.existsActiveByEmail(anyString())).willReturn(false);
        given(userRepository.existsActiveByUsername(anyString())).willReturn(true);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.signup(signupRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USERNAME);
        then(userRepository).should(never()).save(any(User.class));
    }

    @Test
    @DisplayName("회원가입 성공 - 비즈니스 사용자")
    void signup_businessUser_success() {
        // given
        signupRequestDto.setRole(UserRole.OWNER);
        signupRequestDto.setBusinessNumber("1234567890");

        given(userRepository.existsActiveByEmail(anyString())).willReturn(false);
        given(userRepository.existsActiveByUsername(anyString())).willReturn(false);
        given(userRepository.existsActiveByNickname(anyString())).willReturn(false);
        given(userRepository.existsActiveByPhoneNumber(anyString())).willReturn(false);
        given(userRepository.existsActiveByBusinessNumber(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        given(userRepository.save(any(User.class))).willReturn(testUser);

        // when
        UserSignupResponseDto response = userService.signup(signupRequestDto);

        // then
        assertThat(response.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
        then(userRepository).should().existsActiveByBusinessNumber("1234567890");
        then(userRepository).should().save(any(User.class));
    }

    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // given
        UserInfoDto userInfo = new UserInfoDto();
        userInfo.setUserId(1L);
        userInfo.setUsername("testuser");
        userInfo.setEmail("test@example.com");

        given(userRepository.findActiveByEmail(anyString())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtUtil.createAccessToken(any(), anyString(), anyString())).willReturn("accessToken");
        given(jwtUtil.createRefreshToken(any())).willReturn("refreshToken");
        given(jwtUtil.getRefreshTokenExpiration()).willReturn(604800000L);
        given(jwtUtil.getAccessTokenExpirationInSeconds()).willReturn(900L);
        given(userMapper.toUserInfo(any(User.class))).willReturn(userInfo);

        // when
        UserLoginResponseDto response = userService.login(loginRequestDto);

        // then
        assertThat(response.getAccessToken()).isEqualTo("accessToken");
        assertThat(response.getRefreshToken()).isEqualTo("refreshToken");
        assertThat(response.getUser().getUsername()).isEqualTo("testuser");
        then(refreshTokenRepository).should().deleteByUser(testUser);
        then(refreshTokenRepository).should().save(any(RefreshToken.class));
    }

    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 이메일")
    void login_userNotFound_fail() {
        // given
        given(userRepository.findActiveByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> userService.login(loginRequestDto));
        then(passwordEncoder).should(never()).matches(anyString(), anyString());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void login_wrongPassword_fail() {
        // given
        given(userRepository.findActiveByEmail(anyString())).willReturn(Optional.of(testUser));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.login(loginRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
        then(jwtUtil).should(never()).createAccessToken(any(), anyString(), anyString());
    }

    @Test
    @DisplayName("사용자 프로필 조회 성공")
    void getUserProfile_success() {
        // given
        Long userId = 1L;
        UserProfileResponseDto profileResponse = new UserProfileResponseDto();
        profileResponse.setUserId(userId);
        profileResponse.setUsername("testuser");

        given(userRepository.findActiveById(userId)).willReturn(Optional.of(testUser));
        given(userMapper.toProfileResponse(testUser)).willReturn(profileResponse);

        // when
        UserProfileResponseDto response = userService.getUserProfile(userId);

        // then
        assertThat(response.getUsername()).isEqualTo("testuser");
        then(userRepository).should().findActiveById(userId);
        then(userMapper).should().toProfileResponse(testUser);
    }

    @Test
    @DisplayName("사용자 프로필 조회 실패 - 사용자 없음")
    void getUserProfile_userNotFound_fail() {
        // given
        Long userId = 999L;
        given(userRepository.findActiveById(userId)).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> userService.getUserProfile(userId));
        then(userMapper).should(never()).toProfileResponse(any(User.class));
    }

    @Test
    @DisplayName("로그아웃 성공")
    void logout_success() {
        // given
        Long userId = 1L;
        given(userRepository.findActiveById(userId)).willReturn(Optional.of(testUser));

        // when
        var response = userService.logout(userId);

        // then
        assertThat(response).isNotNull();
        then(refreshTokenRepository).should().deleteByUser(testUser);
    }

    @Test
    @DisplayName("로그아웃 멱등성 - 사용자가 없어도 성공")
    void logout_userNotFound_idempotent() {
        // given
        Long userId = 999L;
        given(userRepository.findActiveById(userId)).willReturn(Optional.empty());

        // when
        var response = userService.logout(userId);

        // then
        assertThat(response).isNotNull();
        then(refreshTokenRepository).should(never()).deleteByUser(any(User.class));
    }
}