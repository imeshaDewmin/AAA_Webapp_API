package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;

import java.util.List;

public interface SubscriberService {
    PaginationDto getSubscribersByPage(int page, int size);
}
