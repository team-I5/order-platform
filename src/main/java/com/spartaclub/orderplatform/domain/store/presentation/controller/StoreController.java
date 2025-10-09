package com.spartaclub.orderplatform.domain.store.presentation.controller;

import com.spartaclub.orderplatform.domain.store.application.service.StoreService;
import com.spartaclub.orderplatform.domain.store.presentation.dto.request.StoreSearchRequestDto;
import com.spartaclub.orderplatform.domain.store.presentation.dto.response.StoreSearchResponseDto;
import com.spartaclub.orderplatform.global.application.security.UserDetailsImpl;
import com.spartaclub.orderplatform.global.presentation.dto.ApiResponse;
import com.spartaclub.orderplatform.user.domain.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/v1/stores")
@RestController
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;

    // 목록 조회 API
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<Page<StoreSearchResponseDto>>> searchStore(
        @ModelAttribute StoreSearchRequestDto dto,
        @AuthenticationPrincipal UserDetailsImpl userDetails
    ) {
        User user = userDetails.getUser();
        return ResponseEntity.status(HttpStatus.OK)
            .body(ApiResponse.success(storeService.searchStore(dto, user.getRole(), user)));
    }

}

// TODO
//  상세 조회 API