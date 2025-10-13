package com.spartaclub.orderplatform.domain.category.application.service;

import com.spartaclub.orderplatform.domain.category.infrastructure.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/*
 * Cateogory 서비스 클래스
 * 카테고리 관련 비즈니스 로직 처리
 *
 * @author 이준성
 * @date 2025-10-02
 */
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

}
