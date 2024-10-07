package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.ProfileDto;
import com.aaa.service.AAAService.dtos.ProfileOverrideSubscriberAVPsDto;
import reactor.core.publisher.Flux;

public interface ProfileService {
    Flux<ProfileDto> getProfile();

    Flux<ProfileOverrideSubscriberAVPsDto> getProfileOverrideSubscriberAVPs(int subscriberId, int planId);

}
