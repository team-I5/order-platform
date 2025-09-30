package com.spartaclub.orderplatform.domain.product.repository;

import com.spartaclub.orderplatform.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, UUID> {
}
