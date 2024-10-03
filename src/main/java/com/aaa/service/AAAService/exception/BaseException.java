package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Getter;

@Getter
public class BaseException extends Exception {
    private final ResponseCode responseCode;

    public BaseException(ResponseCode responseCode) {
        super(responseCode.getMessage());
        this.responseCode = responseCode;
    }
}
