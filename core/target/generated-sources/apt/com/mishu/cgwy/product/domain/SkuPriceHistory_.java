package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.inventory.domain.Vendor;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SkuPriceHistory.class)
public abstract class SkuPriceHistory_ {

	public static volatile SingularAttribute<SkuPriceHistory, String> reason;
	public static volatile SingularAttribute<SkuPriceHistory, City> city;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> bundleSalePriceLimit;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> singleSalePrice;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> purchasePrice;
	public static volatile SingularAttribute<SkuPriceHistory, Integer> type;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> fixedPrice;
	public static volatile SingularAttribute<SkuPriceHistory, AdminUser> operator;
	public static volatile SingularAttribute<SkuPriceHistory, Vendor> vendor;
	public static volatile SingularAttribute<SkuPriceHistory, Long> id;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> bundleSalePrice;
	public static volatile SingularAttribute<SkuPriceHistory, Sku> sku;
	public static volatile SingularAttribute<SkuPriceHistory, BigDecimal> singleSalePriceLimit;
	public static volatile SingularAttribute<SkuPriceHistory, Date> createDate;

}

