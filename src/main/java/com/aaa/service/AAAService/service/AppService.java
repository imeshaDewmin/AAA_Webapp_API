package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.StateDto;
import reactor.core.publisher.Flux;

public interface AppService {
    Flux<StateDto> getState();
}
