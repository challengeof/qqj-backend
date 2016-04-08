package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SkuPrice.class)
public abstract class SkuPrice_ {

	public static volatile SingularAttribute<SkuPrice, City> city;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldSingleSalePriceLimit;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldSingleSalePrice;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> bundleSalePriceLimit;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> singleSalePrice;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldBundleSalePrice;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> purchasePrice;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> fixedPrice;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldBundleSalePriceLimit;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldPurchasePrice;
	public static volatile SingularAttribute<SkuPrice, Long> id;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> bundleSalePrice;
	public static volatile SingularAttribute<SkuPrice, Sku> sku;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> singleSalePriceLimit;
	public static volatile SingularAttribute<SkuPrice, BigDecimal> oldFixedPrice;

}

