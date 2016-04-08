package com.mishu.cgwy.profile.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Suggestion.class)
public abstract class Suggestion_ {

	public static volatile SingularAttribute<Suggestion, Date> createTime;
	public static volatile SingularAttribute<Suggestion, Restaurant> restaurant;
	public static volatile SingularAttribute<Suggestion, String> remark;
	public static volatile SingularAttribute<Suggestion, Long> id;

}

