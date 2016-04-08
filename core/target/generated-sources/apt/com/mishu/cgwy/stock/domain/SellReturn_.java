package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SellReturn.class)
public abstract class SellReturn_ {

	public static volatile SingularAttribute<SellReturn, AdminUser> creator;
	public static volatile SingularAttribute<SellReturn, String> auditOpinion;
	public static volatile SingularAttribute<SellReturn, BigDecimal> amount;
	public static volatile SingularAttribute<SellReturn, Depot> depot;
	public static volatile SingularAttribute<SellReturn, AdminUser> auditor;
	public static volatile ListAttribute<SellReturn, SellReturnItem> sellReturnItems;
	public static volatile SingularAttribute<SellReturn, Long> id;
	public static volatile SingularAttribute<SellReturn, Integer> type;
	public static volatile SingularAttribute<SellReturn, Integer> status;
	public static volatile SingularAttribute<SellReturn, Date> createDate;
	public static volatile SingularAttribute<SellReturn, Date> auditDate;
	public static volatile SingularAttribute<SellReturn, Order> order;

}

