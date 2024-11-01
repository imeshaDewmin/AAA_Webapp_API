package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;
@Data
@Builder
public class ProfileAVPDto {

    private int id;
    private int profileId;
    private String avpName;
    private String avpValue;
    private String avpOverrideValue;
    private String avpDefaultIfNull;
    private String includeWhen;
    private String Status;
    private int overrideEnabled;
}


