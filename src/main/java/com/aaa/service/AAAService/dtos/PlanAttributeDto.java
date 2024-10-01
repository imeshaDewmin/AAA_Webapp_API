package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PlanAttributeDto {
    private int overrideId;
    private int subscriberId;
    private int planId;
    private String attributeName;
    private String attributeValue;
}
