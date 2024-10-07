package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class DeviceWhitelistDto {
    private int id;
    private int subscriberId;
    private String MACAddress;
    private String description;
    private String status;
    private String createAt;

}
