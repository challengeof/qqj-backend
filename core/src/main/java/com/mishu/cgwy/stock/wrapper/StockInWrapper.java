package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockInStatus;
import com.mishu.cgwy.stock.domain.StockInType;
import com.mishu.cgwy.stock.domain.StockPrintStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class StockInWrapper {

    private String cityName;
    private Long stockInId;
    private String stockInType;
    private String stockInStatus;
    private Date createDate;
    private String creator;
    private Date receiveDate;
    private String receiverName;
    private String depotName;
    private BigDecimal amount;
    private Long sourceId;
    private String outPrint;

    private Long purchaseOrderId;
    private String purchaseOrderType;
    private Date expectedArrivedDate;
    private String vendorName;
    private String paymentVendorName;

    private Long sellReturnId;
    private Long orderId;
    private String customerName;

    private Long transferId;
    private String sourceDepotName;
    private String targetDepotName;

    private List<StockInItemWrapper> stockInItems = new ArrayList<>();

    public StockInWrapper(StockIn stockIn) {

        this.stockInId = stockIn.getId();
        this.stockInType = StockInType.fromInt(stockIn.getType()).getName();
        this.stockInStatus = StockInStatus.fromInt(stockIn.getStatus()).getName();
        this.createDate = stockIn.getCreateDate();
        this.creator = stockIn.getCreator() == null ? "" : stockIn.getCreator().getRealname();
        this.receiveDate = stockIn.getReceiveDate();
        this.receiverName = stockIn.getReceiver() == null ? "" : stockIn.getReceiver().getRealname();
        this.depotName = stockIn.getDepot() == null ? "" : stockIn.getDepot().getName();
        this.cityName = stockIn.getDepot() == null ? "" : stockIn.getDepot().getCity().getName();
        this.amount = stockIn.getAmount();
        this.outPrint = StockPrintStatus.fromBoolean(stockIn.isOutPrint()).getName();

        if (stockIn.getPurchaseOrder() != null) {
            this.purchaseOrderId = stockIn.getPurchaseOrder().getId();
            this.purchaseOrderType = PurchaseOrderType.fromInt(stockIn.getPurchaseOrder().getType()).getName();
            this.vendorName = stockIn.getPurchaseOrder().getVendor().getName();
            this.paymentVendorName = stockIn.getPurchaseOrder().getVendor().getPaymentVendor() != null ? stockIn.getPurchaseOrder().getVendor().getPaymentVendor().getName() : this.vendorName;
            this.expectedArrivedDate = stockIn.getPurchaseOrder().getExpectedArrivedDate();
            this.sourceId = this.purchaseOrderId;
        } else if (stockIn.getSellReturn() != null) {
            this.sellReturnId = stockIn.getSellReturn().getId();
            this.orderId = stockIn.getSellReturn().getOrder().getId();
            this.customerName = stockIn.getSellReturn().getOrder().getRestaurant().getName();
            this.sourceId = this.sellReturnId;
        } else if (stockIn.getTransfer() != null) {
            this.transferId = stockIn.getTransfer().getId();
            this.sourceDepotName = stockIn.getTransfer().getSourceDepot().getName();
            this.targetDepotName = stockIn.getDepot().getName();
            this.sourceId = this.transferId;
        }
    }
}

