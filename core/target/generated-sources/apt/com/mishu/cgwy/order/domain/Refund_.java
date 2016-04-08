package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Refund.class)
public abstract class Refund_ {

	public static volatile SingularAttribute<Refund, Integer> countQuantity;
	public static volatile SingularAttribute<Refund, Integer> reason;
	public static volatile SingularAttribute<Refund, Integer> singleQuantity;
	public static volatile SingularAttribute<Refund, BigDecimal> totalPrice;
	public static volatile SingularAttribute<Refund, BigDecimal> price;
	public static volatile SingularAttribute<Refund, Date> submitDate;
	public static volatile SingularAttribute<Refund, Long> id;
	public static volatile SingularAttribute<Refund, Sku> sku;
	public static volatile SingularAttribute<Refund, Integer> type;
	public static volatile SingularAttribute<Refund, Boolean> bundle;
	public static volatile SingularAttribute<Refund, Order> order;
	public static volatile SingularAttribute<Refund, Integer> bundleQuantity;

}

