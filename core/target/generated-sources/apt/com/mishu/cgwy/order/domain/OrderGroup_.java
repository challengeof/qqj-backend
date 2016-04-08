package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.StockOut;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(OrderGroup.class)
public abstract class OrderGroup_ {

	public static volatile SingularAttribute<OrderGroup, Depot> depot;
	public static volatile SingularAttribute<OrderGroup, City> city;
	public static volatile ListAttribute<OrderGroup, Order> members;
	public static volatile SingularAttribute<OrderGroup, Organization> organization;
	public static volatile SingularAttribute<OrderGroup, String> name;
	public static volatile ListAttribute<OrderGroup, StockOut> stockOuts;
	public static volatile SingularAttribute<OrderGroup, AdminUser> tracker;
	public static volatile SingularAttribute<OrderGroup, Long> id;
	public static volatile SingularAttribute<OrderGroup, Boolean> checkResult;
	public static volatile SingularAttribute<OrderGroup, Date> createDate;

}

