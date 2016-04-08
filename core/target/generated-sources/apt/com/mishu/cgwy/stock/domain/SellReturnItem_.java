package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SellReturnItem.class)
public abstract class SellReturnItem_ {

	public static volatile SingularAttribute<SellReturnItem, BigDecimal> taxRate;
	public static volatile SingularAttribute<SellReturnItem, SellReturn> sellReturn;
	public static volatile SingularAttribute<SellReturnItem, Integer> quantity;
	public static volatile SingularAttribute<SellReturnItem, BigDecimal> price;
	public static volatile SingularAttribute<SellReturnItem, String> memo;
	public static volatile SingularAttribute<SellReturnItem, SellReturnReason> sellReturnReason;
	public static volatile SingularAttribute<SellReturnItem, Long> id;
	public static volatile SingularAttribute<SellReturnItem, Sku> sku;
	public static volatile SingularAttribute<SellReturnItem, BigDecimal> avgCost;
	public static volatile SingularAttribute<SellReturnItem, Boolean> bundle;

}

