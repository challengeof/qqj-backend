package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Transfer;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CutOrder.class)
public abstract class CutOrder_ {

	public static volatile SingularAttribute<CutOrder, Date> cutDate;
	public static volatile SingularAttribute<CutOrder, Depot> depot;
	public static volatile SingularAttribute<CutOrder, City> city;
	public static volatile SingularAttribute<CutOrder, AdminUser> submitUser;
	public static volatile ListAttribute<CutOrder, Transfer> transfers;
	public static volatile SingularAttribute<CutOrder, Date> submitDate;
	public static volatile ListAttribute<CutOrder, Order> orders;
	public static volatile SingularAttribute<CutOrder, Long> id;
	public static volatile SingularAttribute<CutOrder, Long> version;
	public static volatile ListAttribute<CutOrder, PurchaseOrder> purchaseOrders;
	public static volatile SingularAttribute<CutOrder, AdminUser> operator;
	public static volatile SingularAttribute<CutOrder, Short> status;

}

