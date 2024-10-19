package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanParameterDto {
    private int overrideId;
    private int planId;
    private String parameterName;
    private String parameterValue;
    private String parameterOverrideValue;
}
