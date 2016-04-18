package com.qqj.admin.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AdminUserRequest {
    private String username;

    private String telephone;

    private String realname;

    private boolean enable;

    private List<Long> adminRoleIds = new ArrayList<Long>();

}
