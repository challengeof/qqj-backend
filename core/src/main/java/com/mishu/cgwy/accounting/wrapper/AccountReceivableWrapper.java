package com.mishu.cgwy.accounting.wrapper;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableWriteoff;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableWriteOffStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/10/13.
 */
@Data
public class AccountReceivableWrapper {

    private Long accountReceivableId;
    private Long sourceId;
    private Long orderId;
    private String trackerName;
    private String depotName;
    private String customerName;
    private String accountReceivableStatus;
    private String accountReceivableType;
    private BigDecimal expectedAmount;
    private BigDecimal writeOffAmount;
    private BigDecimal unWriteOffAmount;
    private Date orderDate;
    private Date sendDate;
    private Date receiveDate;
    private Date writeOffDate;
    private String writeOffer;
    private Long accountReceivableWriteoffId;
    private Date cancelDate;
    private String canceler;
    private String accountReceivableWriteoffStatus;

    public AccountReceivableWrapper(AccountReceivable accountReceivable) {

        this.accountReceivableId = accountReceivable.getId();
        if (accountReceivable.getType() == AccountReceivableType.SELL.getValue()) {
            this.sourceId = accountReceivable.getStockOut().getId();
            this.orderId = accountReceivable.getStockOut().getOrder().getId();
            this.trackerName = accountReceivable.getStockOut().getOrderGroup() != null && accountReceivable.getStockOut().getOrderGroup().getTracker() != null ? accountReceivable.getStockOut().getOrderGroup().getTracker().getRealname() : null;
            this.depotName = accountReceivable.getStockOut().getDepot().getName();
            this.orderDate = accountReceivable.getStockOut().getOrder().getSubmitDate();
            this.sendDate = accountReceivable.getStockOut().getFinishDate();

        } else if (accountReceivable.getType() == AccountReceivableType.RETURN.getValue()) {
            this.sourceId = accountReceivable.getStockIn().getId();
            this.orderId = accountReceivable.getStockIn().getSellReturn().getOrder().getId();
            this.trackerName = null;
            this.depotName = accountReceivable.getStockIn().getDepot().getName();
            this.orderDate = accountReceivable.getStockIn().getSellReturn().getOrder().getSubmitDate();
            this.sendDate = null;
        }
        this.customerName = accountReceivable.getRestaurant().getName();
        this.accountReceivableStatus = AccountReceivableStatus.fromInt(accountReceivable.getStatus()).getName();
        this.accountReceivableType = AccountReceivableType.fromInt(accountReceivable.getType()).getName();
        this.expectedAmount = accountReceivable.getAmount();
        this.writeOffAmount = accountReceivable.getWriteOffAmount();
        this.unWriteOffAmount = this.expectedAmount.subtract(this.writeOffAmount);
        this.receiveDate = accountReceivable.getCreateDate();
        this.writeOffDate = accountReceivable.getWriteOffDate();
        this.writeOffer = accountReceivable.getWriteOffer() != null ? accountReceivable.getWriteOffer().getRealname() : "";
    }

    public AccountReceivableWrapper(AccountReceivableWriteoff accountReceivableWriteoff) {
        this(accountReceivableWriteoff.getAccountReceivable());
        this.accountReceivableWriteoffId = accountReceivableWriteoff.getId();
        this.writeOffAmount = accountReceivableWriteoff.getWriteOffAmount();
        this.writeOffDate = accountReceivableWriteoff.getWriteOffDate();
        this.cancelDate = accountReceivableWriteoff.getCancelDate();
        this.canceler = accountReceivableWriteoff.getCanceler() != null ? accountReceivableWriteoff.getCanceler().getRealname() : "";
        this.accountReceivableWriteoffStatus = AccountReceivableWriteOffStatus.fromInt(accountReceivableWriteoff.getStatus()).getName();
    }

}
