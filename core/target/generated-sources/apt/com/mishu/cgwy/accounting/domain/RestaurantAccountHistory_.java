package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.profile.domain.Restaurant;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(RestaurantAccountHistory.class)
public abstract class RestaurantAccountHistory_ {

	public static volatile SingularAttribute<RestaurantAccountHistory, BigDecimal> unWriteoffAmount;
	public static volatile SingularAttribute<RestaurantAccountHistory, BigDecimal> amount;
	public static volatile SingularAttribute<RestaurantAccountHistory, Date> accountDate;
	public static volatile SingularAttribute<RestaurantAccountHistory, AccountReceivable> accountReceivable;
	public static volatile SingularAttribute<RestaurantAccountHistory, Restaurant> restaurant;
	public static volatile SingularAttribute<RestaurantAccountHistory, Collectionment> collectionment;
	public static volatile SingularAttribute<RestaurantAccountHistory, AccountReceivableWriteoff> accountReceivableWriteoff;
	public static volatile SingularAttribute<RestaurantAccountHistory, Long> id;
	public static volatile SingularAttribute<RestaurantAccountHistory, Date> createDate;

}

