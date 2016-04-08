package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.wrapper.AddressWrapper;
import lombok.Data;

/**
 * Created by bowen on 16/3/14.
 */
@Data
public class RestaurantVo {

    private Long id;

    private String telephone;

    private String name;

    private String receiver;

    private AddressWrapper address;

}
