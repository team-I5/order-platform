package com.spartaclub.orderplatform.domain.user.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum AddressErrorCode implements ErrorCode {
    // 주소 조회 관련
    ADDRESS_NOT_FOUND(HttpStatus.NOT_FOUND, "주소를 찾을 수 없습니다."),
    
    // 주소 중복 관련
    DUPLICATE_ADDRESS_NAME(HttpStatus.CONFLICT, "동일한 주소명이 이미 존재합니다."),
    
    // 주소 접근 권한 관련
    ACCESS_DENIED(HttpStatus.FORBIDDEN, "해당 주소에 접근할 권한이 없습니다."),
    
    // 주소 상태 관련
    ALREADY_DELETED(HttpStatus.BAD_REQUEST, "이미 삭제된 주소입니다."),
    CANNOT_EDIT_DELETED(HttpStatus.BAD_REQUEST, "삭제된 주소는 수정할 수 없습니다."),
    
    // 기본 주소 관련
    CANNOT_UNSET_LAST_DEFAULT(HttpStatus.BAD_REQUEST, "마지막 주소는 기본 주소를 해제할 수 없습니다."),
    CANNOT_DELETE_LAST_ADDRESS(HttpStatus.BAD_REQUEST, "마지막 주소는 삭제할 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    AddressErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}