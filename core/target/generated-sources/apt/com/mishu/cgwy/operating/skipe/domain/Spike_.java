package com.mishu.cgwy.operating.skipe.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Spike.class)
public abstract class Spike_ {

	public static volatile SingularAttribute<Spike, AdminUser> operater;
	public static volatile SingularAttribute<Spike, Date> lastModify;
	public static volatile SingularAttribute<Spike, City> city;
	public static volatile SingularAttribute<Spike, Date> createTime;
	public static volatile SingularAttribute<Spike, String> description;
	public static volatile SingularAttribute<Spike, Long> id;
	public static volatile SingularAttribute<Spike, Date> beginTime;
	public static volatile SingularAttribute<Spike, Date> endTime;
	public static volatile SingularAttribute<Spike, Integer> state;
	public static volatile SingularAttribute<Spike, AdminUser> lastModifyOperater;
	public static volatile ListAttribute<Spike, SpikeItem> items;

}

