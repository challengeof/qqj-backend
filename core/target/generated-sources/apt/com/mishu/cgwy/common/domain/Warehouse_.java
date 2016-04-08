package com.mishu.cgwy.common.domain;

import com.mishu.cgwy.stock.domain.Depot;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Warehouse.class)
public abstract class Warehouse_ {

	public static volatile SingularAttribute<Warehouse, Boolean> isDefault;
	public static volatile SingularAttribute<Warehouse, Depot> depot;
	public static volatile SingularAttribute<Warehouse, City> city;
	public static volatile SingularAttribute<Warehouse, String> name;
	public static volatile SingularAttribute<Warehouse, Boolean> active;
	public static volatile SingularAttribute<Warehouse, Long> id;

}

