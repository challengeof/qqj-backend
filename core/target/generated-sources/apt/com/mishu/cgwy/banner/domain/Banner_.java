package com.mishu.cgwy.banner.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Banner.class)
public abstract class Banner_ {

	public static volatile SingularAttribute<Banner, String> shoppingTip;
	public static volatile SingularAttribute<Banner, String> welcomeMessage;
	public static volatile SingularAttribute<Banner, Date> start;
	public static volatile SingularAttribute<Banner, String> description;
	public static volatile SingularAttribute<Banner, String> rule;
	public static volatile SingularAttribute<Banner, Date> end;
	public static volatile SingularAttribute<Banner, Long> id;
	public static volatile SingularAttribute<Banner, Integer> orderValue;
	public static volatile SingularAttribute<Banner, String> content;

}

