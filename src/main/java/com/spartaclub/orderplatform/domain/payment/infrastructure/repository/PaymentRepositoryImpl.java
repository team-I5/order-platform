package com.spartaclub.orderplatform.domain.payment.infrastructure.repository;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.application.dto.query.PaymentQuery;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.repository.PaymentRepository;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.spec.PaymentSpecs;
import java.util.Optional;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class PaymentRepositoryImpl implements PaymentRepository {

    private final PaymentJPARepository paymentJPARepository;


    @Override
    public void save(Payment payment) {
        paymentJPARepository.save(payment);
    }

    @Override
    public boolean existsByOrder(Order order) {
        return paymentJPARepository.existsByOrder(order);
    }

    @Override
    public Optional<Payment> findById(UUID paymentId) {
        return paymentJPARepository.findById(paymentId);
    }

    @Override
    public Page<Payment> findAll(PaymentQuery paymentQuery, Pageable pageable) {
        Specification<Payment> spec = (root, query, cb) -> cb.conjunction();
        spec = spec.and(PaymentSpecs.statusIn(paymentQuery.status()));
        return paymentJPARepository.findAll(spec, pageable);
    }
}
