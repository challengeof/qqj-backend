package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockInItem.class)
public abstract class StockInItem_ {

	public static volatile SingularAttribute<StockInItem, BigDecimal> taxRate;
	public static volatile SingularAttribute<StockInItem, Integer> realQuantity;
	public static volatile SingularAttribute<StockInItem, StockIn> stockIn;
	public static volatile SingularAttribute<StockInItem, BigDecimal> salePrice;
	public static volatile SingularAttribute<StockInItem, BigDecimal> price;
	public static volatile SingularAttribute<StockInItem, Integer> expectedQuantity;
	public static volatile SingularAttribute<StockInItem, Long> id;
	public static volatile SingularAttribute<StockInItem, Sku> sku;
	public static volatile SingularAttribute<StockInItem, BigDecimal> avgCost;

}

