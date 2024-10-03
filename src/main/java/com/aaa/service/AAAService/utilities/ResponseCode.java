package com.aaa.service.AAAService.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    PLAN_FETCH_FAILED("Plan fetch failed"),
    PLAN_ATTRIBUTE_FETCH_FAILED("Plan attribute fetch failed"),
    SUBSCRIBER_CREATE_SUCCESS("Subscriber create success"),
    SUBSCRIBER_CREATE_FAILED("Subscriber create failed"),
    USERNAME_OR_EMAIL_ALREADY_EXISTED("This username or email already used");

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

}
