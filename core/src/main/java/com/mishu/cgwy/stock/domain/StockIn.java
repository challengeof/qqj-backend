package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class StockIn {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private int status;

    private int type;

    private Date createDate;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    private boolean outPrint = false;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "stock_in_id")
    private List<StockInItem> stockInItems = new ArrayList<>();

    private Date receiveDate;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private AdminUser receiver;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;

    @ManyToOne
    @JoinColumn(name = "sell_return_id")
    private SellReturn sellReturn;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;

    public StockIn clone () {
        StockIn stockIn = new StockIn();
        stockIn.setDepot(this.depot);
        stockIn.setStatus(this.status);
        stockIn.setType(this.type);
        stockIn.setCreateDate(this.createDate);
        stockIn.setCreator(this.creator);
        stockIn.setOutPrint(false);
        stockIn.setReceiveDate(this.receiveDate);
        stockIn.setAmount(this.amount);
        stockIn.setReceiver(this.receiver);
        stockIn.setPurchaseOrder(this.purchaseOrder);
        stockIn.setSellReturn(this.sellReturn);
        stockIn.setTransfer(this.transfer);
        return stockIn;
    }

}
