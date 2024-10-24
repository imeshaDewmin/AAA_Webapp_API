package com.aaa.service.AAAService.utilities;

import com.aaa.service.AAAService.dtos.DeviceWhitelistDto;
import lombok.Data;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Data
public class CustomValidator {
    private final JdbcTemplate jdbcTemplate;
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate = null;


    public boolean checkExistDeviceWhitelist(int subscriberId, String MACAddress) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_subscriber_device_whitelist WHERE subscriber_id = :subscriberId AND device_mac = :MACAddress";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            params.put("MACAddress", MACAddress);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    //Check existing subscriber
    public final boolean checkSubscriber(String username) {
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



    public final boolean checkPlan(String planName) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_plan WHERE plan_name = :planName";
            Map<String, Object> params = new HashMap<>();
            params.put("planName", planName);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }




    public final boolean deleteSubscriber(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_subscriber WHERE subscriber_id = :subscriberId";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public final boolean deletePlan(int planId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_plan WHERE plan_id = :planId"; // Ensure the table name is correct
            Map<String, Object> params = new HashMap<>();
            params.put("planId", planId);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0; // Return true if the plan exists
        } catch (Exception e) {
            e.printStackTrace(); // Log the exception for debugging
        }
        return false; // Return false if there was an error or the plan does not exist
    }

    //Check existing NAS
    public boolean checkNasPattern(String subscriberId, String pattern) {
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

    public boolean checkIsExistParameter(int subscriberId, int planId, String parameterName) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_plan_parameter_subscriber_override WHERE subscriber_id = :subscriberId AND plan_id = :planId AND parameter_name = :parameterName";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            params.put("planId", planId);
            params.put("parameterName", parameterName);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkIsExistAttribute(int subscriberId, int planId, String attributeName) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_plan_attribute_subscriber_override WHERE subscriber_id = :subscriberId AND plan_id = :planId AND attribute_name = :attributeName";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            params.put("planId", planId);
            params.put("attributeName", attributeName);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean checkAssignPlan(int subscriberId) {
        try {
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
            String query = "SELECT COUNT(*) FROM bb_subscriber_plan WHERE subscriber_id = :subscriberId";
            Map<String, Object> params = new HashMap<>();
            params.put("subscriberId", subscriberId);
            Integer count = namedParameterJdbcTemplate.queryForObject(query, params, Integer.class);
            return count != null && count > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
