package com.spartaclub.orderplatform.domain.store.entity;

import static jakarta.persistence.EnumType.STRING;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.entity.Review;
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
    @Column(name = "storeId", unique = true, nullable = false, updatable = false)
    private UUID storeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Product> products = new ArrayList<>();

    @OneToMany(mappedBy = "store", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "categoryId", nullable = false)
    private Category category;

    @Column(name = "storeName", nullable = false, length = 20)
    private String storeName;

    @Column(name = "storeAddress", nullable = false, length = 50)
    private String storeAddress;

    @Column(name = "storeNumber", unique = true, nullable = false, length = 13)
    private String storeNumber;

    @Column(name = "storeDescription", length = 250)
    private String storeDescription;

    @Enumerated(value = STRING)     // Enum 타입으로 관리(PENDING, APPROVED, REJECTED)
    @Column(name = "status", nullable = false)
    private StoreStatus status;

    @Column(name = "averageRating", precision = 2, scale = 1)      // 총 두 자리, 소수점 아래 한 자리까지 표시
    private Double averageRating;

    @Column(name = "reviewCount")
    private Integer reviewCount;

    @CreatedBy
    @Column(name = "createdId", updatable = false)
    private Long createdId;

    @LastModifiedBy
    @Column(name = "modifiedId")
    private Long modifiedId;

    @Column(name = "deletedId")
    private Long deletedId;
}
