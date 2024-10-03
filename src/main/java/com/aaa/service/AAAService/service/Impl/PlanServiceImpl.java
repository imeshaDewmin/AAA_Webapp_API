package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.service.PlanService;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class PlanServiceImpl implements PlanService {
    private final JdbcTemplate jdbcTemplate;

    @Override
    public Flux<PlanDto> getPlans() {
        try {
            String query = "SELECT * FROM bb_plan";
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<PlanDto> planList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    PlanDto.builder()
                            .planId(rs.getInt("plan_id"))
                            .typeId(rs.getInt("type_id"))
                            .planName(rs.getString("plan_name"))
                            .description(rs.getString("description"))
                            .build()
            );

            return Flux.fromIterable(planList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_FETCH_FAILED));
        }
    }

    @Override
    public Flux<PlanAttributeDto> getPlanAttributes(int planId) {
        try {
            String query = "SELECT * FROM bb_plan_attribute_subscriber_override tbl WHERE tbl.plan_id= :planId";
            Map<String, Object> params = new HashMap<>();
            params.put("planId", planId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<PlanAttributeDto> planAttributeList = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    PlanAttributeDto.builder()
                            .overrideId(rs.getInt("override_id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .planId(rs.getInt("plan_id"))
                            .attributeName(rs.getString("attribute_name"))
                            .attributeValue(rs.getString("attribute_value"))
                            .build()
            );
            return Flux.fromIterable(planAttributeList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_ATTRIBUTE_FETCH_FAILED));
        }
    }
}
