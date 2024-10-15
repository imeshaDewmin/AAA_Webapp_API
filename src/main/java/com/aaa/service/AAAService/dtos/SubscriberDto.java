package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@Builder
public class SubscriberDto{
    private int subscriberId;
    private int planId;
    private String username;
    private String password;
    private String status;
    private String contactNo;
    private String email;
    private String extId;
    private Date createdDate;
    private Date updatedTime;
    private String realm;
    private String type;
    private List<PlanParameterSubscriberOverrideDto> planParameterOverrides;
    private List<PlanAttributeSubscriberOverrideDto> planAttributeOverrides;
    private List<ProfileOverrideSubscriberAVPsDto> pofileOverrideSubscriberAVPs;
    private List<SubscriberAVPsDto> subscriberAVPs;
    private List<DeviceWhitelistDto> deviceWhitelist;
    private List<NasWhitelistDto> nasWhitelist;
    private List<SubscriberAttributeDto> subscriberAttributes;
    private List<SubscriberParameterDto> subscriberParameters;
}
