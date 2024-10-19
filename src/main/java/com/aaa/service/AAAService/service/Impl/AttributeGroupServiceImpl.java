package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.AttributeGroupDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.service.AttributeGroupService;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Data
@Slf4j
@Service
public class AttributeGroupServiceImpl implements AttributeGroupService {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    @Override
    public Flux<AttributeGroupDto> getNasAttributeGroup() {
        try {
            String query = "SELECT * FROM bb_nas_attrgroup";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<AttributeGroupDto> attributeGroupList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    AttributeGroupDto.builder()
                            .id(rs.getInt("group_id"))
                            .name(rs.getString("group_name"))
                            .description(rs.getString("group_description"))
                            .build()
            );

            return Flux.fromIterable(attributeGroupList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ATTRIBUTE_FETCH_FAILED));
        }
    }
}
