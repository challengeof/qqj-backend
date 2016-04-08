package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.stock.domain.SellCancel;
import com.mishu.cgwy.stock.domain.SellReturn;
import com.mishu.cgwy.stock.domain.SellReturnStatus;
import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 2/27/15
 * Time: 7:45 PM
 */
@Entity
@Table(name = "cgwy_order", indexes = {@Index(name = "ORDER_STATUS_INDEX", columnList = "status", unique = false),
        @Index(name = "ORDER_SUBMITDATE_STATUS_INDEX", columnList = "submitDate, status", unique = false)})
@Data
public class Order {
    private static Logger logger = LoggerFactory.getLogger(Order.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * @return the total item price with offers applied
     */
    private BigDecimal total = BigDecimal.ZERO;

    /**
     * Returns the subtotal price for the order.  The subtotal price is the price of all order items
     * with item offers applied.  The subtotal does not take into account the order promotions, shipping costs or any
     * taxes that apply to this order.
     *
     * @return the total item price
     */
    private BigDecimal subTotal = BigDecimal.ZERO;

    /**
     * 该字段表示实收金额，20151122上线前后赋值途径不同
     * @return the stockOut Amount
     */
    private BigDecimal realTotal = BigDecimal.ZERO;

    private BigDecimal shipping = BigDecimal.ZERO;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    private int status;

    private Date submitDate;

    private Date completeDate;

    private Date cancelDate;

    @Temporal(TemporalType.DATE)
    private Date expectedArrivedDate;

    @OneToMany(cascade = {CascadeType.ALL}, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderItem> orderItems = new ArrayList<OrderItem>();

    private String memo = "";

    @ManyToOne
    @JoinColumn(name = "restaurant_id")
    private Restaurant restaurant;

    @ManyToOne
    @JoinColumn(name = "admin_user_id")
    private AdminUser adminUser;

    @Deprecated
    @OneToMany(mappedBy = "order")
    private List<Refund> refunds = new ArrayList<Refund>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<SellReturn> sellReturns = new ArrayList<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<SellCancel> sellCancels = new ArrayList<>();


    @ManyToMany(fetch = FetchType.LAZY, targetEntity = Promotion.class)
    @JoinTable(name = "order_promotion_xref", joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "promotion_id"))
    private Set<Promotion> promotions = new HashSet<Promotion>();

    @ManyToMany(fetch = FetchType.LAZY, targetEntity = CustomerCoupon.class)
    @JoinTable(name = "order_customercoupon_xref", joinColumns = @JoinColumn(name = "order_id"),
            inverseJoinColumns = @JoinColumn(name = "customercoupon_id"))
    private Set<CustomerCoupon> customerCoupons = new HashSet<CustomerCoupon>();
    
    private Long sequence;

    @ManyToOne
    @JoinColumn(name = "organization_id")
    private Organization organization;

    //是否已经评价
    private boolean hasEvaluated;

    private Integer reason;

    //下单设备id
    private String deviceId;

    //取消设备id
    private String cancelDeviceId;

    @ManyToOne
    @JoinColumn(name = "cut_order_id")
    private CutOrder cutOrder;

    @Version
    private long version;

    private Long type= OrderType.NOMAL.getVal();

    @ManyToOne
    @JoinColumn(name = "admin_operator_id")
    private AdminUser adminOperator;

    @ManyToOne
    @JoinColumn(name = "customer_operator_id")
    private Customer customerOperator;


    @OneToMany(fetch = FetchType.LAZY, mappedBy = "order")
    private List<StockOut> stockOuts;

    public void calculateSubTotal() {
        subTotal = BigDecimal.ZERO;
        for (OrderItem orderItem : orderItems) {
            try {
                if (orderItem.isBundle()) {
                    orderItem.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getBundleQuantity())));
                } else {
                    orderItem.setTotalPrice(orderItem.getPrice().multiply(BigDecimal.valueOf(orderItem.getSingleQuantity())));
                }
            } catch (Exception e) {
                logger.error("calculateSubTotal orderItem:" + orderItem, e);
                throw e;
            }
            subTotal = subTotal.add(orderItem.getTotalPrice());
        }
    }

    public void calculateTotal() {
        BigDecimal discount = BigDecimal.ZERO;
        for (Promotion promotion : promotions) {
            discount = discount.add(promotion.getDiscount());
        }
        for (CustomerCoupon customerCoupon : customerCoupons) {
            discount = discount.add(customerCoupon.getCoupon().getDiscount());
        }

        final BigDecimal subtract = subTotal.add(shipping).subtract(discount);
        total = subtract.compareTo(BigDecimal.ZERO) > 0 ? subtract : BigDecimal.ZERO;
    }

    public void calculateRealTotal() {
        realTotal = total;
        for (SellCancel sellCancel : sellCancels) {
            realTotal = realTotal.subtract(sellCancel.getAmount());
        }
        for (SellReturn sellReturn : sellReturns) {
            if (sellReturn.getStatus() == SellReturnStatus.COMPLETED.getValue()) {
                realTotal.subtract(sellReturn.getAmount());
            }
        }
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", total=" + total +
                ", subTotal=" + subTotal +
                ", shipping=" + shipping +
                ", customer=" + customer +
                ", status=" + status +
                ", submitDate=" + submitDate +
                ", expectedArrivedDate=" + expectedArrivedDate +
                ", restaurant=" + restaurant + '\'' +
                '}';
    }
}
