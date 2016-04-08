package com.mishu.cgwy.promotion.domain;

import com.mishu.cgwy.product.domain.Sku;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PromotableItems.class)
public abstract class PromotableItems_ {

	public static volatile SingularAttribute<PromotableItems, Integer> quantity;
	public static volatile SingularAttribute<PromotableItems, Sku> sku;
	public static volatile SingularAttribute<PromotableItems, Boolean> bundle;

}

