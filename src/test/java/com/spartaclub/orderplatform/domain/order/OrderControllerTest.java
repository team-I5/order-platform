package com.spartaclub.orderplatform.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.order.application.service.OrderService;
import com.spartaclub.orderplatform.domain.order.application.service.query.OrderQueryFacade;
import com.spartaclub.orderplatform.domain.order.domain.model.OrderStatus;
import com.spartaclub.orderplatform.domain.order.presentation.controller.OrderController;
import com.spartaclub.orderplatform.domain.order.presentation.dto.request.PlaceOrderRequestDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderDetailResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrderStatusResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.OrdersResponseDto;
import com.spartaclub.orderplatform.domain.order.presentation.dto.response.PlaceOrderResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
import com.spartaclub.orderplatform.security.WithMockCustomOwner;
import com.spartaclub.orderplatform.security.WithMockCustomUserCustomer;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * 웹 계층 슬라이스 테스트: Security 필터는 사용하고, OrderService/OrderQueryFacade는 mock으로 주입.
 */
@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    OrderService orderService;
    @MockitoBean
    OrderQueryFacade orderQueryFacade;


    // --------------------------------------------------------------------
    // 주문 생성
    // --------------------------------------------------------------------
    @Test
    @DisplayName("주문 생성: 201 Created + Location 헤더 + 서비스에 (PlaceOrderRequestDto, User) 전달")
    @WithMockCustomUserCustomer
    void placeOrder_created() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        PlaceOrderResponseDto resp = new PlaceOrderResponseDto(orderId);
        given(orderService.placeOrder(any(PlaceOrderRequestDto.class), any(User.class)))
            .willReturn(resp);

        // 요청 바디 (프로젝트 DTO 정의에 맞춰 필드 세팅)
        PlaceOrderRequestDto req = new PlaceOrderRequestDto(
            UUID.randomUUID(), // storeId
            "서울",            // address
            List.of(new PlaceOrderRequestDto.OrderItemRequest(UUID.randomUUID(), 1)), // items
            "빨리 부탁드려요"   // memo
        );

        // when + then
        mockMvc.perform(post("/v1/orders")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(req)))
            .andExpect(status().isCreated())
            .andExpect(header().string("Location", "/v1/orders/" + orderId))
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.orderId").value(orderId.toString()));

        // --- 서비스 호출 인자 검증 (PlaceOrderRequestDto, User 순서/타입) ---
        ArgumentCaptor<PlaceOrderRequestDto> reqCaptor = ArgumentCaptor.forClass(
            PlaceOrderRequestDto.class);
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(orderService).placeOrder(reqCaptor.capture(), userCaptor.capture());

        PlaceOrderRequestDto capturedReq = reqCaptor.getValue();
        User capturedUser = userCaptor.getValue();

        assertThat(capturedReq.storeId()).isEqualTo(req.storeId());
        assertThat(capturedReq.address()).isEqualTo("서울");
        assertThat(capturedReq.items()).hasSize(1);
        assertThat(
            capturedUser).isNotNull(); // @WithMockCustomUserCustomer가 주입한 UserDetailsImpl.getUser()
    }

    // --------------------------------------------------------------------
    // 주문 상세 조회
    // --------------------------------------------------------------------
    @Test
    @DisplayName("주문 상세 조회: 200 OK")
    @WithMockCustomUserCustomer
    void getOrderDetail_ok() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        Object dummy = Mockito.mock(OrderDetailResponseDto.class);
        Mockito.when(orderQueryFacade.getOrderDetail(eq(orderId), any()))
            .thenReturn((OrderDetailResponseDto) dummy);

        // when & then
        mockMvc.perform(get("/v1/orders/{orderId}", orderId)
                .header("Authorization", "Bearer asfa122sf"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    // --------------------------------------------------------------------
    // 주문 목록 조회 (페이지네이션)
    // --------------------------------------------------------------------
    @Test
    @DisplayName("주문 목록 조회: 200 OK")
    @WithMockCustomUserCustomer
    void getOrders_ok() throws Exception {
        // given
        OrdersResponseDto dummy = Mockito.mock(OrdersResponseDto.class);
        Mockito.when(orderQueryFacade.getOrders(any(), any(), any(Pageable.class)))
            .thenReturn(dummy);

        // 상태 필터는 생략(모델 속성 바인딩 실패 방지), 페이지 파라미터만 보냄
        mockMvc.perform(get("/v1/orders")
                .header("Authorization", "Bearer asfa122sf")
                .param("page", "0")
                .param("size", "10")
                .param("sort", "createdAt,desc"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    // --------------------------------------------------------------------
    // 주문 취소
    // --------------------------------------------------------------------
    @Test
    @DisplayName("주문 취소: 200 OK")
    @WithMockCustomUserCustomer
    void cancelOrder_ok() throws Exception {
        // given
        UUID orderId = UUID.randomUUID();
        OrderStatusResponseDto resp = new OrderStatusResponseDto(orderId, OrderStatus.CANCELED);
        given(orderService.cancelOrder(any(UserDetailsImpl.class), eq(orderId)))
            .willReturn(resp);

        // when & then
        mockMvc.perform(post("/v1/orders/{orderId}/cancel", orderId)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.orderId").value(orderId.toString()))
            .andExpect(jsonPath("$.data.status").value("CANCELED"));
    }

    // --------------------------------------------------------------------
    // 점주 권한(OWNER) 엔드포인트
    // --------------------------------------------------------------------
    @Nested
    @DisplayName("OWNER 전용 엔드포인트")
    class OwnerEndpoints {

        @Test
        @DisplayName("주문 승인(OWNER): 200 OK")
        @WithMockCustomOwner
        void acceptOrder_ok_owner() throws Exception {
            UUID orderId = UUID.randomUUID();
            given(orderService.acceptOrder(any(UserDetailsImpl.class), eq(orderId)))
                .willReturn(new OrderStatusResponseDto(orderId, OrderStatus.ACCEPTED));

            mockMvc.perform(post("/v1/orders/{orderId}/accept", orderId)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("ACCEPTED"));
        }

        @Test
        @DisplayName("주문 승인: CUSTOMER 권한으로 접근 시 403")
        @WithMockUser(username = "user", roles = {"CUSTOMER"})
        void acceptOrder_forbidden_whenCustomer() throws Exception {
            UUID orderId = UUID.randomUUID();
            mockMvc.perform(post("/v1/orders/{orderId}/accept", orderId))
                .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("주문 거부(OWNER): 200 OK")
        @WithMockCustomOwner
        void rejectOrder_ok_owner() throws Exception {
            UUID orderId = UUID.randomUUID();
            given(orderService.rejectOrder(any(UserDetailsImpl.class), eq(orderId)))
                .willReturn(new OrderStatusResponseDto(orderId, OrderStatus.REJECTED));

            mockMvc.perform(post("/v1/orders/{orderId}/reject", orderId)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("REJECTED"));
        }

        @Test
        @DisplayName("주문 배달 완료(OWNER): 200 OK")
        @WithMockCustomOwner
        void completeDelivery_ok_owner() throws Exception {
            UUID orderId = UUID.randomUUID();
            given(orderService.completeDelivery(any(UserDetailsImpl.class), eq(orderId)))
                .willReturn(new OrderStatusResponseDto(orderId, OrderStatus.DELIVERED));

            mockMvc.perform(post("/v1/orders/{orderId}/complete-delivery", orderId)
                    .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("DELIVERED"));
        }
    }
}

