package com.mishu.cgwy.admin.dto;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;

/**
 * Created by bowen on 2015/4/10.
 */
@Data
public class VendorRequest {

    private String name;

    private String contact;

    private String telephone;

    private String email;

    private String address;

    private String brand;
    
    private OrganizationVo organization;

    private SimpleCityWrapper city;

    private Long paymentVendorId;

    private boolean defaultVendor;
}
