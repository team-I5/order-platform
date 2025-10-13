package com.spartaclub.orderplatform.domain.category.domain.model;

import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

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