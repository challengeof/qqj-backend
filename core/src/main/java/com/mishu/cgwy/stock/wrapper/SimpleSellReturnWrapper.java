package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.SellReturn;
import com.mishu.cgwy.stock.domain.SellReturnStatus;
import com.mishu.cgwy.stock.domain.SellReturnType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
public class SimpleSellReturnWrapper {

    private Long id;

    private SellReturnType type;

    private SellReturnStatus status;

    private Date createDate;

    private String creator;

    private String auditor;

    private Date auditDate;

    private String auditOpinion;

    private BigDecimal amount;
    private Date returnDate;

    private DepotWrapper depot;

    private Long orderId;
    private String restaurantName;
    private String receiver;
    private String telephone;


    public SimpleSellReturnWrapper() {
    }

    public SimpleSellReturnWrapper(SellReturn sellReturn) {
        this.id = sellReturn.getId();
        this.type = SellReturnType.fromInt(sellReturn.getType());
        this.status = SellReturnStatus.fromInt(sellReturn.getStatus());
        this.createDate = sellReturn.getCreateDate();
        if (sellReturn.getCreator() != null) {
            this.creator = sellReturn.getCreator().getRealname();
        }
        this.auditDate = sellReturn.getAuditDate();
        if (sellReturn.getAuditor() != null) {
            this.auditor = sellReturn.getAuditor().getRealname();
        }
        this.auditOpinion = sellReturn.getAuditOpinion();
        this.amount = sellReturn.getAmount();
        this.returnDate = sellReturn.getCreateDate();
        if (sellReturn.getDepot() != null) {
            this.depot = new DepotWrapper(sellReturn.getDepot());
        }
        this.orderId = sellReturn.getOrder().getId();
        this.restaurantName = sellReturn.getOrder().getRestaurant().getName();
        this.receiver = sellReturn.getOrder().getRestaurant().getReceiver();
        this.telephone = sellReturn.getOrder().getRestaurant().getTelephone();
    }
}
