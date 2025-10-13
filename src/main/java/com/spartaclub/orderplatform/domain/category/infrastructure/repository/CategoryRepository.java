package com.spartaclub.orderplatform.domain.category.infrastructure.repository;

import com.spartaclub.orderplatform.domain.category.domain.model.Category;
import java.util.Optional;
import java.util.UUID;
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

    // 조회 메서드들 - Spring Data JPA가 메서드 이름으로 자동 쿼리 생성
    Optional<Category> findByType(String type);     // Enum 유형으로 조회

}
