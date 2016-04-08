package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.stock.domain.SellCancel;
import com.mishu.cgwy.stock.domain.SellReturn;
import com.mishu.cgwy.stock.domain.StockOut;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Order.class)
public abstract class Order_ {

	public static volatile SingularAttribute<Order, Integer> reason;
	public static volatile SingularAttribute<Order, Date> cancelDate;
	public static volatile SingularAttribute<Order, Boolean> hasEvaluated;
	public static volatile SingularAttribute<Order, Date> submitDate;
	public static volatile ListAttribute<Order, StockOut> stockOuts;
	public static volatile SingularAttribute<Order, String> memo;
	public static volatile SingularAttribute<Order, BigDecimal> subTotal;
	public static volatile SingularAttribute<Order, Long> type;
	public static volatile ListAttribute<Order, OrderItem> orderItems;
	public static volatile SingularAttribute<Order, String> deviceId;
	public static volatile SingularAttribute<Order, Date> completeDate;
	public static volatile ListAttribute<Order, Refund> refunds;
	public static volatile ListAttribute<Order, SellCancel> sellCancels;
	public static volatile SingularAttribute<Order, BigDecimal> total;
	public static volatile SingularAttribute<Order, BigDecimal> shipping;
	public static volatile ListAttribute<Order, SellReturn> sellReturns;
	public static volatile SetAttribute<Order, CustomerCoupon> customerCoupons;
	public static volatile SingularAttribute<Order, Long> id;
	public static volatile SingularAttribute<Order, CutOrder> cutOrder;
	public static volatile SingularAttribute<Order, AdminUser> adminOperator;
	public static volatile SingularAttribute<Order, Restaurant> restaurant;
	public static volatile SingularAttribute<Order, String> cancelDeviceId;
	public static volatile SingularAttribute<Order, BigDecimal> realTotal;
	public static volatile SingularAttribute<Order, Long> version;
	public static volatile SingularAttribute<Order, Customer> customerOperator;
	public static volatile SingularAttribute<Order, Long> sequence;
	public static volatile SetAttribute<Order, Promotion> promotions;
	public static volatile SingularAttribute<Order, AdminUser> adminUser;
	public static volatile SingularAttribute<Order, Organization> organization;
	public static volatile SingularAttribute<Order, Customer> customer;
	public static volatile SingularAttribute<Order, Integer> status;
	public static volatile SingularAttribute<Order, Date> expectedArrivedDate;

}

