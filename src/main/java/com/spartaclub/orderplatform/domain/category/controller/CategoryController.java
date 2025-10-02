package com.spartaclub.orderplatform.domain.category.controller;

import com.spartaclub.orderplatform.domain.category.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*
 * Cateogory 컨트롤러 클래스
 * 카테고리 관련 API Endpoint 제공
 *
 * @author 이준성
 * @date 2025-10-02
 */
@RestController
@RequestMapping("/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;


}
