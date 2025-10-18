package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderJpaRepository extends JpaRepository<Order, UUID>,
    JpaSpecificationExecutor<Order> {

    @EntityGraph(attributePaths = {"user"})
    Page<Order> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"user"})
    Page<Order> findAll(Specification<Order> specification, Pageable pageable);
}
