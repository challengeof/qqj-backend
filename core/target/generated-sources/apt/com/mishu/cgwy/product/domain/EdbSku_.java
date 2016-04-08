package com.mishu.cgwy.product.domain;

import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(EdbSku.class)
public abstract class EdbSku_ {

	public static volatile SingularAttribute<EdbSku, BigDecimal> avgPrice;
	public static volatile SingularAttribute<EdbSku, Date> stockDate;
	public static volatile SingularAttribute<EdbSku, Long> id;
	public static volatile SingularAttribute<EdbSku, Sku> sku;
	public static volatile SingularAttribute<EdbSku, Integer> stock;

}

