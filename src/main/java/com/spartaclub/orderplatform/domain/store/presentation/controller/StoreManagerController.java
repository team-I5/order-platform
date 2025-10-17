package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.RejectStoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.RejectStoreResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Store - Manager", description = "매니저의 음식점 승인 관리 API")
public class StoreManagerController {

    private final StoreService storeService;

    // Manager의 음식점 승인 API
    @Operation(summary = "음식점 승인", description = "Manager가 음식점 생성을 승인합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 승인 성공", content = @Content(schema = @Schema(implementation = StoreResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인 대기 상태의 음식점만 승인 상태를 변경할 수 있습니다.")
    })
    @PreAuthorize("hasRole('MANAGER')")
    @PatchMapping("/{storeId}/approve")
    public ResponseEntity<ApiResponse<StoreResponseDto>> approveStore(
        @PathVariable("storeId") UUID storeId
    ) {
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.approveStore(storeId)));
    }

    // Manager의 음식점 거절 API
    @Operation(summary = "음식점 승인 거절", description = "Manager가 음식점 생성을 승인 거절합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 승인 거절 성공", content = @Content(schema = @Schema(implementation = RejectStoreResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인 대기 상태의 음식점만 승인 상태를 변경할 수 있습니다.")
    })
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
