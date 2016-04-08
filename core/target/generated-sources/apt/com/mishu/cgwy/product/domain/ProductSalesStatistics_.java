package com.mishu.cgwy.product.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ProductSalesStatistics.class)
public abstract class ProductSalesStatistics_ {

	public static volatile SingularAttribute<ProductSalesStatistics, Product> product;
	public static volatile SingularAttribute<ProductSalesStatistics, Integer> singleSaleCount;
	public static volatile SingularAttribute<ProductSalesStatistics, Integer> bundleSaleCount;
	public static volatile SingularAttribute<ProductSalesStatistics, Long> id;

}

