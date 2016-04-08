package com.mishu.cgwy.promotion.domain;

import com.mishu.cgwy.organization.domain.Organization;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Promotion.class)
public abstract class Promotion_ {

	public static volatile SingularAttribute<Promotion, Integer> limitedQuantity;
	public static volatile SingularAttribute<Promotion, Integer> promotionConstants;
	public static volatile SingularAttribute<Promotion, Date> start;
	public static volatile SingularAttribute<Promotion, String> description;
	public static volatile SingularAttribute<Promotion, String> rule;
	public static volatile SingularAttribute<Promotion, BigDecimal> discount;
	public static volatile SingularAttribute<Promotion, Integer> type;
	public static volatile SingularAttribute<Promotion, Boolean> enabled;
	public static volatile SingularAttribute<Promotion, String> ruleValue;
	public static volatile SingularAttribute<Promotion, Date> createTime;
	public static volatile SingularAttribute<Promotion, Organization> organization;
	public static volatile SingularAttribute<Promotion, Date> end;
	public static volatile SingularAttribute<Promotion, Long> id;
	public static volatile SingularAttribute<Promotion, Integer> quantitySold;
	public static volatile SingularAttribute<Promotion, PromotableItems> promotableItems;

}

