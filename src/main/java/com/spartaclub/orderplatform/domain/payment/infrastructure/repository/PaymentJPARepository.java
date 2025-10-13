package com.spartaclub.orderplatform.domain.payment.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentJPARepository extends JpaRepository<Payment, UUID>,
    JpaSpecificationExecutor<Payment> {

    boolean existsByOrder(Order order);
}
