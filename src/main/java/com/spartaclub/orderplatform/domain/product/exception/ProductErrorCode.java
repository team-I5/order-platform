package com.spartaclub.orderplatform.domain.product.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum ProductErrorCode implements ErrorCode {
    PRODUCT_NOT_EXIST(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),
    STORE_NOT_EXIST(HttpStatus.NOT_FOUND, "가게를 찾을 수 없습니다."),
    ADDRESS_NOT_EXIST(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),
    PRODUCT_OPTION_GROUP_NOT_EXIST(HttpStatus.NOT_FOUND, "상품 옵션 그룹을 찾을 수 없습니다."),
    PRODUCT_OPTION_ITEM_N0T_EXIST(HttpStatus.NOT_FOUND, "상품 옵션 아이틈을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    ProductErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
