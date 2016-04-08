package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.purchase.domain.ReturnNote;
import com.mishu.cgwy.purchase.domain.ReturnNoteItem;
import com.mishu.cgwy.purchase.domain.ReturnNoteStatus;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class ReturnNoteVo {

    private Long id;

    private DepotWrapper depot;

    private String remark;

    private String creator;

    private Date createTime;

    private String auditor;

    private Date auditTime;

    private String opinion;

    private String vendor;

    private PurchaseOrderType type;

    private ReturnNoteStatus status;

    private List<ReturnNoteItemVo> returnNoteItems;

    private BigDecimal purchaseTotal = BigDecimal.ZERO;

    private BigDecimal returnTotal = BigDecimal.ZERO;

    private PurchaseOrderVo purchaseOrder;

//    public ReturnNoteVo(ReturnNote returnNote) {
//        this.id = returnNote.getId();
//        this.depot = new DepotWrapper(returnNote.getDepot());
//        this.remark = returnNote.getRemark();
//        this.creator = returnNote.getCreator() == null ? null : returnNote.getCreator().getRealname();
//        this.createTime = returnNote.getCreateTime();
//        this.auditor = returnNote.getAuditor() == null ? null : returnNote.getAuditor().getRealname();
//        this.auditTime = returnNote.getAuditTime();
//        this.opinion = returnNote.getOpinion();
//        this.vendor = returnNote.getPurchaseOrder().getVendor().getName();
//        this.type = PurchaseOrderType.fromInt(returnNote.getType());
//        this.status = ReturnNoteStatus.get(returnNote.getStatus());
//        this.purchaseOrder = new PurchaseOrderWrapper(returnNote.getPurchaseOrder());
//        returnNoteItems = new ArrayList<>();
//        for (ReturnNoteItem returnNoteItem : returnNote.getReturnNoteItems()) {
//            returnNoteItems.add(new ReturnNoteItemWrapper(returnNoteItem));
//            purchaseTotal = purchaseTotal.add(returnNoteItem.getPurchaseOrderItem().getPrice().multiply(new BigDecimal(returnNoteItem.getReturnQuantity())));
//            returnTotal = returnTotal.add(returnNoteItem.getReturnPrice().multiply(new BigDecimal(returnNoteItem.getReturnQuantity())));
//        }
//    }
//
//    public ReturnNoteVo() {
//
//    }
}
