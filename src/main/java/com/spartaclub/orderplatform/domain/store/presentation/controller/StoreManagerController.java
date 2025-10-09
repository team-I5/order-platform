package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/managers/stores")
@RequiredArgsConstructor
@RestController
public class StoreManagerController {

    private final StoreService storeService;

    // Manager의 음식점 승인 API
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{storeId}/approve")
    public ResponseEntity<ApiResponse<StoreResponseDto>> approveStore(
        @PathVariable("storeId") UUID storeId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.approveStore(storeId)));
    }

    // Manager의 음식점 거절 API
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{storeId}/reject")
    public ResponseEntity<ApiResponse<RejectStoreResponseDto>> rejectStore(
        @PathVariable("storeId") UUID storeId,
        @Valid @RequestBody RejectStoreRequestDto dto
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.rejectStore(storeId, dto)));
    }
}
