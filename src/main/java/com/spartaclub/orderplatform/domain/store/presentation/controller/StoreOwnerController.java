package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreCategoryRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreCategoryResponseDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreResponseDto;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.global.auth.UserDetailsImpl;
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
@Tag(name = "Store - Owner", description = "Owner의 음식점 관리 API")
public class StoreOwnerController {

    private final StoreService storeService;

    // Owner의 음식점 생성 API
    @Operation(summary = "음식점 생성", description = "음식점을 생성합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "음식점 생성 성공", content = @Content(schema = @Schema(implementation = StoreResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 존재하는 음식점 이름입니다."),
    })
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
    @Operation(summary = "음식점 재승인 신청", description = "승인 거절된 음식점을 재승인 신청합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 재승인 신청 성공", content = @Content(schema = @Schema(implementation = StoreResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인의 음식점만 수정할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인 거절된 음식점만 수정할 수 있습니다."),
    })
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
    @Operation(summary = "음식점 기본 정보 수정", description = "승인된 음식점 기본 정보를 수정합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점 재승인 신청 성공", content = @Content(schema = @Schema(implementation = StoreResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인의 음식점만 수정할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인된 음식점만 수정할 수 있습니다."),
    })
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
    @Operation(summary = "음식점 삭제", description = "음식점을 삭제합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인의 음식점만 삭제할 수 있습니다."),
    })
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{storeId}")
    public ResponseEntity<ApiResponse<Void>> deleteStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId
    ) {
        User user = userDetails.getUser();
        storeService.deleteStore(user, storeId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }

    // Owner의 음식점 카테고리 등록
    @Operation(summary = "음식점 카테고리 추가", description = "음식점의 카테고리를 설정합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점에 카테고리 추가 성공", content = @Content(schema = @Schema(implementation = StoreCategoryResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 음식점에만 카테고리를 등록할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "승인된 음식점만 수정할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 카테고리입니다.")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PostMapping("/{storeId}/categories")
    public ResponseEntity<ApiResponse<StoreCategoryResponseDto>> addCategoryToStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreCategoryRequestDto dto
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.addCategoryToStore(storeId, user, dto)));
    }

    // Owner의 음식점 카테고리 수정
    @Operation(summary = " 음식점 카테고리 수정", description = "음식점의 카테고리를 수정합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "음식점에 카테고리 수정 성공", content = @Content(schema = @Schema(implementation = StoreCategoryResponseDto.class))),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 음식점에만 카테고리를 수정할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "409", description = "이미 등록된 카테고리입니다.")
    })
    @PreAuthorize("hasRole('OWNER')")
    @PutMapping("/{storeId}/categories")
    public ResponseEntity<ApiResponse<StoreCategoryResponseDto>> updateCategoryToStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreCategoryRequestDto dto
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.updateCategoryToStore(storeId, user, dto)));
    }

    // Owner의 음식점 카테고리 삭제
    @Operation(summary = "음식점 카테고리 삭제", description = "음식점의 카테고리를 삭제합니다.")
    @ApiResponses(value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "204", description = "음식점에 카테고리 삭제 성공"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "본인 음식점의 카테고리만 삭제할 수 있습니다."),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "카테고리가 존재하지 않습니다."),
    })
    @PreAuthorize("hasRole('OWNER')")
    @DeleteMapping("/{storeId}/categories")
    public ResponseEntity<ApiResponse<Void>> deleteCategoryFromStore(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID storeId,
        @Valid @RequestBody StoreCategoryRequestDto dto
    ) {
        User user = userDetails.getUser();
        storeService.deleteCategoryFromStore(storeId, user, dto);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(ApiResponse.success());
    }
}
