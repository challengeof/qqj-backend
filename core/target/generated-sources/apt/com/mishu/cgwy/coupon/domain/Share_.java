package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.profile.domain.Customer;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Share.class)
public abstract class Share_ {

	public static volatile SingularAttribute<Share, Customer> reference;
	public static volatile SingularAttribute<Share, Date> createdTime;
	public static volatile SingularAttribute<Share, Long> id;
	public static volatile SingularAttribute<Share, Customer> registrant;
	public static volatile SingularAttribute<Share, Integer> shareType;
	public static volatile SingularAttribute<Share, Boolean> couponSended;

}

