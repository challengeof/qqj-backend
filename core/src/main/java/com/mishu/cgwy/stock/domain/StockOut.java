package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.purchase.domain.ReturnNote;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:23 PM
 */
@Entity
@Data
public class StockOut {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "depot_id")
    private Depot depot;

    private int status;

    private int type;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;

    @ManyToOne
    @JoinColumn(name = "transfer_id")
    private Transfer transfer;

    @ManyToOne
    @JoinColumn(name = "return_note_id")
    private ReturnNote returnNote;

    private Date createDate;

    private Date finishDate;

    @ManyToOne
    @JoinColumn(name = "order_group_id")
    private OrderGroup orderGroup;

    private Date receiveDate;

    @ManyToOne
    @JoinColumn(name = "sender_id")
    private AdminUser sender;

    @ManyToOne
    @JoinColumn(name = "receiver_id")
    private AdminUser receiver;

    @Column(precision = 16, scale = 2)
    private BigDecimal amount;

    @Column(precision = 16, scale = 2)
    private BigDecimal receiveAmount;

    private boolean settle = false;

    private Date settleDate;

    private boolean pickPrint = false;

    private boolean outPrint = false;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "stock_out_id")
    private List<StockOutItem> stockOutItems = new ArrayList<>();

    @Override
    public String toString() {
        return "StockOut{" +
                "id=" + id +
                ", type=" + type +
                ", createDate=" + createDate +
                ", finishDate=" + finishDate +
                ", receiveDate=" + receiveDate +
                ", receiver=" + receiver +
                ", amount=" + amount +
                ", receiveAmount=" + receiveAmount +
                ", settleDate=" + settleDate +
                '}';
    }
}
