package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanParameterDto {
    private int parameterId;
    private int planId;
    private String parameterName;
    private String parameterValue;
    private int rejectOnFailure;
    private String parameterOverrideValue;
}
