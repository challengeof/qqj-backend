package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SkuTag.class)
public abstract class SkuTag_ {

	public static volatile SingularAttribute<SkuTag, Integer> limitedQuantity;
	public static volatile SingularAttribute<SkuTag, City> city;
	public static volatile SingularAttribute<SkuTag, Long> id;
	public static volatile SingularAttribute<SkuTag, Sku> sku;
	public static volatile SingularAttribute<SkuTag, Boolean> inDiscount;

}

