package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.inventory.vo.VendorVo;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by wangguodong on 15/11/4.
 */
@Data
public class PurchaseOrderGroupByVendorId {
    private List<PurchaseOrderItemVo> purchaseOrderItems;
    private VendorVo vendor;
    private BigDecimal total;
}