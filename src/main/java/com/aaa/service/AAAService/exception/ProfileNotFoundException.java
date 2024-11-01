package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class ProfileNotFoundException extends BaseException {

    public ProfileNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
