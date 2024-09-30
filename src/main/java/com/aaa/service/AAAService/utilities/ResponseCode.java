package com.aaa.service.AAAService.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    EMAIL_ALREADY_USED("This email already used"),
    TOKEN_ALREADY_USED("This token already used"),
    CLIENT_NOT_FOUND("Client not found"),
    TOKEN_NOT_FOUND("Token not found"),
    APP_CONFIG_NOT_FOUND("App config not found"),
    MASTER_DATA_NOT_FOUND("Master data not found");

    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

}
