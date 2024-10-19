package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class UsernameOrEmailAlreadyExistedException extends BaseException {
    public UsernameOrEmailAlreadyExistedException(ResponseCode responseCode) {
        super(responseCode);
    }
}
