package com.spartaclub.orderplatform.domain.user.presentation.controller;

import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressDeleteResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressListPageResponseDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressUpdateRequestDto;
import com.spartaclub.orderplatform.domain.user.presentation.dto.AddressUpdateResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.domain.user.application.service.AddressService;
import com.spartaclub.orderplatform.domain.user.domain.entity.User;
import com.spartaclub.orderplatform.user.presentation.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * 주소 관리 컨트롤러 주소 등록, 조회, 수정, 삭제 등의 REST API 엔드포인트 제공
 *
 * @author 전우선
 * @date 2025-10-12(일)
 */
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 주소 등록 API 인증된 사용자의 새로운 주소를 등록
     *
     * @param userDetails 인증된 사용자 정보
     * @param requestDto  주소 등록 요청 데이터
     * @return 등록된 주소 정보
     */
    @PostMapping
    public ResponseEntity<ApiResponse<AddressCreateResponseDto>> createAddress(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @Valid @RequestBody AddressCreateRequestDto requestDto) {

        // 인증된 사용자 정보 추출
        User user = userDetails.getUser();

        // 주소 등록
        AddressCreateResponseDto responseDto = addressService.createAddress(requestDto, user);

        return ResponseEntity.status(HttpStatus.CREATED)
            .body(ApiResponse.success(responseDto));
    }

    /**
     * 주소 목록 조회 API 인증된 사용자의 주소 목록을 조회
     *
     * @param userDetails    인증된 사용자 정보
     * @param includeDeleted 삭제된 주소 포함 여부
     * @return 주소 목록과 통계 정보
     */
    @GetMapping
    public ResponseEntity<ApiResponse<AddressListPageResponseDto>> getAllAddresses(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @RequestParam(name = "includeDeleted", defaultValue = "false") Boolean includeDeleted) {

        // 인증된 사용자 정보 추출
        User user = userDetails.getUser();

        // 주소 목록 조회
        AddressListPageResponseDto responseDto = addressService.getAllAddresses(includeDeleted,
            user);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 주소 수정 API 인증된 사용자의 기존 주소를 수정
     *
     * @param userDetails 인증된 사용자 정보
     * @param addressId   수정할 주소 ID
     * @param requestDto  주소 수정 요청 데이터
     * @return 수정된 주소 정보
     */
    @PutMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressUpdateResponseDto>> updateAddress(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID addressId,
        @Valid @RequestBody AddressUpdateRequestDto requestDto) {

        // 인증된 사용자 정보 추출
        User user = userDetails.getUser();

        // 주소 수정
        AddressUpdateResponseDto responseDto = addressService.updateAddress(addressId, requestDto,
            user);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }

    /**
     * 주소 삭제 API (Soft Delete) 인증된 사용자의 기존 주소를 삭제
     *
     * @param userDetails 인증된 사용자 정보
     * @param addressId   삭제할 주소 ID
     * @return 삭제된 주소 정보
     */
    @DeleteMapping("/{addressId}")
    public ResponseEntity<ApiResponse<AddressDeleteResponseDto>> deleteAddress(
        @AuthenticationPrincipal UserDetailsImpl userDetails,
        @PathVariable UUID addressId) {

        // 인증된 사용자 정보 추출
        User user = userDetails.getUser();

        // 주소 삭제
        AddressDeleteResponseDto responseDto = addressService.deleteAddress(addressId, user);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}