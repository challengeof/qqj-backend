package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SellCancel.class)
public abstract class SellCancel_ {

	public static volatile SingularAttribute<SellCancel, AdminUser> creator;
	public static volatile SingularAttribute<SellCancel, BigDecimal> amount;
	public static volatile ListAttribute<SellCancel, SellCancelItem> sellCancelItems;
	public static volatile SingularAttribute<SellCancel, Long> id;
	public static volatile SingularAttribute<SellCancel, Integer> type;
	public static volatile SingularAttribute<SellCancel, Date> createDate;
	public static volatile SingularAttribute<SellCancel, Customer> customer;
	public static volatile SingularAttribute<SellCancel, Order> order;

}

