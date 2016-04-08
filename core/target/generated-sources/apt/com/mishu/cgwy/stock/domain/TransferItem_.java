package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.product.domain.Sku;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(TransferItem.class)
public abstract class TransferItem_ {

	public static volatile SingularAttribute<TransferItem, Integer> quantity;
	public static volatile SingularAttribute<TransferItem, Transfer> transfer;
	public static volatile SingularAttribute<TransferItem, Long> id;
	public static volatile SingularAttribute<TransferItem, Sku> sku;

}

