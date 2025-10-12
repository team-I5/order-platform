package com.spartaclub.orderplatform.domain.category.domain.model;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

/*
 * Category Entity Class
 * 음식점 종속 유형 정보 저장
 *
 * @author 이준성
 * @date 2025-09-30(화)
 */
@Entity
@Table(name = "p_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
    @Id // primary key
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID categoryId;        // 카테고리 ID

    @Enumerated(EnumType.STRING)
    private CategoryType type;      // 카테고리 종류
}