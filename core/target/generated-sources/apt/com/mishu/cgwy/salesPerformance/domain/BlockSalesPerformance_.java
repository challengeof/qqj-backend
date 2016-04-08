package com.mishu.cgwy.salesPerformance.domain;

import com.mishu.cgwy.common.domain.Block;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(BlockSalesPerformance.class)
public abstract class BlockSalesPerformance_ {

	public static volatile SingularAttribute<BlockSalesPerformance, Date> date;
	public static volatile SingularAttribute<BlockSalesPerformance, BigDecimal> salesAmount;
	public static volatile SingularAttribute<BlockSalesPerformance, Integer> newCustomers;
	public static volatile SingularAttribute<BlockSalesPerformance, Block> block;
	public static volatile SingularAttribute<BlockSalesPerformance, Integer> orders;
	public static volatile SingularAttribute<BlockSalesPerformance, Long> id;
	public static volatile SingularAttribute<BlockSalesPerformance, BigDecimal> avgCostAmount;

}

