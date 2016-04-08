package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Collectionment.class)
public abstract class Collectionment_ {

	public static volatile SingularAttribute<Collectionment, Boolean> valid;
	public static volatile SingularAttribute<Collectionment, BigDecimal> amount;
	public static volatile SingularAttribute<Collectionment, AdminUser> creator;
	public static volatile SingularAttribute<Collectionment, Date> cancelDate;
	public static volatile SingularAttribute<Collectionment, Restaurant> restaurant;
	public static volatile SingularAttribute<Collectionment, Long> id;
	public static volatile SingularAttribute<Collectionment, CollectionPaymentMethod> collectionPaymentMethod;
	public static volatile SingularAttribute<Collectionment, Date> createDate;
	public static volatile SingularAttribute<Collectionment, Date> realCreateDate;
	public static volatile SingularAttribute<Collectionment, AdminUser> canceler;

}

