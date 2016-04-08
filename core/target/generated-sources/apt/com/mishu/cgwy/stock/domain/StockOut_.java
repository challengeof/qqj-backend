package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.purchase.domain.ReturnNote;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(StockOut.class)
public abstract class StockOut_ {

	public static volatile ListAttribute<StockOut, StockOutItem> stockOutItems;
	public static volatile SingularAttribute<StockOut, BigDecimal> amount;
	public static volatile SingularAttribute<StockOut, Depot> depot;
	public static volatile SingularAttribute<StockOut, AdminUser> receiver;
	public static volatile SingularAttribute<StockOut, Boolean> pickPrint;
	public static volatile SingularAttribute<StockOut, Boolean> settle;
	public static volatile SingularAttribute<StockOut, Date> receiveDate;
	public static volatile SingularAttribute<StockOut, Date> settleDate;
	public static volatile SingularAttribute<StockOut, Integer> type;
	public static volatile SingularAttribute<StockOut, BigDecimal> receiveAmount;
	public static volatile SingularAttribute<StockOut, Transfer> transfer;
	public static volatile SingularAttribute<StockOut, ReturnNote> returnNote;
	public static volatile SingularAttribute<StockOut, OrderGroup> orderGroup;
	public static volatile SingularAttribute<StockOut, AdminUser> sender;
	public static volatile SingularAttribute<StockOut, Boolean> outPrint;
	public static volatile SingularAttribute<StockOut, Date> finishDate;
	public static volatile SingularAttribute<StockOut, Long> id;
	public static volatile SingularAttribute<StockOut, Integer> status;
	public static volatile SingularAttribute<StockOut, Order> order;
	public static volatile SingularAttribute<StockOut, Date> createDate;

}

