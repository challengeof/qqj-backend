package com.mishu.cgwy.score.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.stock.domain.StockOut;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ScoreLog.class)
public abstract class ScoreLog_ {

	public static volatile SingularAttribute<ScoreLog, StockOut> stockOut;
	public static volatile SingularAttribute<ScoreLog, Score> score;
	public static volatile SingularAttribute<ScoreLog, Coupon> coupon;
	public static volatile SingularAttribute<ScoreLog, Date> createTime;
	public static volatile SingularAttribute<ScoreLog, AdminUser> sender;
	public static volatile SingularAttribute<ScoreLog, Long> integral;
	public static volatile SingularAttribute<ScoreLog, Integer> count;
	public static volatile SingularAttribute<ScoreLog, String> remark;
	public static volatile SingularAttribute<ScoreLog, Long> id;
	public static volatile SingularAttribute<ScoreLog, Order> order;
	public static volatile SingularAttribute<ScoreLog, Customer> customer;
	public static volatile SingularAttribute<ScoreLog, Integer> status;

}

