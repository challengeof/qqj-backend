package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SellCancelItem.class)
public abstract class SellCancelItem_ {

	public static volatile SingularAttribute<SellCancelItem, SellCancel> sellCancel;
	public static volatile SingularAttribute<SellCancelItem, Integer> reason;
	public static volatile SingularAttribute<SellCancelItem, Integer> quantity;
	public static volatile SingularAttribute<SellCancelItem, BigDecimal> price;
	public static volatile SingularAttribute<SellCancelItem, String> memo;
	public static volatile SingularAttribute<SellCancelItem, Long> id;
	public static volatile SingularAttribute<SellCancelItem, Sku> sku;
	public static volatile SingularAttribute<SellCancelItem, Boolean> bundle;

}

