package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.ProfileOverrideMetaDto;
import com.aaa.service.AAAService.dtos.StateDto;
import com.aaa.service.AAAService.dtos.SubscriberAttributeMetaDto;
import com.aaa.service.AAAService.dtos.SubscriberParameterMetaDto;
import reactor.core.publisher.Flux;

public interface AppService {
    Flux<StateDto> getState();
    Flux<SubscriberAttributeMetaDto> getAttributes();
    Flux<SubscriberParameterMetaDto> getParameters();

    Flux<ProfileOverrideMetaDto> getProfiles();

}
