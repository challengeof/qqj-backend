package com.mishu.cgwy.workTicket.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Restaurant;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(WorkTicket.class)
public abstract class WorkTicket_ {

	public static volatile SingularAttribute<WorkTicket, Integer> process;
	public static volatile SingularAttribute<WorkTicket, String> consultantsTelephone;
	public static volatile SingularAttribute<WorkTicket, Restaurant> restaurant;
	public static volatile SingularAttribute<WorkTicket, String> content;
	public static volatile SingularAttribute<WorkTicket, AdminUser> operator;
	public static volatile SingularAttribute<WorkTicket, AdminUser> followUp;
	public static volatile SingularAttribute<WorkTicket, String> consultants;
	public static volatile SingularAttribute<WorkTicket, Date> createTime;
	public static volatile SingularAttribute<WorkTicket, Long> id;
	public static volatile SingularAttribute<WorkTicket, Integer> problemSources;
	public static volatile SingularAttribute<WorkTicket, String> operateLog;
	public static volatile SingularAttribute<WorkTicket, String> username;
	public static volatile SingularAttribute<WorkTicket, Order> order;
	public static volatile SingularAttribute<WorkTicket, Integer> status;

}

