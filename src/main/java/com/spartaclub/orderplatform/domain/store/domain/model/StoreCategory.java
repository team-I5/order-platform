package com.spartaclub.orderplatform.domain.store.domain.model;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.global.domain.entity.BaseEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "p_stores_categories")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    public StoreCategory(Store store, Category category) {
        this.store = store;
        this.category = category;
    }

    public void scSoftDelete(Long userId) {
        delete(userId);
    }
}
