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


    @QueryMapping(name = "getPlansByPage")
    public Mono<PaginationDto> getPlansByPage(@Argument int page, @Argument int size) {
        return planService.getPlansByPage(page, size);
    }
    @QueryMapping(name = "getPlansById")
    public Mono<PlanDto> getPlansById(@Argument int planId) {
        return planService.getPlansById(planId);
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

    @MutationMapping(name = "deleteSubscriber")
    public Mono<Map<String, Object>> deleteSubscriber(@Argument int subscriberId) {
        return subscriberService.deleteSubscriber(subscriberId);
    }



    @MutationMapping(name = "createPlan")
    public Mono<Map<String, Object>> createPlan(@Argument PlanDto plan) {
        return planService.createPlan(plan);
    }

    @MutationMapping(name = "updatePlan")
    public Mono<Map<String, Object>> updatePlan(@Argument int planId, @Argument PlanDto plan) {
        return planService.updatePlan(planId, plan);
    }

    @MutationMapping(name = "deletePlan")
    public Mono<Map<String, Object>> deletePlan(@Argument int planId) {
        return planService.deletePlan(planId);
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

    @QueryMapping(name = "getAttributeMeta")
    public Flux<SubscriberAttributeMetaDto> getAttributeMeta() {
        return appService.getAttributes();
    }

    @QueryMapping(name = "getParameterMeta")
    public Flux<SubscriberParameterMetaDto> getParameterMeta() {
        return appService.getParameters();
    }

    @QueryMapping(name = "getSubscriberAttribute")
    public Flux<SubscriberAttributeDto> getSubscriberAttribute(@Argument int subscriberId) {
        return subscriberService.getSubscriberAttribute(subscriberId);
    }

    @QueryMapping(name = "getSubscriberParameter")
    public Flux<SubscriberParameterDto> getSubscriberParameter(@Argument int subscriberId) {
        return subscriberService.getSubscriberParameter(subscriberId);
    }

    @QueryMapping(name = "getProfileMeta")
    public Flux<ProfileOverrideMetaDto> getProfileMeta() {
        return appService.getProfiles();
    }

    @QueryMapping(name = "getProfilesByPage")
    public Mono<PaginationDto> getProfilesByPage(@Argument int page, @Argument int size) {
        return profileService.getProfilesByPage(page, size);
    }

    @QueryMapping(name = "getProfilesById")
    public Mono<ProfileDto> getProfilesById(@Argument int profileId) {
        return profileService.getProfilesById(profileId);
    }

    @MutationMapping(name = "createProfile")
    public Mono<Map<String, Object>> createProfile(@Argument ProfileDto profile) {
        return profileService.createProfile(profile);
    }

    @MutationMapping(name = "updateProfile")
    public Mono<Map<String, Object>> updateProfile(@Argument int profileId, @Argument ProfileDto profile) {
        return profileService.updateProfile(profileId, profile);
    }

    @MutationMapping(name = "deleteProfile")
    public Mono<Map<String, Object>> deleteProfile(@Argument int profileId) {
        return profileService.deleteProfile(profileId);
    }

    @QueryMapping(name = "getProfiles")
    public Flux<ProfileDto> getProfiles() {
        return profileService.getProfiles();
    }



}
