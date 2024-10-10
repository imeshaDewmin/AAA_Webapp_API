package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.*;
import com.aaa.service.AAAService.exception.GeneralException;
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
    public Mono<Map<String, Object>> updateSubscriberParameters(int subscriberId, int planId, SubscriberDto subscriber) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
        List<PlanParameterSubscriberOverrideDto> planParameterOverrides = subscriber.getPlanParameterOverrides();
        List<PlanAttributeSubscriberOverrideDto> planAttributeOverrides = subscriber.getPlanAttributeOverrides();
        List<ProfileOverrideSubscriberAVPsDto> pofileOverrideSubscriberAVPs = subscriber.getPofileOverrideSubscriberAVPs();
        List<SubscriberAVPsDto> subscriberAVPs = subscriber.getSubscriberAVPs();
        List<DeviceWhitelistDto> deviceWhitelist = subscriber.getDeviceWhitelist();
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
                    }

                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(planAttribute), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();
                });
            }

//            if (pofileOverrideSubscriberAVPs != null && !pofileOverrideSubscriberAVPs.isEmpty()) {
//                log.info("remove bb_profile_avp_subscriber_override subscriber_id {}", subscriberId);
//                String deleteQuery = "DELETE FROM bb_profile_avp_subscriber_override WHERE subscriber_id= :subscriberId";
//                Map<String, Object> avpParams = new HashMap<>();
//                avpParams.put("subscriberId", subscriberId);
//
//                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
//                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(avpParams), keyHolderDelete, new String[]{"id"});
//                Number deleteIds = keyHolderDelete.getKey();
//                log.info("remove deleted device ids {}", deleteIds);
//                pofileOverrideSubscriberAVPs.forEach(avp -> {
//                    String query = "INSERT INTO bb_profile_avp_subscriber_override (subscriber_id, plan_id, override_key, override_value, override_when) " +
//                            "VALUES (:subscriberId, :planId, :overrideKey, :overrideValue, :overrideWhen)";
//
//                    avpParams.put("subscriberId", avp.getSubscriberId());
//                    avpParams.put("planId", avp.getPlanId());
//                    avpParams.put("overrideKey", avp.getOverrideKey());
//                    avpParams.put("overrideValue", avp.getOverrideValue());
//                    avpParams.put("overrideWhen", avp.getOverrideWhen());
//                    KeyHolder keyHolder = new GeneratedKeyHolder();
//                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(avpParams), keyHolder, new String[]{"id"});
//                    Number generatedId = keyHolder.getKey();
//                });
//            }

            if (subscriberAVPs != null && !subscriberAVPs.isEmpty()) {
                log.info("remove subscriber avps subscriber_id {}", subscriberId);
                String deleteQuery = "DELETE FROM bb_subscriber_avp WHERE subscriber_id= :subscriberId";
                Map<String, Object> deviceWhitelistParams = new HashMap<>();
                deviceWhitelistParams.put("subscriberId", subscriberId);

                KeyHolder keyHolderDelete = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(deleteQuery, new MapSqlParameterSource(deviceWhitelistParams), keyHolderDelete, new String[]{"id"});
                Number deleteIds = keyHolderDelete.getKey();

                subscriberAVPs.forEach(subAvp -> {
                    String query = "INSERT INTO bb_subscriber_avp (subscriber_id, attribute_group, attribute, operation, value, status) " +
                            "VALUES (:subscriberId, :attributeGroup, :attribute, :operation, :value, :status)";

                    Map<String, Object> subAvpParams = new HashMap<>();
                    subAvpParams.put("subscriberId",subscriberId);
                    subAvpParams.put("attributeGroup", subAvp.getAttributeGroupId());
                    subAvpParams.put("attribute", subAvp.getAttribute());
                    subAvpParams.put("operation", subAvp.getOperation());
                    subAvpParams.put("value", subAvp.getValue());
                    subAvpParams.put("status", subAvp.getStatus());
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(subAvpParams), keyHolder, new String[]{"id"});
                    Number generatedId = keyHolder.getKey();
                });
            }
//
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

                    deviceWhitelistParams.put("subscriberId", subscriberId);
                    deviceWhitelistParams.put("MACAddress", device.getMACAddress());
                    deviceWhitelistParams.put("description", device.getDescription());
                    deviceWhitelistParams.put("status", device.getStatus());
                    KeyHolder keyHolder = new GeneratedKeyHolder();
                    namedParameterJdbcTemplate.update(insertQuery, new MapSqlParameterSource(deviceWhitelistParams), keyHolder, new String[]{"id"});
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


}
