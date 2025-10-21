package com.spartaclub.orderplatform.domain.store.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.security.WithMockCustomManager;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StoreManagerController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(StoreManagerControllerTest.SecurityTestConfig.class)
class StoreManagerControllerTest {

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class SecurityTestConfig {

    }

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    StoreService storeService;

    private UUID storeId;
    private RejectStoreRequestDto rejectStoreRequestDto;

    @BeforeEach
    void setup() {
        storeId = UUID.randomUUID();

        rejectStoreRequestDto = new RejectStoreRequestDto();
        rejectStoreRequestDto.setRejectReason("정보 불충분");
    }

    @Test
    @DisplayName("음식점 승인")
    @WithMockCustomManager
    void approveStore_ok() throws Exception {
        StoreResponseDto dummy = Mockito.mock(StoreResponseDto.class);
        Mockito.when(storeService.approveStore(eq(storeId))).thenReturn(dummy);

        mockMvc.perform(patch("/v1/managers/stores/{storeId}/approve", storeId)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).approveStore(eq(storeId));
    }

    @Test
    @DisplayName("음식점 거절")
    @WithMockCustomManager
    void rejectStore_ok() throws Exception {
        RejectStoreResponseDto dummy = Mockito.mock(RejectStoreResponseDto.class);
        Mockito.when(storeService.rejectStore(eq(storeId), any())).thenReturn(dummy);

        mockMvc.perform(patch("/v1/managers/stores/{storeId}/reject", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(rejectStoreRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).rejectStore(eq(storeId), any());
    }

    @Test
    @DisplayName("권한 없는 사용자")
    @WithMockUser(username = "user", roles = {"OWNER"})
    void approveStore_forbidden() throws Exception {
        mockMvc.perform(patch("/v1/managers/stores/{storeId}/approve", storeId)
                .with(csrf()))
            .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("거절 요청 유효성 실패")
    @WithMockCustomManager
    void rejectStore_badRequest_whenBodyMissing() throws Exception {
        mockMvc.perform(patch("/v1/managers/stores/{storeId}/reject", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}