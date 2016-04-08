package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantAuditReview.class)
public abstract class RestaurantAuditReview_ {

	public static volatile SingularAttribute<RestaurantAuditReview, AdminUser> operater;
	public static volatile SingularAttribute<RestaurantAuditReview, Date> createTime;
	public static volatile SingularAttribute<RestaurantAuditReview, Restaurant> restaurant;
	public static volatile SingularAttribute<RestaurantAuditReview, Date> operateTime;
	public static volatile SingularAttribute<RestaurantAuditReview, Integer> reqType;
	public static volatile SingularAttribute<RestaurantAuditReview, AdminUser> createUser;
	public static volatile SingularAttribute<RestaurantAuditReview, Long> id;
	public static volatile SingularAttribute<RestaurantAuditReview, Integer> status;

}

