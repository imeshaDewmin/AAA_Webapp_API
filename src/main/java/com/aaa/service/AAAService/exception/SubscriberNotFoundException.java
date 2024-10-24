package com.aaa.service.AAAService.exception;

import com.aaa.service.AAAService.utilities.ResponseCode;

public class SubscriberNotFoundException extends BaseException {
    public SubscriberNotFoundException(ResponseCode responseCode) {
        super(responseCode);
    }
}
