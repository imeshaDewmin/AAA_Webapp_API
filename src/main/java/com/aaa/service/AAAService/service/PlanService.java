package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import reactor.core.publisher.Flux;

import java.util.List;

public interface PlanService {
    Flux<PlanDto> getPlans();

    Flux<PlanAttributeDto> getPlanAttributes(int planId);
}
