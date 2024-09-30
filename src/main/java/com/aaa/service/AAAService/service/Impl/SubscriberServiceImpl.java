package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.SubscriberDto;
import com.aaa.service.AAAService.service.SubscriberService;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

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


}
