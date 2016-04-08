package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.common.domain.City;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantType.class)
public abstract class RestaurantType_ {

	public static volatile SetAttribute<RestaurantType, City> cities;
	public static volatile ListAttribute<RestaurantType, RestaurantType> childRestaurantTypes;
	public static volatile SingularAttribute<RestaurantType, String> name;
	public static volatile SingularAttribute<RestaurantType, RestaurantType> parentRestaurantType;
	public static volatile SingularAttribute<RestaurantType, Long> id;
	public static volatile SingularAttribute<RestaurantType, Integer> type;
	public static volatile SingularAttribute<RestaurantType, Integer> status;

}

