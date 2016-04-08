package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountReceivableWriteoff.class)
public abstract class AccountReceivableWriteoff_ {

	public static volatile SingularAttribute<AccountReceivableWriteoff, AdminUser> writeOffer;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Date> realWriteOffDate;
	public static volatile SingularAttribute<AccountReceivableWriteoff, BigDecimal> writeOffAmount;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Date> writeOffDate;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Date> cancelDate;
	public static volatile SingularAttribute<AccountReceivableWriteoff, AccountReceivable> accountReceivable;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Long> id;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Date> realCancelDate;
	public static volatile SingularAttribute<AccountReceivableWriteoff, AdminUser> canceler;
	public static volatile SingularAttribute<AccountReceivableWriteoff, Short> status;

}

