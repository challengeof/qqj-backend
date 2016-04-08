package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.stock.domain.Depot;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(PurchaseOrder.class)
public abstract class PurchaseOrder_ {

	public static volatile SingularAttribute<PurchaseOrder, CutOrder> cutOrder;
	public static volatile SingularAttribute<PurchaseOrder, Depot> depot;
	public static volatile SingularAttribute<PurchaseOrder, AdminUser> receiver;
	public static volatile SingularAttribute<PurchaseOrder, AdminUser> auditor;
	public static volatile SingularAttribute<PurchaseOrder, String> remark;
	public static volatile SingularAttribute<PurchaseOrder, Short> type;
	public static volatile ListAttribute<PurchaseOrder, PurchaseOrderItem> purchaseOrderItems;
	public static volatile SingularAttribute<PurchaseOrder, AdminUser> canceler;
	public static volatile SingularAttribute<PurchaseOrder, String> opinion;
	public static volatile SingularAttribute<PurchaseOrder, Date> receiveTime;
	public static volatile SingularAttribute<PurchaseOrder, BigDecimal> total;
	public static volatile SingularAttribute<PurchaseOrder, Boolean> print;
	public static volatile SingularAttribute<PurchaseOrder, Date> createTime;
	public static volatile SingularAttribute<PurchaseOrder, Date> cancelTime;
	public static volatile SingularAttribute<PurchaseOrder, Date> auditTime;
	public static volatile SingularAttribute<PurchaseOrder, Vendor> vendor;
	public static volatile SingularAttribute<PurchaseOrder, AdminUser> creater;
	public static volatile SingularAttribute<PurchaseOrder, Long> id;
	public static volatile SingularAttribute<PurchaseOrder, Date> expectedArrivedDate;
	public static volatile SingularAttribute<PurchaseOrder, Short> status;

}

