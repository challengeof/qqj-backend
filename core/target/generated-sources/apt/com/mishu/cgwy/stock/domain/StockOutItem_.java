package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockOutItem.class)
public abstract class StockOutItem_ {

	public static volatile SingularAttribute<StockOutItem, StockOut> transferOut;
	public static volatile SingularAttribute<StockOutItem, Integer> realQuantity;
	public static volatile SingularAttribute<StockOutItem, Integer> expectedQuantity;
	public static volatile SingularAttribute<StockOutItem, BigDecimal> purchasePrice;
	public static volatile SingularAttribute<StockOutItem, Integer> receiveQuantity;
	public static volatile SingularAttribute<StockOutItem, StockOut> stockOut;
	public static volatile SingularAttribute<StockOutItem, BigDecimal> taxRate;
	public static volatile SingularAttribute<StockOutItem, BigDecimal> price;
	public static volatile SingularAttribute<StockOutItem, Long> id;
	public static volatile SingularAttribute<StockOutItem, Sku> sku;
	public static volatile SingularAttribute<StockOutItem, BigDecimal> avgCost;
	public static volatile SingularAttribute<StockOutItem, Boolean> bundle;
	public static volatile SingularAttribute<StockOutItem, Integer> status;

}

