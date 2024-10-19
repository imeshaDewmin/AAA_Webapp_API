package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.AttributeGroupDto;
import reactor.core.publisher.Flux;

public interface AttributeGroupService {
    Flux<AttributeGroupDto> getNasAttributeGroup();
}
