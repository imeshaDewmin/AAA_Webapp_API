package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.*;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.exception.SubscriberNotFoundException;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.SubscriberService;
import com.aaa.service.AAAService.utilities.CustomValidator;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class SubscriberServiceImpl implements SubscriberService {

    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    private final CustomValidator customValidator;

    @Override
    public Mono<PaginationDto> getSubscribersByPage(int page, int size) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT \n" +
                    "    s.subscriber_id,\n" +
                    "    s.username,\n" +
                    "    s.password,\n" +
                    "    s.status,\n" +
                    "    s.contact_no,\n" +
                    "    s.email,\n" +
                    "    s.ext_id,\n" +
                    "    s.created_date,\n" +
                    "    s.updated_time,\n" +
                    "    s.realm,\n" +
                    "    s.type,\n" +
                    "    sp.plan_id\n" +
                    "FROM \n" +
                    "    bb_subscriber s\n" +
                    "INNER JOIN \n" +
                    "    bb_subscriber_plan sp ON s.subscriber_id = sp.subscriber_id\n" +
                    "LIMIT :size OFFSET :offset;";
            ;
            String countQuery = "SELECT COUNT(*) FROM bb_subscriber";
            int offset = (page - 1) * size;
            Map<String, Object> params = new HashMap<>();
            params.put("size", size);
            params.put("offset", offset);

            int totalElements = namedParameterJdbcTemplate.queryForObject(countQuery, new HashMap<>(), Integer.class);
            List<SubscriberDto> subscribers = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    SubscriberDto.builder()
                            .subscriberId(rs.getInt("subscriber_id"))
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .status(rs.getString("status"))
                            .contactNo(rs.getString("contact_no"))
                            .email(rs.getString("email"))
                            .extId(rs.getString("ext_id"))
                            .createdDate(rs.getDate("created_date"))
                            .updatedTime(rs.getDate("updated_time"))
                            .realm(rs.getString("realm"))
                            .type(rs.getString("type"))
                            .planId(rs.getInt("plan_id"))
                            .build()
            );

            return Mono.just(PaginationDto.<List<SubscriberDto>>builder()
                    .page(page)
                    .size(size)
                    .content(subscribers)
                    .totalElements(totalElements)
                    .build());
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }


    @Override
    public Mono<Map<String, Object>> createSubscriber(SubscriberDto subscriber) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        boolean isFound = customValidator.checkSubscriber(subscriber.getUsername());
        try {
            if (!isFound) {
                String query = "INSERT INTO bb_subscriber (username, password, status, contact_no, email, ext_id, realm, type) " +
                        "VALUES (:username, :password, :status, :contactNo, :email, :extId, :realm, :type)";
                Map<String, Object> params = new HashMap<>();
                params.put("username", subscriber.getUsername());
                params.put("password", subscriber.getPassword());
                params.put("status", subscriber.getStatus());
                params.put("contactNo", subscriber.getContactNo());
                params.put("email", subscriber.getEmail());
                params.put("extId", subscriber.getExtId());
                params.put("realm", subscriber.getRealm());
                params.put("type", subscriber.getType());

                KeyHolder keyHolder = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params), keyHolder, new String[]{"id"});
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    result.put("subscriberId", generatedId.longValue());
                }

                result.put("status", ResponseCode.SUBSCRIBER_CREATE_SUCCESS);
                result.put("responseCode", ResponseCode.SUBSCRIBER_CREATE_SUCCESS.ordinal());
                return Mono.just(result);
            } else {
                return Mono.error(new UsernameOrEmailAlreadyExistedException(ResponseCode.USERNAME_OR_EMAIL_ALREADY_EXISTED));
            }
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.SUBSCRIBER_CREATE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> deleteSubscriber(int subscriberId) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            // Validate if subscriber exists by ID
            boolean isFound = customValidator.deleteSubscriber(subscriberId); // Assuming a method to validate by ID

            if (isFound) {
                // Use correct column name, e.g., 'subscriber_id' if 'id' doesn't exist
                String query = "DELETE FROM bb_subscriber WHERE subscriber_id = :subscriberId"; // Update here
                Map<String, Object> params = new HashMap<>();
                params.put("subscriberId", subscriberId);

                int rowsAffected = namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params));

                if (rowsAffected > 0) {
                    result.put("status", ResponseCode.SUBSCRIBER_DELETE_SUCCESS);
                    result.put("responseCode", ResponseCode.SUBSCRIBER_DELETE_SUCCESS.ordinal());
                } else {
                    return Mono.error(new GeneralException(ResponseCode.SUBSCRIBER_DELETE_FAILED));
                }

                return Mono.just(result);
            } else {
                return Mono.error(new SubscriberNotFoundException(ResponseCode.SUBSCRIBER_NOT_FOUND));
            }
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.SUBSCRIBER_DELETE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> updateSubscriberParameters(int subscriberId, int planId, SubscriberDto subscriber) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<PlanParameterSubscriberOverrideDto> planParameterOverrides = subscriber.getPlanParameterOverrides();
        List<PlanAttributeSubscriberOverrideDto> planAttributeOverrides = subscriber.getPlanAttributeOverrides();
        List<ProfileOverrideSubscriberAVPsDto> pofileOverrideSubscriberAVPs = subscriber.getPofileOverrideSubscriberAVPs();
        List<SubscriberAVPsDto> subscriberAVPs = subscriber.getSubscriberAVPs();
        List<DeviceWhitelistDto> deviceWhitelist = subscriber.getDeviceWhitelist();
        List<NasWhitelistDto> nasWhitelistList = subscriber.getNasWhitelist();
        List<SubscriberAttributeDto> subscriberAttributes = subscriber.getSubscriberAttributes();
        List<SubscriberParameterDto> subscriberParameterList = subscriber.getSubscriberParameters();
        try {

            if (planParameterOverrides != null && !planParameterOverrides.isEmpty()) {
                planParameterOverrides.forEach(override -> {
                    boolean isExisted = customValidator.checkIsExistParameter(subscriberId, planId, override.getParameterName());
                    String query = "";
                    Map<String, Object> planParameters = new HashMap<>();
                    if (isExisted) {
                        query = "UPDATE bb_plan_parameter_subscriber_override\n" +
                                "SET parameter_value = :parameterValue WHERE subscriber_id = :subscriberId AND plan_id = :planId AND parameter_name = :parameterName";
                    } else {
                        query = "INSERT INTO bb_plan_parameter_subscriber_override(subscriber_id, plan_id, parameter_name, parameter_value) \n" +
                                "VALUES (:subscriberId, :planId, :parameterName, :parameterValue)";

                    }
                    planParameters.put("subscriberId", subscriberId);
                    planParameters.put("planId", planId);
                    planParameters.put("parameterName", override.getParameterName());
                    if (override.getParameterOverrideValue() != null) {
                        planParameters.put("parameterValue", override.getParameterOverrideValue());
                    } else {
                        planParameters.put("parameterValue", "");
                    }
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(planParameters), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });

            }

            if (planAttributeOverrides != null && !planAttributeOverrides.isEmpty()) {
                planAttributeOverrides.forEach(attribute -> {
                    String query = "";
                    boolean isExisted = customValidator.checkIsExistAttribute(subscriberId, planId, attribute.getAttributeName());

                    if (isExisted) {
                        query = "UPDATE bb_plan_attribute_subscriber_override\n" +
                                "SET attribute_value = :attributeValue WHERE subscriber_id = :subscriberId AND plan_id = :planId AND attribute_name = :attributeName";
                    } else {
                        query = "INSERT INTO bb_plan_attribute_subscriber_override (subscriber_id, plan_id, attribute_name, attribute_value) " +
                                "VALUES (:subscriberId, :planId, :attributeName, :attributeValue)";
                    }
                    Map<String, Object> planAttribute = new HashMap<>();
                    planAttribute.put("subscriberId", subscriberId);
                    planAttribute.put("planId", planId);
                    planAttribute.put("attributeName", attribute.getAttributeName());
                    if (attribute.getAttributeOverrideValue() != null) {
                        planAttribute.put("attributeValue", attribute.getAttributeOverrideValue());
                    } else {
                        planAttribute.put("attributeValue", "");
                    }

                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(planAttribute), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();
                });
            }

            if (pofileOverrideSubscriberAVPs != null && !pofileOverrideSubscriberAVPs.isEmpty()) {
                log.info("remove bb_profile_avp_subscriber_override subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_profile_avp_subscriber_override WHERE subscriber_id= :subscriberId";
                Map<String, Object> avpParams = new HashMap<>();
                avpParams.put("subscriberId", subscriberId);
                avpParams.put("planId", planId);

                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(avpParams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted device ids {}", deleteIds);
                pofileOverrideSubscriberAVPs.forEach(avp -> {
                    String query = "INSERT INTO bb_profile_avp_subscriber_override (subscriber_id, plan_id, override_key, override_value, override_when) " +
                            "VALUES (:subscriberId, :planId, :overrideKey, :overrideValue, :overrideWhen)";
                    avpParams.put("overrideKey", avp.getOverrideKey());
                    avpParams.put("overrideValue", avp.getOverrideValue());
                    avpParams.put("overrideWhen", avp.getOverrideWhen());
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(avpParams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();
                });
            }


            if (subscriberAVPs != null && !subscriberAVPs.isEmpty()) {
                log.info("remove avps subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_avp WHERE subscriber_id= :subscriberId";
                Map<String, Object> subscriberAvpParam = new HashMap<>();
                subscriberAvpParam.put("subscriberId", subscriberId);

                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(subscriberAvpParam), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted device ids {}", deleteIds);
                subscriberAVPs.forEach(subscriberAVP -> {
                    log.info("adding device subscriber_id {} avps", subscriberId, subscriberAVP);
                    subscriberAvpParam.put("attributeGroup", subscriberAVP.getAttributeGroupId());
                    subscriberAvpParam.put("attribute", subscriberAVP.getAttribute());
                    subscriberAvpParam.put("operation", subscriberAVP.getOperation());
                    subscriberAvpParam.put("value", subscriberAVP.getValue());
                    subscriberAvpParam.put("status", subscriberAVP.getStatus());
                    String insertQuery = "INSERT INTO bb_subscriber_avp (subscriber_id, attribute_group, attribute, operation, value, status)" +
                            "VALUES (:subscriberId, :attributeGroup, :attribute, :operation, :value,  :status)";

                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(subscriberAvpParam), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });
            }

            if (deviceWhitelist != null && !deviceWhitelist.isEmpty()) {
                log.info("remove devices subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_device_whitelist WHERE subscriber_id= :subscriberId";
                Map<String, Object> deviceWhitelistParams = new HashMap<>();
                deviceWhitelistParams.put("subscriberId", subscriberId);

                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(deviceWhitelistParams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted device ids {}", deleteIds);
                deviceWhitelist.forEach(device -> {
                    log.info("adding device subscriber_id {} device_mac", subscriberId, device.getMACAddress());
                    deviceWhitelistParams.put("MACAddress", device.getMACAddress());
                    deviceWhitelistParams.put("description", device.getDescription());
                    deviceWhitelistParams.put("status", device.getStatus());
                    String insertQuery = "INSERT INTO bb_subscriber_device_whitelist (subscriber_id, device_mac, device_description, status)" +
                            "VALUES (:subscriberId, :MACAddress, :description, :status)";
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(deviceWhitelistParams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });
            }

            if (nasWhitelistList != null && !nasWhitelistList.isEmpty()) {
                log.info("remove all  naswhitelist subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_nas_whitelist WHERE subscriber_id= :subscriberId";
                Map<String, Object> nasWhitelistParams = new HashMap<>();
                nasWhitelistParams.put("subscriberId", subscriberId);
                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(nasWhitelistParams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted nas whitelist ids {}", deleteIds);
                nasWhitelistList.forEach(nas -> {
                    log.info("adding nas whitle list subscriber_id {} device_mac", subscriberId, nas);
                    nasWhitelistParams.put("nasIdPattern", nas.getNasIdPattern());
                    String insertQuery = "INSERT INTO bb_subscriber_nas_whitelist (subscriber_id, nas_id_pattern)" +
                            "VALUES (:subscriberId, :nasIdPattern)";
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(nasWhitelistParams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });
            }

            if (subscriberAttributes != null && !subscriberAttributes.isEmpty()) {
                log.info("remove all  attributes subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_attribute WHERE subscriber_id= :subscriberId";
                Map<String, Object> subscriberAttributePrams = new HashMap<>();
                subscriberAttributePrams.put("subscriberId", subscriberId);
                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(subscriberAttributePrams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted  ids {}", deleteIds);
                subscriberAttributes.forEach(attribute -> {
                    log.info("adding nas attributes list subscriber_id {} device_mac", subscriberId, attribute);
                    subscriberAttributePrams.put("attributeName", attribute.getAttributeName());
                    subscriberAttributePrams.put("attributeValue", attribute.getAttributeValue());
                    String insertQuery = "INSERT INTO bb_subscriber_attribute (subscriber_id, attribute_name, attribute_value)" +
                            "VALUES (:subscriberId, :attributeName, :attributeValue)";
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(subscriberAttributePrams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });

            }
            if (subscriberParameterList != null && !subscriberParameterList.isEmpty()) {
                log.info("remove all  parameters subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_parameter WHERE subscriber_id= :subscriberId";
                Map<String, Object> subscriberParameterPrams = new HashMap<>();
                subscriberParameterPrams.put("subscriberId", subscriberId);
                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(subscriberParameterPrams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();
                log.info("remove deleted  ids {}", deleteIds);
                subscriberParameterList.forEach(parameter -> {
                    log.info("adding nas attributes list subscriber_id {} device_mac", subscriberId, parameter);
                    subscriberParameterPrams.put("parameterName", parameter.getParameterName());
                    subscriberParameterPrams.put("parameterValue", parameter.getParameterValue());
                    subscriberParameterPrams.put("rejectOnFailure", parameter.getRejectOnFailure());
                    String insertQuery = "INSERT INTO bb_subscriber_parameter (subscriber_id, parameter_name, parameter_value, reject_on_failure)" +
                            "VALUES (:subscriberId, :parameterName, :parameterValue, :rejectOnFailure)";
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(subscriberParameterPrams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();

                });

            }

            result.put("status", ResponseCode.UPDATE_PARAMETERS);
            result.put("responseCode", ResponseCode.UPDATE_PARAMETERS.ordinal());
            return Mono.just(result);

        } catch (Exception e) {
            e.printStackTrace();
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }


    @Override
    public Flux<NasWhitelistDto> getNasWhitelist(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            String query = "SELECT * FROM bb_subscriber_nas_whitelist WHERE subscriber_id= :subscriberId";
            List<NasWhitelistDto> nasWhitelistList = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    NasWhitelistDto.builder()
                            .id(rs.getInt("id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .nasIdPattern(rs.getString("nas_id_pattern"))
                            .build()
            ));
            return Flux.fromIterable(nasWhitelistList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ERROR));
        }

    }

    @Override
    public Flux<DeviceWhitelistDto> getDeviceWhitelist(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            String query = "SELECT * FROM bb_subscriber_device_whitelist WHERE subscriber_id= :subscriberId";
            List<DeviceWhitelistDto> deviceNasWhitelist = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    DeviceWhitelistDto.builder()
                            .id(rs.getInt("id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .MACAddress(rs.getString("device_mac"))
                            .description(rs.getString("device_description"))
                            .status(rs.getString("status"))
                            .createAt(rs.getString("created_date"))
                            .build()
            ));
            return Flux.fromIterable(deviceNasWhitelist);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ERROR));
        }
    }

    @Override
    public Flux<SubscriberAVPsDto> getSubscriberAVPs(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            String query = "SELECT * FROM bb_subscriber_avp WHERE subscriber_id= :subscriberId";
            List<SubscriberAVPsDto> subscriberAvpList = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    SubscriberAVPsDto.builder()
                            .id(rs.getInt("id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .attributeGroupId(rs.getInt("attribute_group"))
                            .attribute(rs.getString("attribute"))
                            .operation(rs.getString("operation"))
                            .value(rs.getString("value"))
                            .status(rs.getString("status"))
                            .build()
            ));
            return Flux.fromIterable(subscriberAvpList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ERROR));
        }
    }

    @Override
    public Mono<SubscriberDto> getSubscriberById(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);

            String query = "SELECT\n" +
                    "    s.subscriber_id,\n" +
                    "    s.username,\n" +
                    "    s.password,\n" +
                    "    s.status,\n" +
                    "    s.contact_no,\n" +
                    "    s.email,\n" +
                    "    s.ext_id,\n" +
                    "    s.created_date,\n" +
                    "    s.updated_time,\n" +
                    "    s.realm,\n" +
                    "    s.type,\n" +
                    "    sp.plan_id\n" +
                    "FROM\n" +
                    "    bb_subscriber s\n" +
                    "    INNER JOIN bb_subscriber_plan sp ON s.subscriber_id = sp.subscriber_id " +
                    "WHERE s.subscriber_id = :subscriberId"; // Fixed query syntax

            List<SubscriberDto> subscribers = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    SubscriberDto.builder()
                            .subscriberId(rs.getInt("subscriber_id"))
                            .username(rs.getString("username"))
                            .password(rs.getString("password"))
                            .status(rs.getString("status"))
                            .contactNo(rs.getString("contact_no"))
                            .email(rs.getString("email"))
                            .extId(rs.getString("ext_id"))
                            .createdDate(rs.getDate("created_date"))
                            .updatedTime(rs.getDate("updated_time"))
                            .realm(rs.getString("realm"))
                            .type(rs.getString("type"))
                            .planId(rs.getInt("plan_id"))
                            .build()
            );

            return subscribers.isEmpty() ? Mono.empty() : Mono.just(subscribers.get(0));
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }


    @Override
    public Mono<Map<String, Object>> applyPlan(int subscriberId, int planId, String state) {
        try {
            boolean isAssignPlan = customValidator.checkAssignPlan(subscriberId);
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> result = new HashMap<>();
            resetAllPlanParameterWhenPlanApply(subscriberId);
            if (isAssignPlan) {
                String updateQuery = "UPDATE bb_subscriber_plan SET plan_state = :planState, plan_id= :planId WHERE subscriber_id = :subscriberId";
                Map<String, Object> params = new HashMap<>();
                params.put("subscriberId", subscriberId);
                params.put("planState", state);
                params.put("planId", planId);
                int rowsAffected = namedParameterJdbcTemplate.update(updateQuery, params);

                result.put("update", rowsAffected > 0 ? "success" : "failed");
            } else {
                String insertQuery = "INSERT INTO bb_subscriber_plan (subscriber_id, plan_id, plan_state) VALUES (:subscriberId, :planId, :planState)";
                Map<String, Object> params = new HashMap<>();
                params.put("subscriberId", subscriberId);
                params.put("planId", planId);
                params.put("planState", state);
                int rowsAffected = namedParameterJdbcTemplate.update(insertQuery, params);
                result.put("update", rowsAffected > 0 ? "success" : "failed");
            }

            return Mono.just(result);

        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }

    @Override
    public void resetAllPlanParameterWhenPlanApply(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);

            String[] queries = new String[3];
            queries[0] = "DELETE FROM bb_plan_parameter_subscriber_override WHERE subscriber_id= :subscriberId";
            queries[1] = "DELETE FROM bb_plan_attribute_subscriber_override WHERE subscriber_id= :subscriberId";
            queries[2] = "DELETE FROM bb_profile_avp_subscriber_override WHERE subscriber_id= :subscriberId";

            for (String query : queries) {
                if (query != null) {
                    int rowsAffected = namedParameterJdbcTemplate.update(query, params);
                    if (rowsAffected > 0) {
                        log.info("Successfully removed plan overrides with query: {}", query);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public Mono<Map<String, Object>> updateSubscriber(int subscriberId, SubscriberDto subscriber) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            StringBuilder queryBuilder = new StringBuilder("UPDATE bb_subscriber SET ");
            Map<String, Object> params = new HashMap<>();
            if (subscriber.getUsername() != null) {
                queryBuilder.append("username = :username, ");
                params.put("username", subscriber.getUsername());
            }
            if (subscriber.getPassword() != null) {
                queryBuilder.append("password = :password, ");
                params.put("password", subscriber.getPassword());
            }
            if (subscriber.getStatus() != null) {
                queryBuilder.append("status = :status, ");
                params.put("status", subscriber.getStatus());
            }
            if (subscriber.getContactNo() != null) {
                queryBuilder.append("contact_no = :contactNo, ");
                params.put("contactNo", subscriber.getContactNo());
            }
            if (subscriber.getEmail() != null) {
                queryBuilder.append("email = :email, ");
                params.put("email", subscriber.getEmail());
            }
            if (subscriber.getExtId() != null) {
                queryBuilder.append("ext_id = :extId, ");
                params.put("extId", subscriber.getExtId());
            }
            if (subscriber.getRealm() != null) {
                queryBuilder.append("realm = :realm, ");
                params.put("realm", subscriber.getRealm());
            }
            if (subscriber.getType() != null) {
                queryBuilder.append("type = :type, ");
                params.put("type", subscriber.getType());
            }
            queryBuilder.setLength(queryBuilder.length() - 2);
            queryBuilder.append(" WHERE subscriber_id = :subscriberId");
            params.put("subscriberId", subscriberId);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(queryBuilder.toString(), new MapSqlParameterSource(params), keyHolder, new String[]{"id"});

            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                result.put("subscriberId", generatedId.longValue());
            }
            result.put("status", ResponseCode.SUBSCRIBER_UPDATE_SUCCESS);
            result.put("responseCode", ResponseCode.SUBSCRIBER_UPDATE_SUCCESS.ordinal());

            return Mono.just(result);
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.SUBSCRIBER_UPDATE_FAILED));
        }
    }

    @Override
    public Flux<SubscriberAttributeDto> getSubscriberAttribute(int subscriberId) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("subscriberId", subscriberId);
        try {
            String query = "SELECT * FROM bb_subscriber_attribute WHERE subscriber_id= :subscriberId";
            List<SubscriberAttributeDto> subscriberAttributes = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    SubscriberAttributeDto.builder()
                            .id(rs.getInt("id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .attributeName(rs.getString("attribute_name"))
                            .attributeValue(rs.getString("attribute_value"))
                            .build()
            ));
            return Flux.fromIterable(subscriberAttributes);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ERROR));
        }
    }

    @Override
    public Flux<SubscriberParameterDto> getSubscriberParameter(int subscriberId) {
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        Map<String, Object> params = new HashMap<>();
        params.put("subscriberId", subscriberId);
        try {
            String query = "SELECT * FROM bb_subscriber_parameter WHERE subscriber_id= :subscriberId";
            List<SubscriberParameterDto> subscriberParameterList = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    SubscriberParameterDto.builder()
                            .id(rs.getInt("parameter_id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .parameterName(rs.getString("parameter_name"))
                            .parameterValue(rs.getString("parameter_value"))
                            .rejectOnFailure(rs.getInt("reject_on_failure"))
                            .build()
            ));
            return Flux.fromIterable(subscriberParameterList);

        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ERROR));
        }
    }


}
