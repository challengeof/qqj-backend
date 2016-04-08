package com.mishu.cgwy.accounting.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.inventory.domain.Vendor;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Payment.class)
public abstract class Payment_ {

	public static volatile SingularAttribute<Payment, BigDecimal> amount;
	public static volatile SingularAttribute<Payment, AdminUser> creator;
	public static volatile SingularAttribute<Payment, Date> cancelDate;
	public static volatile SingularAttribute<Payment, City> city;
	public static volatile SingularAttribute<Payment, Vendor> vendor;
	public static volatile SingularAttribute<Payment, String> remark;
	public static volatile SingularAttribute<Payment, Long> id;
	public static volatile SingularAttribute<Payment, CollectionPaymentMethod> collectionPaymentMethod;
	public static volatile SingularAttribute<Payment, Date> payDate;
	public static volatile SingularAttribute<Payment, Date> createDate;
	public static volatile SingularAttribute<Payment, Short> status;
	public static volatile SingularAttribute<Payment, AdminUser> canceler;

}

