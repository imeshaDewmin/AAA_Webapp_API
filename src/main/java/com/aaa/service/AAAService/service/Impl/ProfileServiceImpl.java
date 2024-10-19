package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.ProfileDto;
import com.aaa.service.AAAService.dtos.ProfileOverrideSubscriberAVPsDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.service.ProfileService;
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
public class ProfileServiceImpl implements ProfileService {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;

    @Override
    public Flux<ProfileDto> getProfile() {
        return null;
    }

    @Override
    public Flux<ProfileOverrideSubscriberAVPsDto> getProfileOverrideSubscriberAVPs(int subscriberId, int planId) {
        try {
            String query = "SELECT * FROM bb_profile_avp_subscriber_override WHERE subscriber_id = :subscriberId AND plan_id = :planId";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            params.put("planId", planId);

            List<ProfileOverrideSubscriberAVPsDto> avpList = namedParameterJdbcTemplate.query(query, params, ((rs, rowNum) ->
                    ProfileOverrideSubscriberAVPsDto.builder()
                            .overrideId(rs.getInt("override_id"))
                            .subscriberId(rs.getInt("subscriber_id"))
                            .planId(rs.getInt("plan_id"))
                            .overrideKey(rs.getString("override_key"))
                            .overrideValue(rs.getString("override_value"))
                            .overrideWhen(rs.getString("override_when"))
                            .build()
            ));
            return Flux.fromIterable(avpList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.FETCH_PROFILE_OVERRIDE_AVPS_FETCH_FAILED));
        }
    }
}
