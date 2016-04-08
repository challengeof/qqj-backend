package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 3/14/15
 * Time: 10:41 AM
 */
@Entity
@Table(name = "cgwy_order_group")
@Data
public class OrderGroup {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany
    @JoinTable(
            name="order_group_member_xref",
            joinColumns = @JoinColumn( name="order_group_id"),
            inverseJoinColumns = @JoinColumn( name="order_id")
    )
    private List<Order> members = new ArrayList<>();

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "order_group_id")
    private List<StockOut> stockOuts = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "tracker_id")
    private AdminUser tracker;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    @ManyToOne
    @JoinColumn(name = "city_id")
    private City city;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private Date createDate;

    private boolean checkResult = Boolean.FALSE;
}
