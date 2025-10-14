package com.spartaclub.orderplatform.domain.category.infrastructure.repository;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import com.spartaclub.orderplatform.domain.category.domain.model.CategoryType;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/*
 * Category Entity 레포지토리 인터페이스
 *
 * @author 이준성
 * @date 2025-10-02(수)
 */
@Repository // spring 레포지토리 컴포넌트로 등록
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    Page<Category> findByTypeAndDeletedAtIsNull(CategoryType type, Pageable pageable);

}
