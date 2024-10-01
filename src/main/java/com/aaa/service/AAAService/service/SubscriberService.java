package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PaginationDto;

public interface SubscriberService {
    PaginationDto getSubscribersByPage(int page, int size);
}
