package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.NASWhitelistDto;
import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SubscriberService {
    Mono<PaginationDto> getSubscribersByPage(int page, int size);
    Mono<Map<String, Object>> createSubscriber(SubscriberDto subscriber);
    Flux<NASWhitelistDto> getNasWhitelist(int subscriberId);
}
