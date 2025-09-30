package com.spartaclub.orderplatform.domain.category.repository;

import com.spartaclub.orderplatform.domain.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, UUID> {


}
