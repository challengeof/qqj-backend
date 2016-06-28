package com.qqj.purchase.controller;

import com.qqj.product.controller.PurchaseInfo;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PurchaseRequest {

    // 购买人
    private Long customerId;

    private List<PurchaseInfo> purchaseInfoList;
}
