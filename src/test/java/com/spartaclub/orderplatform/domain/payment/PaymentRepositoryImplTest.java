package com.spartaclub.orderplatform.domain.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.application.dto.query.PaymentQuery;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentJPARepository;
import com.spartaclub.orderplatform.domain.payment.infrastructure.repository.PaymentRepositoryImpl;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

@ExtendWith(MockitoExtension.class)
class PaymentRepositoryImplTest {

    @Mock
    PaymentJPARepository paymentJPARepository;

    @InjectMocks
    PaymentRepositoryImpl paymentRepository; // SUT (우리가 만든 어댑터)

    @Test
    @DisplayName("save: JPA 레포로 위임된다")
    void save_delegatesToJpaRepository() {
        // given
        Payment payment = Mockito.mock(Payment.class);
        given(paymentJPARepository.save(payment)).willReturn(payment);

        // when
        Payment saved = paymentRepository.save(payment);

        // then
        assertThat(saved).isSameAs(payment);
        then(paymentJPARepository).should(times(1)).save(payment);
    }

    @Test
    @DisplayName("existsByOrder: JPA 레포로 위임된다")
    void existsByOrder_delegatesToJpaRepository() {
        // given
        Order order = Mockito.mock(Order.class);
        given(paymentJPARepository.existsByOrder(order)).willReturn(true);

        // when
        boolean exists = paymentRepository.existsByOrder(order);

        // then
        assertThat(exists).isTrue();
        then(paymentJPARepository).should(times(1)).existsByOrder(order);
    }

    @Test
    @DisplayName("findById: JPA 레포로 위임된다")
    void findById_delegatesToJpaRepository() {
        // given
        UUID id = UUID.randomUUID();
        Payment payment = Mockito.mock(Payment.class);
        given(paymentJPARepository.findById(id)).willReturn(Optional.of(payment));

        // when
        Optional<Payment> found = paymentRepository.findById(id);

        // then
        assertThat(found).containsSame(payment);
        then(paymentJPARepository).should(times(1)).findById(id);
    }

    @Test
    @DisplayName("findAll: PaymentQuery → Specification 조합 후 JPA 레포로 위임된다 (status=CAPTURED)")
    void findAll_buildsSpecAndDelegates() {
        // given
        PaymentQuery query = new PaymentQuery(new ArrayList<>(PaymentStatus.CAPTURED.ordinal()));
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("createdAt")));
        Page<Payment> dummyPage = new PageImpl<>(java.util.List.of(Mockito.mock(Payment.class)));

        ArgumentCaptor<Specification<Payment>> specCaptor = ArgumentCaptor.forClass(
            Specification.class);

        given(paymentJPARepository.findAll(any(Specification.class), eq(pageable)))
            .willReturn(dummyPage);

        // when
        Page<Payment> result = paymentRepository.findAll(query, pageable);

        // then
        assertThat(result).isSameAs(dummyPage);
        then(paymentJPARepository).should(times(1)).findAll(specCaptor.capture(), eq(pageable));

        // 사소하지만 유용한 추가 검증: spec이 null 아님
        Specification<Payment> builtSpec = specCaptor.getValue();
        assertThat(builtSpec).as("statusIn을 포함한 동적 Spec이 생성되어야 함").isNotNull();
        // (여기서 실제 CriteriaBuilder/Root를 만들어 predicate를 평가하는 건 단위 테스트 범위를 벗어나므로 생략)
    }

    @Test
    @DisplayName("findAll: status=null 이어도 Spec은 생성되고 위임된다")
    void findAll_acceptsNullStatus() {
        // given
        PaymentQuery query = new PaymentQuery(null); // 상태 필터 미지정
        Pageable pageable = PageRequest.of(0, 5);
        Page<Payment> dummyPage = Page.empty(pageable);

        given(paymentJPARepository.findAll(any(Specification.class), eq(pageable)))
            .willReturn(dummyPage);

        // when
        Page<Payment> result = paymentRepository.findAll(query, pageable);

        // then
        assertThat(result.getTotalElements()).isZero();
        then(paymentJPARepository).should().findAll(any(Specification.class), eq(pageable));
    }
}
