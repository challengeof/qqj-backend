package com.mishu.cgwy.common.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Zone.class)
public abstract class Zone_ {

	public static volatile SingularAttribute<Zone, City> city;
	public static volatile SingularAttribute<Zone, String> name;
	public static volatile SingularAttribute<Zone, Boolean> active;
	public static volatile SingularAttribute<Zone, Long> id;
	public static volatile SingularAttribute<Zone, Warehouse> warehouse;
	public static volatile SingularAttribute<Zone, Region> region;

}

