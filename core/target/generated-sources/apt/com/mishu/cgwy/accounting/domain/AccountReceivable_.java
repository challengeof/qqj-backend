package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockOut;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AccountReceivable.class)
public abstract class AccountReceivable_ {

	public static volatile SingularAttribute<AccountReceivable, AdminUser> writeOffer;
	public static volatile SingularAttribute<AccountReceivable, StockOut> stockOut;
	public static volatile SingularAttribute<AccountReceivable, BigDecimal> amount;
	public static volatile SingularAttribute<AccountReceivable, BigDecimal> writeOffAmount;
	public static volatile SingularAttribute<AccountReceivable, Date> writeOffDate;
	public static volatile SingularAttribute<AccountReceivable, StockIn> stockIn;
	public static volatile ListAttribute<AccountReceivable, AccountReceivableItem> accountReceivableItems;
	public static volatile SingularAttribute<AccountReceivable, Restaurant> restaurant;
	public static volatile SingularAttribute<AccountReceivable, Long> id;
	public static volatile SingularAttribute<AccountReceivable, Integer> type;
	public static volatile SingularAttribute<AccountReceivable, Date> createDate;
	public static volatile SingularAttribute<AccountReceivable, Integer> status;

}

