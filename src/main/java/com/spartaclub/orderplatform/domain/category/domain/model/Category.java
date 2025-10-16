package com.spartaclub.orderplatform.domain.category.domain.model;

import com.spartaclub.orderplatform.domain.store.domain.model.StoreCategory;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
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

    // Enum에 요소가 추가되어야 하는 상황에 적용해보려고 CategoryConverter 고민했었음.
    //    @Convert(converter = CategoryConverter.class)

    @Column(nullable = false)
    public String type;      // 카테고리 종류

    public Category(String type) {
        this.type = type;
    }

    // 외래 키 관계 설정 StoreyCategory → Category
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StoreCategory> storeCategories = new ArrayList<>();

    // 카테고리 수정 메서드
    public void updateCategory(String type) {
        this.type = type;
    }

    // 카테고리 삭제 메서드
    public void deleteCategory(Long userId) {
        delete(userId);
    }

    public static Category of(String type) {
        Category category = new Category();
        category.type = type;
        return category;
    }
}