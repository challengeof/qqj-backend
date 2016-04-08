package com.mishu.cgwy.task.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Task.class)
public abstract class Task_ {

	public static volatile SingularAttribute<Task, String> result;
	public static volatile SingularAttribute<Task, AdminUser> submitUser;
	public static volatile SingularAttribute<Task, Date> submitDate;
	public static volatile SingularAttribute<Task, String> description;
	public static volatile SingularAttribute<Task, String> remark;
	public static volatile SingularAttribute<Task, Long> id;
	public static volatile SingularAttribute<Task, Long> timeCost;
	public static volatile SingularAttribute<Task, Short> type;
	public static volatile SingularAttribute<Task, String> taskCondition;
	public static volatile SingularAttribute<Task, Short> status;

}

