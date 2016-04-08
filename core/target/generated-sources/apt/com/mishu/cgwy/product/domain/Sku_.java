package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Sku.class)
public abstract class Sku_ {

	public static volatile SingularAttribute<Sku, Integer> capacityInBundle;
	public static volatile SingularAttribute<Sku, BigDecimal> singleGross_wight;
	public static volatile SingularAttribute<Sku, Product> product;
	public static volatile SingularAttribute<Sku, BigDecimal> marketPrice;
	public static volatile SingularAttribute<Sku, BigDecimal> bundleHeight;
	public static volatile SingularAttribute<Sku, BigDecimal> singleWidth;
	public static volatile SingularAttribute<Sku, BigDecimal> singleHeight;
	public static volatile SingularAttribute<Sku, BigDecimal> bundleLong;
	public static volatile ListAttribute<Sku, SkuTag> skuTags;
	public static volatile SingularAttribute<Sku, String> bundleUnit;
	public static volatile SingularAttribute<Sku, BigDecimal> bundleGross_wight;
	public static volatile SingularAttribute<Sku, BigDecimal> bundleNet_weight;
	public static volatile SingularAttribute<Sku, BigDecimal> bundleWidth;
	public static volatile SingularAttribute<Sku, BigDecimal> rate;
	public static volatile SingularAttribute<Sku, BigDecimal> singleLong;
	public static volatile ListAttribute<Sku, DynamicSkuPrice> dynamicSkuPrice;
	public static volatile SingularAttribute<Sku, String> singleUnit;
	public static volatile SingularAttribute<Sku, Long> id;
	public static volatile SingularAttribute<Sku, BigDecimal> singleNet_weight;
	public static volatile SingularAttribute<Sku, Integer> status;
	public static volatile SingularAttribute<Sku, Date> createDate;

}

