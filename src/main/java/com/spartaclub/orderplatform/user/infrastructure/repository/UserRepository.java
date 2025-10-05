package com.spartaclub.orderplatform.user.infrastructure.repository; // User 레포지토리 패키지 선언

import com.spartaclub.orderplatform.user.domain.entity.User; // User 엔티티 임포트
import org.springframework.data.jpa.repository.JpaRepository; // Spring Data JPA 레포지토리 인터페이스
import org.springframework.stereotype.Repository; // 레포지토리 어노테이션

import java.util.Optional; // Optional 타입 임포트

/**
 * User 엔티티 레포지토리 인터페이스
 * Spring Data JPA 메서드 이름 기반 쿼리 사용으로 간소화
 * 
 * @author 전우선
 * @date 2025-10-01(수)
 */
@Repository // Spring 레포지토리 컴포넌트로 등록
public interface UserRepository extends JpaRepository<User, Long> { // JpaRepository 상속으로 기본 CRUD 메서드 제공

    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    
    Optional<User> findByEmailAndDeletedAtIsNull(String email); // 이메일로 활성 사용자 조회 (로그인용)
    
    Optional<User> findByUserIdAndDeletedAtIsNull(Long userId); // 사용자 ID로 활성 사용자 조회 (JWT 인증용)
    
    Optional<User> findByUsernameAndDeletedAtIsNull(String username); // 사용자명으로 활성 사용자 조회
    
    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname); // 닉네임으로 활성 사용자 조회
    
    Optional<User> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber); // 전화번호로 활성 사용자 조회
    
    Optional<User> findByBusinessNumberAndDeletedAtIsNull(String businessNumber); // 사업자등록번호로 활성 사용자 조회

    // 중복 체크 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    
    boolean existsByEmailAndDeletedAtIsNull(String email); // 이메일 중복 체크
    
    boolean existsByUsernameAndDeletedAtIsNull(String username); // 사용자명 중복 체크
    
    boolean existsByNicknameAndDeletedAtIsNull(String nickname); // 닉네임 중복 체크
    
    boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber); // 전화번호 중복 체크
    
    boolean existsByBusinessNumberAndDeletedAtIsNull(String businessNumber); // 사업자등록번호 중복 체크
}