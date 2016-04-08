package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockTotal.class)
public abstract class StockTotal_ {

	public static volatile SingularAttribute<StockTotal, Integer> quantity;
	public static volatile SingularAttribute<StockTotal, City> city;
	public static volatile SingularAttribute<StockTotal, Long> id;
	public static volatile SingularAttribute<StockTotal, Sku> sku;
	public static volatile SingularAttribute<StockTotal, BigDecimal> avgCost;
	public static volatile SingularAttribute<StockTotal, Long> version;
	public static volatile SingularAttribute<StockTotal, BigDecimal> totalCost;

}

