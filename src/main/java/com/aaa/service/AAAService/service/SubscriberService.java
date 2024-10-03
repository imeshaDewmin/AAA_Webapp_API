package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface SubscriberService {
    PaginationDto getSubscribersByPage(int page, int size);
    Mono<Map<String, Object>> createSubscriber(SubscriberDto subscriber);
}
