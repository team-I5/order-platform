package com.spartaclub.orderplatform.domain.store.presentation.controller;

import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.eq;
import static org.mockito.BDDMockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreDetailResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.security.WithMockCustomUser;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = StoreController.class)
@AutoConfigureMockMvc(addFilters = true)
@Import(StoreControllerTest.SecurityTestConfig.class)
class StoreControllerTest {

    @TestConfiguration
    @EnableMethodSecurity(prePostEnabled = true)
    static class SecurityTestConfig {

    }

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    StoreService storeService;

    @Test
    @DisplayName("음식점 목록 조회")
    @WithMockCustomUser
    void searchStore_ok() throws Exception {
        Page<StoreSearchResponseDto> dummyPage =
            new PageImpl<>(List.of(mock(StoreSearchResponseDto.class)));

        Mockito.when(storeService.searchStore(any(), any(), any(Pageable.class)))
            .thenReturn(dummyPage);

        mockMvc.perform(get("/v1/stores/search")
                .param("status", "APPROVED")
                .param("page", "0")
                .param("size", "5")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService).searchStore(any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("음식점 상세 조회")
    @WithMockCustomUser
    void searchStoreDetail_ok() throws Exception {
        UUID storeId = UUID.randomUUID();
        StoreDetailResponseDto dummy = Mockito.mock(StoreDetailResponseDto.class);
        Mockito.when(storeService.searchStoreDetail(eq(storeId), any(), any()))
            .thenReturn(dummy);

        mockMvc.perform(get("/v1/stores/search/{storeId}", storeId)
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService)
            .searchStoreDetail(eq(storeId), any(), any());
    }

    @Test
    @DisplayName("음식점 카테고리별 검색")
    @WithMockCustomUser
    void searchStoreCategory_ok() throws Exception {
        Page<StoreSearchResponseDto> dummyPage =
            new PageImpl<>(List.of(Mockito.mock(StoreSearchResponseDto.class)));

        Mockito.when(storeService.searchStoreByCategory(any(), any(), any(Pageable.class)))
            .thenReturn(dummyPage);

        mockMvc.perform(get("/v1/stores/search-category")
                .param("categoryType", "KOREAN")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService)
            .searchStoreByCategory(any(), any(), any(Pageable.class));
    }

    @Test
    @DisplayName("음식점 키워드 검색")
    @WithMockCustomUser
    void searchStoreListByKeyword_ok() throws Exception {
        Page<StoreSearchResponseDto> dummyPage =
            new PageImpl<>(List.of(Mockito.mock(StoreSearchResponseDto.class)));

        Mockito.when(storeService.searchStoreListByKeyword(any(), any(Pageable.class)))
            .thenReturn(dummyPage);

        mockMvc.perform(get("/v1/stores/search-store-name")
                .param("keyword", "pizza")
                .param("page", "0")
                .param("size", "10")
                .with(csrf()))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));

        Mockito.verify(storeService)
            .searchStoreListByKeyword(any(), any(Pageable.class));
    }

    @Test
    @DisplayName("권한 없는 사용자")
    @WithMockUser(username = "anonymous", roles = {})
    void searchStore_forbidden() throws Exception {
        mockMvc.perform(get("/v1/stores/search")
                .param("status", "APPROVED")
                .param("page", "0")
                .param("size", "5"))
            .andExpect(status().isForbidden());
    }

}