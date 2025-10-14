package com.spartaclub.orderplatform.domain.product.presentation.controller;

import com.spartaclub.orderplatform.domain.product.application.service.ProductOptionGroupService;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupRequestDto;
import com.spartaclub.orderplatform.domain.product.presentation.dto.ProductOptionGroupResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static com.spartaclub.orderplatform.global.application.security.SecurityUtils.getCurrentUserId;

@RestController
@RequestMapping("/v1/product-option-groups")
@RequiredArgsConstructor
public class ProductOptionGroupController {

    private final ProductOptionGroupService productOptionGroupService;

    @PostMapping
    public ProductOptionGroupResponseDto createProductOptionGroup(@RequestBody ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        return productOptionGroupService.createProductOptionGroup(productOptionGroupRequestDto);
    }


    @PutMapping("/{productOptionGroupId}")
    public ProductOptionGroupResponseDto updateProductOptionGroup(@PathVariable UUID productOptionGroupId, @RequestBody ProductOptionGroupRequestDto productOptionGroupRequestDto) {
        return productOptionGroupService.updateProductOptionGroup(productOptionGroupId, productOptionGroupRequestDto);
    }

    @DeleteMapping("/{productOptionGroupId}")
    public void deleteProductOptionGroup(@PathVariable UUID productOptionGroupId) {
        Long userId = getCurrentUserId();
        productOptionGroupService.deleteProductOptionGroup(userId, productOptionGroupId);
    }

}
