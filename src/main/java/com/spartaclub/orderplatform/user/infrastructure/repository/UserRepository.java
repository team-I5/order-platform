package com.spartaclub.orderplatform.user.infrastructure.repository; // User 레포지토리 패키지 선언

import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.domain.entity.UserRole;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * User 엔티티 레포지토리 인터페이스 Spring Data JPA 메서드 이름 기반 쿼리 사용으로 간소화
 *
 * @author 전우선
 * @date 2025-10-08(수)
 */
@Repository // Spring 레포지토리 컴포넌트로 등록
public interface UserRepository extends JpaRepository<User, Long>,
    JpaSpecificationExecutor<User> { // JpaRepository 상속으로 기본 CRUD 메서드 제공

    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성

    Optional<User> findByEmailAndDeletedAtIsNull(String email); // 이메일로 활성 사용자 조회 (로그인용)

    Optional<User> findByUserIdAndDeletedAtIsNull(Long userId); // 사용자 ID로 활성 사용자 조회 (JWT 인증용)

    Optional<User> findByUsernameAndDeletedAtIsNull(String username); // 사용자명으로 활성 사용자 조회

    Optional<User> findByNicknameAndDeletedAtIsNull(String nickname); // 닉네임으로 활성 사용자 조회

    Optional<User> findByPhoneNumberAndDeletedAtIsNull(String phoneNumber); // 전화번호로 활성 사용자 조회

    Optional<User> findByBusinessNumberAndDeletedAtIsNull(
        String businessNumber); // 사업자등록번호로 활성 사용자 조회

    // 중복 체크 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성

    boolean existsByEmailAndDeletedAtIsNull(String email); // 이메일 중복 체크

    boolean existsByUsernameAndDeletedAtIsNull(String username); // 사용자명 중복 체크

    boolean existsByNicknameAndDeletedAtIsNull(String nickname); // 닉네임 중복 체크

    boolean existsByPhoneNumberAndDeletedAtIsNull(String phoneNumber); // 전화번호 중복 체크

    boolean existsByBusinessNumberAndDeletedAtIsNull(String businessNumber); // 사업자등록번호 중복 체크

    // 관리자용 회원 목록 조회 메서드
    // 기본 활성 사용자 조회
    Page<User> findByDeletedAtIsNull(Pageable pageable);

    // 탈퇴 사용자 포함 전체 조회  
    Page<User> findAll(Pageable pageable);

    // 권한별 조회
    Page<User> findByRoleAndDeletedAtIsNull(UserRole role, Pageable pageable);

    Page<User> findByRole(UserRole role, Pageable pageable);

    // 키워드 검색
    Page<User> findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseAndDeletedAtIsNull(
        String username, String email, String nickname, Pageable pageable);

    // 날짜 범위 조회
    Page<User> findByCreatedAtBetweenAndDeletedAtIsNull(LocalDateTime start, LocalDateTime end,
        Pageable pageable);

    // 통계용 권한별 회원 수 조회
    @Query("SELECT u.role, COUNT(u) FROM User u WHERE u.deletedAt IS NULL GROUP BY u.role")
    List<Object[]> countByRoleAndActiveUsers();

    // 전체 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u")
    long countAllUsers();

    // 활성 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NULL")
    long countActiveUsers();

    // 탈퇴 회원 수 조회
    @Query("SELECT COUNT(u) FROM User u WHERE u.deletedAt IS NOT NULL")
    long countDeletedUsers();
}