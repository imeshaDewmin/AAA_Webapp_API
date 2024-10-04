package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NASWhitelistDto {
    private int id;
    private int subscriberId;
    private String nasIdPattern;
}
