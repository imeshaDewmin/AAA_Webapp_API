package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanParameterSubscriberOverrideDto {
    private int overrideId;
    private int planId;
    private int subscriberId;
    private String parameterName;
    private String parameterValue;

}
