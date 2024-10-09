package com.aaa.service.AAAService.service;


import com.aaa.service.AAAService.dtos.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SubscriberService {
    Mono<PaginationDto> getSubscribersByPage(int page, int size);

    Mono<Map<String, Object>> createSubscriber(SubscriberDto subscriber);
    Mono<Map<String, Object>> updateSubscriberParameters(int subscriberId, int planId, SubscriberDto subscriber);

    Flux<NasWhitelistDto> getNasWhitelist(int subscriberId);

    Flux<DeviceWhitelistDto> getDeviceWhitelist(int subscriberId);
    Flux<SubscriberAVPsDto> getSubscriberAVPs(int subscriberId);

    Mono<Map<String, Object>> applyPlan(int subscriberId, int planId, String state);

}
