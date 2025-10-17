package com.spartaclub.orderplatform.domain.user.application.service;

import com.spartaclub.orderplatform.global.auth.jwt.JwtUtil;
import com.spartaclub.orderplatform.domain.user.application.mapper.UserMapper;
import com.spartaclub.orderplatform.domain.user.exception.UserErrorCode;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import com.spartaclub.orderplatform.domain.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.RefreshTokenRepository;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import com.spartaclub.orderplatform.domain.user.presentation.dto.LogoutResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.ManagerCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.ManagerCreateResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.TokenRefreshRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.TokenRefreshResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserDeleteRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserDeleteResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserInfoDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserListPageResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserListRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserListResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserProfileResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserUpdateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.UserUpdateResponseDto;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * User 서비스 클래스 사용자 관련 비즈니스 로직 처리
 *
 * @author 전우선
 * @date 2025-10-09(목)
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserMapper userMapper;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    /**
     * 회원가입 처리 중복 체크, 비밀번호 암호화, 사용자 생성
     *
     * @param requestDto 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    @Transactional
    public UserSignupResponseDto signup(UserSignupRequestDto requestDto) {

        // 1. 중복 데이터 검증
        validateDuplicateData(requestDto);

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. User 엔티티 생성 (정적 팩터리 메서드 사용)
        User user;
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber().trim().isEmpty()) {
            // 사업자번호가 있는 경우 (OWNER 등)
            user = User.createBusinessUser(
                requestDto.getUsername(),
                requestDto.getEmail(), 
                encodedPassword,
                requestDto.getNickname(),
                requestDto.getPhoneNumber(),
                requestDto.getRole(),
                requestDto.getBusinessNumber()
            );
        } else {
            // 일반 사용자
            user = User.createUser(
                requestDto.getUsername(),
                requestDto.getEmail(),
                encodedPassword,
                requestDto.getNickname(),
                requestDto.getPhoneNumber(),
                requestDto.getRole()
            );
        }

        // 4. 데이터베이스 저장
        User savedUser = userRepository.save(user);

        // 5. 응답 DTO 반환
        return new UserSignupResponseDto("회원가입이 완료되었습니다.", savedUser.getUserId());
    }

    /**
     * 중복 데이터 검증 이메일, 사용자명, 닉네임, 전화번호, 사업자번호의 중복 여부 확인
     *
     * @param requestDto 회원가입 요청 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    private void validateDuplicateData(UserSignupRequestDto requestDto) {

        if (userRepository.existsActiveByEmail(requestDto.getEmail())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsActiveByUsername(requestDto.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.existsActiveByNickname(requestDto.getNickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (userRepository.existsActiveByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_PHONE_NUMBER);
        }

        // 사업자번호 중복 체크 (입력된 경우에만)
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber().trim()
            .isEmpty()) {
            if (userRepository.existsActiveByBusinessNumber(requestDto.getBusinessNumber())) {
                throw new BusinessException(UserErrorCode.DUPLICATE_BUSINESS_NUMBER);
            }
        }
    }

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        // 1. 이메일로 사용자 조회 (탈퇴하지 않은 사용자만)
        User user = userRepository.findActiveByEmail(requestDto.getEmail())
            .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.INVALID_LOGIN_CREDENTIALS);
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(),
            user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // 4. 기존 리프레시 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByUser(user);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
            jwtUtil.getRefreshTokenExpiration() / 1000);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, user, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity);

        // 5. MapStruct를 사용한 응답 DTO 생성
        UserInfoDto userInfo = userMapper.toUserInfo(user);
        return new UserLoginResponseDto(accessToken, refreshToken,
            jwtUtil.getAccessTokenExpirationInSeconds(), userInfo);
    }

    /**
     * 토큰 갱신 처리 (RTR 패턴) 리프레시 토큰을 검증하고 새로운 액세스/리프레시 토큰 발급
     *
     * @param requestDto 토큰 갱신 요청 데이터
     * @return 토큰 갱신 응답 데이터
     * @throws RuntimeException 토큰 검증 실패 시
     */
    @Transactional
    public TokenRefreshResponseDto refreshToken(TokenRefreshRequestDto requestDto) {
        String refreshTokenValue = requestDto.getRefreshToken();

        // 1. 리프레시 토큰 유효성 검증
        if (!jwtUtil.validateToken(refreshTokenValue)) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 2. 리프레시 토큰인지 확인
        if (!jwtUtil.isRefreshToken(refreshTokenValue)) {
            throw new BusinessException(UserErrorCode.INVALID_REFRESH_TOKEN);
        }

        // 3. DB에서 리프레시 토큰 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findActiveByToken(
                refreshTokenValue)
            .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        // 4. 토큰 만료 여부 확인
        if (refreshTokenEntity.isExpired()) {
            // 만료된 토큰 삭제
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new BusinessException(UserErrorCode.EXPIRED_REFRESH_TOKEN);
        }

        // 5. 사용자 정보 조회 및 상태 확인
        User user = refreshTokenEntity.getUser();
        if (user.isDeleted()) {
            // 탈퇴한 사용자의 토큰 삭제
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new BusinessException(UserErrorCode.USER_NOT_FOUND);
        }

        // 6. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(),
            user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // 7. RTR 패턴: 기존 리프레시 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.delete(refreshTokenEntity);

        LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(
            jwtUtil.getRefreshTokenExpiration() / 1000);
        RefreshToken newRefreshTokenEntity = new RefreshToken(newRefreshToken, user, newExpiresAt);
        refreshTokenRepository.save(newRefreshTokenEntity);

        // 8. MapStruct를 사용한 응답 DTO 생성
        UserInfoDto userInfo = userMapper.toUserInfo(user);
        return new TokenRefreshResponseDto(newAccessToken, newRefreshToken,
            jwtUtil.getAccessTokenExpirationInSeconds(), userInfo);
    }

    /**
     * 로그아웃 처리 사용자의 모든 리프레시 토큰을 무효화하여 로그아웃 처리 액세스 토큰은 짧은 만료시간(15분)으로 자연 만료 처리
     *
     * @param userId 로그아웃할 사용자 ID
     * @return 로그아웃 응답 데이터
     */
    @Transactional
    public LogoutResponseDto logout(Long userId) {
        // 1. 사용자 조회 (이미 인증된 사용자이므로 존재함이 보장됨)
        User user = userRepository.findActiveById(userId)
            .orElse(null); // 사용자가 없어도 멱등성을 위해 성공 처리

        // 2. 해당 사용자의 모든 리프레시 토큰 삭제 (토큰 무효화)
        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }

        // 3. 성공 응답 반환 (멱등성: 이미 로그아웃된 상태여도 성공)
        return new LogoutResponseDto();
    }

    /**
     * 회원정보 조회 JWT 토큰에서 추출한 사용자 ID로 최신 회원정보 조회 실시간 정보 반영 (권한 변경, 정보 수정 등)
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        // 1. DB에서 최신 사용자 정보 조회 (실시간 정보 반영)
        User user = userRepository.findActiveById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. MapStruct를 사용한 응답 DTO 생성 및 반환 (민감정보 제외)
        return userMapper.toProfileResponse(user);
    }

    /**
     * 회원정보 수정 선택적 필드 수정, 중복 체크, 권한 검증, 비밀번호 변경 처리
     *
     * @param userId     수정할 사용자 ID
     * @param requestDto 수정 요청 데이터
     * @return 수정된 사용자 정보
     * @throws RuntimeException 검증 실패 시
     */
    @Transactional
    public UserUpdateResponseDto updateUserProfile(Long userId, UserUpdateRequestDto requestDto) {
        // 1. 사용자 조회
        User user = userRepository.findActiveById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 변경 검증
        if (requestDto.isPasswordChangeRequested()) {
            validatePasswordChange(requestDto, user);
        }

        // 3. 중복 데이터 검증 (변경하려는 필드만)
        validateDuplicateDataForUpdate(requestDto, user);

        // 4. 권한별 제한 검증
        validateBusinessNumberPermission(requestDto, user);

        // 5. 필드별 선택적 업데이트
        updateUserFields(user, requestDto);

        // 6. 변경사항 저장
        User updatedUser = userRepository.save(user);

        // 7. MapStruct를 사용한 응답 DTO 생성
        return userMapper.toUpdateResponse(updatedUser);
    }

    /**
     * 비밀번호 변경 검증 새 비밀번호 제공 시 현재 비밀번호 확인 필수
     */
    private void validatePasswordChange(UserUpdateRequestDto requestDto, User user) {
        if (!requestDto.isCurrentPasswordProvided()) {
            throw new BusinessException(UserErrorCode.CURRENT_PASSWORD_REQUIRED);
        }

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.CURRENT_PASSWORD_MISMATCH);
        }
    }

    /**
     * 수정 시 중복 데이터 검증 현재 사용자 데이터와 다른 경우에만 중복 체크
     */
    private void validateDuplicateDataForUpdate(UserUpdateRequestDto requestDto, User currentUser) {
        // 사용자명 중복 체크 (변경하려는 경우만)
        if (requestDto.getUsername() != null && !requestDto.getUsername()
            .equals(currentUser.getUsername())) {
            if (userRepository.existsActiveByUsername(requestDto.getUsername())) {
                throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
            }
        }

        // 닉네임 중복 체크 (변경하려는 경우만)
        if (requestDto.getNickname() != null && !requestDto.getNickname()
            .equals(currentUser.getNickname())) {
            if (userRepository.existsActiveByNickname(requestDto.getNickname())) {
                throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
            }
        }

        // 연락처 중복 체크 (변경하려는 경우만)
        if (requestDto.getPhoneNumber() != null && !requestDto.getPhoneNumber()
            .equals(currentUser.getPhoneNumber())) {
            if (userRepository.existsActiveByPhoneNumber(requestDto.getPhoneNumber())) {
                throw new BusinessException(UserErrorCode.DUPLICATE_PHONE_NUMBER);
            }
        }

        // 사업자번호 중복 체크 (변경하려는 경우만)
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber()
            .equals(currentUser.getBusinessNumber())) {
            if (userRepository.existsActiveByBusinessNumber(
                requestDto.getBusinessNumber())) {
                throw new BusinessException(UserErrorCode.DUPLICATE_BUSINESS_NUMBER);
            }
        }
    }

    /**
     * 사업자번호 수정 권한 검증 CUSTOMER 권한은 사업자번호 수정 불가
     */
    private void validateBusinessNumberPermission(UserUpdateRequestDto requestDto, User user) {
        if (requestDto.getBusinessNumber() != null && user.getRole().name().equals("CUSTOMER")) {
            throw new BusinessException(UserErrorCode.INSUFFICIENT_PERMISSION_FOR_BUSINESS_NUMBER);
        }
    }

    /**
     * 사용자 필드 선택적 업데이트 null이 아닌 필드만 업데이트
     */
    private void updateUserFields(User user, UserUpdateRequestDto requestDto) {
        // 비밀번호 암호화 처리
        String encodedPassword = null;
        if (requestDto.isPasswordChangeRequested()) {
            encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
        }

        // User 엔티티의 updateProfile 메서드 사용
        user.updateProfile(
            requestDto.getUsername(),
            requestDto.getNickname(),
            requestDto.getPhoneNumber(),
            requestDto.getBusinessNumber(),
            encodedPassword
        );
    }

    /**
     * 회원 탈퇴 처리 비밀번호 확인 후 소프트 삭제 처리 및 토큰 무효화
     *
     * @param userId     탈퇴할 사용자 ID
     * @param requestDto 탈퇴 요청 데이터 (비밀번호)
     * @return 탈퇴 완료 응답 데이터
     * @throws RuntimeException 검증 실패 시
     */
    @Transactional
    public UserDeleteResponseDto deleteUser(Long userId, UserDeleteRequestDto requestDto) {
        // 1. 사용자 조회
        User user = userRepository.findActiveById(userId)
            .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 비밀번호 확인 (본인 인증)
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new BusinessException(UserErrorCode.PASSWORD_MISMATCH);
        }

        // 3. 소프트 삭제 처리 (deletedAt 설정)
        user.delete(userId); // BaseEntity의 deleteProductOptionGroup() 메서드 사용

        // 4. 관련 토큰 무효화 (모든 리프레시 토큰 삭제)
        refreshTokenRepository.deleteByUser(user);

        // 5. 변경사항 저장
        User deletedUser = userRepository.save(user);

        // 6. 응답 DTO 생성
        return new UserDeleteResponseDto(deletedUser.getUserId(), deletedUser.getDeletedAt());
    }

    /**
     * 회원 전체 조회 (관리자용) 검색, 필터링, 정렬, 페이징 지원 통계 정보 포함
     *
     * @param requestDto 검색/필터링 조건
     * @param pageable   페이징/정렬 정보
     * @return 회원 목록과 통계 정보
     */
    @Transactional(readOnly = true)
    public UserListPageResponseDto getAllUsers(UserListRequestDto requestDto, int page, int size, String sortBy, boolean ascending) {
        // 1. 날짜 범위 조건 변환 (LocalDate -> LocalDateTime)
        LocalDateTime startDateTime = requestDto.getStartDate() != null ?
            requestDto.getStartDate().atStartOfDay() : null;
        LocalDateTime endDateTime = requestDto.getEndDate() != null ?
            requestDto.getEndDate().atTime(23, 59, 59) : null;

        // 2. 페이징 요청 객체 생성
        UserRepository.UserPageRequest pageRequest = new UserRepository.UserPageRequest(page, size, sortBy, ascending);
        
        // 3. 조건별 회원 목록 조회
        UserRepository.UserPage userPage;

        if (requestDto.getRole() != null) {
            // 권한별 조회
            if (Boolean.TRUE.equals(requestDto.getIncludeDeleted())) {
                userPage = userRepository.findUsersByRole(requestDto.getRole(), pageRequest);
            } else {
                userPage = userRepository.findActiveUsersByRole(requestDto.getRole(), pageRequest);
            }
        } else {
            // 전체 조회
            if (Boolean.TRUE.equals(requestDto.getIncludeDeleted())) {
                userPage = userRepository.findAllUsers(pageRequest);
            } else {
                userPage = userRepository.findActiveUsers(pageRequest);
            }
        }

        // 4. User -> UserListResponseDto 변환
        List<UserListResponseDto> userList = userPage.content().stream()
            .map(userMapper::toListResponse)
            .collect(Collectors.toList());

        // 5. 통계 정보 계산 (별도 메서드로 분리)
        UserListPageResponseDto.SummaryInfo summaryInfo = calculateUserStatistics();

        // 6. Mapper를 통한 응답 DTO 생성 - 도메인 UserPage를 Spring Page로 변환
        return userMapper.toPageResponse(userList, convertToSpringPage(userPage), summaryInfo);
    }

    /**
     * 사용자 통계 정보 계산 전체/활성/삭제 사용자 수와 권한별 분포를 계산
     *
     * @return 통계 정보 DTO
     */
    private UserListPageResponseDto.SummaryInfo calculateUserStatistics() {
        // 기본 통계 정보 조회
        long totalUsers = userRepository.countAllUsers();
        long activeUsers = userRepository.countActiveUsers();
        long deletedUsers = userRepository.countDeletedUsers();

        // 권한별 분포 계산
        Map<String, Long> roleDistribution = calculateRoleDistribution();

        // Mapper를 통한 SummaryInfo 생성
        return userMapper.toSummaryInfo(totalUsers, activeUsers, deletedUsers, roleDistribution);
    }

    /**
     * 권한별 사용자 분포 계산 활성 사용자들의 권한별 개수를 계산
     *
     * @return 권한별 사용자 수 맵
     */
    private Map<String, Long> calculateRoleDistribution() {
        Map<UserRole, Long> roleCount = userRepository.countActiveUsersByRole();
        Map<String, Long> roleDistribution = new HashMap<>();
        
        for (Map.Entry<UserRole, Long> entry : roleCount.entrySet()) {
            roleDistribution.put(entry.getKey().name(), entry.getValue());
        }

        return roleDistribution;
    }

    /**
     * 도메인 UserPage를 Spring Page로 변환하는 유틸리티 메서드
     * UserMapper가 Spring Page를 기대하는 경우를 위한 임시 변환
     */
    private org.springframework.data.domain.Page<User> convertToSpringPage(UserRepository.UserPage userPage) {
        return new org.springframework.data.domain.PageImpl<>(
            userPage.content(),
            org.springframework.data.domain.PageRequest.of(userPage.page(), userPage.size()),
            userPage.totalElements()
        );
    }

    /**
     * 관리자 계정 생성 (MASTER 전용) MASTER 권한 사용자가 MANAGER 계정을 생성
     *
     * @param requestDto  관리자 생성 요청 데이터
     * @param masterEmail 생성을 요청한 MASTER의 이메일
     * @return 관리자 생성 응답 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    @Transactional
    public ManagerCreateResponseDto createManager(ManagerCreateRequestDto requestDto,
        String masterEmail) {

        // 1. 중복 데이터 검증
        validateDuplicateDataForManager(requestDto);

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        // 3. User 엔티티 생성 (정적 팩터리 메서드 사용)
        User user = User.createManager(
            requestDto.getUsername(),
            requestDto.getEmail(),
            encodedPassword,
            requestDto.getNickname(),
            requestDto.getPhoneNumber()
        );

        // 4. 데이터베이스 저장
        User savedUser = userRepository.save(user);

        // 5. 응답 DTO 생성 (Builder 패턴)
        return ManagerCreateResponseDto.builder()
            .message("관리자 계정이 생성되었습니다.")
            .user(userMapper.toManagerUserInfo(savedUser))
            .createdBy(masterEmail)
            .build();
    }

    /**
     * 관리자 생성 시 중복 데이터 검증 이메일, 사용자명, 닉네임, 전화번호의 중복 여부 확인
     *
     * @param requestDto 관리자 생성 요청 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    private void validateDuplicateDataForManager(ManagerCreateRequestDto requestDto) {

        if (userRepository.existsActiveByEmail(requestDto.getEmail())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_EMAIL);
        }

        if (userRepository.existsActiveByUsername(requestDto.getUsername())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_USERNAME);
        }

        if (userRepository.existsActiveByNickname(requestDto.getNickname())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_NICKNAME);
        }

        if (userRepository.existsActiveByPhoneNumber(requestDto.getPhoneNumber())) {
            throw new BusinessException(UserErrorCode.DUPLICATE_PHONE_NUMBER);
        }
    }
}