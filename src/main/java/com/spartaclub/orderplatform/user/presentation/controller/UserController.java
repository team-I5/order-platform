package com.spartaclub.orderplatform.user.presentation.controller;

import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.user.application.service.UserService;
import com.spartaclub.orderplatform.user.presentation.dto.*;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * User 컨트롤러 클래스
 * 사용자 관련 API 엔드포인트 제공
 *
 * @author 전우선
 * @date 2025-10-04(토)
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 API
     * POST /v1/users/signup
     * 신규 사용자 계정 생성 및 유효성 검증
     *
     * @param requestDto 회원가입 요청 데이터 (사용자명, 이메일, 비밀번호, 닉네임, 연락처, 권한 등)
     * @return 회원가입 성공 시 생성된 사용자 ID와 성공 메시지 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(@Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = userService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success(responseDto));
    }

    /**
     * 로그인 API
     * POST /v1/users/login
     * 사용자 인증 후 JWT 토큰 발급
     *
     * @param requestDto 로그인 요청 데이터 (이메일, 비밀번호)
     * @param response   HTTP 응답 객체 (Authorization 헤더 설정용)
     * @return 로그인 성공 시 JWT 토큰과 사용자 정보 반환
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(
            @Valid @RequestBody UserLoginRequestDto requestDto,
            HttpServletResponse response) {

        UserLoginResponseDto responseDto = userService.login(requestDto);

        // Authorization 헤더에 Bearer 토큰 추가
        response.setHeader(JwtUtil.AUTHORIZATION_HEADER,
                JwtUtil.BEARER_PREFIX + responseDto.getAccessToken());

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 로그아웃 API
     * POST /v1/users/logout
     * 사용자의 리프레시 토큰을 무효화하여 로그아웃 처리
     * 액세스 토큰은 짧은 만료시간(15분)으로 자연 만료 처리
     *
     * @param userDetails 인증된 사용자 정보 (JWT에서 추출)
     * @return 로그아웃 성공 메시지와 처리 시간
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<LogoutResponseDto>> logout(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 인증된 사용자 ID로 로그아웃 처리
        Long userId = userDetails.getUser().getUserId();
        LogoutResponseDto responseDto = userService.logout(userId);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 회원정보 조회 API
     * GET /v1/users/me
     * 인증된 사용자의 최신 프로필 정보 조회
     * 실시간 정보 반영 (권한 변경, 정보 수정 등)
     *
     * @param userDetails 인증된 사용자 정보 (JWT에서 추출)
     * @return 사용자 프로필 정보 (민감정보 제외)
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserProfileResponseDto>> getUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails) {

        // 인증된 사용자 ID로 최신 프로필 정보 조회
        Long userId = userDetails.getUser().getUserId();
        UserProfileResponseDto responseDto = userService.getUserProfile(userId);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 회원정보 수정 API
     * PUT /v1/users/me
     * 인증된 사용자의 프로필 정보 선택적 수정
     * 비밀번호 변경, 중복 체크, 권한별 제한 처리
     *
     * @param userDetails 인증된 사용자 정보 (JWT에서 추출)
     * @param requestDto  수정할 정보 (선택적 필드)
     * @return 수정된 사용자 정보
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<UserUpdateResponseDto>> updateUserProfile(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserUpdateRequestDto requestDto) {

        // 인증된 사용자 ID로 프로필 정보 수정
        Long userId = userDetails.getUser().getUserId();
        UserUpdateResponseDto responseDto = userService.updateUserProfile(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 회원 탈퇴 API
     * DELETE /v1/users/me
     * 본인 확인 후 소프트 삭제 처리 및 토큰 무효화
     *
     * @param userDetails 인증된 사용자 정보 (JWT에서 추출)
     * @param requestDto  탈퇴 요청 데이터 (비밀번호 확인)
     * @return 탈퇴 완료 메시지와 처리 시간
     */
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<UserDeleteResponseDto>> deleteUser(
            @AuthenticationPrincipal UserDetailsImpl userDetails,
            @Valid @RequestBody UserDeleteRequestDto requestDto) {

        // 인증된 사용자 ID로 회원 탈퇴 처리
        Long userId = userDetails.getUser().getUserId();
        UserDeleteResponseDto responseDto = userService.deleteUser(userId, requestDto);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}