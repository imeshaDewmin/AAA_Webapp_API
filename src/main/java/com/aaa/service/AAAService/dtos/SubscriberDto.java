package com.aaa.service.AAAService.dtos;

import lombok.Builder;
import lombok.Data;
import java.util.Date;

@Data
@Builder
public class SubscriberDto{
    private int subscriberId;
    private String username;
    private String password;
    private String status;
    private String contactNo;
    private String email;
    private String extId;
    private Date createdDate;
    private Date updatedTime;
    private String realm;
    private String type;
}
