package com.spartaclub.orderplatform.domain.payment.application;

import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.AUTHORIZED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.CAPTURED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.FAILED;
import static com.spartaclub.orderplatform.domain.payment.domain.model.PaymentStatus.REFUNDED;

import com.spartaclub.orderplatform.domain.order.application.OrderService;
import com.spartaclub.orderplatform.domain.order.domain.model.Order;
import com.spartaclub.orderplatform.domain.payment.application.dto.query.PaymentQuery;
import com.spartaclub.orderplatform.domain.payment.application.mapper.PaymentMapper;
import com.spartaclub.orderplatform.domain.payment.domain.model.Payment;
import com.spartaclub.orderplatform.domain.payment.domain.repository.PaymentRepository;
import com.spartaclub.orderplatform.domain.payment.exception.PaymentErrorCode;
import com.spartaclub.orderplatform.domain.payment.infrastructure.pg.TossPaymentsClient;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.CancelPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.ConfirmPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.GetPaymentsListRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.request.InitPaymentRequestDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.InitPaymentResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.exception.BusinessException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderService orderService;
    private final TossPaymentsClient tossPaymentsClient;
    private final PaymentMapper paymentMapper;

    //결제 생성
    @Transactional
    public InitPaymentResponseDto initPayment(InitPaymentRequestDto requestDto) {
        Order order = orderService.findById(requestDto.orderId());

        //주문 상태 및 결제 금액 검증
        order.validatePaymentAvailable(requestDto.amount());

        if (paymentRepository.existsByOrder(order)) {
            log.warn("[Payment] Duplicate Payment Exception");
            throw new BusinessException(PaymentErrorCode.DUPLICATE_PAYMENT);
        }

        //PG사 결제 요청
        String redirectUrl = tossPaymentsClient.requestPaymentReady(requestDto.amount());

        //리다이렉트 URL 파싱
        String[] redirectUrlParts = parseRedirectUrl(redirectUrl);
        String result = redirectUrlParts[0];
        String PgPaymentKey = redirectUrlParts[1];
        String PgOrderId = redirectUrlParts[2];

        if (result.equals("success")) {
            Payment payment = Payment.ofStatus(order, AUTHORIZED, requestDto.amount(),
                PgPaymentKey, PgOrderId);
            paymentRepository.save(payment);
            return new InitPaymentResponseDto(payment.getPaymentId(), redirectUrl, PgPaymentKey,
                PgOrderId);
        } else {
            Payment payment = Payment.ofStatus(order, FAILED, requestDto.amount());
            paymentRepository.save(payment);
            return new InitPaymentResponseDto(payment.getPaymentId(), redirectUrl, null, null);
        }
    }

    //결제 승인
    @Transactional
    public void confirmPayment(ConfirmPaymentRequestDto requestDto, UUID paymentId) {
        Order order = orderService.findById(requestDto.orderId());

        //주문 상태 및 결제 금액 검증
        order.validatePaymentAvailable(requestDto.amount());

        Payment payment = findById(paymentId);

        //결제 상태, PG 결제키, PG orderId, 금액 검증
        payment.validateApproval(requestDto.pgPaymentKey(), requestDto.pgOrderId(),
            requestDto.amount());

        boolean success = tossPaymentsClient.confirmPayment(requestDto.pgPaymentKey(),
            requestDto.pgOrderId(), requestDto.amount());

        if (success) {
            payment.changeStatus(CAPTURED);
        }
//        else {
//            payment.changeStatus(PaymentStatus.FAILED);
//        }
    }

    //결제 취소
    @Transactional
    public void cancelPayment(CancelPaymentRequestDto requestDto, UUID paymentId) {
        Payment payment = findById(paymentId);

        //결제 취소 검증
        payment.checkCancelable(requestDto.pgPaymentKey());

        boolean success = tossPaymentsClient.cancelPayment(requestDto.pgPaymentKey(),
            requestDto.cancelReason());
        if (success) {
            payment.changeStatus(REFUNDED);
        }
    }

    //결제 상세 조회
    public PaymentDetailResponseDto getPaymentDetail(UUID paymentId) {
        Payment payment = findById(paymentId);
        return paymentMapper.toDto(payment);
    }

    public Payment findById(UUID paymentId) {
        return paymentRepository.findById(paymentId)
            .orElseThrow(() -> {
                log.warn("[Payment] NOT_EXIST - paymentId={}", paymentId);
                return new BusinessException(PaymentErrorCode.NOT_EXIST);
            });
    }

    private String[] parseRedirectUrl(String redirectUrl) {
        String[] resultParts = new String[3];
        try {
            // 결과 구분
            resultParts[0] = redirectUrl.contains("success") ? "success" : "fail";

            // 쿼리 파라미터 부분만 분리
            String[] split = redirectUrl.split("\\?");
            if (split.length < 2) {
                log.warn("[RedirectURL-Parsing] 잘못된 형식 - '?' 구분자가 없습니다. URL: {}", redirectUrl);
                throw new BusinessException(PaymentErrorCode.INVALID_REDIRECT_URL_FORMAT);
            }

            // 각 파라미터를 '=' 기준으로 분리
            String[] params = split[1].split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                if (keyValue.length != 2) {
                    log.warn("[RedirectURL-Parsing] 잘못된 파라미터 형식: {}", param);
                    throw new BusinessException(PaymentErrorCode.INVALID_REDIRECT_URL_FORMAT);
                }
                String key = keyValue[0];
                String value = keyValue[1];
                if (key.equals("paymentKey")) {
                    resultParts[1] = value;
                } else if (key.equals("orderId")) {
                    resultParts[2] = value;
                }
            }

        } catch (Exception e) {
            log.warn("[RedirectURL-Parsing] Payment 예외 발생 - message={}", e.getMessage());
            throw new BusinessException(PaymentErrorCode.REDIRECT_URL_PARSING_FAILED);
        }
        return resultParts;
    }

    //결제 전체 조회
    public PaymentsListResponseDto getPayments(GetPaymentsListRequestDto requestDto, User user) {
        //페이징 객체 생성
        PageRequest pageable = PageRequest.of(requestDto.page() - 1, requestDto.size(),
            parseSort(requestDto.sort()));

        //조회
        PaymentQuery paymentQuery = new PaymentQuery(requestDto.status());
        Page<Payment> paymentPage = paymentRepository.findAll(paymentQuery, pageable);

        //매핑
        List<PaymentDetailResponseDto> payments = paymentPage.stream()
            .map(paymentMapper::toDto)
            .collect(Collectors.toList());

        PaymentsListResponseDto.PageableDto pageableDto = paymentMapper.toPageableDto(paymentPage);

        return new PaymentsListResponseDto(payments, pageableDto);
    }

    //페이지네이션 Sort 객체 생성
    private Sort parseSort(List<String> sortParams) {
        //기본값
        String defaultProperty = "createdAt";
        Sort.Direction defaultDir = Sort.Direction.DESC;
        // 정렬 허용 필드
        Set<String> allowedProperties = Set.of("createdAt", "paymentAmount");

        List<Sort.Order> orders = new ArrayList<>();

        for (String param : sortParams) {
            if (param == null || param.isBlank()) {
                continue;
            }

            String[] parts = param.split(",");
            String property = parts[0].trim();
            Sort.Direction direction = (parts.length > 1 && "asc".equalsIgnoreCase(parts[1].trim()))
                ? Sort.Direction.ASC : Sort.Direction.DESC;

            if (allowedProperties.contains(property)) {
                orders.add(new Sort.Order(direction, property));
            }
        }

        return orders.isEmpty() ? Sort.by(defaultDir, defaultProperty) : Sort.by(orders);
    }
}
