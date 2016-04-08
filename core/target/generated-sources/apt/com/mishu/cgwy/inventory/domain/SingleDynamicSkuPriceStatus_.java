package com.mishu.cgwy.inventory.domain;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SingleDynamicSkuPriceStatus.class)
public abstract class SingleDynamicSkuPriceStatus_ {

	public static volatile SingularAttribute<SingleDynamicSkuPriceStatus, Boolean> singleAvailable;
	public static volatile SingularAttribute<SingleDynamicSkuPriceStatus, BigDecimal> singleSalePrice;
	public static volatile SingularAttribute<SingleDynamicSkuPriceStatus, Boolean> singleInSale;

}

