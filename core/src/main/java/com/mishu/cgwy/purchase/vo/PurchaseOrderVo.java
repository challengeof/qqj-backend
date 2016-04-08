package com.mishu.cgwy.purchase.vo;

import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.purchase.domain.PurchaseOrderPrint;
import com.mishu.cgwy.purchase.domain.PurchaseOrderStatus;
import com.mishu.cgwy.purchase.domain.PurchaseOrderType;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
public class PurchaseOrderVo {

    private Long id;

    private Long cityId;

    private Long organizationId;

    private VendorVo vendor;

    private VendorVo paymentVendor;

    private PurchaseOrderStatus status;

    private PurchaseOrderPrint print;

    private BigDecimal total;

    private DepotWrapper depot;

    private String remark;

    private String creater;

    private Date createTime;

    private String auditor;

    private Date auditTime;

    private String receiver;

    private Date receiveTime;

    private Date expectedArrivedDate;

    private String opinion;

    private List<PurchaseOrderItemVo> purchaseOrderItems = new ArrayList<>();

    private PurchaseOrderType purchaseOrderType;

//    public PurchaseOrderVo(PurchaseOrder purchaseOrder) {
//        this.id = purchaseOrder.getId();
//        this.cityId = purchaseOrder.getDepot().getCity().getId();
//        this.organizationId = purchaseOrder.getVendor().getOrganization().getId();
//        this.status = PurchaseOrderStatus.get(purchaseOrder.getStatus());
//        this.print = PurchaseOrderPrint.get(purchaseOrder.getPrint());
//        this.total = purchaseOrder.getTotal();
//        this.depot = new DepotWrapper(purchaseOrder.getDepot());
//        this.remark = purchaseOrder.getRemark();
//        this.creater = purchaseOrder.getCreater() == null ? null : purchaseOrder.getCreater().getRealname();
//        this.createTime = purchaseOrder.getCreateTime();
//        this.auditor = purchaseOrder.getAuditor() == null ? null : purchaseOrder.getAuditor().getRealname();
//        this.auditTime = purchaseOrder.getAuditTime();
//        this.receiver = purchaseOrder.getReceiver() == null ? null : purchaseOrder.getReceiver().getRealname();
//        this.receiveTime = purchaseOrder.getReceiveTime();
//        this.expectedArrivedDate = purchaseOrder.getExpectedArrivedDate();
//        this.opinion = purchaseOrder.getOpinion();
//        this.purchaseOrderType = PurchaseOrderType.fromInt(purchaseOrder.getType());
//
//        Vendor purchaseVendor = purchaseOrder.getVendor();
//        vendor = new VendorVo();
//        vendor.setId(purchaseVendor.getId());
//        vendor.setName(purchaseVendor.getName());
//    }
}
