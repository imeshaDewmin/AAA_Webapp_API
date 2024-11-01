package com.aaa.service.AAAService.utilities;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ResponseCode {

    ERROR("Error"),
    PLAN_FETCH_FAILED("Plan fetch failed"),
    PLAN_ATTRIBUTE_FETCH_FAILED("Plan attribute fetch failed"),
    SUBSCRIBER_CREATE_SUCCESS("Subscriber create success"),
    SUBSCRIBER_UPDATE_SUCCESS("Subscriber update success"),
    SUBSCRIBER_UPDATE_FAILED("Subscriber update failed"),
    SUBSCRIBER_CREATE_FAILED("Subscriber create failed"),
    USERNAME_OR_EMAIL_ALREADY_EXISTED("This username or email already used"),
    PLAN_PARAMETER_FETCH_FAILED("Plan parameter fetch failed"),
    ATTRIBUTE_FETCH_FAILED("Attribute group fetch failed"),
    FETCH_PROFILE_OVERRIDE_AVPS_FETCH_FAILED("Profile override avps fetch failed"),
    UPDATE_PARAMETERS("Update subscriber parameters success"),
    ATTRIBUTE_META_FETCH_FAILED("Attribute fetch failed"),
    PARAMETER_META_FETCH_FAILED("Parameter fetch failed"),
    PROFILE_META_FETCH_FAILED("Profile fetch failed"),
    SUBSCRIBER_DELETE_SUCCESS("Subscriber Deleted Successfully"),
    SUBSCRIBER_DELETE_FAILED("Subscriber Delete Failed"),
    SUBSCRIBER_NOT_FOUND("Subscriber Not Found"),
    PLAN_NOT_FOUND("Plan Not Found"),
    PLAN_CREATE_SUCCESS("Plan create success"),
    PLAN_UPDATE_SUCCESS("Plan update success"),
    PLAN_UPDATE_FAILED("Plan update failed"),
    PLAN_CREATE_FAILED("Plan create failed"),
    PLAN_DELETE_SUCCESS("Plan Deleted Successfully"),
    PLAN_DELETE_FAILED("Plan Delete Failed"),
    PLAN_ALREADY_EXISTS("This Plan Already Exists"),
    PROFILE_NOT_FOUND("profile Not Found"),
    PROFILE_CREATE_SUCCESS("Plan create success"),
    PROFILE_UPDATE_SUCCESS("Plan update success"),
    PROFILE_UPDATE_FAILED("Plan update failed"),
    PROFILE_CREATE_FAILED("Plan create failed"),
    PROFILE_DELETE_SUCCESS("Plan Deleted Successfully"),
    PROFILE_DELETE_FAILED("Plan Delete Failed"),
    PROFILE_FETCH_FAILED("Plan fetch failed"),
    PROFILE_ALREADY_EXISTS("This Plan Already Exists");
    private String message;

    public void setMessage(String message) {
        this.message = message;
    }

}
