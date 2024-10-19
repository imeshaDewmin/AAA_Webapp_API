package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.ProfileOverrideMetaDto;
import com.aaa.service.AAAService.dtos.StateDto;
import com.aaa.service.AAAService.dtos.SubscriberAttributeMetaDto;
import com.aaa.service.AAAService.dtos.SubscriberParameterMetaDto;
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

    @Override
    public Flux<SubscriberAttributeMetaDto> getAttributes() {
        try {
            String query = "SELECT * FROM bb_subscriber_attribute_meta";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<SubscriberAttributeMetaDto> attributeList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    SubscriberAttributeMetaDto.builder()
                            .id(rs.getInt("id"))
                            .attribute(rs.getString("attribute"))
                            .build()
            );
            return Flux.fromIterable(attributeList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.ATTRIBUTE_META_FETCH_FAILED));
        }
    }

    @Override
    public Flux<SubscriberParameterMetaDto> getParameters() {
        try {
            String query = "SELECT * FROM bb_parameter_meta";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<SubscriberParameterMetaDto> parameterMetaList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    SubscriberParameterMetaDto.builder()
                            .id(rs.getInt("id"))
                            .parameter(rs.getString("parameter_name"))
                            .build()
            );
            return Flux.fromIterable(parameterMetaList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PARAMETER_META_FETCH_FAILED));
        }
    }

    @Override
    public Flux<ProfileOverrideMetaDto> getProfiles() {
        try {
            String query = "SELECT * FROM bb_profile_avp_override_key";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<ProfileOverrideMetaDto> parameterMetaList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    ProfileOverrideMetaDto.builder()
                            .id(rs.getInt("id"))
                            .profile(rs.getString("override_key"))
                            .build()
            );
            return Flux.fromIterable(parameterMetaList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PROFILE_META_FETCH_FAILED));
        }
    }
}
