package com.spartaclub.orderplatform.domain.user.infrastructure.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.domain.user.domain.entity.UserRole;
import com.spartaclub.orderplatform.domain.user.domain.repository.UserRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

/**
 * UserRepository 간단한 통합 테스트
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class UserRepositorySimpleTest {

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("사용자 저장 및 조회 테스트")
    void saveAndFind_success() {
        // given
        User user = User.createUser(
            "testuser12",
            "test12@example.com",
            "encodedPassword",
            "testnick12",
            "01012345678",
            UserRole.CUSTOMER
        );

        // when
        User savedUser = userRepository.save(user);
        Optional<User> foundUser = userRepository.findById(savedUser.getUserId());

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser12");
        assertThat(foundUser.get().getEmail()).isEqualTo("test12@example.com");
    }

    @Test
    @DisplayName("이메일로 활성 사용자 조회 테스트")
    void findActiveByEmail_success() {
        // given
        User user = User.createUser(
            "testuser45",
            "test45@example.com",
            "encodedPassword",
            "testnick45",
            "01087654321",
            UserRole.CUSTOMER
        );
        userRepository.save(user);

        // when
        Optional<User> foundUser = userRepository.findActiveByEmail("test45@example.com");

        // then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser45");
    }

    @Test
    @DisplayName("중복 체크 테스트")
    void duplicateCheck_success() {
        // given
        User user = User.createUser(
            "testuser78",
            "test78@example.com",
            "encodedPassword",
            "testnick78",
            "01011111111",
            UserRole.CUSTOMER
        );
        userRepository.save(user);

        // when & then
        assertThat(userRepository.existsActiveByEmail("test78@example.com")).isTrue();
        assertThat(userRepository.existsActiveByUsername("testuser78")).isTrue();
        assertThat(userRepository.existsActiveByNickname("testnick78")).isTrue();
        
        assertThat(userRepository.existsActiveByEmail("notexist@example.com")).isFalse();
        assertThat(userRepository.existsActiveByUsername("notexist")).isFalse();
    }
}