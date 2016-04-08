package com.mishu.cgwy.common.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Region.class)
public abstract class Region_ {

	public static volatile SingularAttribute<Region, City> city;
	public static volatile SingularAttribute<Region, String> name;
	public static volatile SingularAttribute<Region, Long> id;
	public static volatile ListAttribute<Region, Zone> zones;

}

