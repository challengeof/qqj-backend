package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.stock.domain.Depot;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PurchaseOrderItemSign.class)
public abstract class PurchaseOrderItemSign_ {

	public static volatile SingularAttribute<PurchaseOrderItemSign, Depot> depot;
	public static volatile SingularAttribute<PurchaseOrderItemSign, City> city;
	public static volatile SingularAttribute<PurchaseOrderItemSign, Long> id;
	public static volatile SingularAttribute<PurchaseOrderItemSign, Sku> sku;
	public static volatile SingularAttribute<PurchaseOrderItemSign, Short> status;

}

