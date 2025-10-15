package com.spartaclub.orderplatform.domain.payment.domain.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.application.dto.query.PaymentQuery;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PaymentRepository {

    void save(Payment payment);

    boolean existsByOrder(Order order);

    Optional<Payment> findById(UUID paymentId);

    Page<Payment> findAll(PaymentQuery paymentQuery, Pageable pageable);
}
