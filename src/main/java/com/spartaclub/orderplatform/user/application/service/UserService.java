package com.spartaclub.orderplatform.user.application.service;

import com.spartaclub.orderplatform.user.presentation.dto.UserLoginRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserLoginResponseDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserSignupRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.UserSignupResponseDto;
import com.spartaclub.orderplatform.user.domain.entity.RefreshToken;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.application.mapper.UserMapper;
import com.spartaclub.orderplatform.user.infrastructure.repository.RefreshTokenRepository;
import com.spartaclub.orderplatform.user.infrastructure.repository.UserRepository;
import com.spartaclub.orderplatform.global.application.jwt.JwtUtil;
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
 * @date 2025-10-02(목)
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
}