package com.spartaclub.orderplatform.domain.user.domain.entity;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * User 도메인 엔티티 테스트
 * - 도메인 로직 검증
 * - 비즈니스 규칙 테스트
 * - 엔티티 상태 변화 테스트
 */
class UserTest {

    @Test
    @DisplayName("일반 사용자 생성 - 정적 팩터리 메서드")
    void createUser_success() {
        // given
        String username = "testuser";
        String email = "test@example.com";
        String password = "encodedPassword";
        String nickname = "testnick";
        String phoneNumber = "01012345678";
        UserRole role = UserRole.CUSTOMER;

        // when
        User user = User.createUser(username, email, password, nickname, phoneNumber, role);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getPassword()).isEqualTo(password);
        assertThat(user.getNickname()).isEqualTo(nickname);
        assertThat(user.getPhoneNumber()).isEqualTo(phoneNumber);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getBusinessNumber()).isNull();
    }

    @Test
    @DisplayName("비즈니스 사용자 생성 - 사업자번호 포함")
    void createBusinessUser_success() {
        // given
        String username = "businessuser";
        String email = "business@example.com";
        String password = "encodedPassword";
        String nickname = "비즈니스";
        String phoneNumber = "01087654321";
        UserRole role = UserRole.OWNER;
        String businessNumber = "1234567890";

        // when
        User user = User.createBusinessUser(username, email, password, nickname, phoneNumber, role, businessNumber);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getEmail()).isEqualTo(email);
        assertThat(user.getRole()).isEqualTo(role);
        assertThat(user.getBusinessNumber()).isEqualTo(businessNumber);
    }

    @Test
    @DisplayName("관리자 계정 생성")
    void createManager_success() {
        // given
        String username = "manager";
        String email = "manager@example.com";
        String password = "encodedPassword";
        String nickname = "매니저";
        String phoneNumber = "01011111111";

        // when
        User user = User.createManager(username, email, password, nickname, phoneNumber);

        // then
        assertThat(user.getUsername()).isEqualTo(username);
        assertThat(user.getRole()).isEqualTo(UserRole.MANAGER);
        assertThat(user.getBusinessNumber()).isNull();
    }

    @Test
    @DisplayName("사용자 프로필 업데이트 - 전체 필드")
    void updateProfile_allFields_success() {
        // given
        User user = User.createUser("olduser", "old@example.com", "oldPassword", 
                                   "oldnick", "01000000000", UserRole.CUSTOMER);
        
        String newUsername = "newuser";
        String newNickname = "newnick";
        String newPhoneNumber = "01011111111";
        String newBusinessNumber = null;
        String newPassword = "newEncodedPassword";

        // when
        user.updateProfile(newUsername, newNickname, newPhoneNumber, newBusinessNumber, newPassword);

        // then
        assertThat(user.getUsername()).isEqualTo(newUsername);
        assertThat(user.getNickname()).isEqualTo(newNickname);
        assertThat(user.getPhoneNumber()).isEqualTo(newPhoneNumber);
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("사용자 프로필 업데이트 - 부분 필드만")
    void updateProfile_partialFields_success() {
        // given
        User user = User.createUser("olduser", "old@example.com", "oldPassword", 
                                   "oldnick", "01000000000", UserRole.CUSTOMER);
        String originalUsername = user.getUsername();
        String originalPhoneNumber = user.getPhoneNumber();
        
        String newNickname = "newnick";

        // when
        user.updateProfile(null, newNickname, null, null, null);

        // then
        assertThat(user.getUsername()).isEqualTo(originalUsername); // 변경되지 않음
        assertThat(user.getNickname()).isEqualTo(newNickname); // 변경됨
        assertThat(user.getPhoneNumber()).isEqualTo(originalPhoneNumber); // 변경되지 않음
    }

    @Test
    @DisplayName("비밀번호 변경")
    void changePassword_success() {
        // given
        User user = User.createUser("testuser", "test@example.com", "oldPassword", 
                                   "testnick", "01012345678", UserRole.CUSTOMER);
        String newPassword = "newEncodedPassword";

        // when
        user.changePassword(newPassword);

        // then
        assertThat(user.getPassword()).isEqualTo(newPassword);
    }

    @Test
    @DisplayName("사용자 권한별 생성 검증")
    void createUserByRole_success() {
        // given & when & then
        User customer = User.createUser("customer", "customer@example.com", "password", 
                                       "고객", "01012345678", UserRole.CUSTOMER);
        assertThat(customer.getRole()).isEqualTo(UserRole.CUSTOMER);

        User owner = User.createBusinessUser("owner", "owner@example.com", "password", 
                                            "사장", "01087654321", UserRole.OWNER, "1234567890");
        assertThat(owner.getRole()).isEqualTo(UserRole.OWNER);

        User manager = User.createManager("manager", "manager@example.com", "password", 
                                         "매니저", "01011111111");
        assertThat(manager.getRole()).isEqualTo(UserRole.MANAGER);
    }
}