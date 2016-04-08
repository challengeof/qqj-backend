package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SalesFinance.class)
public abstract class SalesFinance_ {

	public static volatile SingularAttribute<SalesFinance, Long> returnedQuantity;
	public static volatile SingularAttribute<SalesFinance, Long> stockUsed;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> avgPrice;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> salesTotal;
	public static volatile SingularAttribute<SalesFinance, Long> purchaseUsed;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> purchasePrice;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> grossMargins;
	public static volatile SingularAttribute<SalesFinance, Warehouse> warehouse;
	public static volatile SingularAttribute<SalesFinance, Long> orderQuantity;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> salesUnitPrice;
	public static volatile SingularAttribute<SalesFinance, Date> statisticsDate;
	public static volatile SingularAttribute<SalesFinance, BigDecimal> spendingTotal;
	public static volatile SingularAttribute<SalesFinance, Long> id;
	public static volatile SingularAttribute<SalesFinance, Sku> sku;
	public static volatile SingularAttribute<SalesFinance, Integer> stock;

}

