package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.*;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.exception.PlanAlreadyExistsException;
import com.aaa.service.AAAService.exception.PlanNotFoundException;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.PlanService;
import com.aaa.service.AAAService.utilities.CustomValidator;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class PlanServiceImpl implements PlanService {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    private final CustomValidator customValidator;
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
    public Mono<PaginationDto> getPlansByPage(int page, int size) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

            String query = "SELECT \n" +
                    "    p.plan_id,\n" +
                    "    p.type_id,\n" +
                    "    p.plan_name,\n" +
                    "    p.description\n" +
                    "FROM \n" +
                    "    bb_plan p\n" +
                    "LIMIT :size OFFSET :offset;";

            String countQuery = "SELECT COUNT(*) FROM bb_plan";

            int offset = (page - 1) * size;
            Map<String, Object> params = new HashMap<>();
            params.put("size", size);
            params.put("offset", offset);

            int totalElements = namedParameterJdbcTemplate.queryForObject(countQuery, new HashMap<>(), Integer.class);
            List<PlanDto> plans = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    PlanDto.builder()
                            .planId(rs.getInt("plan_id"))
                            .typeId(rs.getInt("type_id"))
                            .planName(rs.getString("plan_name"))
                            .description(rs.getString("description"))
                            .build()
            );

            return Mono.just(PaginationDto.<List<PlanDto>>builder()
                    .page(page)
                    .size(size)
                    .content(plans)
                    .totalElements(totalElements)
                    .build());
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }


    @Override
    public Mono<PlanDto> getPlansById(int planId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("planId", planId); // Correct parameter

            String query = "SELECT\n" +
                    "    p.plan_id,\n" +
                    "    p.plan_name,\n" +
                    "    p.description,\n" +
                    "    pt.type_id\n" +
                    "FROM\n" +
                    "    bb_plan p\n" +
                    "    INNER JOIN bb_plan_type pt ON p.type_Id = pt.type_Id " +
                    "WHERE p.plan_id = :planId"; // Filter by planId

            List<PlanDto> plans = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    PlanDto.builder()
                            .planId(rs.getInt("plan_id"))
                            .typeId(rs.getInt("type_id"))
                            .planName(rs.getString("plan_name"))
                            .description(rs.getString("description"))
                            .build()
            );

            return plans.isEmpty() ? Mono.empty() : Mono.just(plans.get(0));
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }


    @Override
    public Mono<Map<String, Object>> createPlan(PlanDto plan) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        boolean isFound = customValidator.checkPlan(plan.getPlanName());
        try {
            if (!isFound) {
                String query = "INSERT INTO bb_plan (type_Id, plan_name, description) " +
                        "VALUES (:typeId, :planName, :description)";
                Map<String, Object> params = new HashMap<>();
                params.put("typeId", plan.getTypeId());
                params.put("planName", plan.getPlanName());
                params.put("description", plan.getDescription());

                KeyHolder keyHolder = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params), keyHolder, new String[]{"id"});
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    result.put("planId", generatedId.longValue());
                }

                result.put("status", ResponseCode.PLAN_CREATE_SUCCESS);
                result.put("responseCode", ResponseCode.PLAN_CREATE_SUCCESS.ordinal());
                return Mono.just(result);
            } else {
                return Mono.error(new PlanAlreadyExistsException(ResponseCode.PLAN_ALREADY_EXISTS));
            }
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.PLAN_CREATE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> updatePlan(int planId, PlanDto plan) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            StringBuilder queryBuilder = new StringBuilder("UPDATE bb_plan SET ");
            Map<String, Object> params = new HashMap<>();

            // Check which fields are not null and append them to the query
            if (plan.getTypeId() != 0) { // Assuming typeId can't be null and is an integer
                queryBuilder.append("type_id = :typeId, ");
                params.put("typeId", plan.getTypeId());
            }
            if (plan.getPlanName() != null) {
                queryBuilder.append("plan_name = :planName, ");
                params.put("planName", plan.getPlanName());
            }
            if (plan.getDescription() != null) {
                queryBuilder.append("description = :description, ");
                params.put("description", plan.getDescription());
            }

            // Remove the last comma and space, and append the WHERE clause
            queryBuilder.setLength(queryBuilder.length() - 2);
            queryBuilder.append(" WHERE plan_id = :planId");
            params.put("planId", planId);

            // Execute the update
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(queryBuilder.toString(), new MapSqlParameterSource(params), keyHolder, new String[]{"plan_id"});

            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                result.put("planId", generatedId.longValue());
            }
            result.put("status", ResponseCode.PLAN_UPDATE_SUCCESS); // Define the appropriate response code
            result.put("responseCode", ResponseCode.PLAN_UPDATE_SUCCESS.ordinal());

            return Mono.just(result);
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.PLAN_UPDATE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> deletePlan(int planId) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            // Validate if the plan exists by ID
            boolean isFound = customValidator.deletePlan(planId); // Assuming a method to validate by ID

            if (isFound) {
                // Use the correct column name for deletion
                String query = "DELETE FROM bb_plan WHERE plan_id = :planId"; // Update here
                Map<String, Object> params = new HashMap<>();
                params.put("planId", planId);

                int rowsAffected = namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params));

                if (rowsAffected > 0) {
                    result.put("status", ResponseCode.PLAN_DELETE_SUCCESS); // Define the appropriate response code
                    result.put("responseCode", ResponseCode.PLAN_DELETE_SUCCESS.ordinal());
                } else {
                    return Mono.error(new GeneralException(ResponseCode.PLAN_DELETE_FAILED));
                }

                return Mono.just(result);
            } else {
                return Mono.error(new PlanNotFoundException(ResponseCode.PLAN_NOT_FOUND)); // Custom exception for plan not found
            }
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.PLAN_DELETE_FAILED));
        }
    }



    @Override
    public Flux<PlanAttributeDto> getPlanAttributes(int subscriberId, int planId) {
        try {
            String queryForAttribute = "SELECT\n" +
                    "    bb_plan_attribute.id,\n" +
                    "    bb_plan_attribute.plan_id,\n" +
                    "    bb_plan_attribute.attribute_name,\n" +
                    "    bb_plan_attribute.attribute_value,\n" +
                    "    bb_plan_attribute_subscriber_override.attribute_value AS override_value,\n" +
                    "    bb_subscriber.subscriber_id\n" +
                    "FROM\n" +
                    "    bb_plan_attribute\n" +
                    "        LEFT JOIN bb_plan_attribute_subscriber_override\n" +
                    "                  ON bb_plan_attribute.plan_id = bb_plan_attribute_subscriber_override.plan_id\n" +
                    "                      AND bb_plan_attribute.attribute_name = bb_plan_attribute_subscriber_override.attribute_name\n" +
                    "                      AND bb_plan_attribute_subscriber_override.subscriber_id = :subscriberId" +
                    "        LEFT JOIN bb_subscriber\n" +
                    "                  ON bb_plan_attribute_subscriber_override.subscriber_id = bb_subscriber.subscriber_id\n" +
                    "WHERE\n" +
                    "        bb_plan_attribute.plan_id = :planId";

            Map<String, Object> params1 = new HashMap<>();
            params1.put("planId", planId);
            params1.put("subscriberId", subscriberId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

            List<PlanAttributeDto> planAttributeList = namedParameterJdbcTemplate.query(queryForAttribute, params1, (rs, rowNum) ->
                    PlanAttributeDto.builder()
                            .overrideId(rs.getInt("id"))
                            .planId(rs.getInt("plan_id"))
                            .attributeName(rs.getString("attribute_name"))
                            .attributeValue(rs.getString("attribute_value"))
                            .attributeOverrideValue(rs.getString("override_value"))
                            .build()
            );

            return Flux.fromIterable(planAttributeList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_PARAMETER_FETCH_FAILED));
        }
    }

    @Override
    public Flux<PlanParameterDto> getPlanParameters(int subscriberId, int planId) {
        try {
            String queryForParameter = "SELECT\n" +
                    "    bb_plan_parameter.parameter_id,\n" +
                    "    bb_plan_parameter.plan_id,\n" +
                    "    bb_plan_parameter.parameter_name,\n" +
                    "    bb_plan_parameter.parameter_value,\n" +
                    "    bb_plan_parameter.reject_on_failure,\n" +
                    "    bb_plan_parameter_subscriber_override.parameter_value AS override_value,\n" +
                    "    bb_subscriber.subscriber_id,\n" +
                    "    bb_subscriber.username\n" +
                    "FROM\n" +
                    "    bb_plan_parameter\n" +
                    "        LEFT JOIN bb_plan_parameter_subscriber_override\n" +
                    "                  ON bb_plan_parameter.plan_id = bb_plan_parameter_subscriber_override.plan_id\n" +
                    "                      AND bb_plan_parameter.parameter_name = bb_plan_parameter_subscriber_override.parameter_name\n" +
                    "                      AND bb_plan_parameter_subscriber_override.subscriber_id = :subscriberId" +
                    "        LEFT JOIN bb_subscriber\n" +
                    "                  ON bb_plan_parameter_subscriber_override.subscriber_id = bb_subscriber.subscriber_id\n" +
                    "WHERE\n" +
                    "        bb_plan_parameter.plan_id = :planId";

            Map<String, Object> params1 = new HashMap<>();
            params1.put("planId", planId);
            params1.put("subscriberId", subscriberId);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

            List<PlanParameterDto> planParameterList = namedParameterJdbcTemplate.query(queryForParameter, params1, (rs, rowNum) ->
                    PlanParameterDto.builder()
                            .overrideId(rs.getInt("parameter_id"))
                            .planId(rs.getInt("plan_id"))
                            .parameterName(rs.getString("parameter_name"))
                            .parameterValue(rs.getString("parameter_value"))
                            .parameterOverrideValue(rs.getString("override_value"))
                            .build()
            );

            return Flux.fromIterable(planParameterList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_PARAMETER_FETCH_FAILED));
        }
    }

}
