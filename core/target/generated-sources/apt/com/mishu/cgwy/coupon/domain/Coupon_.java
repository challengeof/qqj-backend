package com.mishu.cgwy.coupon.domain;

import com.mishu.cgwy.product.domain.Sku;
import java.math.BigDecimal;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Coupon.class)
public abstract class Coupon_ {

	public static volatile SingularAttribute<Coupon, Integer> quantity;
	public static volatile SingularAttribute<Coupon, Integer> couponRestriction;
	public static volatile SingularAttribute<Coupon, Date> start;
	public static volatile SingularAttribute<Coupon, String> description;
	public static volatile SingularAttribute<Coupon, BigDecimal> discount;
	public static volatile SingularAttribute<Coupon, String> remark;
	public static volatile SingularAttribute<Coupon, Integer> beginningDays;
	public static volatile SingularAttribute<Coupon, String> useRule;
	public static volatile SingularAttribute<Coupon, Long> score;
	public static volatile SingularAttribute<Coupon, String> ruleValue;
	public static volatile SingularAttribute<Coupon, Integer> couponConstants;
	public static volatile SingularAttribute<Coupon, Date> createTime;
	public static volatile SingularAttribute<Coupon, Integer> sendCouponQuantity;
	public static volatile SingularAttribute<Coupon, String> name;
	public static volatile SingularAttribute<Coupon, Date> end;
	public static volatile SingularAttribute<Coupon, Long> id;
	public static volatile SingularAttribute<Coupon, Integer> periodOfValidity;
	public static volatile SingularAttribute<Coupon, Sku> sku;
	public static volatile SingularAttribute<Coupon, Date> deadline;
	public static volatile SingularAttribute<Coupon, String> sendRule;

}

