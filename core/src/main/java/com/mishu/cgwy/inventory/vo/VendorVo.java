package com.mishu.cgwy.inventory.vo;

import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import lombok.Data;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 5:12 PM
 */
@Data
public class VendorVo {
    private Long id;

    private String name;

    private String telephone;

    private String email;

    private String address;

    private String brand;

    private String contact;

    private CityVo city;

    private OrganizationVo organization;

    private Long paymentVendorId;

    private boolean defaultVendor;
}
