package com.aaa.service.AAAService.controllers;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.PlanService;
import com.aaa.service.AAAService.service.SubscriberService;
import lombok.Data;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
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
    public PaginationDto<List<SubscriberDto>> getSubscribersByPage(@Argument int page, @Argument int size) {
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

    @MutationMapping(name = "createSubscriber")
    public Mono<Map<String, Object>> createSubscriber(@Argument SubscriberDto subscriber){
        return subscriberService.createSubscriber(subscriber);
    }
}
