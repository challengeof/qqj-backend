package com.mishu.cgwy.operating.skipe.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SpikeItem.class)
public abstract class SpikeItem_ {

	public static volatile SingularAttribute<SpikeItem, BigDecimal> originalPrice;
	public static volatile SingularAttribute<SpikeItem, Integer> takeNum;
	public static volatile SingularAttribute<SpikeItem, BigDecimal> price;
	public static volatile SingularAttribute<SpikeItem, Integer> num;
	public static volatile SingularAttribute<SpikeItem, Long> id;
	public static volatile SingularAttribute<SpikeItem, Sku> sku;
	public static volatile SingularAttribute<SpikeItem, Boolean> bundle;
	public static volatile SingularAttribute<SpikeItem, Integer> perMaxNum;
	public static volatile SingularAttribute<SpikeItem, Spike> spike;

}

