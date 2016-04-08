package com.mishu.cgwy.bonus.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CustomerServiceStatistics.class)
public abstract class CustomerServiceStatistics_ {

	public static volatile SingularAttribute<CustomerServiceStatistics, Date> month;
	public static volatile SingularAttribute<CustomerServiceStatistics, AdminUser> adminUser;
	public static volatile SingularAttribute<CustomerServiceStatistics, BigDecimal> bonus;
	public static volatile SingularAttribute<CustomerServiceStatistics, Long> restaurantHavingOrderCount;
	public static volatile SingularAttribute<CustomerServiceStatistics, BigDecimal> consumption;
	public static volatile SingularAttribute<CustomerServiceStatistics, Long> id;
	public static volatile SingularAttribute<CustomerServiceStatistics, Long> complaintCount;
	public static volatile SingularAttribute<CustomerServiceStatistics, Long> newRestaurantCount;

}

