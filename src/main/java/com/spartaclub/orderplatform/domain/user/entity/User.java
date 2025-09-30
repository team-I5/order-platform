package com.spartaclub.orderplatform.domain.user.entity; // User 엔티티 패키지 선언

import com.spartaclub.orderplatform.global.entity.BaseEntity; // BaseEntity 클래스 임포트
import lombok.Getter; // Lombok - getter 메서드 자동 생성
import lombok.NoArgsConstructor; // Lombok - 기본 생성자 자동 생성
import lombok.Setter; // Lombok - setter 메서드 자동 생성

import jakarta.persistence.*; // JPA 어노테이션들 임포트 (Spring Boot 3.x에서는 jakarta 사용)

/**
 * 회원 엔티티 클래스
 * 회원가입, 로그인, 사용자 관리 기능을 위한 사용자 정보 저장
 * 
 * @author 전우선
 * @date 2025-09-30(화)
 */
@Entity // JPA 엔티티 클래스임을 나타내는 어노테이션
@Table(name = "p_user") // 데이터베이스 테이블명을 "p_user"로 지정 (p_ 접두사 규칙 적용, 단수형)
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok - 모든 필드에 대한 setter 메서드 자동 생성
@NoArgsConstructor // Lombok - 기본 생성자 자동 생성 (JPA에서 필요)
public class User extends BaseEntity { // BaseEntity를 상속받아 공통 필드(생성일, 수정일, 삭제일) 자동 관리

    @Id // 기본키 필드임을 나타내는 어노테이션
    @GeneratedValue(strategy = GenerationType.IDENTITY) // 자동 증가 방식으로 기본키 생성 (MySQL AUTO_INCREMENT 방식)
    @Column(name = "userId") // 데이터베이스 컬럼명을 "userId"로 지정
    private Long userId; // 사용자 고유 식별자 (BIGINT 타입)

    @Column(name = "username", nullable = false, unique = true, length = 10) // username 컬럼 - 필수, 유니크
    private String username; // 사용자명 (4-10자, 영소문자a-z, 숫자0-9만 허용)

    @Column(name = "email", nullable = false, unique = true, length = 50) // email 컬럼 - 필수, 유니크
    private String email; // 이메일 주소 (로그인 ID로 사용, 중복 불가)

    @Column(name = "password", nullable = false, length = 60) // password 컬럼 - 필수, BCrypt 해시 저장용
    private String password; // BCrypt로 암호화된 비밀번호 (입력: 8-15자, 저장: BCrypt 해시)

    @Column(name = "nickname", nullable = false, unique = true, length = 10) // nickname 컬럼 - 필수, 유니크
    private String nickname; // 사용자 닉네임 (화면에 표시될 이름, 중복 불가, 최대 10자)

    @Column(name = "address", length = 200) // address 컬럼 - 선택사항
    private String address; // 사용자 주소 (배송지 정보)

    @Enumerated(EnumType.STRING) // Enum 타입을 문자열로 데이터베이스에 저장
    @Column(name = "role", nullable = false) // role 컬럼 - 필수 (MANAGER, MASTER는 관리자만 생성 가능)
    private UserRole role; // 사용자 권한 (CUSTOMER, OWNER만 선택 가능)

    @Column(name = "businessNumber", unique = true, length = 10) // businessNumber 컬럼 - OWNER 필수, 중복 불가
    private String businessNumber; // 사업자등록번호 (OWNER 권한 시 필수, 10자리 숫자만, 하이픈 제외)

    @Column(name = "phoneNumber", nullable = false, unique = true, length = 13) // phoneNumber 컬럼 - 필수, 유니크, 하이픈 포함
    private String phoneNumber; // 연락처 전화번호 (예: 010-1234-5678, 중복 불가)

    // 공통 필드들(createdAt, modifiedAt, deletedAt)과 관련 메서드들은 BaseEntity에서 상속받음
}