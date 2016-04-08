package com.mishu.cgwy.stock.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Shelf.class)
public abstract class Shelf_ {

	public static volatile SingularAttribute<Shelf, String> area;
	public static volatile SingularAttribute<Shelf, String> number;
	public static volatile SingularAttribute<Shelf, String> shelfCode;
	public static volatile SingularAttribute<Shelf, Depot> depot;
	public static volatile SingularAttribute<Shelf, String> name;
	public static volatile SingularAttribute<Shelf, Long> id;
	public static volatile SingularAttribute<Shelf, String> row;

}

