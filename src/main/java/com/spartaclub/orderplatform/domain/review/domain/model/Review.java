package com.spartaclub.orderplatform.domain.review.domain.model;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.store.domain.model.Store;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Review Entity Class
 * 회원,가게별 리뷰 관리를 위한 리뷰 정보 저장
 *
 * @author 이준성
 * @date 2025-09-30(화)
 */
@Entity
@Table(name = "p_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id // primary key
    @GeneratedValue(strategy = GenerationType.UUID) // UUID 자동 생성
    private UUID reviewId;              // 리뷰 ID
    @Column(nullable = false) // Null 값 허용 안함
    private Integer rating;             // 리뷰 별점
    // Null 값 허용 안함. 길이 1000자 까지
    @Column(nullable = false, length = 1000)
    private String contents;            // 리뷰 내용

    // 외래 키 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)  // 리뷰 : 회원 → Many to one
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)   // 리뷰 : 음식점 → Many to One
    @JoinColumn(name = "storeId", nullable = false)
    private Store store;

    //정적 팩토리 메서드 (리뷰 생성용)
    public static Review create(User user, Store store, Product product, Order order,
        Integer rating,
        String contents) {
        Review review = new Review();
        review.user = user;
        review.store = store;
        review.order = order;
        review.product = product;
        review.rating = rating;
        review.contents = contents;
        return review;
    }

    public void setStore(Store store) {
        this.store = store;
        if (!store.getReviews().contains(this)) {
            store.getReviews().add(this);
        }
    }

    @OneToOne(fetch = FetchType.LAZY)    // 리뷰 : 주문 → One to One
    @JoinColumn(name = "orderId", nullable = false)
    private Order order;
//    private UUID orderId;

    @ManyToOne(fetch = FetchType.LAZY)    // 리뷰 : 상품 → Many to One
    @JoinColumn(name = "productId", nullable = false)
    private Product product;

    public void setProduct(Product product) {
        this.product = product;
        if (!product.getReviews().contains(this)) {
            product.getReviews().add(this);
        }
    }

    // 리뷰 수정 메서드
    public void updateReview(Integer rating, String contents) {
        this.rating = rating;
        this.contents = contents;
    }

    // 리뷰 삭제 메서드(soft delete)
    public void deleteReview(Long userId) {
        delete(userId);
    }
}