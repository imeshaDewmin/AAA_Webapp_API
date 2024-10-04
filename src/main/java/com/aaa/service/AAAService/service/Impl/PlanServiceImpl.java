package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PlanAttributeDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.dtos.PlanParameterDto;
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
            String queryForAttribute = "SELECT\n" +
                    "    bb_plan_attribute.id,\n" +
                    "    bb_plan_attribute.plan_id,\n" +
                    "    bb_plan_attribute.attribute_name,\n" +
                    "    bb_plan_attribute.attribute_value,\n" +
                    "    bb_plan_attribute_subscriber_override.attribute_value AS override_value\n" +
                    "FROM\n" +
                    "    bb_plan_attribute\n" +
                    "        LEFT JOIN\n" +
                    "    bb_plan_attribute_subscriber_override\n" +
                    "    ON\n" +
                    "                bb_plan_attribute.plan_id = bb_plan_attribute_subscriber_override.plan_id\n" +
                    "            AND bb_plan_attribute.attribute_name = bb_plan_attribute_subscriber_override.attribute_name\n" +
                    "WHERE\n" +
                    "        bb_plan_attribute.plan_id = :planId";
            Map<String, Object> params1 = new HashMap<>();
            params1.put("planId", planId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<PlanAttributeDto> planAttributeList = namedParameterJdbcTemplate.query(queryForAttribute, params1, (rs, rowNum) ->
                    PlanAttributeDto.builder()
                            .id(rs.getInt("id"))
                            .planId(rs.getInt("plan_id"))
                            .attributeName(rs.getString("attribute_name"))
                            .attributeValue(rs.getString("attribute_value"))
                            .attributeOverrideValue(rs.getString("override_value"))
                            .build()


            );
            return Flux.fromIterable(planAttributeList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_ATTRIBUTE_FETCH_FAILED));
        }
    }

    @Override
    public Flux<PlanParameterDto> getPlanParameters(int planId) {
        try {
            String queryForParameter = "SELECT\n" +
                    "    bb_plan_parameter.parameter_id,\n" +
                    "    bb_plan_parameter.plan_id,\n" +
                    "    bb_plan_parameter.parameter_name,\n" +
                    "    bb_plan_parameter.parameter_value,\n" +
                    "    bb_plan_parameter.reject_on_failure,\n" +
                    "    bb_plan_parameter_subscriber_override.parameter_value AS override_value\n" +
                    "FROM\n" +
                    "    bb_plan_parameter\n" +
                    "        LEFT JOIN\n" +
                    "    bb_plan_parameter_subscriber_override\n" +
                    "    ON\n" +
                    "                bb_plan_parameter.plan_id = bb_plan_parameter_subscriber_override.plan_id\n" +
                    "            AND bb_plan_parameter.parameter_name = bb_plan_parameter_subscriber_override.parameter_name\n" +
                    "WHERE\n" +
                    "        bb_plan_parameter.plan_id = :planId";
            Map<String, Object> params1 = new HashMap<>();
            params1.put("planId", planId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<PlanParameterDto> planParameterList = namedParameterJdbcTemplate.query(queryForParameter, params1, (rs, rowNum) ->
                    PlanParameterDto.builder()
                            .parameterId(rs.getInt("parameter_id"))
                            .planId(rs.getInt("plan_id"))
                            .parameterName(rs.getString("parameter_name"))
                            .parameterValue(rs.getString("parameter_value"))
                            .parameterOverrideValue(rs.getString("override_value"))
                            .rejectOnFailure(rs.getInt("reject_on_failure")).build()


            );
            return Flux.fromIterable(planParameterList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_PARAMETER_FETCH_FAILED));
        }
    }
}
