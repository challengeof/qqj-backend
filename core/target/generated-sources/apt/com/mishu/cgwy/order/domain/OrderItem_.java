package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OrderItem.class)
public abstract class OrderItem_ {

	public static volatile SingularAttribute<OrderItem, SpikeItem> spikeItem;
	public static volatile SingularAttribute<OrderItem, Integer> singleQuantity;
	public static volatile SingularAttribute<OrderItem, BigDecimal> totalPrice;
	public static volatile SingularAttribute<OrderItem, Integer> sellReturnQuantity;
	public static volatile SingularAttribute<OrderItem, Integer> sellCancelQuantity;
	public static volatile SingularAttribute<OrderItem, Integer> countQuantity;
	public static volatile SingularAttribute<OrderItem, BigDecimal> price;
	public static volatile SingularAttribute<OrderItem, Long> id;
	public static volatile SingularAttribute<OrderItem, Sku> sku;
	public static volatile SingularAttribute<OrderItem, BigDecimal> avgCost;
	public static volatile SingularAttribute<OrderItem, Boolean> bundle;
	public static volatile SingularAttribute<OrderItem, Order> order;
	public static volatile SingularAttribute<OrderItem, Integer> bundleQuantity;

}

