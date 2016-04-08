package com.mishu.cgwy.salesPerformance.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantSalesPerformance.class)
public abstract class RestaurantSalesPerformance_ {

	public static volatile SingularAttribute<RestaurantSalesPerformance, Date> date;
	public static volatile SingularAttribute<RestaurantSalesPerformance, BigDecimal> salesAmount;
	public static volatile SingularAttribute<RestaurantSalesPerformance, Restaurant> restaurant;
	public static volatile SingularAttribute<RestaurantSalesPerformance, Integer> orders;
	public static volatile SingularAttribute<RestaurantSalesPerformance, Long> id;
	public static volatile SingularAttribute<RestaurantSalesPerformance, BigDecimal> avgCostAmount;

}

