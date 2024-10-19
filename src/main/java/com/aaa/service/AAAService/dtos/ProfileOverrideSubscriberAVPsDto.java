package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileOverrideSubscriberAVPsDto {
    private int overrideId;
    private int subscriberId;
    private int planId;
    private String overrideKey;
    private String overrideValue;
    private String overrideWhen;
}
