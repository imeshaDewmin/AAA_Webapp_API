package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.NASWhitelistDto;
import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.SubscriberService;
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
public class SubscriberServiceImpl implements SubscriberService {

    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    @Override
    public Mono<PaginationDto> getSubscribersByPage(int page, int size) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT * FROM bb_subscriber LIMIT :size OFFSET :offset";
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
        boolean isFound = checkUser(subscriber.getUsername());
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
    public Flux<NASWhitelistDto> getNasWhitelist(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            String query = "SELECT * FROM bb_subscriber_nas_whitelist WHERE subscriber_id= :subscriberId";
            List<NASWhitelistDto> nasWhitelistList = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    NASWhitelistDto.builder()
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


    private boolean checkUser(String username) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_subscriber WHERE username = :username";
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean checkNasPattern(String subscriberId, String pattern) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_subscriber_nas_whitelist WHERE subscriber_id = :username AND nas_id_pattern = :pattern";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            params.put("nasIdPattern", pattern);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
