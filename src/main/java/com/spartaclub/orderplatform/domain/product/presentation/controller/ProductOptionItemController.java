package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductOptionItemService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionItemResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/v1/product-option-items")
@RequiredArgsConstructor
public class ProductOptionItemController {

    private final ProductOptionItemService service;

    @PostMapping
    public ProductOptionItemResponseDto createProductOptionItem(@RequestBody ProductOptionItemRequestDto productOptionItemRequestDto) {
        return service.createProductOptionItem(productOptionItemRequestDto);
    }


    @PutMapping("/{itemId}")
    public ProductOptionItemResponseDto updateProductOptionItem(@PathVariable UUID itemId, @RequestBody ProductOptionItemRequestDto productOptionItemRequestDto) {
        return service.updateProductOptionItem(itemId, productOptionItemRequestDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteProductOptionItem(@PathVariable UUID itemId) {
        Long userId = getCurrentUserId();
        service.deleteProductOptionItem(userId, itemId);
    }

}
