package com.spartaclub.orderplatform.domain.user.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.spartaclub.orderplatform.domain.user.application.mapper.UserMapper;
import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.RefreshTokenRepository;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import com.spartaclub.orderplatform.domain.user.exception.UserErrorCode;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserDeleteRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserUpdateRequestDto;
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
 * <p>
 * 사용자 서비스의 비즈니스 로직을 검증하는 단위 테스트 - 회원가입, 로그인, 회원정보 수정, 회원 탈퇴 등의 핵심 기능 테스트 - Mock 객체를 사용하여 외부 의존성을
 * 격리하고 순수한 비즈니스 로직만 테스트
 *
 * @author 전우선
 * @since 2025-10-16(목)
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    // === Mock 객체 정의 ===
    // 데이터 접근 계층 Mock
    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    // 유틸리티 및 매퍼 Mock
    @Mock
    private UserMapper userMapper;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private JwtUtil jwtUtil;

    // 테스트 대상 서비스 (Mock 객체들이 주입됨)
    @InjectMocks
    private UserService userService;

    // === 테스트 데이터 ===
    private User testUser;
    private UserSignupRequestDto signupRequestDto;
    private UserLoginRequestDto loginRequestDto;

    /**
     * 테스트 데이터 초기화 각 테스트 메서드 실행 전에 공통으로 사용할 테스트 데이터를 설정
     */
    @BeforeEach
    void setUp() {
        // 기존 사용자 데이터 (로그인, 수정, 탈퇴 테스트용)
        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPassword("encodedPassword");
        testUser.setNickname("테스터");
        testUser.setPhoneNumber("01012345678");
        testUser.setRole(UserRole.CUSTOMER);

        // 회원가입 요청 데이터
        signupRequestDto = new UserSignupRequestDto();
        signupRequestDto.setUsername("newuser");
        signupRequestDto.setEmail("new@example.com");
        signupRequestDto.setPassword("password123");
        signupRequestDto.setNickname("새사용자");
        signupRequestDto.setPhoneNumber("01087654321");
        signupRequestDto.setRole(UserRole.CUSTOMER);

        // 로그인 요청 데이터
        loginRequestDto = new UserLoginRequestDto();
        loginRequestDto.setEmail("test@example.com");
        loginRequestDto.setPassword("password123");
    }

    // === 회원가입 테스트 ===

    /**
     * 회원가입 성공 테스트
     * <p>
     * 테스트 시나리오: 1. 모든 중복 검증을 통과한 유효한 회원가입 요청 2. 비밀번호 암호화 및 사용자 엔티티 생성 3. 데이터베이스 저장 후 성공 응답 반환
     * <p>
     * 검증 사항: - 성공 메시지와 생성된 사용자 ID 반환 확인 - 데이터베이스 저장 메서드 호출 확인
     */
    @Test
    @DisplayName("회원가입 성공")
    void signup_success() {
        // Given - 중복 검증 모두 통과하도록 Mock 설정
        when(userRepository.existsActiveByEmail(anyString())).thenReturn(false);
        when(userRepository.existsActiveByUsername(anyString())).thenReturn(false);
        when(userRepository.existsActiveByNickname(anyString())).thenReturn(false);
        when(userRepository.existsActiveByPhoneNumber(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(userMapper.toEntity(any(UserSignupRequestDto.class))).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When - 회원가입 서비스 호출
        UserSignupResponseDto result = userService.signup(signupRequestDto);

        // Then - 응답 데이터 및 메서드 호출 검증
        assertThat(result.getMessage()).isEqualTo("회원가입이 완료되었습니다.");
        assertThat(result.getUserId()).isEqualTo(1L);
        verify(userRepository).save(any(User.class)); // 저장 메서드 호출 확인
    }

    /**
     * 회원가입 실패 테스트 - 이메일 중복
     * <p>
     * 테스트 시나리오: 1. 이미 존재하는 이메일로 회원가입 시도 2. 이메일 중복 검증에서 실패 3. DUPLICATE_EMAIL 에러코드 반환
     * <p>
     * 검증 사항: - BusinessException 발생 확인 - 정확한 에러코드 반환 확인
     */
    @Test
    @DisplayName("회원가입 실패 - 이메일 중복")
    void signup_fail_duplicateEmail() {
        // Given - 이메일 중복 상황 Mock 설정
        when(userRepository.existsActiveByEmail(anyString())).thenReturn(true);

        // When & Then - 예외 발생 및 에러코드 검증
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.signup(signupRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_EMAIL);
    }

    /**
     * 회원가입 실패 테스트 - 사용자명 중복
     * <p>
     * 테스트 시나리오: 1. 이메일 검증은 통과하지만 사용자명이 중복인 경우 2. 사용자명 중복 검증에서 실패 3. DUPLICATE_USERNAME 에러코드 반환
     */
    @Test
    @DisplayName("회원가입 실패 - 사용자명 중복")
    void signup_fail_duplicateUsername() {
        // Given - 이메일은 통과, 사용자명은 중복 상황 Mock 설정
        when(userRepository.existsActiveByEmail(anyString())).thenReturn(false);
        when(userRepository.existsActiveByUsername(anyString())).thenReturn(true);

        // When & Then - 예외 발생 및 에러코드 검증
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.signup(signupRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_USERNAME);
    }

    // === 로그인 테스트 ===

    /**
     * 로그인 성공 테스트
     * <p>
     * 테스트 시나리오: 1. 유효한 이메일과 비밀번호로 로그인 시도 2. 사용자 조회 및 비밀번호 검증 성공 3. JWT 토큰 생성 및 리프레시 토큰 저장 4. 기존
     * 리프레시 토큰 삭제 후 새 토큰 저장 (RTR 패턴)
     * <p>
     * 검증 사항: - 액세스 토큰과 리프레시 토큰 반환 확인 - 기존 토큰 삭제 및 새 토큰 저장 확인
     */
    @Test
    @DisplayName("로그인 성공")
    void login_success() {
        // Given - 로그인 성공 시나리오 Mock 설정
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        when(jwtUtil.createAccessToken(any(), anyString(), anyString())).thenReturn("accessToken");
        when(jwtUtil.createRefreshToken(any())).thenReturn("refreshToken");
        when(jwtUtil.getRefreshTokenExpiration()).thenReturn(604800000L); // 7일
        when(jwtUtil.getAccessTokenExpirationInSeconds()).thenReturn(900L); // 15분
        doNothing().when(refreshTokenRepository).deleteByUser(any(User.class));
        when(refreshTokenRepository.save(any(RefreshToken.class))).thenReturn(new RefreshToken());

        // When - 로그인 서비스 호출
        UserLoginResponseDto result = userService.login(loginRequestDto);

        // Then - 토큰 반환 및 저장소 동작 검증
        assertThat(result.getAccessToken()).isEqualTo("accessToken");
        assertThat(result.getRefreshToken()).isEqualTo("refreshToken");
        verify(refreshTokenRepository).deleteByUser(testUser); // 기존 토큰 삭제
        verify(refreshTokenRepository).save(any(RefreshToken.class)); // 새 토큰 저장
    }

    /**
     * 로그인 실패 테스트 - 존재하지 않는 사용자
     * <p>
     * 테스트 시나리오: 1. 등록되지 않은 이메일로 로그인 시도 2. 사용자 조회 실패 3. 보안을 위해 일반적인 에러 메시지 반환
     * <p>
     * 검증 사항: - RuntimeException 발생 확인 - 보안 메시지 반환 확인 (이메일/비밀번호 구분 불가)
     */
    @Test
    @DisplayName("로그인 실패 - 존재하지 않는 사용자")
    void login_fail_userNotFound() {
        // Given - 사용자 조회 실패 상황 Mock 설정
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then - 예외 발생 및 메시지 검증
        RuntimeException exception = assertThrows(
            RuntimeException.class,
            () -> userService.login(loginRequestDto)
        );

        assertThat(exception.getMessage()).contains("이메일 또는 비밀번호가 일치하지 않습니다.");
    }

    /**
     * 로그인 실패 테스트 - 비밀번호 불일치
     * <p>
     * 테스트 시나리오: 1. 유효한 이메일이지만 잘못된 비밀번호로 로그인 시도 2. 사용자 조회는 성공하지만 비밀번호 검증 실패
     * 3.INVALID_LOGIN_CREDENTIALS 에러코드 반환
     * <p>
     * 검증 사항: - BusinessException 발생 확인 - 정확한 에러코드 반환 확인
     */
    @Test
    @DisplayName("로그인 실패 - 비밀번호 불일치")
    void login_fail_passwordMismatch() {
        // Given - 사용자는 존재하지만 비밀번호 불일치 상황 Mock 설정
        when(userRepository.findActiveByEmail(anyString())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then - 예외 발생 및 에러코드 검증
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.login(loginRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
    }

    @Test
    @DisplayName("회원정보 수정 성공")
    void updateUserProfile_success() {
        // Given
        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto();
        updateRequestDto.setNickname("수정된닉네임");
        updateRequestDto.setPhoneNumber("01099998888");

        when(userRepository.findActiveById(any())).thenReturn(Optional.of(testUser));
        when(userRepository.existsActiveByNickname(anyString())).thenReturn(false);
        when(userRepository.existsActiveByPhoneNumber(anyString())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.updateUserProfile(1L, updateRequestDto);

        // Then
        verify(userRepository).save(testUser);
        assertThat(testUser.getNickname()).isEqualTo("수정된닉네임");
        assertThat(testUser.getPhoneNumber()).isEqualTo("01099998888");
    }

    @Test
    @DisplayName("회원정보 수정 실패 - 닉네임 중복")
    void updateUserProfile_fail_duplicateNickname() {
        // Given
        UserUpdateRequestDto updateRequestDto = new UserUpdateRequestDto();
        updateRequestDto.setNickname("중복닉네임");

        when(userRepository.findActiveById(any())).thenReturn(Optional.of(testUser));
        when(userRepository.existsActiveByNickname(anyString())).thenReturn(true);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.updateUserProfile(1L, updateRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.DUPLICATE_NICKNAME);
    }

    @Test
    @DisplayName("회원 탈퇴 성공")
    void deleteUser_success() {
        // Given
        UserDeleteRequestDto deleteRequestDto = new UserDeleteRequestDto();
        deleteRequestDto.setPassword("password123");

        when(userRepository.findActiveById(any())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);
        doNothing().when(refreshTokenRepository).deleteByUser(any(User.class));
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When
        userService.deleteUser(1L, deleteRequestDto);

        // Then
        verify(userRepository).save(testUser);
        verify(refreshTokenRepository).deleteByUser(testUser);
        assertThat(testUser.getDeletedAt()).isNotNull();
    }

    @Test
    @DisplayName("회원 탈퇴 실패 - 비밀번호 불일치")
    void deleteUser_fail_passwordMismatch() {
        // Given
        UserDeleteRequestDto deleteRequestDto = new UserDeleteRequestDto();
        deleteRequestDto.setPassword("wrongPassword");

        when(userRepository.findActiveById(any())).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        BusinessException exception = assertThrows(
            BusinessException.class,
            () -> userService.deleteUser(1L, deleteRequestDto)
        );

        assertThat(exception.getErrorCode()).isEqualTo(UserErrorCode.PASSWORD_MISMATCH);
    }
}