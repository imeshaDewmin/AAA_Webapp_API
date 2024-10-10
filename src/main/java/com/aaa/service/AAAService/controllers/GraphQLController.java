package com.aaa.service.AAAService.controllers;

import com.aaa.service.AAAService.dtos.*;
import com.aaa.service.AAAService.service.*;
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
    private final AttributeGroupService attributeGroupService;
    private final AppService appService;
    private final ProfileService profileService;

    @QueryMapping(name = "test")
    public String test() {
        return "Hi AAA Web API Service";
    }

    @QueryMapping(name = "getSubscribersByPage")
    public Mono<PaginationDto> getSubscribersByPage(@Argument int page, @Argument int size) {
        return subscriberService.getSubscribersByPage(page, size);
    }

    @QueryMapping(name = "getSubscriberById")
    public Mono<SubscriberDto> getSubscriberById(@Argument int subscriberId) {
        return subscriberService.getSubscriberById(subscriberId);
    }

    @QueryMapping(name = "getPlans")
    public Flux<PlanDto> getPlans() {
        return planService.getPlans();
    }

    @QueryMapping(name = "getPlanAttribute")
    public Flux<PlanAttributeDto> getPlanAttribute(@Argument int subscriberId, @Argument int planId) {
        return planService.getPlanAttributes(subscriberId, planId);
    }

    @QueryMapping(name = "getPlanParameter")
    public Flux<PlanParameterDto> getPlanParameter(@Argument int subscriberId, @Argument int planId) {
        return planService.getPlanParameters(subscriberId, planId);
    }

    @MutationMapping(name = "updateSubscriberParameters")
    public Mono<Map<String, Object>> updateSubscriberParameters(@Argument int subscriberId, @Argument int planId, @Argument SubscriberDto subscriber) {
        return subscriberService.updateSubscriberParameters(subscriberId, planId, subscriber);
    }

    @MutationMapping(name = "createSubscriber")
    public Mono<Map<String, Object>> createSubscriber(@Argument SubscriberDto subscriber) {
        return subscriberService.createSubscriber(subscriber);
    }

    @MutationMapping(name = "updateSubscriber")
    public Mono<Map<String, Object>> updateSubscriber(@Argument int subscriberId, @Argument SubscriberDto subscriber) {
        return subscriberService.updateSubscriber(subscriberId, subscriber);
    }

    @MutationMapping(name = "applyPlan")
    public Mono<Map<String, Object>> applyPlan(@Argument int subscriberId, @Argument int planId, @Argument String state) {
        return subscriberService.applyPlan(subscriberId, planId, state);
    }

    @QueryMapping(name = "getNasWhiteList")
    public Flux<NasWhitelistDto> getNasWhiteList(@Argument int subscriberId) {
        return subscriberService.getNasWhitelist(subscriberId);
    }

    @QueryMapping(name = "getDeviceWhitelist")
    public Flux<DeviceWhitelistDto> getDeviceWhitelist(@Argument int subscriberId) {
        return subscriberService.getDeviceWhitelist(subscriberId);
    }

    @QueryMapping(name = "getSubscriberAVPs")
    public Flux<SubscriberAVPsDto> getSubscriberAVPs(@Argument int subscriberId) {
        return subscriberService.getSubscriberAVPs(subscriberId);
    }

    @QueryMapping(name = "getNasAttributeGroup")
    public Flux<AttributeGroupDto> getNasAttributeGroup() {
        return attributeGroupService.getNasAttributeGroup();
    }

    @QueryMapping(name = "getState")
    public Flux<StateDto> getState() {
        return appService.getState();
    }

    @QueryMapping(name = "getProfileOverrideSubscriberAVPs")
    public Flux<ProfileOverrideSubscriberAVPsDto> getProfileOverrideSubscriberAVPs(@Argument int subscriberId, @Argument int planId) {
        return profileService.getProfileOverrideSubscriberAVPs(subscriberId, planId);
    }
}
