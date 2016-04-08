package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AvgCostHistory.class)
public abstract class AvgCostHistory_ {

	public static volatile SingularAttribute<AvgCostHistory, Date> date;
	public static volatile SingularAttribute<AvgCostHistory, BigDecimal> amount;
	public static volatile SingularAttribute<AvgCostHistory, Integer> quantity;
	public static volatile SingularAttribute<AvgCostHistory, City> city;
	public static volatile SingularAttribute<AvgCostHistory, Long> id;
	public static volatile SingularAttribute<AvgCostHistory, Sku> sku;
	public static volatile SingularAttribute<AvgCostHistory, BigDecimal> avgCost;

}

