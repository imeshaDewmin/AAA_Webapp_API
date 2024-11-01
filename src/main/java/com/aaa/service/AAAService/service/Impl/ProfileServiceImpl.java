package com.aaa.service.AAAService.service.Impl;

import com.aaa.service.AAAService.dtos.PaginationDto;
import com.aaa.service.AAAService.dtos.PlanDto;
import com.aaa.service.AAAService.dtos.ProfileDto;
import com.aaa.service.AAAService.dtos.ProfileOverrideSubscriberAVPsDto;
import com.aaa.service.AAAService.exception.GeneralException;
import com.aaa.service.AAAService.exception.ProfileNotFoundException;
import com.aaa.service.AAAService.exception.UsernameOrEmailAlreadyExistedException;
import com.aaa.service.AAAService.service.ProfileService;
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
public class ProfileServiceImpl implements ProfileService {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;
    private final CustomValidator customValidator;

    @Override
    public Flux<ProfileDto> getProfile() {
        return null;
    }

    @Override
    public Flux<ProfileDto> getProfiles() {
        try {
            String query = "SELECT * FROM bb_profile";
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            List<ProfileDto> profileList = namedParameterJdbcTemplate.query(query, (rs, rowNum) ->
                    ProfileDto.builder()
                            .profileId(rs.getInt("profile_id"))
                            .attributeGroup(rs.getInt("attribute_group"))
                            .profileKey(rs.getString("profile_key"))
                            .description(rs.getString("description"))
                            .build()
            );

            return Flux.fromIterable(profileList);
        } catch (Exception e) {
            return Flux.error(new GeneralException(ResponseCode.PROFILE_FETCH_FAILED));
        }
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

    public Mono<PaginationDto> getProfilesByPage(int page, int size) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

            // Corrected SQL query without unnecessary join and comma
            String query = "SELECT \n" +
                    "    profile_id,\n" +
                    "    attribute_group,\n" +
                    "    profile_key,\n" +
                    "    description \n" + // Removed the trailing comma
                    "FROM \n" +
                    "    bb_profile \n" + // Removed the unnecessary alias
                    "LIMIT :size OFFSET :offset;";

            String countQuery = "SELECT COUNT(*) FROM bb_profile";

            // Calculate the offset for pagination
            int offset = (page - 1) * size;
            Map<String, Object> params = new HashMap<>();
            params.put("size", size);
            params.put("offset", offset);

            // Get the total number of elements
            int totalElements = namedParameterJdbcTemplate.queryForObject(countQuery, new HashMap<>(), Integer.class);

            // Query to fetch paginated profile
            List<ProfileDto> profile = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    ProfileDto.builder()
                            .profileId(rs.getInt("profile_id"))
                            .attributeGroup(rs.getInt("attribute_group"))
                            .profileKey(rs.getString("profile_key"))
                            .description(rs.getString("description"))
                            .build()
            );

