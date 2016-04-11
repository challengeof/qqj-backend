package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import lombok.Data;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 4:50 PM
 */
@Data
public class AddressWrapper {

    private String address;

    private String streeNumer;

    private Wgs84Point wgs84Point;

    public AddressWrapper() {

    }

    public AddressWrapper(Address obj) {
        address = obj.getAddress();
        streeNumer = obj.getStreetNumber();
        wgs84Point = obj.getWgs84Point();
    }
}
