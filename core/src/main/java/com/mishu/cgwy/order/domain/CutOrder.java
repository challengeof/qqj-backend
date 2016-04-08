package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Transfer;
import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/22.
 */
@Entity
@Data
public class CutOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Date cutDate;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser operator;

    private Date submitDate;

    @ManyToOne
    @JoinColumn(name = "submit_user_id")
    private AdminUser submitUser;

    private Short status;

    @Version
    private long version;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "cut_order_id")
    private List<Order> orders = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "cut_order_id")
    private List<Transfer> transfers = new ArrayList<>();

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "cut_order_id")
    private List<PurchaseOrder> purchaseOrders = new ArrayList<>();

    @Override
    public String toString() {
        return "CutOrder{" +
                "id=" + id +
                ", cutDate=" + cutDate +
                '}';
    }
}
