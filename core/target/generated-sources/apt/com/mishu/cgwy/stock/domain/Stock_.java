package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Stock.class)
public abstract class Stock_ {

	public static volatile SingularAttribute<Stock, StockOut> stockOut;
	public static volatile SingularAttribute<Stock, BigDecimal> taxRate;
	public static volatile SingularAttribute<Stock, StockIn> stockIn;
	public static volatile SingularAttribute<Stock, Depot> depot;
	public static volatile SingularAttribute<Stock, StockAdjust> stockAdjust;
	public static volatile SingularAttribute<Stock, Long> id;
	public static volatile SingularAttribute<Stock, Sku> sku;
	public static volatile SingularAttribute<Stock, Integer> stock;
	public static volatile SingularAttribute<Stock, Long> version;
	public static volatile SingularAttribute<Stock, Shelf> shelf;
	public static volatile SingularAttribute<Stock, Date> expirationDate;

}

