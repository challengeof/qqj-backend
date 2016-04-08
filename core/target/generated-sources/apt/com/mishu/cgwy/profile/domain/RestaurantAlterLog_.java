package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantAlterLog.class)
public abstract class RestaurantAlterLog_ {

	public static volatile SingularAttribute<RestaurantAlterLog, String> val;
	public static volatile SingularAttribute<RestaurantAlterLog, AdminUser> operater;
	public static volatile SingularAttribute<RestaurantAlterLog, Restaurant> restaurant;
	public static volatile SingularAttribute<RestaurantAlterLog, Long> id;
	public static volatile SingularAttribute<RestaurantAlterLog, Date> createDate;

}

