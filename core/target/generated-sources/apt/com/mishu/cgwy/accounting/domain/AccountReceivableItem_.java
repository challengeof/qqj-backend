package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountReceivableItem.class)
public abstract class AccountReceivableItem_ {

	public static volatile SingularAttribute<AccountReceivableItem, BigDecimal> taxRate;
	public static volatile SingularAttribute<AccountReceivableItem, AccountReceivable> accountReceivable;
	public static volatile SingularAttribute<AccountReceivableItem, Integer> quantity;
	public static volatile SingularAttribute<AccountReceivableItem, BigDecimal> price;
	public static volatile SingularAttribute<AccountReceivableItem, Long> id;
	public static volatile SingularAttribute<AccountReceivableItem, Sku> sku;
	public static volatile SingularAttribute<AccountReceivableItem, BigDecimal> avgCost;

}

