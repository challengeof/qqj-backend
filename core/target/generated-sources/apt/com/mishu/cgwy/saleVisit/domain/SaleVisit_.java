package com.mishu.cgwy.saleVisit.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.domain.Restaurant;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SaleVisit.class)
public abstract class SaleVisit_ {

	public static volatile SingularAttribute<SaleVisit, AdminUser> creator;
	public static volatile SingularAttribute<SaleVisit, Restaurant> restaurant;
	public static volatile SingularAttribute<SaleVisit, String> visitSolutions;
	public static volatile SingularAttribute<SaleVisit, Integer> visitStage;
	public static volatile SingularAttribute<SaleVisit, String> remark;
	public static volatile SingularAttribute<SaleVisit, Date> visitTime;
	public static volatile SingularAttribute<SaleVisit, Date> createTime;
	public static volatile SingularAttribute<SaleVisit, String> visitTroubles;
	public static volatile SingularAttribute<SaleVisit, Integer> nextVisitStage;
	public static volatile SingularAttribute<SaleVisit, String> intentionProductions;
	public static volatile SingularAttribute<SaleVisit, Long> id;
	public static volatile SingularAttribute<SaleVisit, String> visitPurposes;
	public static volatile SingularAttribute<SaleVisit, Date> nextVisitTime;

}

