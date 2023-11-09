package com.wanted.budgetmanagement.global.exception;

import lombok.Getter;

@Getter
public class BaseException extends RuntimeException{

    private final BaseExceptionStatus status;

    public BaseException(BaseExceptionStatus status){
        super(status.getMessage());
        this.status = status;
    }
}
