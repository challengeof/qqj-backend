package com.mishu.cgwy.purchase.vo;

import lombok.Data;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderDetailVo {

    private Long purchaseOrderId;
    private String purchaseOrderStatus;
    private String purchaseOrderType;
    private Long vendorId;
    private String vendorName;
    private String creatorName;
    private Date createTime;
    private String auditorName;
    private Date auditTime;
    private String receiverName;
    private Date receiveTime;

    private List<PurchaseOrderItemVo> purchaseOrderItems = new ArrayList<>();
    private List<ReturnNoteVo> returnNotes = new ArrayList<>();

}
