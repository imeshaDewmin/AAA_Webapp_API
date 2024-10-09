package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class PlanAttributeSubscriberOverrideDto {
    private int overrideId;
    private int subscriberId;
    private int planId;
    private String attributeName;
    private String attributeValue;
    private String attributeOverrideValue;
}
