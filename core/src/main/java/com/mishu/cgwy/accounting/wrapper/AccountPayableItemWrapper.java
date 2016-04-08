package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableItem;
import com.mishu.cgwy.accounting.enumeration.AccountPayableType;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.purchase.vo.PurchaseOrderVo;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class AccountPayableItemWrapper {

    private PurchaseOrderVo purchaseOrder;

    private int quantity;

    private BigDecimal price;

    private AccountPayableWrapper accountPayable;

    private String productName;

    private String singleUnit;

    private String bundleUnit;

    private BigDecimal bundleQuantity;

    private BigDecimal bundlePrice;

    private BigDecimal itemTotal;

    public AccountPayableItemWrapper(AccountPayableItem accountPayableItem) {
        AccountPayable accountPayable = accountPayableItem.getAccountPayable();
        AccountPayableType type = AccountPayableType.fromInt(accountPayable.getType());

        PurchaseOrder purchaseOrderEntity = null;
        if (AccountPayableType.PURCHASE.equals(type)) {
            purchaseOrderEntity = accountPayable.getStockIn().getPurchaseOrder();
        } else if (AccountPayableType.RETURN.equals(type)) {
            purchaseOrderEntity = accountPayable.getStockOut().getReturnNote().getPurchaseOrder();
        }
        purchaseOrder = new PurchaseOrderVo();
        purchaseOrder.setId(purchaseOrderEntity.getId());
        purchaseOrder.setPurchaseOrderType(PurchaseOrderType.fromInt(purchaseOrderEntity.getType()));

        this.quantity = accountPayableItem.getQuantity();
        this.price = accountPayableItem.getPrice();
        this.accountPayable = new AccountPayableWrapper(accountPayable);
        this.productName = accountPayableItem.getSku().getName();
        this.singleUnit = accountPayableItem.getSku().getSingleUnit();
        this.bundleUnit = accountPayableItem.getSku().getBundleUnit();
        int capacityInBundle =  accountPayableItem.getSku().getCapacityInBundle();
        this.bundleQuantity = new BigDecimal(this.quantity).divide(new BigDecimal(capacityInBundle), 2, RoundingMode.HALF_UP);
        this.bundlePrice = new BigDecimal(capacityInBundle).multiply(price).setScale(2, RoundingMode.HALF_UP);
        this.itemTotal = new BigDecimal(quantity).multiply(price).setScale(2, RoundingMode.HALF_UP);
    }
}
