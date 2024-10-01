package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
public class PlanDto {
    private int planId;
    private int typeId;
    private String planName;
    private String description;
}
