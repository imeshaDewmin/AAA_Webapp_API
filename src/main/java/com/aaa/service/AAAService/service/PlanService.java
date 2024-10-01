package com.aaa.service.AAAService.service;

import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;

import java.util.List;

public interface PlanService {
    List<PlanDto> getPlans();

    List<PlanAttributeDto> getPlanAttributes(int planId);
}
