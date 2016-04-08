package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.CutOrder;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 9/15/15
 * Time: 3:13 PM
 */
@Entity
@Data
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot targetDepot;

    @ManyToOne
    @JoinColumn(name = "source_depot_id")
    private Depot sourceDepot;

    @ManyToOne
    @JoinColumn(name = "creator_id")
    private AdminUser creator;

    @ManyToOne
    @JoinColumn(name = "auditor_id")
    private AdminUser auditor;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createDate;

    @Temporal(TemporalType.TIMESTAMP)
    private Date auditDate;

    private Short status;

    private String remark;

    private String opinion;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "transfer_id")
    private List<TransferItem> transferItems = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "cut_order_id")
    private CutOrder cutOrder;

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", createDate=" + createDate +
                ", auditDate=" + auditDate +
                ", status=" + status +
                ", remark='" + remark + '\'' +
                '}';
    }
}
