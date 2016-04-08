package com.mishu.cgwy.score.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by bowen on 15/11/10.
 */
@Entity
@Data
public class ScoreLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "stockOut_id")
    private StockOut stockOut;  //收单

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private Long integral = 0L;

    @ManyToOne
    @JoinColumn(name = "score_id")
    private Score score;

    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private Coupon coupon;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createTime;

    @ManyToOne
    @JoinColumn(name = "sender")
    private AdminUser sender;

    private String remark;

    private int status;

    private int count;


}
