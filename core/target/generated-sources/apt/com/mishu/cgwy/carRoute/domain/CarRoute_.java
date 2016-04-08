package com.mishu.cgwy.carRoute.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.stock.domain.Depot;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CarRoute.class)
public abstract class CarRoute_ {

	public static volatile SingularAttribute<CarRoute, Depot> depot;
	public static volatile SingularAttribute<CarRoute, City> city;
	public static volatile SingularAttribute<CarRoute, Double> price;
	public static volatile SingularAttribute<CarRoute, String> name;
	public static volatile SingularAttribute<CarRoute, Long> id;
	public static volatile SingularAttribute<CarRoute, AdminUser> operator;

}

