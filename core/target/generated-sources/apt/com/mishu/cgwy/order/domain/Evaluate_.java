package com.mishu.cgwy.order.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.score.domain.ScoreLog;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Evaluate.class)
public abstract class Evaluate_ {

	public static volatile SingularAttribute<Evaluate, String> msg;
	public static volatile SingularAttribute<Evaluate, AdminUser> adminUser;
	public static volatile SingularAttribute<Evaluate, Integer> deliverySpeedScore;
	public static volatile SingularAttribute<Evaluate, Integer> trackerServiceScore;
	public static volatile SingularAttribute<Evaluate, Integer> productQualityScore;
	public static volatile SingularAttribute<Evaluate, Organization> organization;
	public static volatile SingularAttribute<Evaluate, AdminUser> tracker;
	public static volatile SingularAttribute<Evaluate, Long> id;
	public static volatile SingularAttribute<Evaluate, ScoreLog> scoreLog;
	public static volatile SingularAttribute<Evaluate, Order> order;
	public static volatile SingularAttribute<Evaluate, Customer> customer;

}

