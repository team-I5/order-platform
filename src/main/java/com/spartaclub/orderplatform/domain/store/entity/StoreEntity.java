package com.spartaclub.orderplatform.domain.store.entity;

import com.spartaclub.orderplatform.domain.store.type.StoreStatusType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "p_store")
public class StoreEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String storeName;
    private String storeAddress;
    private StoreStatusType status;
    private String storeNumber;
    private Double averageRating;
    private Integer reviewCount;
    private Long createdId;
    private Long modifiedId;
    private Long deletedId;
}
