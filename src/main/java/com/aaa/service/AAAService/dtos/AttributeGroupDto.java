package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AttributeGroupDto {
    private int id;
    private String name;
    private String description;
}
