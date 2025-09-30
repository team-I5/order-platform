package com.spartaclub.orderplatform.domain.store.entity;

import static com.spartaclub.orderplatform.domain.store.entity.StoreStatus.PENDING;
import static jakarta.persistence.EnumType.STRING;

import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * 음식점 엔티티 클래스
 *
 * @author 이현주
 * @date 2025-09-30(화)
 *
 */
@Entity
@Table(name = "p_stores")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@EntityListeners(AuditingEntityListener.class)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String storeName;               // 음식점 이름
    private String storeAddress;            // 음식점 주소
    private String storeNumber;             // 음식점 전화번호

    @Enumerated(value = STRING)
    private StoreStatus status = PENDING;   // 음식점 승인 상태
    // - 음식점 생성을 하면 초기는 승인 대기(PENDING) 상태

    private Double averageRating;           // 음식점 평균평점
    private Integer reviewCount;            // 음식점 리뷰 개수

    @CreatedBy
    @Column(updatable = false)
    private Long createdId;                 // 음식점 생성자 ID

    @LastModifiedBy
    private Long modifiedId;                // 음식점 수정자 ID

    private Long deletedId;                 // 음식점 삭제자 ID
}
