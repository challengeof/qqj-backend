package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Restaurant.class)
public abstract class Restaurant_ {

	public static volatile SingularAttribute<Restaurant, Integer> stockRate;
	public static volatile SingularAttribute<Restaurant, String> telephone3;
	public static volatile SingularAttribute<Restaurant, String> telephone2;
	public static volatile SingularAttribute<Restaurant, RestaurantType> type;
	public static volatile SingularAttribute<Restaurant, String> concern;
	public static volatile SingularAttribute<Restaurant, String> receiver2;
	public static volatile SingularAttribute<Restaurant, String> receiver3;
	public static volatile SingularAttribute<Restaurant, String> opponent;
	public static volatile SingularAttribute<Restaurant, Boolean> warning;
	public static volatile SingularAttribute<Restaurant, Long> id;
	public static volatile SingularAttribute<Restaurant, Integer> cooperatingState;
	public static volatile SingularAttribute<Restaurant, Integer> restaurantReason;
	public static volatile SingularAttribute<Restaurant, String> specialReq;
	public static volatile SingularAttribute<Restaurant, AdminUser> statusLastOperater;
	public static volatile SingularAttribute<Restaurant, Address> address;
	public static volatile SingularAttribute<Restaurant, String> receiver;
	public static volatile ListAttribute<Restaurant, RestaurantAuditReview> auditReviews;
	public static volatile SingularAttribute<Restaurant, Date> lastPurchaseTime;
	public static volatile SingularAttribute<Restaurant, String> telephone;
	public static volatile SingularAttribute<Restaurant, Date> statusLastOperateTime;
	public static volatile SingularAttribute<Restaurant, AdminUser> createOperater;
	public static volatile SingularAttribute<Restaurant, Integer> auditShowStatus;
	public static volatile SingularAttribute<Restaurant, Date> lastOperateTime;
	public static volatile SingularAttribute<Restaurant, String> license;
	public static volatile SingularAttribute<Restaurant, Integer> activeType;
	public static volatile SingularAttribute<Restaurant, Date> auditTime;
	public static volatile SingularAttribute<Restaurant, Date> createTime;
	public static volatile SingularAttribute<Restaurant, Short> grade;
	public static volatile SingularAttribute<Restaurant, String> name;
	public static volatile SingularAttribute<Restaurant, Boolean> openWarning;
	public static volatile ListAttribute<Restaurant, Order> orders;
	public static volatile SingularAttribute<Restaurant, AdminUser> lastOperater;
	public static volatile SingularAttribute<Restaurant, Integer> status;
	public static volatile SingularAttribute<Restaurant, Customer> customer;

}

