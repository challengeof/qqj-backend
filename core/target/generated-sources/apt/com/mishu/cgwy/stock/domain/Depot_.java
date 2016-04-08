package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Depot.class)
public abstract class Depot_ {

	public static volatile SingularAttribute<Depot, City> city;
	public static volatile SingularAttribute<Depot, Boolean> isMain;
	public static volatile SingularAttribute<Depot, Wgs84Point> wgs84Point;
	public static volatile SingularAttribute<Depot, String> name;
	public static volatile SingularAttribute<Depot, Long> id;

}

