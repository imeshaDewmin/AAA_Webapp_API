package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.StateDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.service.AppService;
import com.aaa.service.AAAService.utilities.ResponseCode;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Data
@Service
public class AppServiceImpl implements AppService {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    @Override
    public Flux<StateDto> getState() {
        try {
            String query = "SELECT * FROM bb_states";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<StateDto> statusList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    StateDto.builder()
                            .state(rs.getString("state_id"))
                            .isAuthorized(rs.getInt("is_authorized"))
                            .description(rs.getString("description"))
                            .build()
            );

            return Flux.fromIterable(statusList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PLAN_FETCH_FAILED));
        }
    }
}
