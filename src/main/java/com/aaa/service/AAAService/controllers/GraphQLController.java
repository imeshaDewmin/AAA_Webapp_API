package com.aaa.service.AAAService.controllers;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.service.SubscriberService;
import lombok.Data;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@Data
public class GraphQLController {
    private final SubscriberService subscriberService;

    @QueryMapping(name = "test")
    public String test() {
        return "Hi AAA Web API Service";
    }

    @QueryMapping(name = "getSubscribersByPage")
    public PaginationDto<List<SubscriberDto>> getSubscribersByPage(@Argument int page, @Argument int size) {
        return subscriberService.getSubscribersByPage(page, size);
    }
}