            // Build and return the pagination DTO
            return Mono.just(PaginationDto.<List<ProfileDto>>builder()
                    .page(page)
                    .size(size)
                    .content(profile)
                    .totalElements(totalElements)
                    .build());
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }
    @Override
    public Mono<ProfileDto> getProfilesById(int profileId) {
        try {
            System.out.println("Fetching profile with ID: " + profileId);
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            Map<String, Object> params = new HashMap<>();
            params.put("profileId", profileId);

            String query = "SELECT " +
                    "profile_id AS profile_id, " +
                    "attribute_group AS attribute_group, " +
                    "profile_key AS profile_key, " +
                    "description AS description " +
                    "FROM bb_profile " +
                    "WHERE profile_id = :profileId";

            List<ProfileDto> profile = namedParameterJdbcTemplate.query(query, params, (rs, rowNum) ->
                    ProfileDto.builder()
                            .profileId(rs.getInt("profile_id"))
                            .attributeGroup(rs.getInt("attribute_group"))
                            .profileKey(rs.getString("profile_key"))
                            .description(rs.getString("description"))
                            .build()
            );

            return profile.isEmpty() ? Mono.empty() : Mono.just(profile.get(0));
        } catch (Exception e) {
            System.err.println("Error fetching profile: " + e.getMessage());
            return Mono.error(new GeneralException(ResponseCode.ERROR));
        }
    }

    @Override
    public Mono<Map<String, Object>> createProfile(ProfileDto profile) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate); // Ensure jdbcTemplate is initialized

        // Check if profile with the given attributeGroup already exists
        boolean isFound = customValidator.checkProfile(profile.getAttributeGroup());
        try {
            if (!isFound) {
                // SQL query for inserting profile data
                String query = "INSERT INTO bb_profile (attribute_group, profile_key, description) " +
                        "VALUES (:attributeGroup, :profileKey, :description)";
                Map<String, Object> params = new HashMap<>();
                params.put("attributeGroup", profile.getAttributeGroup());
                params.put("profileKey", profile.getProfileKey());
                params.put("description", profile.getDescription());

                // KeyHolder to capture generated ID
                KeyHolder keyHolder = new GeneratedKeyHolder();
                namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params), keyHolder, new String[]{"id"});

                // Retrieve generated ID
                Number generatedId = keyHolder.getKey();
                if (generatedId != null) {
                    result.put("profileId", generatedId.longValue());
                }

                // Set success response
                result.put("status", ResponseCode.PROFILE_CREATE_SUCCESS);
                result.put("responseCode", ResponseCode.PROFILE_CREATE_SUCCESS.ordinal());
                return Mono.just(result);
            } else {
                // Return error if profile already exists
                return Mono.error(new ProfileNotFoundException(ResponseCode.PROFILE_NOT_FOUND));
            }
        } catch (Exception e) {
            // Log the error and return a general failure response
            e.printStackTrace(); // Log the error for debugging
            return Mono.error(new GeneralException(ResponseCode.PROFILE_CREATE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> deleteProfile(int profileId) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            // Validate if profile exists by ID
            boolean isFound = customValidator.deleteProfile(profileId); // Assuming a method to validate by ID

            if (isFound) {
                String query = "DELETE FROM bb_profile WHERE profile_id = :profileId"; // Update here
                Map<String, Object> params = new HashMap<>();
                params.put("profileId", profileId);

                int rowsAffected = namedParameterJdbcTemplate.update(query, new MapSqlParameterSource(params));

                if (rowsAffected > 0) {
                    result.put("status", ResponseCode.PROFILE_DELETE_SUCCESS);
                    result.put("responseCode", ResponseCode.PROFILE_DELETE_SUCCESS.ordinal());
                } else {
                    return Mono.error(new GeneralException(ResponseCode.PROFILE_DELETE_FAILED));
                }

                return Mono.just(result);
            } else {
                return Mono.error(new ProfileNotFoundException(ResponseCode.PROFILE_NOT_FOUND));
            }
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.PROFILE_DELETE_FAILED));
        }
    }


    @Override
    public Mono<Map<String, Object>> updateProfile(int profileId, ProfileDto profile) {
        Map<String, Object> result = new HashMap<>();
        namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);

        try {
            StringBuilder queryBuilder = new StringBuilder("UPDATE bb_profile SET ");
            Map<String, Object> params = new HashMap<>();

            // Check which fields are not null and append them to the query
            if (profile.getAttributeGroup() != 0) { // Assuming typeId can't be null and is an integer
                queryBuilder.append("attribute_group = :attributeGroup, ");
                params.put("attributeGroup", profile.getAttributeGroup());
            }
            if (profile.getProfileKey() != null) {
                queryBuilder.append("profile_key = :profileKey, ");
                params.put("profileKey", profile.getProfileKey());
            }
            if (profile.getDescription() != null) {
                queryBuilder.append("description = :description, ");
                params.put("description", profile.getDescription());
            }

            // Remove the last comma and space, and append the WHERE clause
            queryBuilder.setLength(queryBuilder.length() - 2);
            queryBuilder.append(" WHERE profile_id = :profileId");
            params.put("profileId", profileId);

            // Execute the update
            KeyHolder keyHolder = new GeneratedKeyHolder();
            namedParameterJdbcTemplate.update(queryBuilder.toString(), new MapSqlParameterSource(params), keyHolder, new String[]{"profile_id"});

            Number generatedId = keyHolder.getKey();
            if (generatedId != null) {
                result.put("profileId", generatedId.longValue());
            }
            result.put("status", ResponseCode.PROFILE_UPDATE_SUCCESS); // Define the appropriate response code
            result.put("responseCode", ResponseCode.PROFILE_UPDATE_SUCCESS.ordinal());

            return Mono.just(result);
        } catch (Exception e) {
            return Mono.error(new GeneralException(ResponseCode.PROFILE_UPDATE_FAILED));
        }
    }


}
