package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Favorite.class)
public abstract class Favorite_ {

	public static volatile SingularAttribute<Favorite, Date> updateTime;
	public static volatile SingularAttribute<Favorite, Long> id;
	public static volatile SingularAttribute<Favorite, Sku> sku;
	public static volatile SingularAttribute<Favorite, Customer> customer;

}

