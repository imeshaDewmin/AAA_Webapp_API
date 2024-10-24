package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface PlanService {
    Flux<PlanDto> getPlans();
    Flux<PlanAttributeDto> getPlanAttributes(int subscriberId, int planId);
    Flux<PlanParameterDto> getPlanParameters(int subscriberId, int planId);
    Mono<PlanDto> getPlansById(int planId);

    Mono<PaginationDto> getPlansByPage(int page, int size);
    Mono<Map<String, Object>> createPlan(PlanDto plan);
    Mono<Map<String, Object>> updatePlan(int planId, PlanDto plan);
    Mono<Map<String, Object>> deletePlan(int planId);

}
