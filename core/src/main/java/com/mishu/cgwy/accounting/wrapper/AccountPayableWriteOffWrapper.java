package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayableWriteoff;
import com.mishu.cgwy.accounting.enumeration.AccountPayableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountPayableType;
import com.mishu.cgwy.accounting.enumeration.AccountPayableWriteOffStatus;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPayableWriteOffWrapper {

    private Long id;

    private Long stockOperationId;

    private String purchaseVendor;

    private String vendor;

    private Date accountPayableDate;

    private Date createDate;

    private Date writeOffDate;

    private BigDecimal writeOffAmount;

    private Date cancelDate;

    private AccountPayableWriteOffStatus status;

    private String dealer;

    private Long purchaseOrderId;

    public AccountPayableWriteOffWrapper(AccountPayableWriteoff accountPayableWriteoff) {
        this.id = accountPayableWriteoff.getId();
        AccountPayable accountPayable = accountPayableWriteoff.getAccountPayable();
        AccountPayableType type = AccountPayableType.fromInt(accountPayable.getType());

        PurchaseOrder purchaseOrder = null;
        if (AccountPayableType.PURCHASE.equals(type)) {
            this.stockOperationId = accountPayable.getStockIn().getId();
            purchaseOrder = accountPayable.getStockIn().getPurchaseOrder();
        } else if (AccountPayableType.RETURN.equals(type)) {
            this.stockOperationId = accountPayable.getStockOut().getId();
            purchaseOrder = accountPayable.getStockOut().getReturnNote().getPurchaseOrder();
        }

        this.purchaseVendor = purchaseOrder.getVendor().getName();
        this.purchaseOrderId = purchaseOrder.getId();

        this.vendor = accountPayableWriteoff.getAccountPayable().getVendor().getName();
        this.accountPayableDate = accountPayableWriteoff.getAccountPayable().getCreateDate();
        this.createDate = accountPayableWriteoff.getCreateDate();
        this.writeOffDate = accountPayableWriteoff.getWriteOffDate();
        this.writeOffAmount = accountPayableWriteoff.getWriteOffAmount();
        this.cancelDate  = accountPayableWriteoff.getCancelDate();
        this.status = AccountPayableWriteOffStatus.from(accountPayableWriteoff.getStatus());
        if (this.cancelDate != null) {
            this.dealer = accountPayableWriteoff.getCanceler().getRealname();
        } else {
            this.dealer = accountPayableWriteoff.getWriteOffer().getRealname();
        }
    }
}
