package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.SubscriberService;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class SubscriberServiceImpl implements SubscriberService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public PaginationDto<List<SubscriberDto>> getSubscribersByPage(int page, int size) {
        String query = "SELECT * FROM bb_subscriber LIMIT :size OFFSET :offset";
        String countQuery = "SELECT COUNT(*) FROM bb_subscriber";
        int offset = (page - 1) * size;
        Map<String, Object> params = new HashMap<>();
        params.put("size", size);
        params.put("offset", offset);
        NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
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

        return PaginationDto.<List<SubscriberDto>>builder()
                .page(page)
                .size(size)
                .content(subscribers)
                .totalElements(totalElements)
                .build();
    }

    @Override
    public Mono<Map<String, Object>> createSubscriber(SubscriberDto subscriber) {
        Map<String, Object> result = new HashMap<>();
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
                NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
                namedParameterJdbcTemplate.update(query, params);
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


    public boolean checkUser(String username) {
        try {
            String query = "SELECT COUNT(*) FROM bb_subscriber WHERE username = :username";
            Map<String, Object> params = new HashMap<>();
            params.put("username", username);
            NamedParameterJdbcTemplate namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


}
