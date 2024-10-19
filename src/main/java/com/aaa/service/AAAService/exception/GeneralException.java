package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class GeneralException extends BaseException{
    public GeneralException(ResponseCode responseCode) {
        super(responseCode);
    }
}
