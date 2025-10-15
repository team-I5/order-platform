package com.spartaclub.orderplatform.domain.user.domain.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * User 도메인 레포지토리 인터페이스
 * 도메인 계층에서 필요한 User 관련 데이터 접근 메서드 정의
 * JPA에 의존하지 않는 순수 도메인 인터페이스
 *
 * @author 전우선
 * @date 2025-10-15
 */
public interface UserRepository {

    // 기본 CRUD
    User save(User user);
    Optional<User> findById(Long userId);
    void delete(User user);

    // 조회 메서드들
    Optional<User> findActiveByEmail(String email);
    Optional<User> findActiveById(Long userId);
    Optional<User> findActiveByUsername(String username);
    Optional<User> findActiveByNickname(String nickname);
    Optional<User> findActiveByPhoneNumber(String phoneNumber);
    Optional<User> findActiveByBusinessNumber(String businessNumber);

    // 중복 체크 메서드들
    boolean existsActiveByEmail(String email);
    boolean existsActiveByUsername(String username);
    boolean existsActiveByNickname(String nickname);
    boolean existsActiveByPhoneNumber(String phoneNumber);
    boolean existsActiveByBusinessNumber(String businessNumber);

    // 페이징 조회 (도메인 객체로 추상화)
    UserPage findActiveUsers(UserPageRequest pageRequest);
    UserPage findAllUsers(UserPageRequest pageRequest);
    UserPage findActiveUsersByRole(UserRole role, UserPageRequest pageRequest);
    UserPage findUsersByRole(UserRole role, UserPageRequest pageRequest);
    UserPage findActiveUsersByKeyword(String keyword, UserPageRequest pageRequest);
    UserPage findActiveUsersByDateRange(LocalDateTime start, LocalDateTime end, UserPageRequest pageRequest);

    // 통계 조회
    Map<UserRole, Long> countActiveUsersByRole();
    long countAllUsers();
    long countActiveUsers();
    long countDeletedUsers();

    /**
     * 페이징 요청 도메인 객체
     */
    record UserPageRequest(
        int page,
        int size,
        String sortBy,
        boolean ascending
    ) {}

    /**
     * 페이징 결과 도메인 객체
     */
    record UserPage(
        List<User> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean first,
        boolean last
    ) {}
}