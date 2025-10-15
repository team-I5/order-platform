package com.spartaclub.orderplatform.domain.user.presentation.controller;

import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.global.auth.jwt.JwtUtil;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.domain.user.application.service.UserService;
import com.spartaclub.orderplatform.domain.user.presentation.dto.LogoutResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.ManagerCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.ManagerCreateResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserDeleteRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserDeleteResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserListPageResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserListRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserProfileResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserUpdateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserUpdateResponseDto;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User 컨트롤러 클래스 사용자 관련 API 엔드포인트 제공
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 API POST /v1/users/signup 신규 사용자 계정 생성 및 유효성 검증
     *
     * @param requestDto 회원가입 요청 데이터 (사용자명, 이메일, 비밀번호, 닉네임, 연락처, 권한 등)
     * @return 회원가입 성공 시 생성된 사용자 ID와 성공 메시지 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<UserSignupResponseDto>> signup(
        @Valid @RequestBody UserSignupRequestDto requestDto) {
        UserSignupResponseDto responseDto = userService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * 로그인 API POST /v1/users/login 사용자 인증 후 JWT 토큰 발급
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
     * 로그아웃 API POST /v1/users/logout 사용자의 리프레시 토큰을 무효화하여 로그아웃 처리 액세스 토큰은 짧은 만료시간(15분)으로 자연 만료 처리
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
     * 회원정보 조회 API GET /v1/users/me 인증된 사용자의 최신 프로필 정보 조회 실시간 정보 반영 (권한 변경, 정보 수정 등)
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
     * 회원정보 수정 API PUT /v1/users/me 인증된 사용자의 프로필 정보 선택적 수정 비밀번호 변경, 중복 체크, 권한별 제한 처리
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
     * 회원 탈퇴 API DELETE /v1/users/me 본인 확인 후 소프트 삭제 처리 및 토큰 무효화
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

    /**
     * 회원 전체 조회 API (관리자용) 검색, 필터링, 정렬, 페이징 지원 MANAGER, MASTER 권한만 접근 가능
     *
     * @param requestDto 검색/필터링 조건
     * @param pageable   페이징/정렬 정보
     * @return 회원 목록과 통계 정보
     */
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'MASTER')")
    public ResponseEntity<ApiResponse<UserListPageResponseDto>> getAllUsers(
        @ModelAttribute UserListRequestDto requestDto,
        @PageableDefault(size = 10)
        Pageable pageable) {

        // 페이지 크기 검증 (최대 50)
        if (pageable.getPageSize() > 50) {
            throw new RuntimeException("페이지 크기는 1~50 사이여야 합니다.");
        }

        // 정렬 정보 추출
        String sortBy = "createdAt"; // 기본 정렬 필드
        boolean ascending = false; // 기본 내림차순
        
        if (pageable.getSort().isSorted()) {
            sortBy = pageable.getSort().iterator().next().getProperty();
            ascending = pageable.getSort().iterator().next().isAscending();
        }

        UserListPageResponseDto responseDto = userService.getAllUsers(
            requestDto, 
            pageable.getPageNumber(), 
            pageable.getPageSize(), 
            sortBy, 
            ascending
        );

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 관리자 계정 생성 API (MASTER 전용) MASTER 권한 사용자만 MANAGER 계정을 생성할 수 있음
     *
     * @param userDetails 인증된 MASTER 사용자 정보
     * @param requestDto  관리자 생성 요청 데이터
     * @return 생성된 관리자 정보와 생성자 정보
     */
    @PostMapping("/manager")
    @PreAuthorize("hasRole('MASTER')")
    public ResponseEntity<ApiResponse<ManagerCreateResponseDto>> createManager(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody ManagerCreateRequestDto requestDto) {

        // 인증된 MASTER 사용자의 이메일 추출
        String masterEmail = userDetails.getUser().getEmail();

        // 관리자 계정 생성
        ManagerCreateResponseDto responseDto = userService.createManager(requestDto, masterEmail);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }
}