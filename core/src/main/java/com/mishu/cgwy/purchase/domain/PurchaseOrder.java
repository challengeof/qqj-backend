package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
@Entity
@Data
public class PurchaseOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short type;

    @ManyToOne
    @JoinColumn(name = "vendor_id")
    private Vendor vendor;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private Date expectedArrivedDate;

    private String remark;

    @ManyToOne
    private AdminUser creater;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne
    private AdminUser canceler;

    @Temporal(TemporalType.TIMESTAMP)
    private Date cancelTime;

    @ManyToOne
    private AdminUser auditor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auditTime;

    @ManyToOne
    private AdminUser receiver;

    @Temporal(TemporalType.TIMESTAMP)
    private Date receiveTime;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "purchase_order_id")
    private List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<PurchaseOrderItem>();

    private Short status;

    @Column(precision = 16, scale = 6)
    private BigDecimal total;

    private String opinion;

    @ManyToOne
    @JoinColumn(name = "cut_order_id")
    private CutOrder cutOrder;

    private Boolean print = Boolean.FALSE;

    @Override
    public String toString() {
        return "PurchaseOrder{" +
                "id=" + id +
                ", type=" + type +
                ", expectedArrivedDate=" + expectedArrivedDate +
                ", remark='" + remark + '\'' +
                ", createTime=" + createTime +
                ", auditTime=" + auditTime +
                ", receiveTime=" + receiveTime +
                ", status=" + status +
                ", total=" + total +
                ", opinion='" + opinion + '\'' +
                '}';
    }
}
