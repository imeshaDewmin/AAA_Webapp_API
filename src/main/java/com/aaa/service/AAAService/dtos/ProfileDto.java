package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProfileDto {
    private int profileId;
    private int attributeGroup;
    private String profileKey;
    private String description;
}
