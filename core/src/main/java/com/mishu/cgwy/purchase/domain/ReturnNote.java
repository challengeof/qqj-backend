package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.stock.domain.Depot;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangguodong on 15/10/10.
 */
@Entity
@Getter
@Setter
public class ReturnNote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Short type;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private String remark;

    @ManyToOne
    private AdminUser creator;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne
    private AdminUser auditor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auditTime;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "return_note_id")
    private List<ReturnNoteItem> returnNoteItems = new ArrayList<ReturnNoteItem>();

    private Short status;

    private String opinion;

    @ManyToOne
    @JoinColumn(name = "purchase_order_id")
    private PurchaseOrder purchaseOrder;
}
