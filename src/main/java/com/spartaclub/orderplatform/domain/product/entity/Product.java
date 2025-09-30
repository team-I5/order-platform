package com.spartaclub.orderplatform.domain.product.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Builder;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID productId;

    // 상품명
    @NotBlank(message = "상품 이름은 필수입니다")
    @Size(max = 100, message = "상품 이름은 100자 이내여야 합니다")
    @Column(nullable = false, length = 100)
    private String name;

    // 가격
    @NotNull(message = "가격은 필수입니다")
    @Min(value = 0, message = "가격은 0 이상이어야 합니다")
    @Column(nullable = false)
    private Integer price;

    // 상품 설명
    @Size(max = 500, message = "설명은 500자 이내여야 합니다")
    @Column(length = 500)
    private String description;

    // 상품 숨김 여부
    @Column(nullable = false)
    private Boolean isHidden = false;

    // 소속 가게
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "store_id", nullable = false)
//    private Store store;

    // 생성일, 수정일 자동 관리
    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    private LocalDateTime deletedAt;

    @CreatedBy
    @Column(updatable = false)
    private Long createdId;

    @LastModifiedBy
    private Long updatedId;

    private Long deletedId;

}