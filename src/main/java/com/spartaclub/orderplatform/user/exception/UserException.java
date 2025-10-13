package com.spartaclub.orderplatform.user.exception;

import lombok.Getter;

@Getter
public class UserException extends RuntimeException {

    public UserException(UserErrorCode userErrorCode) {
        super(userErrorCode.getMessage());
    }
}
