package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantAccount.class)
public abstract class RestaurantAccount_ {

	public static volatile SingularAttribute<RestaurantAccount, BigDecimal> unWriteoffAmount;
	public static volatile SingularAttribute<RestaurantAccount, BigDecimal> amount;
	public static volatile SingularAttribute<RestaurantAccount, Restaurant> restaurant;
	public static volatile SingularAttribute<RestaurantAccount, Long> id;

}

