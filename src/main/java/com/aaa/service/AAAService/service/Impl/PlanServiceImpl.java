package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.*;
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
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    @Override
    public Flux<PlanDto> getPlans() {
        try {
            String query = "SELECT * FROM bb_plan";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
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
    public Flux<PlanAttributeDto> getPlanAttributes(int subscriberId, int planId) {
        try {
            String queryForAttribute = "SELECT * FROM bb_plan_attribute tbl WHERE tbl.plan_id= :planId";
            Map<String, Object> params1 = new HashMap<>();
            params1.put("planId", planId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<PlanAttributeDto> planAttributeList = namedParameterJdbcTemplate.query(queryForAttribute, params1, (rs, rowNum) ->
                    PlanAttributeDto.builder()
                            .overrideId(rs.getInt("id"))
                            .planId(rs.getInt("plan_id"))
                            .attributeName(rs.getString("attribute_name"))
                            .attributeValue(rs.getString("attribute_value"))
                            .build()


            );

            planAttributeList.forEach(planAttribute-> {
                Map<String, Object> params2 = new HashMap<>();
                params2.put("planId", planId);
                params2.put("subscriberId", subscriberId);
                String queryForAttributeOverride = "SELECT * FROM bb_plan_attribute_subscriber_override tbl WHERE tbl.plan_id= :planId AND tbl.subscriber_id= :subscriberId";
                List<PlanAttributeSubscriberOverrideDto> planAttributeSubscriberOverrides = namedParameterJdbcTemplate.query(queryForAttributeOverride, params2, (rs, rowNum) ->
                        PlanAttributeSubscriberOverrideDto.builder()
                                    .overrideId(rs.getInt("override_id"))
                                    .subscriberId(rs.getInt("subscriber_id"))
                                    .planId(rs.getInt("plan_id"))
                                    .attributeName(rs.getString("attribute_name"))
                                    .attributeValue(rs.getString("attribute_value"))
                                    .build()

                );
                if (planAttributeSubscriberOverrides.size() > 0 && planAttribute.getAttributeName().equals(planAttributeSubscriberOverrides.get(0).getAttributeName())) {
                    planAttribute.setAttributeOverrideValue(planAttributeSubscriberOverrides.get(0).getAttributeValue());
                } else {
                    planAttribute.setAttributeOverrideValue("");
                }
            });
            return Flux.fromIterable(planAttributeList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_ATTRIBUTE_FETCH_FAILED));
        }
    }

        @Override
        public Flux<PlanParameterDto> getPlanParameters ( int subscriberId, int planId){
            try {
                String queryForAttribute = "SELECT * FROM bb_plan_parameter tbl WHERE tbl.plan_id= :planId";
                Map<String, Object> params1 = new HashMap<>();
                params1.put("planId", planId);
                NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
                List<PlanParameterDto> planParameterList = namedParameterJdbcTemplate.query(queryForAttribute, params1, (rs, rowNum) ->
                        PlanParameterDto.builder()
                                .overrideId(rs.getInt("parameter_id"))
                                .planId(rs.getInt("plan_id"))
                                .parameterName(rs.getString("parameter_name"))
                                .parameterValue(rs.getString("parameter_value"))
                                .build()


                );
                planParameterList.forEach(planParameter -> {
                    Map<String, Object> params2 = new HashMap<>();
                    params2.put("planId", planId);
                    params2.put("subscriberId", subscriberId);
                    String queryForPlanParameterOverride = "SELECT * FROM bb_plan_parameter_subscriber_override tbl WHERE tbl.plan_id= :planId AND tbl.subscriber_id= :subscriberId";
                    List<PlanParameterSubscriberOverrideDto> planParameterSubscriberDtoOverrides = namedParameterJdbcTemplate.query(queryForPlanParameterOverride, params2, (rs, rowNum) ->
                            PlanParameterSubscriberOverrideDto.builder()
                                    .overrideId(rs.getInt("override_id"))
                                    .subscriberId(rs.getInt("subscriber_id"))
                                    .planId(rs.getInt("plan_id"))
                                    .parameterName(rs.getString("attribute_name"))
                                    .parameterValue(rs.getString("attribute_value"))
                                    .build()


                    );
                    if (planParameterSubscriberDtoOverrides.size() > 0 && planParameter.getParameterName().equals(planParameterSubscriberDtoOverrides.get(0).getParameterName())) {
                        planParameter.setParameterOverrideValue(planParameterSubscriberDtoOverrides.get(0).getParameterValue());
                    } else {
                        planParameter.setParameterOverrideValue("");
                    }
                });
                return Flux.fromIterable(planParameterList);

            } catch (Exception e) {
                return Flux.error(new GeneralException(ResponseCode.PLAN_ATTRIBUTE_FETCH_FAILED));
        }
    }
}
