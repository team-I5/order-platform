package com.spartaclub.orderplatform.domain.store.domain.model;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import java.util.UUID;

@Entity
@Table(name = "p_stores_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class StoreCategory extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID storeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "store_id")
    private Store store;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @CreatedBy
    @Column(updatable = false)
    private Long createdId;

    @LastModifiedBy
    private Long modifiedId;

    private Long deletedId;

    public StoreCategory(Store store, Category category) {
        this.store = store;
        this.category = category;
    }

    public void scSoftDelete(Long userId) {
        this.deletedId = userId;
    }
}
