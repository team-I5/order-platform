package com.spartaclub.orderplatform.user.presentation.controller;

import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.user.application.service.AddressService;
import com.spartaclub.orderplatform.user.domain.entity.User;
import com.spartaclub.orderplatform.user.presentation.dto.AddressCreateRequestDto;
import com.spartaclub.orderplatform.user.presentation.dto.AddressCreateResponseDto;
import com.spartaclub.orderplatform.user.presentation.dto.AddressListPageResponseDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 주소 관리 컨트롤러
 * 주소 등록, 조회, 수정, 삭제 등의 REST API 엔드포인트 제공
 *
 * @author 전우선
 * @date 2025-10-10(금)
 */
@RestController
@RequestMapping("/v1/addresses")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;

    /**
     * 주소 등록 API
     * 인증된 사용자의 새로운 주소를 등록
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
     * 주소 목록 조회 API
     * 인증된 사용자의 주소 목록을 조회
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
        AddressListPageResponseDto responseDto = addressService.getAllAddresses(includeDeleted, user);

        return ResponseEntity.ok(ApiResponse.success(responseDto));
    }
}