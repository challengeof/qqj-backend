package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.wrapper.SimpleOrderWrapper;
import com.mishu.cgwy.stock.domain.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
public class SellReturnWrapper {

    private Long id;

    private SellReturnType type;

    private SellReturnStatus status;

    private Date createDate;

    private String creator;

    private String auditor;

    private Date auditDate;

    private String auditOpinion;

    private BigDecimal amount;

    private DepotWrapper depot;

    private SimpleOrderWrapper order;

    private List<SellReturnItemWrapper> sellReturnItems = new ArrayList<>();

    public SellReturnWrapper(){}

    public SellReturnWrapper(SellReturn sellReturn) {
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
        if (sellReturn.getDepot() != null) {
            this.depot = new DepotWrapper(sellReturn.getDepot());
        }
        this.order = new SimpleOrderWrapper(sellReturn.getOrder());
        for (SellReturnItem sellReturnItem : sellReturn.getSellReturnItems()) {
            sellReturnItems.add(new SellReturnItemWrapper(sellReturnItem));
        }
    }
}
