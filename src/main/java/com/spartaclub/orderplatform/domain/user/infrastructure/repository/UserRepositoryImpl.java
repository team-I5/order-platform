package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * UserRepository 도메인 인터페이스의 구현체
 * JPA 기술을 사용하여 User 도메인 데이터 접근 구현
 * Infrastructure 계층에서 도메인 계층의 추상화를 구체화
 *
 * @author 전우선
 * @date 2025-10-15
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryImpl implements UserRepository {

    private final UserJpaRepository userJpaRepository;

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

    @Override
    public Optional<User> findActiveByEmail(String email) {
        return userJpaRepository.findByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public Optional<User> findActiveById(Long userId) {
        return userJpaRepository.findByUserIdAndDeletedAtIsNull(userId);
    }

    @Override
    public Optional<User> findActiveByUsername(String username) {
        return userJpaRepository.findByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public Optional<User> findActiveByNickname(String nickname) {
        return userJpaRepository.findByNicknameAndDeletedAtIsNull(nickname);
    }

    @Override
    public Optional<User> findActiveByPhoneNumber(String phoneNumber) {
        return userJpaRepository.findByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    public Optional<User> findActiveByBusinessNumber(String businessNumber) {
        return userJpaRepository.findByBusinessNumberAndDeletedAtIsNull(businessNumber);
    }

    @Override
    public boolean existsActiveByEmail(String email) {
        return userJpaRepository.existsByEmailAndDeletedAtIsNull(email);
    }

    @Override
    public boolean existsActiveByUsername(String username) {
        return userJpaRepository.existsByUsernameAndDeletedAtIsNull(username);
    }

    @Override
    public boolean existsActiveByNickname(String nickname) {
        return userJpaRepository.existsByNicknameAndDeletedAtIsNull(nickname);
    }

    @Override
    public boolean existsActiveByPhoneNumber(String phoneNumber) {
        return userJpaRepository.existsByPhoneNumberAndDeletedAtIsNull(phoneNumber);
    }

    @Override
    public boolean existsActiveByBusinessNumber(String businessNumber) {
        return userJpaRepository.existsByBusinessNumberAndDeletedAtIsNull(businessNumber);
    }

    @Override
    public UserPage findActiveUsers(UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findByDeletedAtIsNull(springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public UserPage findAllUsers(UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findAll(springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public UserPage findActiveUsersByRole(UserRole role, UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findByRoleAndDeletedAtIsNull(role, springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public UserPage findUsersByRole(UserRole role, UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findByRole(role, springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public UserPage findActiveUsersByKeyword(String keyword, UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCaseOrNicknameContainingIgnoreCaseAndDeletedAtIsNull(
            keyword, keyword, keyword, springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public UserPage findActiveUsersByDateRange(LocalDateTime start, LocalDateTime end, UserPageRequest pageRequest) {
        PageRequest springPageRequest = createSpringPageRequest(pageRequest);
        Page<User> page = userJpaRepository.findByCreatedAtBetweenAndDeletedAtIsNull(start, end, springPageRequest);
        return convertToUserPage(page);
    }

    @Override
    public Map<UserRole, Long> countActiveUsersByRole() {
        List<Object[]> results = userJpaRepository.countByRoleAndActiveUsers();
        Map<UserRole, Long> roleCount = new HashMap<>();
        
        for (Object[] result : results) {
            UserRole role = (UserRole) result[0];
            Long count = (Long) result[1];
            roleCount.put(role, count);
        }
        
        return roleCount;
    }

    @Override
    public long countAllUsers() {
        return userJpaRepository.countAllUsers();
    }

    @Override
    public long countActiveUsers() {
        return userJpaRepository.countActiveUsers();
    }

    @Override
    public long countDeletedUsers() {
        return userJpaRepository.countDeletedUsers();
    }

    private PageRequest createSpringPageRequest(UserPageRequest pageRequest) {
        Sort sort = pageRequest.ascending() 
            ? Sort.by(pageRequest.sortBy()).ascending()
            : Sort.by(pageRequest.sortBy()).descending();
        
        return PageRequest.of(pageRequest.page(), pageRequest.size(), sort);
    }

    private UserPage convertToUserPage(Page<User> page) {
        return new UserPage(
            page.getContent(),
            page.getNumber(),
            page.getSize(),
            page.getTotalElements(),
            page.getTotalPages(),
            page.isFirst(),
            page.isLast()
        );
    }
}