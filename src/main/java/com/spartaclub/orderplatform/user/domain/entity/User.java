package com.spartaclub.orderplatform.user.domain.entity;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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
@NoArgsConstructor
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

    public void setPassword(String encoddedPassword) {
        password = encoddedPassword;
    }

    public void updateNickname(String newNickname) {
        nickname = newNickname;
    }
}