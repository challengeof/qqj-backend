package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockTotalDaily.class)
public abstract class StockTotalDaily_ {

	public static volatile SingularAttribute<StockTotalDaily, Integer> quantity;
	public static volatile SingularAttribute<StockTotalDaily, City> city;
	public static volatile SingularAttribute<StockTotalDaily, Long> id;
	public static volatile SingularAttribute<StockTotalDaily, Sku> sku;
	public static volatile SingularAttribute<StockTotalDaily, BigDecimal> avgCost;
	public static volatile SingularAttribute<StockTotalDaily, BigDecimal> totalCost;
	public static volatile SingularAttribute<StockTotalDaily, Date> createDate;

}

