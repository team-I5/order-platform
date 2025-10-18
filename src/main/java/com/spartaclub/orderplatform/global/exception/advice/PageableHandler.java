package com.spartaclub.orderplatform.global.exception.advice;

import org.springframework.core.MethodParameter;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Arrays;
import java.util.List;

/**
 * CustomPageableResolver
 * <p>
 * Controller에서 Pageable 파라미터를 받을 때,
 * page / size / sort 요청값을 커스터마이징하여 제약을 적용하는 Resolver입니다.
 * <p>
 * - 허용된 size 값: 10, 30, 50 (그 외 값은 기본값 10으로 강제)
 * - page 기본값: 0
 * - sort 허용 필드: createdAt, rating (기본값: createdAt 내림차순)
 * <p>
 * 예시 요청:
 * GET /products?page=1&size=30&sort=rating,asc
 */
@Component
public class PageableHandler implements HandlerMethodArgumentResolver {

    // 허용된 페이지 크기
    private final List<Integer> ALLOWED_SIZES = Arrays.asList(10, 30, 50);

    // 허용된 정렬 기준
    private static final List<String> ALLOWED_SORT_FIELDS = Arrays.asList("createdAt", "rating", "totalPrice", "paymentAmount", "averageRating", "reviewCount");

    // 기본 정렬 기준
    private static final Sort DEFAULT_SORT = Sort.by(Sort.Direction.DESC, "createdAt");

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // Controller 메서드의 파라미터가 Pageable 타입일 때만 처리
        return Pageable.class.isAssignableFrom(parameter.getParameterType());
    }

    @Override
    public Object resolveArgument(
            MethodParameter parameter,
            ModelAndViewContainer mavContainer,
            NativeWebRequest webRequest,
            org.springframework.web.bind.support.WebDataBinderFactory binderFactory
    ) {
        // 1. web 요청으로부터 페이지/사이즈 파라미터 추출
        int page = parseIntOrDefault(webRequest.getParameter("page"), 0);
        int size = parseIntOrDefault(webRequest.getParameter("size"), 10);

        // 허용되지 않은 값이면 기본값 10
        if (!ALLOWED_SIZES.contains(size)) {
            size = 10;
        }

        // 정렬 기준 설정 기본:createdAt
        String sortStr = webRequest.getParameter("sort");
        Sort sort = parseSortOrDefault(sortStr);

        return PageRequest.of(page, size, sort);
    }


    private int parseIntOrDefault(String value, int defaultValue) {
        try {
            return (value != null) ? Integer.parseInt(value) : defaultValue;
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Sort parseSortOrDefault(String sortStr) {

        // 정렬 기준 없으면 기본 값
        if (sortStr == null || sortStr.isBlank()) {
            return DEFAULT_SORT;
        }

        // 예시) ?sort=rating,asc 에서 정렬 기준과 방향 분리
        String[] parts = sortStr.split(",");
        // 정렬 기준
        String field = parts[0].trim();
        // 정렬 방향, 기본값 DESC
        String direction = (parts.length > 1) ? parts[1].trim().toUpperCase() : "DESC";

        // 허용되지 않은 필드면 기본값 사용
        if (!ALLOWED_SORT_FIELDS.contains(field)) {
            return DEFAULT_SORT;
        }

        // 허용되지 않은 방향이면 기본 DESC
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            direction = "DESC";
        }

        return Sort.by(Sort.Direction.fromString(direction), field);
    }
}

