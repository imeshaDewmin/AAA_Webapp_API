package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

public interface ProfileService {
    Flux<ProfileDto> getProfile();
    Flux<ProfileDto> getProfiles();
    Flux<ProfileOverrideSubscriberAVPsDto> getProfileOverrideSubscriberAVPs(int subscriberId, int planId);

    Mono<ProfileDto> getProfilesById(int profileId);

    Mono<PaginationDto> getProfilesByPage(int page, int size);
    Mono<Map<String, Object>> createProfile(ProfileDto profile);
    Mono<Map<String, Object>> updateProfile(int profileId, ProfileDto profile);
    Mono<Map<String, Object>> deleteProfile(int profileId);



}
