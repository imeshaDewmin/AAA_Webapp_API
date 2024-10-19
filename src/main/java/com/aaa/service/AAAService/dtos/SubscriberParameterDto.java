package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriberParameterDto {
    private int id;
    private int subscriberId;
    private String parameterName;
    private String parameterValue;
    private int rejectOnFailure;
}
