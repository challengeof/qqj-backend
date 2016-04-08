package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountPayableWriteoff.class)
public abstract class AccountPayableWriteoff_ {

	public static volatile SingularAttribute<AccountPayableWriteoff, AdminUser> writeOffer;
	public static volatile SingularAttribute<AccountPayableWriteoff, BigDecimal> writeOffAmount;
	public static volatile SingularAttribute<AccountPayableWriteoff, Date> writeOffDate;
	public static volatile SingularAttribute<AccountPayableWriteoff, Date> cancelDate;
	public static volatile SingularAttribute<AccountPayableWriteoff, AccountPayable> accountPayable;
	public static volatile SingularAttribute<AccountPayableWriteoff, Long> id;
	public static volatile SingularAttribute<AccountPayableWriteoff, Date> realCancelDate;
	public static volatile SingularAttribute<AccountPayableWriteoff, Date> createDate;
	public static volatile SingularAttribute<AccountPayableWriteoff, AdminUser> canceler;
	public static volatile SingularAttribute<AccountPayableWriteoff, Short> status;

}

