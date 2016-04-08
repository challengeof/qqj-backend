package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.enumeration.AccountPayableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountPayableType;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class AccountPayableWrapper {

    private Long id;

    private Date createDate;

    private Long stockOperationId;

    private String vendor;

    private Date accountPayableDate;

    private BigDecimal balance;

    private BigDecimal payable;

    private BigDecimal writeOffAmount;

    private BigDecimal currentWriteOffAmount;

    private Date writeOffDate;

    private BigDecimal unWriteOffAmount;

    private AccountPayableStatus status;

    private AccountPayableType type;

    private String purchaseVendor;

    private Long purchaseOrderId;

    private String remark;

    public AccountPayableWrapper(AccountPayable accountPayable) {
        this.id = accountPayable.getId();
        this.createDate = accountPayable.getCreateDate();
        this.vendor = accountPayable.getVendor().getName();
        this.accountPayableDate = accountPayable.getCreateDate();
        this.balance = accountPayable.getVendor().getAccount().getBalance();
        this.payable = accountPayable.getAmount();
        this.writeOffAmount = accountPayable.getWriteOffAmount() == null ? BigDecimal.ZERO : accountPayable.getWriteOffAmount();
        this.unWriteOffAmount = this.payable.subtract(this.writeOffAmount);
        this.status = AccountPayableStatus.fromInt(accountPayable.getStatus());
        this.type = AccountPayableType.fromInt(accountPayable.getType());

        PurchaseOrder purchaseOrder = null;

        if (type == AccountPayableType.PURCHASE) {
            this.stockOperationId = accountPayable.getStockIn() != null ? accountPayable.getStockIn().getId() : null;
            purchaseOrder = accountPayable.getStockIn().getPurchaseOrder();
        } else if (type == AccountPayableType.RETURN) {
            this.stockOperationId = accountPayable.getStockOut() != null ? accountPayable.getStockOut().getId() : null;
            purchaseOrder = accountPayable.getStockOut().getReturnNote().getPurchaseOrder();
        }

        this.purchaseVendor = purchaseOrder.getVendor().getName();
        this.purchaseOrderId = purchaseOrder.getId();
        this.currentWriteOffAmount = this.payable;
        this.writeOffDate = this.accountPayableDate;

        if (type == AccountPayableType.RETURN) {
            this.remark = accountPayable.getStockOut().getReturnNote().getRemark();
        } else {
            this.remark = purchaseOrder.getRemark();
        }
    }
}
