package com.spartaclub.orderplatform.domain.review.entity;

import com.spartaclub.orderplatform.global.entity.BaseEntity;
import jakarta.persistence.*;
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
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID reviewId;          // 리뷰 ID

    private Integer rating;         // 리뷰 별점
    @Column(length = 1000)
    private String contents;        // 리뷰 내용
    @CreatedBy
    @Column(updatable = false)
    private Long createdId;         // 리뷰 생성자 ID
    @LastModifiedBy
    private Long modifiedId;        // 리뷰 수정자 ID
    private Long deletedId;         // 리뷰 삭제자 ID
}
