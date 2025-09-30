package com.spartaclub.orderplatform.global.entity; // 전역 엔티티 패키지 선언

import jakarta.persistence.*; // JPA 어노테이션들 임포트 (Spring Boot 3.x에서는 jakarta 사용)
import lombok.Getter; // Lombok - getter 메서드 자동 생성
import lombok.Setter; // Lombok - setter 메서드 자동 생성
import org.springframework.data.annotation.CreatedDate; // Spring Data JPA - 생성일 자동 설정
import org.springframework.data.annotation.LastModifiedDate; // Spring Data JPA - 수정일 자동 설정
import org.springframework.data.jpa.domain.support.AuditingEntityListener; // Spring Data JPA Auditing 리스너

import java.time.LocalDateTime; // 날짜/시간 타입 임포트

/**
 * 모든 엔티티의 공통 필드를 관리하는 베이스 엔티티 클래스
 * Spring Data JPA Auditing을 사용하여 생성일, 수정일 자동 관리
 * 
 * @author 전우선
 * @date 2025-09-30(화)
 */
@MappedSuperclass // JPA - 이 클래스는 테이블로 생성되지 않고 상속받는 엔티티에 필드만 제공
@EntityListeners(AuditingEntityListener.class) // Spring Data JPA Auditing 기능 활성화
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성
@Setter // Lombok - 모든 필드에 대한 setter 메서드 자동 생성
public abstract class BaseEntity { // 추상 클래스로 선언 (직접 인스턴스 생성 불가)

    @CreatedDate // Spring Data JPA - 엔티티 생성 시 자동으로 현재 시간 설정
    @Column(name = "createdAt", nullable = false, updatable = false) // createdAt 컬럼 - 필수, 수정 불가
    private LocalDateTime createdAt; // 엔티티 생성 일시 (자동 설정)

    @LastModifiedDate // Spring Data JPA - 엔티티 수정 시 자동으로 현재 시간 설정
    @Column(name = "modifiedAt") // modifiedAt 컬럼 - 선택사항
    private LocalDateTime modifiedAt; // 엔티티 마지막 수정 일시 (자동 설정)

    @Column(name = "deletedAt") // deletedAt 컬럼 - 선택사항 (소프트 삭제용)
    private LocalDateTime deletedAt; // 엔티티 삭제 일시 (소프트 삭제 플래그, 수동 설정)
}