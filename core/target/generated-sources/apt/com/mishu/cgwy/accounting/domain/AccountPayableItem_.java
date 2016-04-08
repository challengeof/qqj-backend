package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountPayableItem.class)
public abstract class AccountPayableItem_ {

	public static volatile SingularAttribute<AccountPayableItem, BigDecimal> taxRate;
	public static volatile SingularAttribute<AccountPayableItem, Integer> quantity;
	public static volatile SingularAttribute<AccountPayableItem, AccountPayable> accountPayable;
	public static volatile SingularAttribute<AccountPayableItem, BigDecimal> price;
	public static volatile SingularAttribute<AccountPayableItem, Long> id;
	public static volatile SingularAttribute<AccountPayableItem, Sku> sku;

}

