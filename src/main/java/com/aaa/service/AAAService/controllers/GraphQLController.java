package com.aaa.service.AAAService.controllers;

import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

@Controller
public class GraphQLController {
    @QueryMapping(name = "test")
    public String test(){
        return "Hi AAA Web API Service";
    }
}
