package com.mishu.cgwy.admin.security;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.organization.domain.Organization;

import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 4/28/15
 * Time: 8:46 PM
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class LegacyLoginResponse extends RestError {
    private Long adminId;
    private List<Long> roleList = new ArrayList<>();
    private String username;
    private OrganizationVo organization;
}
