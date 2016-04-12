package com.qqj.admin.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 3/13/15
 * Time: 6:28 PM
 */
@Data
public class AdminUserRequest {
    private String username;

    private String telephone;

    private String realname;

    private boolean enable;

    private List<Long> adminRoleIds = new ArrayList<Long>();

    private List<String> blockIds = new ArrayList<>();

    private Long organizationId;

    private boolean globalAdmin;

    private Long cityId;

    private List<String> cityWarehouseBlockIds = new ArrayList<>();

    private List<String> depotIds = new ArrayList<>();

}
