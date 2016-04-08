package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.common.domain.City;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(CollectionPaymentMethod.class)
public abstract class CollectionPaymentMethod_ {

	public static volatile SingularAttribute<CollectionPaymentMethod, Boolean> valid;
	public static volatile SingularAttribute<CollectionPaymentMethod, String> code;
	public static volatile SingularAttribute<CollectionPaymentMethod, City> city;
	public static volatile SingularAttribute<CollectionPaymentMethod, String> name;
	public static volatile SingularAttribute<CollectionPaymentMethod, Long> id;
	public static volatile SingularAttribute<CollectionPaymentMethod, Boolean> cash;

}

