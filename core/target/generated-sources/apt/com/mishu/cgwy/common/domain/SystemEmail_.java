package com.mishu.cgwy.common.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SystemEmail.class)
public abstract class SystemEmail_ {

	public static volatile SingularAttribute<SystemEmail, String> sendTo;
	public static volatile SingularAttribute<SystemEmail, City> city;
	public static volatile SingularAttribute<SystemEmail, String> name;
	public static volatile SingularAttribute<SystemEmail, String> sendCc;
	public static volatile SingularAttribute<SystemEmail, Long> id;
	public static volatile SingularAttribute<SystemEmail, Integer> type;

}

