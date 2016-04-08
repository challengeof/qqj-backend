package com.mishu.cgwy.profile.constants;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/3/15.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CustomerQueryType {

    My("MyCustomer","crm我的客户"), all("AllCustomer","crm客户检索"),  sea("seaCustomer","crm客户公海");

    public final String val;
    public final String desc;

    private CustomerQueryType(String val, String desc) {
        this.val = val;
        this.desc = desc;
    }

    public static CustomerQueryType find(String val) {
        for (CustomerQueryType type : CustomerQueryType.values()) {
            if (type.val.equals(val)) {
                return type;
            }
        }
        return null;
    }

}
