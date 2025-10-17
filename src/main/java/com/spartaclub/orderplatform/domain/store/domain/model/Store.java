package com.spartaclub.orderplatform.domain.store.domain.model;

import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.APPROVED;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.PENDING;
import static com.spartaclub.orderplatform.domain.store.domain.model.StoreStatus.REJECTED;
import static jakarta.persistence.EnumType.STRING;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.domain.model.Review;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_stores")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class Store extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(unique = true, nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategory> storeCategories = new ArrayList<>();

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

    private Double averageRating = 0.0;
    private Integer reviewCount = 0;


    /*  ==================
            정적 팩토리
        ================== */
    public static Store create(User user, StoreRequestDto storeRequestDto) {
        Store store = new Store();
        store.user = user;
        store.storeName = storeRequestDto.getStoreName();
        store.storeAddress = storeRequestDto.getStoreAddress();
        store.storeNumber = storeRequestDto.getStoreNumber();
        store.storeDescription = storeRequestDto.getStoreDescription();
        store.storeNumber = storeRequestDto.getStoreNumber();
        store.status = PENDING;
        store.averageRating = 0.0;
        store.reviewCount = 0;
        store.rejectReason = null;
        return store;
    }

    /*  ==================
            업데이트 관련
        ================== */
    public void updateStoreInfo(StoreRequestDto dto) {
        this.storeName = dto.getStoreName();
        this.storeAddress = dto.getStoreAddress();
        this.storeNumber = dto.getStoreNumber();
        this.storeDescription = dto.getStoreDescription();
    }

    /*  ==================
           승인 상태 관련
        ================== */
    public void approve() {
        this.status = APPROVED;
        this.rejectReason = null;
    }

    public void reject(String rejectReason) {
        this.status = REJECTED;
        this.rejectReason = rejectReason;
    }

    public void requestReapproval() {
        this.status = PENDING;
        this.rejectReason = null;
    }

    /*  ==================
           카테고리 관련
        ================== */
    public void addCategory(Category category) {
        StoreCategory storeCategory = new StoreCategory(this, category);
        storeCategories.add(storeCategory);
    }

    public void removeCategory(Long userId, Category category) {
        this.storeCategories.stream()
            .filter(storeCategory ->
                storeCategory.getCategory().equals(category)
                    && storeCategory.getDeletedId() != null)
            .findFirst()
            .ifPresent(storeCategory -> {
                storeCategory.scSoftDelete(userId);
            });
    }

    /*  ==================
            평점 관련
        ================== */
    public void updateAverageRatingAndReviewCount(double averageRating, int reviewCount) {
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    /*  ==================
              삭제
        ================== */
    public void storeSoftDelete(Long userId) {
        delete(userId);
    }

}
