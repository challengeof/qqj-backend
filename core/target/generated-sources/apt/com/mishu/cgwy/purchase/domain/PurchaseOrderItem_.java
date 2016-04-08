package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PurchaseOrderItem.class)
public abstract class PurchaseOrderItem_ {

	public static volatile SingularAttribute<PurchaseOrderItem, Integer> returnQuantity;
	public static volatile SingularAttribute<PurchaseOrderItem, BigDecimal> rate;
	public static volatile SingularAttribute<PurchaseOrderItem, Integer> needQuantity;
	public static volatile SingularAttribute<PurchaseOrderItem, BigDecimal> price;
	public static volatile SingularAttribute<PurchaseOrderItem, Integer> purchaseQuantity;
	public static volatile SingularAttribute<PurchaseOrderItem, PurchaseOrder> purchaseOrder;
	public static volatile SingularAttribute<PurchaseOrderItem, Long> id;
	public static volatile SingularAttribute<PurchaseOrderItem, Sku> sku;
	public static volatile SingularAttribute<PurchaseOrderItem, Short> status;

}

