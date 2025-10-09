package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/owner/stores")
@RequiredArgsConstructor
@RestController
public class StoreOwnerController {

    private final StoreService storeService;

    // Owner의 음식점 생성 API
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping
    public ResponseEntity<ApiResponse<StoreResponseDto>> createStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody StoreRequestDto dto
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(storeService.createStore(user, dto)));
    }

    //Owner의 음식점 재승인 요청 API
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{storeId}/reapply")
    public ResponseEntity<ApiResponse<StoreResponseDto>> reapplyStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreRequestDto dto
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.reapplyStore(user, storeId, dto)));
    }


    // Owner의 음식점 기본 정보 수정 API
    @PreAuthorize("hasRole('OWNER')")
    @PatchMapping("/{storeId}")
    public ResponseEntity<ApiResponse<StoreResponseDto>> updateApprovedStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreRequestDto dto

    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.updateApprovedStore(user, storeId, dto)));
    }

    // Owner의 음식점 삭제 API
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId
    ) {
        User user = userDetails.getUser();
        storeService.deleteStore(user, storeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
