package com.spartaclub.orderplatform.domain.user.service; // User 서비스 패키지 선언

import com.spartaclub.orderplatform.domain.user.dto.UserSignupRequestDto; // 회원가입 요청 DTO 임포트
import com.spartaclub.orderplatform.domain.user.dto.UserSignupResponseDto; // 회원가입 응답 DTO 임포트
import com.spartaclub.orderplatform.domain.user.entity.User; // User 엔티티 임포트
import com.spartaclub.orderplatform.domain.user.mapper.UserMapper; // User 매퍼 임포트
import com.spartaclub.orderplatform.domain.user.repository.UserRepository; // User 레포지토리 임포트
import lombok.RequiredArgsConstructor; // Lombok RequiredArgsConstructor 어노테이션
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // BCrypt 암호화 임포트
import org.springframework.stereotype.Service; // Service 어노테이션
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 어노테이션

/**
 * User 서비스 클래스
 * 사용자 관련 비즈니스 로직 처리
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Service // Spring 서비스 컴포넌트로 등록
@RequiredArgsConstructor // Lombok - final 필드에 대한 생성자 자동 생성
@Transactional(readOnly = true) // 기본적으로 읽기 전용 트랜잭션 설정
public class UserService {

    private final UserRepository userRepository; // User 레포지토리 의존성 주입
    private final UserMapper userMapper; // User 매퍼 의존성 주입
    private final BCryptPasswordEncoder passwordEncoder; // BCrypt 암호화 의존성 주입

    /**
     * 회원가입 처리
     * 중복 체크, 비밀번호 암호화, 사용자 생성
     * 
     * @param requestDto 회원가입 요청 데이터
     * @return 회원가입 응답 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 활성화
    public UserSignupResponseDto signup(UserSignupRequestDto requestDto) {
        
        // 1. 중복 데이터 검증
        validateDuplicateData(requestDto); // 이메일, 사용자명, 닉네임, 전화번호, 사업자번호 중복 체크
        
        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword()); // BCrypt로 비밀번호 해싱
        
        // 3. User 엔티티 생성 (MapStruct 사용)
        User user = userMapper.toEntity(requestDto); // DTO를 엔티티로 변환
        user.setPassword(encodedPassword); // 암호화된 비밀번호 별도 설정
        
        // 4. 데이터베이스 저장
        User savedUser = userRepository.save(user); // 사용자 정보 저장
        
        // 5. 응답 DTO 반환
        return UserSignupResponseDto.success(savedUser.getUserId()); // 성공 응답 생성
    }

    /**
     * 중복 데이터 검증
     * 이메일, 사용자명, 닉네임, 전화번호, 사업자번호의 중복 여부 확인
     * 
     * @param requestDto 회원가입 요청 데이터
     * @throws RuntimeException 중복 데이터 발견 시
     */
    private void validateDuplicateData(UserSignupRequestDto requestDto) {
        
        // 이메일 중복 체크
        if (userRepository.existsByEmailAndDeletedAtIsNull(requestDto.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다."); // 이메일 중복 시 예외 발생
        }
        
        // 사용자명 중복 체크
        if (userRepository.existsByUsernameAndDeletedAtIsNull(requestDto.getUserName())) {
            throw new RuntimeException("이미 존재하는 사용자명입니다."); // 사용자명 중복 시 예외 발생
        }
        
        // 닉네임 중복 체크
        if (userRepository.existsByNicknameAndDeletedAtIsNull(requestDto.getNickname())) {
            throw new RuntimeException("이미 존재하는 닉네임입니다."); // 닉네임 중복 시 예외 발생
        }
        
        // 전화번호 중복 체크
        if (userRepository.existsByPhoneNumberAndDeletedAtIsNull(requestDto.getPhoneNumber())) {
            throw new RuntimeException("이미 존재하는 전화번호입니다."); // 전화번호 중복 시 예외 발생
        }
        
        // 사업자번호 중복 체크 (입력된 경우에만)
        if (requestDto.getBusinessNumber() != null && !requestDto.getBusinessNumber().trim().isEmpty()) {
            if (userRepository.existsByBusinessNumberAndDeletedAtIsNull(requestDto.getBusinessNumber())) {
                throw new RuntimeException("이미 존재하는 사업자번호입니다."); // 사업자번호 중복 시 예외 발생
            }
        }
    }

}