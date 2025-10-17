package com.spartaclub.orderplatform.domain.payment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.payment.application.PaymentService;
import com.spartaclub.orderplatform.domain.payment.presentation.controller.PaymentController;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.InitPaymentResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentDetailResponseDto;
import com.spartaclub.orderplatform.domain.payment.presentation.dto.response.PaymentsListResponseDto;
import com.spartaclub.orderplatform.security.WithMockCustomUser;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = PaymentController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(PaymentControllerTest.SecurityTestConfig.class)
class PaymentControllerTest {

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class SecurityTestConfig {

    }

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    // Service는 목으로 대체
    @MockitoBean
    PaymentService paymentService;

    // DTO들은 생성자/필드가 미정일 수 있어, 안전하게 Mockito.mock()으로 더미 객체를 사용합니다.
    @Test
    @DisplayName("결제 초기화: 201 Created + 응답 본문")
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void initPayment_created() throws Exception {
        Object dummyResponse = Mockito.mock(InitPaymentResponseDto.class);
        Mockito.when(paymentService.initPayment(any()))
            .thenReturn((InitPaymentResponseDto) dummyResponse);

        Map<String, Object> body = Map.of(
            "orderId", UUID.randomUUID().toString(),
            "amount", 34500,
            "method", "card"
        );

        mockMvc.perform(
                post("/v1/payments/init")
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(paymentService).initPayment(any());
    }

    @Test
    @DisplayName("결제 승인: 200 OK")
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void confirmPayment_ok() throws Exception {
        UUID paymentId = UUID.randomUUID();

        Map<String, Object> body = Map.of(
            "orderId", UUID.randomUUID().toString(),
            "pgPaymentKey", "pay_abc123",
            "amount", 34500,
            "pgOrderId", "ORDER-20251017-0001"
        );

        mockMvc.perform(
                post("/v1/payments/{paymentId}/confirm", paymentId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(paymentService).confirmPayment(any(), eq(paymentId));
    }

    @Test
    @DisplayName("결제 취소: 200 OK")
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void cancelPayment_ok() throws Exception {
        UUID paymentId = UUID.randomUUID();

        Map<String, Object> body = Map.of(
            "pgPaymentKey", "pay_abc123",
            "cancelReason", "사용자 취소 요청"
        );

        mockMvc.perform(
                post("/v1/payments/{paymentId}/cancel", paymentId)
                    .with(csrf())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(body))
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(paymentService).cancelPayment(any(), eq(paymentId));
    }

    @Test
    @DisplayName("결제 상세 조회: 200 OK + 응답 본문")
    @WithMockUser(username = "user", roles = {"CUSTOMER"})
    void getPaymentDetail_ok() throws Exception {
        UUID paymentId = UUID.randomUUID();
        Object dummy = Mockito.mock(PaymentDetailResponseDto.class);
        Mockito.when(paymentService.getPaymentDetail(eq(paymentId)))
            .thenReturn((PaymentDetailResponseDto) dummy);

        mockMvc.perform(get("/v1/payments/{paymentId}", paymentId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(paymentService).getPaymentDetail(paymentId);
    }

    @Nested
    @DisplayName("관리자 결제 목록 조회")
    class AdminListTests {

        @Test
        @DisplayName("권한 없음: 403 Forbidden")
        @WithMockUser(username = "user", roles = {"CUSTOMER"})
        void getPaymentsList_forbiddenWithoutAdminRole() throws Exception {
            mockMvc.perform(get("/v1/payments")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "createdAt,desc"))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("MANAGER 권한: 200 OK")
        @WithMockCustomUser
        void getPaymentsList_okWithManagerRole() throws Exception {
            // given
            PaymentsListResponseDto dummy = Mockito.mock(PaymentsListResponseDto.class);
            Mockito.when(paymentService.getPayments(any(), any(), any(Pageable.class)))
                .thenReturn(dummy);

            // when + then
            mockMvc.perform(get("/v1/payments")
                    .header("Authorization", "Bearer asfa122sf")
                    .param("page", "0")
                    .param("size", "10")
                    .param("sort", "createdAt,desc")
                    .param("status", "CAPTURED"))           // PaymentStatus 실제 값 사용
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andDo(print()); // 실패 시 원인 로그 확인용

            Mockito.verify(paymentService).getPayments(any(), any(), any(Pageable.class));
        }

        @Test
        @DisplayName("MASTER 권한: 200 OK")
        @WithMockCustomUser
        void getPaymentsList_okWithMasterRole() throws Exception {
            Object dummy = Mockito.mock(PaymentsListResponseDto.class);
            Mockito.when(paymentService.getPayments(any(), any(), any(Pageable.class)))
                .thenReturn((PaymentsListResponseDto) dummy);

            mockMvc.perform(get("/v1/payments")
                    .header("Authorization", "Bearer asfa122sf")
                    .param("page", "1")
                    .param("size", "20")
                    .param("sort", "createdAt,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));

            Mockito.verify(paymentService).getPayments(any(), any(), any(Pageable.class));
        }
    }

    @Nested
    @DisplayName("유효성 실패 케이스(예시)")
    class ValidationFailCases {

        @Test
        @DisplayName("결제 초기화: 본문 누락 시 400")
        @WithMockUser(username = "user", roles = {"CUSTOMER"})
        void initPayment_badRequest_whenBodyMissing() throws Exception {
            mockMvc.perform(
                    post("/v1/payments/init")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest()); // DTO @Valid 조건에 따라 400 발생 기대
        }

        @Test
        @DisplayName("결제 승인: 본문 누락 시 400")
        @WithMockUser(username = "user", roles = {"CUSTOMER"})
        void confirmPayment_badRequest_whenBodyMissing() throws Exception {
            UUID paymentId = UUID.randomUUID();
            mockMvc.perform(
                    post("/v1/payments/{paymentId}/confirm", paymentId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}")
                )
                .andExpect(status().isBadRequest());
        }
    }
}
