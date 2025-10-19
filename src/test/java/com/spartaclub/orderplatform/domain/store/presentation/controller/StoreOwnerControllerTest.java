package com.spartaclub.orderplatform.domain.store.presentation.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.security.WithMockCustomOwner;
import java.util.Collections;
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
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StoreOwnerController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(StoreOwnerControllerTest.SecurityTestConfig.class)
class StoreOwnerControllerTest {

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

    private StoreRequestDto storeRequestDto;
    private StoreCategoryRequestDto storeCategoryRequestDto;
    private UUID storeId;

    @BeforeEach
    void setup() {
        storeRequestDto = new StoreRequestDto();
        storeRequestDto.setStoreName("테스트 음식점");
        storeRequestDto.setStoreAddress("서울시 강남구");
        storeRequestDto.setStoreNumber("01012345678");
        storeRequestDto.setStoreDescription("테스트 음식점 설명");

        storeCategoryRequestDto = new StoreCategoryRequestDto();
        storeCategoryRequestDto.setCategoryIds(Collections.singletonList(UUID.randomUUID()));

        storeId = UUID.randomUUID();
    }

    @Test
    @DisplayName("음식점 생성")
    @WithMockCustomOwner
    void createStore_ok() throws Exception {
        StoreResponseDto dummy = Mockito.mock(StoreResponseDto.class);
        Mockito.when(storeService.createStore(any(), any())).thenReturn(dummy);

        mockMvc.perform(post("/v1/owner/stores")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).createStore(any(), any());
    }

    @Test
    @DisplayName("음식점 재승인 요청")
    @WithMockCustomOwner
    void reapplyStore_ok() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreResponseDto dummy = Mockito.mock(StoreResponseDto.class);
        Mockito.when(storeService.reapplyStore(any(), eq(storeId), any())).thenReturn(dummy);

        mockMvc.perform(put("/v1/owner/stores/{storeId}/reapply", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).reapplyStore(any(), eq(storeId), any());
    }

    @Test
    @DisplayName("음식점 기본 정보 수정")
    @WithMockCustomOwner
    void updateApprovedStore_ok() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreResponseDto dummy = Mockito.mock(StoreResponseDto.class);
        Mockito.when(storeService.updateApprovedStore(any(), eq(storeId), any())).thenReturn(dummy);

        mockMvc.perform(patch("/v1/owner/stores/{storeId}", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).updateApprovedStore(any(), eq(storeId), any());
    }

    @Test
    @DisplayName("음식점 삭제")
    @WithMockCustomOwner
    void deleteStore_ok() throws Exception {
        UUID storeId = UUID.randomUUID();

        mockMvc.perform(delete("/v1/owner/stores/{storeId}", storeId)
                .with(csrf()))
            .andExpect(status().isNoContent())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).deleteStore(any(), eq(storeId));
    }

    @Test
    @DisplayName("음식점에 카테고리 등록")
    @WithMockCustomOwner
    void addCategoryToStore_ok() throws Exception {
        StoreCategoryResponseDto dummy = Mockito.mock(StoreCategoryResponseDto.class);
        Mockito.when(storeService.addCategoryToStore(eq(storeId), any(), any())).thenReturn(dummy);

        mockMvc.perform(post("/v1/owner/stores/{storeId}/categories", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeCategoryRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).addCategoryToStore(eq(storeId), any(), any());
    }

    @Test
    @DisplayName("음식점에 카테고리 수정")
    @WithMockCustomOwner
    void updateCategoryToStore_ok() throws Exception {
        StoreCategoryResponseDto dummy = Mockito.mock(StoreCategoryResponseDto.class);
        Mockito.when(storeService.updateCategoryToStore(eq(storeId), any(), any()))
            .thenReturn(dummy);

        mockMvc.perform(put("/v1/owner/stores/{storeId}/categories", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeCategoryRequestDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).updateCategoryToStore(eq(storeId), any(), any());
    }

    @Test
    @DisplayName("음식점에 카테고리 삭제")
    @WithMockCustomOwner
    void deleteCategoryFromStore_noContent() throws Exception {
        mockMvc.perform(delete("/v1/owner/stores/{storeId}/categories", storeId)
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(storeCategoryRequestDto)))
            .andExpect(status().isNoContent());

        Mockito.verify(storeService).deleteCategoryFromStore(eq(storeId), any(), any());
    }
}