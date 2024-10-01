package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.service.PlanService;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Data
public class PlanServiceImpl implements PlanService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<PlanDto> getPlans() {
        String query = "SELECT * FROM bb_plan";
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<PlanDto> plans = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                PlanDto.builder()
                        .planId(rs.getInt("plan_id"))
                        .typeId(rs.getInt("type_id"))
                        .planName(rs.getString("plan_name"))
                        .description(rs.getString("description"))
                        .build()

        );
        return plans;
    }
}
