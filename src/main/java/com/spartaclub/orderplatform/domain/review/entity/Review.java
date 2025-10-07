package com.spartaclub.orderplatform.domain.review.entity;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.product.domain.entity.Product;
import com.spartaclub.orderplatform.domain.review.dto.ReviewUpdateRequestDto;
import com.spartaclub.orderplatform.domain.store.entity.Store;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import com.spartaclub.orderplatform.user.domain.entity.User;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.UUID;

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

    private Integer rating;             // 리뷰 별점
    // Null 값 허용 안함. 길이 1000자 까지
    @Column(nullable = false, length = 1000)
    private String contents;            // 리뷰 내용
    @CreatedBy
    @Column(updatable = false, nullable = false)
    private Long createdId;             // 리뷰 생성자 ID
    @LastModifiedBy
    private Long modifiedId;            // 리뷰 수정자 ID
    private Long deletedId;             // 리뷰 삭제자 ID
    // 외래 키 관계 설정
    @ManyToOne(fetch = FetchType.LAZY)  // 리뷰 : 회원 → Many to one
    @JoinColumn(name = "userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)   // 리뷰 : 음식점 → Many to One
    @JoinColumn(name = "storeId")
    private Store store;

    @OneToOne(fetch = FetchType.LAZY)    // 리뷰 : 주문 → One to One
    @JoinColumn(name = "orderId")
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)    // 리뷰 : 상품 → Many to One
    @JoinColumn(name = "productId")
    private Product product;

    // 리뷰 수정 메서드
    public void updateReview(@Valid ReviewUpdateRequestDto reviewUpdateRequestDto) {
        this.rating = reviewUpdateRequestDto.getRating();
        this.contents = reviewUpdateRequestDto.getContents();
    }

    // 리뷰 삭제 메서드(soft delete)
    public void deleteReview(Long userId) {
        this.deletedId = userId;
        delete();
    }


}