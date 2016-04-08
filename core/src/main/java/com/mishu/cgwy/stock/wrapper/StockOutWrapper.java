package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.domain.StockOutType;
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
public class StockOutWrapper {

    private Long stockOutId;
    private String stockOutType;
    private int stockOutTypeCode;
    private String stockOutStatus;
    private Long cityId;
    private String cityName;
    private String depotName;
    private Date createDate;
    private Date finishDate;
    private String senderName;
    private Date expectedArrivedDate;
    private Long sourceId;
    private String outPrint;

    private String pickPrint;
    private Long orderId;
    private String orderType;
    private String warehouseName;
    private String blockName;
    private String trackerName;
    private String orderSubmitName;
    private Date orderSubmitDate;
    private Date receiveDate;
    private Date settleDate;
    private boolean settle;
    private BigDecimal amount;
    private BigDecimal receiveAmount;//应收金额
    private BigDecimal receivedAmount;//收款金额
    private String customerName;
    private String receiverName;

    private Long returnId;
    private Long purchaseId;
    private Long vendorId;
    private String vendorName;
    private BigDecimal purchaseAmount;
    private BigDecimal returnAmount;
    private String returnCreatorName;
    private String returnAuditorName;
    private Date returnCreateDate;
    private Date returnAuditDate;

    private Long transferId;
    private String sourceDepotName;
    private String targetDepotName;
    private String transferCreatorName;
    private String transferAuditorName;
    private Date transferCreateDate;
    private Date transferAuditDate;

    private List<StockOutItemWrapper> stockOutItems = new ArrayList<>();

    public StockOutWrapper() {
    }

    public StockOutWrapper(StockOut stockOut) {

        stockOutId = stockOut.getId();
        stockOutType = StockOutType.fromInt(stockOut.getType()).getName();
        stockOutTypeCode = stockOut.getType();
        stockOutStatus = StockOutStatus.fromInt(stockOut.getStatus()).getName();
        createDate = stockOut.getCreateDate();
        finishDate = stockOut.getFinishDate();
        senderName = stockOut.getSender() == null ? "" : stockOut.getSender().getRealname();
        receiveDate = stockOut.getReceiveDate();
        settleDate = stockOut.getSettleDate();
        settle = stockOut.isSettle();
        cityId = stockOut.getDepot().getCity().getId();
        cityName = stockOut.getDepot().getCity().getName();
        depotName = stockOut.getDepot().getName();
        receiverName = stockOut.getReceiver() == null ? "" : stockOut.getReceiver().getRealname();
        amount = stockOut.getAmount();
        receiveAmount = stockOut.getReceiveAmount();
        receivedAmount = stockOut.isSettle() ? stockOut.getReceiveAmount() : BigDecimal.ZERO;
        outPrint = StockPrintStatus.fromBoolean(stockOut.isOutPrint()).getName();
        pickPrint = StockPrintStatus.fromBoolean(stockOut.isPickPrint()).getName();

        if (stockOut.getOrder() != null) {
            orderId = stockOut.getOrder().getId();
            orderType = OrderType.find(stockOut.getOrder().getType(), OrderType.NOMAL).getDesc();
            warehouseName = stockOut.getOrder().getCustomer().getBlock() != null && stockOut.getOrder().getCustomer().getBlock().getWarehouse() != null ? stockOut.getOrder().getCustomer().getBlock().getWarehouse().getName() : null;
            blockName = stockOut.getOrder().getCustomer().getBlock() != null ? stockOut.getOrder().getCustomer().getBlock().getName() : null;
            trackerName = stockOut.getOrderGroup() != null && stockOut.getOrderGroup().getTracker() != null ? stockOut.getOrderGroup().getTracker().getRealname() : null;
            customerName = stockOut.getOrder().getRestaurant().getName();
            sourceId = orderId;
            expectedArrivedDate = stockOut.getOrder().getExpectedArrivedDate();
            orderSubmitDate = stockOut.getOrder().getSubmitDate();
            if (stockOut.getOrder().getAdminOperator() != null) {
                orderSubmitName = stockOut.getOrder().getAdminOperator().getRealname();
            }
            if (stockOut.getOrder().getCustomerOperator() != null) {
                orderSubmitName = stockOut.getOrder().getCustomerOperator().getUsername();
            }
        }
        if (stockOut.getReturnNote() != null) {
            returnId = stockOut.getReturnNote().getId();
            purchaseId = stockOut.getReturnNote().getPurchaseOrder().getId();
            vendorId = stockOut.getReturnNote().getPurchaseOrder().getVendor().getId();
            vendorName = stockOut.getReturnNote().getPurchaseOrder().getVendor().getName();
            purchaseAmount = stockOut.getReturnNote().getPurchaseOrder().getTotal();
//            for (ReturnNoteItem item : stockOut.getReturnNote().getReturnNoteItems()) {
//                purchaseAmount = purchaseAmount.add(item.getPurchaseOrderItem().getPrice().multiply(new BigDecimal(item.getReturnQuantity())));
//            }
            returnAmount = stockOut.getAmount();
            returnCreatorName = stockOut.getReturnNote().getCreator().getRealname();
            returnAuditorName = stockOut.getReturnNote().getAuditor().getRealname();
            returnCreateDate = stockOut.getReturnNote().getCreateTime();
            returnAuditDate = stockOut.getReturnNote().getAuditTime();
            sourceId = returnId;
        }
        if (stockOut.getTransfer() != null) {
            transferId = stockOut.getTransfer().getId();
            sourceDepotName = stockOut.getDepot().getName();
            targetDepotName = stockOut.getTransfer().getTargetDepot().getName();
            transferCreatorName = stockOut.getTransfer().getCreator().getRealname();
            transferAuditorName = stockOut.getTransfer().getAuditor() == null ? "" : stockOut.getTransfer().getAuditor().getRealname();
            transferCreateDate = stockOut.getTransfer().getCreateDate();
            transferCreateDate = stockOut.getTransfer().getAuditDate();
            sourceId = transferId;
        }
    }
}

