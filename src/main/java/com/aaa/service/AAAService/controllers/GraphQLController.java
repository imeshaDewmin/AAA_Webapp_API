package com.aaa.service.AAAService.controllers;

import com.aaa.service.AAAService.dtos.*;
import com.aaa.service.AAAService.service.PlanService;
import com.aaa.service.AAAService.service.SubscriberService;
import lombok.Data;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

@Controller
@Data
public class GraphQLController {
    private final SubscriberService subscriberService;
    private final PlanService planService;

    @QueryMapping(name = "test")
    public String test() {
        return "Hi AAA Web API Service";
    }

    @QueryMapping(name = "getSubscribersByPage")
    public Mono<PaginationDto> getSubscribersByPage(@Argument int page, @Argument int size) {
        return subscriberService.getSubscribersByPage(page, size);
    }

    @QueryMapping(name = "getPlans")
    public Flux<PlanDto> getPlans() {
        return planService.getPlans();
    }

    @QueryMapping(name = "getPlanAttribute")
    public Flux<PlanAttributeDto> getPlanAttribute(@Argument int planId) {
        return planService.getPlanAttributes(planId);
    }

    @QueryMapping(name = "getPlanParameter")
    public Flux<PlanParameterDto> getPlanParameter(@Argument int planId) {
        return planService.getPlanParameters(planId);
    }
    @MutationMapping(name = "createSubscriber")
    public Mono<Map<String, Object>> createSubscriber(@Argument SubscriberDto subscriber){
        return subscriberService.createSubscriber(subscriber);
    }

    @QueryMapping(name = "getNasWhiteList")
    public Flux<NASWhitelistDto> getNasWhiteList(@Argument int subscriberId) {
        return subscriberService.getNasWhitelist(subscriberId);
    }
}
