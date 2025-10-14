package com.spartaclub.orderplatform.domain.category.domain.model;

/* 카테고리 종류 enum class
 * 음식점이 해당 할 수 있는 종류 정의
 *
 * @author 이준성
 * @date 2025-09-30(화)
 */

import java.util.Arrays;
import lombok.Getter;

public enum CategoryType {
    KOREANFOOD("한식"),
    CHICKEN("치킨"),
    CHINESEFOOD("중식"),
    WESTERNFOOD("양식"),
    SNACKFOOD("분식"),
    JAPANESEFOOD("일식");

    // 필드 선언
    @Getter
    private final String name;

    // 생성자
    CategoryType(String name) {
        this.name = name;
    }

    // 인스턴스로 해당되는 값만 가져오기
    public static CategoryType getInstance(String name) {
        return Arrays.stream(values())
            .filter(cat -> cat.getName().equalsIgnoreCase(name))
            .findFirst()
            .orElseThrow();
    }
}