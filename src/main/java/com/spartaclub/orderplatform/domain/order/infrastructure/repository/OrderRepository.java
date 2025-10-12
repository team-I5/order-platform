package com.spartaclub.orderplatform.domain.order.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @EntityGraph(attributePaths = {"store", "user"})
    Page<Order> findByUser_UserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "user"})
    Page<Order> findByStore_User_UserId(Long userId, Pageable pageable);

    @EntityGraph(attributePaths = {"store", "user"})
    Page<Order> findAll(Pageable pageable);
}
