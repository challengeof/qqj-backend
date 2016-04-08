package com.mishu.cgwy.salesPerformance.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SellerSalesPerformance.class)
public abstract class SellerSalesPerformance_ {

	public static volatile SingularAttribute<SellerSalesPerformance, Date> date;
	public static volatile SingularAttribute<SellerSalesPerformance, AdminUser> adminUser;
	public static volatile SingularAttribute<SellerSalesPerformance, BigDecimal> salesAmount;
	public static volatile SingularAttribute<SellerSalesPerformance, Integer> newCustomers;
	public static volatile SingularAttribute<SellerSalesPerformance, Integer> orders;
	public static volatile SingularAttribute<SellerSalesPerformance, Long> id;
	public static volatile SingularAttribute<SellerSalesPerformance, BigDecimal> avgCostAmount;

}

