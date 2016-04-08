package com.mishu.cgwy.inventory.domain;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BundleDynamicSkuPriceStatus.class)
public abstract class BundleDynamicSkuPriceStatus_ {

	public static volatile SingularAttribute<BundleDynamicSkuPriceStatus, Boolean> bundleAvailable;
	public static volatile SingularAttribute<BundleDynamicSkuPriceStatus, Boolean> bundleInSale;
	public static volatile SingularAttribute<BundleDynamicSkuPriceStatus, BigDecimal> bundleSalePrice;

}

