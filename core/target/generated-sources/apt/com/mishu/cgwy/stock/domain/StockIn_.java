package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockIn.class)
public abstract class StockIn_ {

	public static volatile SingularAttribute<StockIn, AdminUser> creator;
	public static volatile SingularAttribute<StockIn, BigDecimal> amount;
	public static volatile SingularAttribute<StockIn, Depot> depot;
	public static volatile SingularAttribute<StockIn, AdminUser> receiver;
	public static volatile SingularAttribute<StockIn, Date> receiveDate;
	public static volatile SingularAttribute<StockIn, Integer> type;
	public static volatile SingularAttribute<StockIn, SellReturn> sellReturn;
	public static volatile SingularAttribute<StockIn, Transfer> transfer;
	public static volatile ListAttribute<StockIn, StockInItem> stockInItems;
	public static volatile SingularAttribute<StockIn, Boolean> outPrint;
	public static volatile SingularAttribute<StockIn, PurchaseOrder> purchaseOrder;
	public static volatile SingularAttribute<StockIn, Long> id;
	public static volatile SingularAttribute<StockIn, Integer> status;
	public static volatile SingularAttribute<StockIn, Date> createDate;

}

