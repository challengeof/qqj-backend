package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockOut;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountPayable.class)
public abstract class AccountPayable_ {

	public static volatile SingularAttribute<AccountPayable, AdminUser> writeOffer;
	public static volatile SingularAttribute<AccountPayable, StockOut> stockOut;
	public static volatile SingularAttribute<AccountPayable, BigDecimal> amount;
	public static volatile SingularAttribute<AccountPayable, BigDecimal> writeOffAmount;
	public static volatile SingularAttribute<AccountPayable, Date> writeOffDate;
	public static volatile SingularAttribute<AccountPayable, StockIn> stockIn;
	public static volatile SingularAttribute<AccountPayable, Vendor> vendor;
	public static volatile SingularAttribute<AccountPayable, Long> id;
	public static volatile SingularAttribute<AccountPayable, Short> type;
	public static volatile ListAttribute<AccountPayable, AccountPayableItem> accountPayableItems;
	public static volatile SingularAttribute<AccountPayable, Date> createDate;
	public static volatile SingularAttribute<AccountPayable, Short> status;

}

