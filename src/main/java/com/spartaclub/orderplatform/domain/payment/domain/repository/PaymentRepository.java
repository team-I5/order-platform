package com.spartaclub.orderplatform.domain.payment.domain.repository;

import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, UUID> {

}
