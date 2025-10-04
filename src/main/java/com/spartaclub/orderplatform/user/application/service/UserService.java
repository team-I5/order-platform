package com.spartaclub.orderplatform.user.application.service;

import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
import com.spartaclub.orderplatform.user.application.mapper.UserMapper;
import com.spartaclub.orderplatform.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.infrastructure.repository.RefreshTokenRepository;
import com.spartaclub.orderplatform.user.infrastructure.repository.UserRepository;
import com.spartaclub.orderplatform.user.presentation.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * User 서비스 클래스
 * 사용자 관련 비즈니스 로직 처리
 *
 * @author 전우선
 * @date 2025-10-04(토)
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
     * 회원가입 처리
     * 중복 체크, 비밀번호 암호화, 사용자 생성
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

        // 3. User 엔티티 생성
        User user = userMapper.toEntity(requestDto);
        user.setPassword(encodedPassword);

        // 4. 데이터베이스 저장
        User savedUser = userRepository.save(user);

        // 5. 응답 DTO 반환
        return UserSignupResponseDto.success(savedUser.getUserId());
    }

    /**
     * 중복 데이터 검증
     * 이메일, 사용자명, 닉네임, 전화번호, 사업자번호의 중복 여부 확인
     *
     * @param requestDto 회원가입 요청 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    private void validateDuplicateData(UserSignupRequestDto requestDto) {

        if (userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        if (userRepository.existsByUsernameAndDeletedAtIsNull(requestDto.getUsername())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다.");
        }

        if (userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다.");
        }

        if (userRepository.existsByPhoneNumberAndDeletedAtIsNull(requestDto.getPhoneNumber())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다.");
        }

        // 사업자번호 중복 체크 (입력된 경우에만)
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber().trim().isEmpty()) {
            if (userRepository.existsByBusinessNumberAndDeletedAtIsNull(requestDto.getBusinessNumber())) {
                throw new RuntimeException("이미 존재하는 사업자번호입니다.");
            }
        }
    }

    @Transactional
    public UserLoginResponseDto login(UserLoginRequestDto requestDto) {
        // 1. 이메일로 사용자 조회 (탈퇴하지 않은 사용자만)
        User user = userRepository.findByEmailAndDeletedAtIsNull(requestDto.getEmail())
                .orElseThrow(() -> new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // 2. 비밀번호 검증
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("이메일 또는 비밀번호가 일치하지 않습니다.");
        }

        // 3. JWT 토큰 생성
        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
        String refreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // 4. 기존 리프레시 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.deleteByUser(user);

        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(
                jwtUtil.getRefreshTokenExpiration() / 1000);
        RefreshToken refreshTokenEntity = new RefreshToken(refreshToken, user, expiresAt);
        refreshTokenRepository.save(refreshTokenEntity);

        // 5. 응답 DTO 생성
        return new UserLoginResponseDto(accessToken, refreshToken, jwtUtil.getAccessTokenExpirationInSeconds(), user);
    }

    /**
     * 토큰 갱신 처리 (RTR 패턴)
     * 리프레시 토큰을 검증하고 새로운 액세스/리프레시 토큰 발급
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
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 2. 리프레시 토큰인지 확인
        if (!jwtUtil.isRefreshToken(refreshTokenValue)) {
            throw new RuntimeException("유효하지 않은 리프레시 토큰입니다.");
        }

        // 3. DB에서 리프레시 토큰 조회
        RefreshToken refreshTokenEntity = refreshTokenRepository.findByTokenAndDeletedAtIsNull(refreshTokenValue)
                .orElseThrow(() -> new RuntimeException("유효하지 않은 리프레시 토큰입니다."));

        // 4. 토큰 만료 여부 확인
        if (refreshTokenEntity.isExpired()) {
            // 만료된 토큰 삭제
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new RuntimeException("만료된 리프레시 토큰입니다.");
        }

        // 5. 사용자 정보 조회 및 상태 확인
        User user = refreshTokenEntity.getUser();
        if (user.isDeleted()) {
            // 탈퇴한 사용자의 토큰 삭제
            refreshTokenRepository.delete(refreshTokenEntity);
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        // 6. 새로운 토큰 생성
        String newAccessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());
        String newRefreshToken = jwtUtil.createRefreshToken(user.getUserId());

        // 7. RTR 패턴: 기존 리프레시 토큰 삭제 후 새 토큰 저장
        refreshTokenRepository.delete(refreshTokenEntity);

        LocalDateTime newExpiresAt = LocalDateTime.now().plusSeconds(
                jwtUtil.getRefreshTokenExpiration() / 1000);
        RefreshToken newRefreshTokenEntity = new RefreshToken(newRefreshToken, user, newExpiresAt);
        refreshTokenRepository.save(newRefreshTokenEntity);

        // 8. 응답 DTO 생성
        return new TokenRefreshResponseDto(newAccessToken, newRefreshToken,
                jwtUtil.getAccessTokenExpirationInSeconds(), user);
    }

    /**
     * 로그아웃 처리
     * 사용자의 모든 리프레시 토큰을 무효화하여 로그아웃 처리
     * 액세스 토큰은 짧은 만료시간(15분)으로 자연 만료 처리
     *
     * @param userId 로그아웃할 사용자 ID
     * @return 로그아웃 응답 데이터
     */
    @Transactional
    public LogoutResponseDto logout(Long userId) {
        // 1. 사용자 조회 (이미 인증된 사용자이므로 존재함이 보장됨)
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElse(null); // 사용자가 없어도 멱등성을 위해 성공 처리

        // 2. 해당 사용자의 모든 리프레시 토큰 삭제 (토큰 무효화)
        if (user != null) {
            refreshTokenRepository.deleteByUser(user);
        }

        // 3. 성공 응답 반환 (멱등성: 이미 로그아웃된 상태여도 성공)
        return LogoutResponseDto.success();
    }

    /**
     * 회원정보 조회
     * JWT 토큰에서 추출한 사용자 ID로 최신 회원정보 조회
     * 실시간 정보 반영 (권한 변경, 정보 수정 등)
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자 프로필 정보
     * @throws RuntimeException 사용자를 찾을 수 없는 경우
     */
    @Transactional(readOnly = true)
    public UserProfileResponseDto getUserProfile(Long userId) {
        // 1. DB에서 최신 사용자 정보 조회 (실시간 정보 반영)
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 응답 DTO 생성 및 반환 (민감정보 제외)
        return UserProfileResponseDto.from(user);
    }

    /**
     * 회원정보 수정
     * 선택적 필드 수정, 중복 체크, 권한 검증, 비밀번호 변경 처리
     *
     * @param userId     수정할 사용자 ID
     * @param requestDto 수정 요청 데이터
     * @return 수정된 사용자 정보
     * @throws RuntimeException 검증 실패 시
     */
    @Transactional
    public UserUpdateResponseDto updateUserProfile(Long userId, UserUpdateRequestDto requestDto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
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

        // 7. 응답 DTO 생성
        return UserUpdateResponseDto.success(updatedUser);
    }

    /**
     * 비밀번호 변경 검증
     * 새 비밀번호 제공 시 현재 비밀번호 확인 필수
     */
    private void validatePasswordChange(UserUpdateRequestDto requestDto, User user) {
        if (!requestDto.isCurrentPasswordProvided()) {
            throw new RuntimeException("비밀번호 변경 시 현재 비밀번호는 필수입니다.");
        }

        if (!passwordEncoder.matches(requestDto.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("현재 비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 수정 시 중복 데이터 검증
     * 현재 사용자 데이터와 다른 경우에만 중복 체크
     */
    private void validateDuplicateDataForUpdate(UserUpdateRequestDto requestDto, User currentUser) {
        // 사용자명 중복 체크 (변경하려는 경우만)
        if (requestDto.getUsername() != null && !requestDto.getUsername().equals(currentUser.getUsername())) {
            if (userRepository.existsByUsernameAndDeletedAtIsNull(requestDto.getUsername())) {
                throw new RuntimeException("이미 존재하는 사용자명입니다.");
            }
        }

        // 닉네임 중복 체크 (변경하려는 경우만)
        if (requestDto.getNickname() != null && !requestDto.getNickname().equals(currentUser.getNickname())) {
            if (userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())) {
                throw new RuntimeException("이미 존재하는 닉네임입니다.");
            }
        }

        // 연락처 중복 체크 (변경하려는 경우만)
        if (requestDto.getPhoneNumber() != null && !requestDto.getPhoneNumber().equals(currentUser.getPhoneNumber())) {
            if (userRepository.existsByPhoneNumberAndDeletedAtIsNull(requestDto.getPhoneNumber())) {
                throw new RuntimeException("이미 존재하는 전화번호입니다.");
            }
        }

        // 사업자번호 중복 체크 (변경하려는 경우만)
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber().equals(currentUser.getBusinessNumber())) {
            if (userRepository.existsByBusinessNumberAndDeletedAtIsNull(requestDto.getBusinessNumber())) {
                throw new RuntimeException("이미 존재하는 사업자번호입니다.");
            }
        }
    }

    /**
     * 사업자번호 수정 권한 검증
     * CUSTOMER 권한은 사업자번호 수정 불가
     */
    private void validateBusinessNumberPermission(UserUpdateRequestDto requestDto, User user) {
        if (requestDto.getBusinessNumber() != null && user.getRole().name().equals("CUSTOMER")) {
            throw new RuntimeException("CUSTOMER 권한으로는 사업자번호를 수정할 수 없습니다.");
        }
    }

    /**
     * 사용자 필드 선택적 업데이트
     * null이 아닌 필드만 업데이트
     */
    private void updateUserFields(User user, UserUpdateRequestDto requestDto) {
        // 사용자명 업데이트
        if (requestDto.getUsername() != null) {
            user.setUsername(requestDto.getUsername());
        }

        // 닉네임 업데이트
        if (requestDto.getNickname() != null) {
            user.setNickname(requestDto.getNickname());
        }

        // 연락처 업데이트
        if (requestDto.getPhoneNumber() != null) {
            user.setPhoneNumber(requestDto.getPhoneNumber());
        }

        // 사업자번호 업데이트 (권한 검증 완료된 경우)
        if (requestDto.getBusinessNumber() != null) {
            user.setBusinessNumber(requestDto.getBusinessNumber());
        }

        // 비밀번호 업데이트 (암호화 처리)
        if (requestDto.isPasswordChangeRequested()) {
            String encodedPassword = passwordEncoder.encode(requestDto.getNewPassword());
            user.setPassword(encodedPassword);
        }
    }

    /**
     * 회원 탈퇴 처리
     * 비밀번호 확인 후 소프트 삭제 처리 및 토큰 무효화
     *
     * @param userId     탈퇴할 사용자 ID
     * @param requestDto 탈퇴 요청 데이터 (비밀번호)
     * @return 탈퇴 완료 응답 데이터
     * @throws RuntimeException 검증 실패 시
     */
    @Transactional
    public UserDeleteResponseDto deleteUser(Long userId, UserDeleteRequestDto requestDto) {
        // 1. 사용자 조회
        User user = userRepository.findByUserIdAndDeletedAtIsNull(userId)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 2. 이미 탈퇴한 회원인지 확인
        if (user.isDeleted()) {
            throw new RuntimeException("이미 탈퇴한 회원입니다.");
        }

        // 3. 비밀번호 확인 (본인 인증)
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }

        // 4. 소프트 삭제 처리 (deletedAt 설정)
        user.delete(); // BaseEntity의 delete() 메서드 사용

        // 5. 관련 토큰 무효화 (모든 리프레시 토큰 삭제)
        refreshTokenRepository.deleteByUser(user);

        // 6. 변경사항 저장
        User deletedUser = userRepository.save(user);

        // 7. 응답 DTO 생성
        return UserDeleteResponseDto.success(deletedUser.getUserId(), deletedUser.getDeletedAt());
    }
}