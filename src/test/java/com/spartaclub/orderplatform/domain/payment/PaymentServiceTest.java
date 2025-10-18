package com.spartaclub.orderplatform.domain.payment;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.application.mapper.PaymentMapper;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus;
import com.spartaclub.orderplatform.domain.payment.domain.repository.PaymentRepository;
import com.spartaclub.orderplatform.domain.payment.infrastructure.pg.TossPaymentsClient;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.CancelPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.GetPaymentsListRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.InitPaymentResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;
    @Mock
    private OrderService orderService;
    @Mock
    private TossPaymentsClient tossPaymentsClient;
    @Mock
    private PaymentMapper paymentMapper;

    @InjectMocks
    private PaymentService paymentService;

    // ----------------------------------------------------------------
    // 결제 생성 테스트
    // ----------------------------------------------------------------
    @Test
    @DisplayName("결제 생성 성공 - PG 요청 성공 시 CAPTURED 대기 상태로 저장")
    void initPayment_success() {
        // given
        UUID orderId = UUID.randomUUID();
        long amount = 10000L;
        InitPaymentRequestDto request = new InitPaymentRequestDto(orderId, "card", amount);
        Order order = mock(Order.class);

        given(orderService.findById(orderId)).willReturn(order);
        willDoNothing().given(order).validatePaymentAvailable(amount);
        given(paymentRepository.existsByOrder(order)).willReturn(false);
        given(tossPaymentsClient.requestPaymentReady(amount))
            .willReturn("https://toss.com/pay/success?paymentKey=abc123&orderId=ord-001");

        Payment payment = Payment.ofStatus(order, PaymentStatus.PAYMENT_PENDING, amount, "abc123",
            "ord-001");
        given(paymentRepository.save(any(Payment.class))).willReturn(payment);

        // when
        InitPaymentResponseDto result = paymentService.initPayment(request);

        // then
        assertThat(result).isNotNull();
        assertThat(result.PgPaymentKey()).isEqualTo("abc123");
        assertThat(result.PgOrderId()).isEqualTo("ord-001");
        then(paymentRepository).should(times(1)).save(any(Payment.class));
    }

    @Test
    @DisplayName("결제 생성 실패 - 중복 결제 시 BusinessException 발생")
    void initPayment_duplicatePayment() {
        // given
        UUID orderId = UUID.randomUUID();
        InitPaymentRequestDto request = new InitPaymentRequestDto(orderId, "card", 10000L);
        Order order = mock(Order.class);

        given(orderService.findById(orderId)).willReturn(order);
        given(paymentRepository.existsByOrder(order)).willReturn(true);

        // when + then
        assertThatThrownBy(() -> paymentService.initPayment(request))
            .isInstanceOf(BusinessException.class);
    }

    // ----------------------------------------------------------------
    // 결제 승인 테스트
    // ----------------------------------------------------------------
    @Test
    @DisplayName("결제 승인 성공 시 CAPTURED 상태로 변경")
    void confirmPayment_success() {
        // given
        UUID paymentId = UUID.randomUUID();
        UUID orderId = UUID.randomUUID();
        ConfirmPaymentRequestDto request = new ConfirmPaymentRequestDto(orderId, "pgKey", "ord001",
            10000L);

        Order order = mock(Order.class);
        Payment payment = mock(Payment.class);

        given(orderService.findById(orderId)).willReturn(order);
        willDoNothing().given(order).validatePaymentAvailable(10000L);
        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        willDoNothing().given(payment).validateApproval("pgKey", "ord001", 10000L);
        given(tossPaymentsClient.confirmPayment("pgKey", "ord001", 10000L)).willReturn(true);

        // when
        paymentService.confirmPayment(request, paymentId);

        // then
        then(payment).should().changeStatus(PaymentStatus.CAPTURED);
    }

    // ----------------------------------------------------------------
    // 결제 취소 테스트
    // ----------------------------------------------------------------
    @Test
    @DisplayName("결제 취소 성공 시 REFUNDED 상태로 변경")
    void cancelPayment_success() {
        // given
        UUID paymentId = UUID.randomUUID();
        CancelPaymentRequestDto request = new CancelPaymentRequestDto("pgKey", "사용자 요청 취소");
        Payment payment = mock(Payment.class);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        willDoNothing().given(payment).checkCancelable("pgKey");
        given(tossPaymentsClient.cancelPayment("pgKey", "사용자 요청 취소")).willReturn(true);

        // when
        paymentService.cancelPayment(request, paymentId);

        // then
        then(payment).should().changeStatus(PaymentStatus.REFUNDED);
    }

    // ----------------------------------------------------------------
    // 결제 상세 조회 테스트
    // ----------------------------------------------------------------
    @Test
    @DisplayName("결제 상세 조회 성공")
    void getPaymentDetail_success() {
        // given
        UUID paymentId = UUID.randomUUID();
        Payment payment = mock(Payment.class);
        PaymentDetailResponseDto dto = new PaymentDetailResponseDto(paymentId, UUID.randomUUID(),
            10000L, PaymentStatus.CAPTURED.name(), "pgKey", "ord001", null);

        given(paymentRepository.findById(paymentId)).willReturn(Optional.of(payment));
        given(paymentMapper.toDto(payment)).willReturn(dto);

        // when
        PaymentDetailResponseDto result = paymentService.getPaymentDetail(paymentId);

        // then
        assertThat(result.paymentId()).isEqualTo(paymentId);
    }

    // ----------------------------------------------------------------
    // 결제 전체 조회 테스트
    // ----------------------------------------------------------------
    @Test
    @DisplayName("결제 목록 조회 성공")
    void getPayments_success() {
        // given
        GetPaymentsListRequestDto request = new GetPaymentsListRequestDto(new ArrayList<>(
            PaymentStatus.CAPTURED.ordinal()));
        Pageable pageable = PageRequest.of(0, 10);
        Payment payment = mock(Payment.class);
        Page<Payment> page = new PageImpl<>(List.of(payment));
        PaymentDetailResponseDto dto = mock(PaymentDetailResponseDto.class);
        PaymentsListResponseDto.PageableDto pageableDto = mock(
            PaymentsListResponseDto.PageableDto.class);

        given(paymentRepository.findAll(any(), any(Pageable.class))).willReturn(page);
        given(paymentMapper.toDto(payment)).willReturn(dto);
        given(paymentMapper.toPageableDto(page)).willReturn(pageableDto);

        // when
        PaymentsListResponseDto result = paymentService.getPayments(request, null, pageable);

        // then
        assertThat(result).isNotNull();
        then(paymentRepository).should().findAll(any(), any(Pageable.class));
    }
}
