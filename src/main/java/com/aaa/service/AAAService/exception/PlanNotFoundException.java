package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class PlanNotFoundException extends BaseException {
    public PlanNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
