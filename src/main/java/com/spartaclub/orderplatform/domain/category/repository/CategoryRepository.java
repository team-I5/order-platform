package com.spartaclub.orderplatform.domain.category.repository;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CategoryRepository extends JpaRepository<Category, UUID> {
    Optional<Category> findByType(String type);     // Enum 타입으로 조회

}
