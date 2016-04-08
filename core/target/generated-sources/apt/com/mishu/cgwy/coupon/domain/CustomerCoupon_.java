package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CustomerCoupon.class)
public abstract class CustomerCoupon_ {

	public static volatile SingularAttribute<CustomerCoupon, Short> reason;
	public static volatile SingularAttribute<CustomerCoupon, Coupon> coupon;
	public static volatile SingularAttribute<CustomerCoupon, AdminUser> operater;
	public static volatile SingularAttribute<CustomerCoupon, Date> sendDate;
	public static volatile SingularAttribute<CustomerCoupon, Date> operateTime;
	public static volatile SingularAttribute<CustomerCoupon, Date> start;
	public static volatile SingularAttribute<CustomerCoupon, String> remark;
	public static volatile SingularAttribute<CustomerCoupon, Date> useDate;
	public static volatile SingularAttribute<CustomerCoupon, AdminUser> sender;
	public static volatile SetAttribute<CustomerCoupon, Order> orders;
	public static volatile SingularAttribute<CustomerCoupon, Date> end;
	public static volatile SingularAttribute<CustomerCoupon, Long> id;
	public static volatile SingularAttribute<CustomerCoupon, Integer> status;
	public static volatile SingularAttribute<CustomerCoupon, Customer> customer;

}

