package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriberAttributeDto {
    private int id;
    private int subscriberId;
    private String attributeName;
    private String attributeValue;
}
