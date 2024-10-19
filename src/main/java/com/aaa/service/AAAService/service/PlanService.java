package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.dtos.PlanParameterDto;
import reactor.core.publisher.Flux;

public interface PlanService {
    Flux<PlanDto> getPlans();
    Flux<PlanAttributeDto> getPlanAttributes(int subscriberId, int planId);
    Flux<PlanParameterDto> getPlanParameters(int subscriberId, int planId);


}
