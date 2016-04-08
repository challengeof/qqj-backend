package com.mishu.cgwy.bonus.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CustomerServiceBonus.class)
public abstract class CustomerServiceBonus_ {

	public static volatile SingularAttribute<CustomerServiceBonus, Date> month;
	public static volatile SingularAttribute<CustomerServiceBonus, AdminUser> adminUser;
	public static volatile SingularAttribute<CustomerServiceBonus, BigDecimal> bonus;
	public static volatile SingularAttribute<CustomerServiceBonus, Integer> weekOfMonth;
	public static volatile SingularAttribute<CustomerServiceBonus, Restaurant> restaurant;
	public static volatile SingularAttribute<CustomerServiceBonus, Long> id;
	public static volatile SingularAttribute<CustomerServiceBonus, Integer> bonusType;

}

