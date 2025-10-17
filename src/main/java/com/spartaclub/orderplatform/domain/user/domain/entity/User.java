package com.spartaclub.orderplatform.domain.user.domain.entity;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 회원 엔티티 클래스 회원가입, 로그인, 사용자 관리 기능을 위한 사용자 정보 저장
 *
 * @author 전우선
 * @date 2025-10-02(목)
 */
@Entity
@Table(name = "p_users")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverrides({
        @AttributeOverride(name = "createdId", column = @Column(insertable = false, updatable = false)),
})
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "username", nullable = false, unique = true, length = 10)
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "password", nullable = false, length = 60)
    private String password;

    @Column(name = "nickname", nullable = false, unique = true, length = 10)
    private String nickname;


    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false)
    private UserRole role;

    @Column(name = "businessNumber", unique = true, length = 10)
    private String businessNumber;

    @Column(name = "phoneNumber", nullable = false, unique = true, length = 13)
    private String phoneNumber;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Address> addresses = new ArrayList<>();

    // === 정적 팩터리 메서드 ===

    /**
     * 일반 사용자 회원가입용 정적 팩터리 메서드
     */
    public static User createUser(String username, String email, String encodedPassword, 
                                  String nickname, String phoneNumber, UserRole role) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = role;
        return user;
    }

    /**
     * 사업자 회원가입용 정적 팩터리 메서드 (사업자번호 포함)
     */
    public static User createBusinessUser(String username, String email, String encodedPassword, 
                                          String nickname, String phoneNumber, UserRole role, 
                                          String businessNumber) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = role;
        user.businessNumber = businessNumber;
        return user;
    }

    /**
     * 관리자 계정 생성용 정적 팩터리 메서드
     */
    public static User createManager(String username, String email, String encodedPassword, 
                                     String nickname, String phoneNumber) {
        User user = new User();
        user.username = username;
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.phoneNumber = phoneNumber;
        user.role = UserRole.MANAGER;
        return user;
    }

    // === 비즈니스 메서드 ===

    /**
     * 사용자 정보 업데이트 (선택적 필드)
     */
    public void updateProfile(String username, String nickname, String phoneNumber, 
                              String businessNumber, String encodedPassword) {
        if (username != null) this.username = username;
        if (nickname != null) this.nickname = nickname;
        if (phoneNumber != null) this.phoneNumber = phoneNumber;
        if (businessNumber != null) this.businessNumber = businessNumber;
        if (encodedPassword != null) this.password = encodedPassword;
    }

    /**
     * 비밀번호 변경
     */
    public void changePassword(String encodedPassword) {
        this.password = encodedPassword;
    }
}