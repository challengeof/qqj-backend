package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class CartListResponse extends RestError {
    private List<CartListData> cartList = new ArrayList<CartListData>();
    private int shippingFee;
    //设置满多少元免运费
    private int shippingFeeLimit;
    private int orderSyncTime;

}
