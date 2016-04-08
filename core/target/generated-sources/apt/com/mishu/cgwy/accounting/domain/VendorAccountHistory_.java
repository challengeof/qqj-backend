package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.inventory.domain.Vendor;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(VendorAccountHistory.class)
public abstract class VendorAccountHistory_ {

	public static volatile SingularAttribute<VendorAccountHistory, BigDecimal> unWriteoffAmount;
	public static volatile SingularAttribute<VendorAccountHistory, BigDecimal> amount;
	public static volatile SingularAttribute<VendorAccountHistory, Date> accountDate;
	public static volatile SingularAttribute<VendorAccountHistory, AccountPayableWriteoff> accountPayableWriteoff;
	public static volatile SingularAttribute<VendorAccountHistory, AccountPayable> accountPayable;
	public static volatile SingularAttribute<VendorAccountHistory, Vendor> vendor;
	public static volatile SingularAttribute<VendorAccountHistory, Payment> payment;
	public static volatile SingularAttribute<VendorAccountHistory, Long> id;
	public static volatile SingularAttribute<VendorAccountHistory, Short> type;
	public static volatile SingularAttribute<VendorAccountHistory, Date> createDate;

}

