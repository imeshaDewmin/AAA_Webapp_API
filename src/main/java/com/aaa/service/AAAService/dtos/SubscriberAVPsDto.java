package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class SubscriberAVPsDto {
    private int id;
    private int subscriberId;
    private int attributeGroupId;
    private String attribute;
    private String operation;
    private String value;
    private String status;

}
