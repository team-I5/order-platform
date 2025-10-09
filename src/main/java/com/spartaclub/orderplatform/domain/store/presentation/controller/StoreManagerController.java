package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.user.domain.entity.User;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable("storeId") UUID storeId
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.approveStore(user, storeId)));
    }

    // Manager의 음식점 거절 API
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{storeId}/reject")
    public ResponseEntity<ApiResponse<RejectStoreResponseDto>> rejectStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable("storeId") UUID storeId,
        @Valid @RequestBody RejectStoreRequestDto dto
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.rejectStore(user, storeId, dto)));
    }
}
