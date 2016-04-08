package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.inventory.domain.Vendor;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VendorAccount.class)
public abstract class VendorAccount_ {

	public static volatile SingularAttribute<VendorAccount, BigDecimal> balance;
	public static volatile SingularAttribute<VendorAccount, BigDecimal> payable;
	public static volatile SingularAttribute<VendorAccount, Vendor> vendor;
	public static volatile SingularAttribute<VendorAccount, Long> id;

}

