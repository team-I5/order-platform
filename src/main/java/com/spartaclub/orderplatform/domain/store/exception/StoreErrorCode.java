package com.spartaclub.orderplatform.domain.store.exception;

import com.spartaclub.orderplatform.global.exception.ErrorCode;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public enum StoreErrorCode implements ErrorCode {
    // 공통
    NO_PERMISSION(HttpStatus.FORBIDDEN, "권한이 없습니다."),

    // 음식점
    NOT_EXIST(HttpStatus.NOT_FOUND, "음식점을 찾을 수 없습니다."),
    DUPLICATE_STORE_NAME(HttpStatus.CONFLICT, "이미 존재하는 음식점 이름입니다."),

    NOT_OWNED_STORE_TO_UPDATE(HttpStatus.FORBIDDEN, "본인의 음식점만 수정할 수 있습니다."),
    NOT_OWNED_STORE_TO_DELETE(HttpStatus.FORBIDDEN, "본인의 음식점만 삭제할 수 있습니다."),
    ONLY_REJECTED_STORE_MODIFIABLE(HttpStatus.BAD_REQUEST, "승인 거절된 음식점만 수정할 수 있습니다."),
    ONLY_APPROVED_STORE_MODIFIABLE(HttpStatus.BAD_REQUEST, "승인된 음식점만 수정할 수 있습니다."),
    ONLY_PENDING_STORE_APPROVABLE(HttpStatus.BAD_REQUEST, "승인 대기 상태의 음식점만 승인 상태를 변경할 수 있습니다."),

    // 음식점 - 카테고리
    CATEGORY_NOT_EXIST(HttpStatus.NOT_FOUND, "카테고리가 존재하지 않습니다."),
    DUPLICATE_CATEGORY(HttpStatus.CONFLICT, "이미 등록된 카테고리입니다."),

    NOT_OWNED_STORE_TO_REGISTER_CATEGORY(HttpStatus.FORBIDDEN, "본인 음식점에만 카테고리를 등록할 수 있습니다."),
    NOT_OWNED_STORE_TO_MODIFY_CATEGORY(HttpStatus.FORBIDDEN, "본인 음식점의 카테고리만 수정할 수 있습니다."),
    NOT_OWNED_STORE_TO_DELETE_CATEGORY(HttpStatus.FORBIDDEN, "본인 음식점의 카테고리만 삭제할 수 있습니다."),

    ONLY_APPROVED_STORE_REGISTER_CATEGORY(HttpStatus.BAD_REQUEST, "승인된 음식점만 카테고리를 등록할 수 있습니다.");

    private final HttpStatus status;
    private final String message;

    StoreErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return this.name();
    }
}
