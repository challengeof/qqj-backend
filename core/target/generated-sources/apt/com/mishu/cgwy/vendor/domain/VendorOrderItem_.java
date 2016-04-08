package com.mishu.cgwy.vendor.domain;

import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.stock.domain.Depot;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VendorOrderItem.class)
public abstract class VendorOrderItem_ {

	public static volatile SingularAttribute<VendorOrderItem, Integer> quantityReady;
	public static volatile SingularAttribute<VendorOrderItem, Integer> quantityNeed;
	public static volatile SingularAttribute<VendorOrderItem, Depot> depot;
	public static volatile SingularAttribute<VendorOrderItem, Vendor> vendor;
	public static volatile SingularAttribute<VendorOrderItem, Long> id;
	public static volatile SingularAttribute<VendorOrderItem, Sku> sku;

}

