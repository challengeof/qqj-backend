package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(DynamicSkuPrice.class)
public abstract class DynamicSkuPrice_ {

	public static volatile SingularAttribute<DynamicSkuPrice, BundleDynamicSkuPriceStatus> bundlePriceStatus;
	public static volatile SingularAttribute<DynamicSkuPrice, Long> id;
	public static volatile SingularAttribute<DynamicSkuPrice, Sku> sku;
	public static volatile SingularAttribute<DynamicSkuPrice, Warehouse> warehouse;
	public static volatile SingularAttribute<DynamicSkuPrice, SingleDynamicSkuPriceStatus> singlePriceStatus;

}

