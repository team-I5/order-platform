package com.spartaclub.orderplatform.global.domain.entity; // 전역 엔티티 패키지 선언

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

/**
 * 모든 엔티티의 공통 필드를 관리하는 베이스 엔티티 클래스
 * Spring Data JPA Auditing을 사용하여 생성일, 수정일 자동 관리
 *
 * @author 전우선
 * @date 2025-10-01(수)
 */
@MappedSuperclass // JPA - 이 클래스는 테이블로 생성되지 않고 상속받는 엔티티에 필드만 제공
@EntityListeners(AuditingEntityListener.class) // Spring Data JPA Auditing 기능 활성화
@Getter // Lombok - 모든 필드에 대한 getter 메서드 자동 생성
public abstract class BaseEntity { // 추상 클래스로 선언 (직접 인스턴스 생성 불가)

    @CreatedDate // Spring Data JPA - 엔티티 생성 시 자동으로 현재 시간 설정
    @Column(name = "createdAt", nullable = false, updatable = false) // createdAt 컬럼 - 필수, 수정 불가
    private LocalDateTime createdAt; // 엔티티 생성 일시 (자동 설정)

    @LastModifiedDate // Spring Data JPA - 엔티티 수정 시 자동으로 현재 시간 설정
    @Column(name = "modifiedAt") // modifiedAt 컬럼 - 선택사항
    private LocalDateTime modifiedAt; // 엔티티 마지막 수정 일시 (자동 설정)

    @Column(name = "deletedAt") // deletedAt 컬럼 - 선택사항 (소프트 삭제용)
    private LocalDateTime deletedAt; // 엔티티 삭제 일시 (소프트 삭제 플래그, 수동 설정)

    // 생성자 ID
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;

    // 수정자 ID
    @LastModifiedBy
    private Long modifiedId;

    // 삭제자 ID
    private Long deletedId;

    /**
     * 소프트 삭제 처리
     * deletedAt 필드에 현재 시간을 설정하여 논리적 삭제 수행
     */
    public void delete(Long userId) {
        this.deletedAt = LocalDateTime.now();
        this.deletedId = userId;
    }

    /**
     * 삭제 상태 확인
     *
     * @return 삭제된 상태면 true, 아니면 false
     */
    public boolean isDeleted() {
        return this.deletedAt != null;
    }
}