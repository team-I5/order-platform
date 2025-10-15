package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserDomainRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserDomainRepository의 JPA 구현체
 * Infrastructure 계층에서 JPA 구현 세부사항을 처리하고
 * 도메인 계층에는 추상화된 인터페이스만 노출
 */
@Component
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserDomainRepository {

    private final UserRepository userJpaRepository;

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long userId) {
        return userJpaRepository.findById(userId);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }

    // 도메인 관점의 조회 메서드들 - JPA 메서드명을 숨김
    @Override
    public Optional<User> findActiveUserByEmail(String email) {
        return userJpaRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public Optional<User> findActiveUserById(Long userId) {
        return userJpaRepository.findByUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public Optional<User> findActiveUserByUsername(String username) {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findActiveUserByNickname(String nickname) {
        return userJpaRepository.findByNicknameAndDeletedAtIsNull(nickname);
    }

    @Override
    public Optional<User> findActiveUserByPhoneNumber(String phoneNumber) {
        return userJpaRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    public Optional<User> findActiveUserByBusinessNumber(String businessNumber) {
        return userJpaRepository.findByBusinessNumberAndDeletedAtIsNull(businessNumber);
    }

    // 도메인 관점의 중복 검증 - JPA 메서드명을 숨김
    @Override
    public boolean isEmailAlreadyTaken(String email) {
        return userJpaRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean isUsernameAlreadyTaken(String username) {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public boolean isNicknameAlreadyTaken(String nickname) {
        return userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }

    @Override
    public boolean isPhoneNumberAlreadyTaken(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    public boolean isBusinessNumberAlreadyTaken(String businessNumber) {
        return userJpaRepository.existsByBusinessNumberAndDeletedAtIsNull(businessNumber);
    }

    // 관리자용 조회
    @Override
    public Page<User> findActiveUsers(Pageable pageable) {
        return userJpaRepository.findByDeletedAtIsNull(pageable);
    }

    @Override
    public Page<User> findAllUsers(Pageable pageable) {
        return userJpaRepository.findAll(pageable);
    }

    @Override
    public Page<User> findActiveUsersByRole(UserRole role, Pageable pageable) {
        return userJpaRepository.findByRoleAndDeletedAtIsNull(role, pageable);
    }

    @Override
    public Page<User> findAllUsersByRole(UserRole role, Pageable pageable) {
        return userJpaRepository.findByRole(role, pageable);
    }

    @Override
    public Page<User> searchActiveUsers(String keyword, Pageable pageable) {
        return userJpaRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseAndDeletedAtIsNull(
            keyword, keyword, keyword, pageable);
    }

    @Override
    public Page<User> findActiveUsersByDateRange(LocalDateTime start, LocalDateTime end, Pageable pageable) {
        return userJpaRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end, pageable);
    }

    // 통계 정보
    @Override
    public Map<UserRole, Long> getActiveUserCountByRole() {
        List<Object[]> roleStats = userJpaRepository.countByRoleAndActiveUsers();
        Map<UserRole, Long> result = new HashMap<>();
        
        for (Object[] stat : roleStats) {
            UserRole role = (UserRole) stat[0];
            Long count = (Long) stat[1];
            result.put(role, count);
        }
        
        return result;
    }

    @Override
    public long getTotalUserCount() {
        return userJpaRepository.countAllUsers();
    }

    @Override
    public long getActiveUserCount() {
        return userJpaRepository.countActiveUsers();
    }

    @Override
    public long getDeletedUserCount() {
        return userJpaRepository.countDeletedUsers();
    }
}