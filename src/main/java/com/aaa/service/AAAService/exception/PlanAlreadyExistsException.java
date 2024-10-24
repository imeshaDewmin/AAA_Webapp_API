package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class PlanAlreadyExistsException extends BaseException{
    public PlanAlreadyExistsException(ResponseCode responseCode) {
        super(responseCode);
    }
}
