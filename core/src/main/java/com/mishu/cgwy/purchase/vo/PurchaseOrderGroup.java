package com.mishu.cgwy.purchase.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangguodong on 15/11/4.
 */
@Data
public class PurchaseOrderGroup {
    private List<PurchaseOrderGroupByVendorId> purchaseOrderGroupByVendorIdList = new ArrayList<>();
    private BigDecimal total;
}