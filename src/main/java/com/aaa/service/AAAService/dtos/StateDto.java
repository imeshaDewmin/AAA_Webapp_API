package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StateDto {
    private String state;
    private String description;
    private int isAuthorized;
}
