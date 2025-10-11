package com.spartaclub.orderplatform.domain.store.domain.model;

import static jakarta.persistence.EnumType.STRING;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.entity.Review;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.user.domain.entity.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "p_stores")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;          // TODO 중간테이블 만들기

    @Column(nullable = false, length = 20)
    private String storeName;

    @Column(nullable = false, length = 50)
    private String storeAddress;

    @Column(unique = true, nullable = false, length = 13)
    private String storeNumber;

    @Column(length = 250)
    private String storeDescription;

    @Enumerated(value = STRING)     // Enum 타입으로 관리(PENDING, APPROVED, REJECTED)
    @Column(nullable = false)
    private StoreStatus status;

    @Column(length = 300)
    private String rejectReason;

    private Double averageRating;
    private Integer reviewCount;

    @CreatedBy
    @Column(updatable = false)
    private Long createdId;

    @LastModifiedBy
    private Long modifiedId;

    private Long deletedId;

    // 음식점 정보 업데이트(기본 정보만)
    public void updateStoreInfo(StoreRequestDto dto) {
        this.storeName = dto.getStoreName();
        this.storeAddress = dto.getStoreAddress();
        this.storeNumber = dto.getStoreNumber();
        this.storeDescription = dto.getStoreDescription();
    }

    // 재승인 요청
    public void requestReapproval() {
        this.status = StoreStatus.PENDING;
        this.rejectReason = null;
    }

    // 음식점 승인
    public void approve() {
        this.status = StoreStatus.APPROVED;
        this.rejectReason = null;
    }

    // 음식점 승인 거절
    public void reject(String rejectReason) {
        this.status = StoreStatus.REJECTED;
        this.rejectReason = rejectReason;
    }

    // 음식점 삭제 처리
    public void softDelete(Long userId) {
        this.deletedId = userId;
    }
}
