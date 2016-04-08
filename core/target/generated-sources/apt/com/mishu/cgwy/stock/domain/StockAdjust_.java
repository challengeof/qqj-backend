package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockAdjust.class)
public abstract class StockAdjust_ {

	public static volatile SingularAttribute<StockAdjust, AdminUser> creator;
	public static volatile SingularAttribute<StockAdjust, Integer> quantity;
	public static volatile SingularAttribute<StockAdjust, Depot> depot;
	public static volatile SingularAttribute<StockAdjust, AdminUser> auditor;
	public static volatile SingularAttribute<StockAdjust, Long> version;
	public static volatile SingularAttribute<StockAdjust, String> shelfName;
	public static volatile SingularAttribute<StockAdjust, BigDecimal> taxRate;
	public static volatile SingularAttribute<StockAdjust, Integer> adjustQuantity;
	public static volatile SingularAttribute<StockAdjust, String> comment;
	public static volatile SingularAttribute<StockAdjust, Long> id;
	public static volatile SingularAttribute<StockAdjust, Sku> sku;
	public static volatile SingularAttribute<StockAdjust, BigDecimal> avgCost;
	public static volatile SingularAttribute<StockAdjust, Integer> status;
	public static volatile SingularAttribute<StockAdjust, Date> expirationDate;
	public static volatile SingularAttribute<StockAdjust, Date> createDate;
	public static volatile SingularAttribute<StockAdjust, Date> auditDate;

}

