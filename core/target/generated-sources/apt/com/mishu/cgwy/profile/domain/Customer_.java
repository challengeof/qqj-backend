package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.score.domain.Score;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Customer.class)
public abstract class Customer_ {

	public static volatile SingularAttribute<Customer, Long> referrerId;
	public static volatile SingularAttribute<Customer, City> city;
	public static volatile ListAttribute<Customer, Restaurant> restaurant;
	public static volatile SingularAttribute<Customer, String> telephone;
	public static volatile SingularAttribute<Customer, Date> adminUserFollowBegin;
	public static volatile SingularAttribute<Customer, Date> adminUserFollowEnd;
	public static volatile SingularAttribute<Customer, Integer> followUpStatus;
	public static volatile SingularAttribute<Customer, Integer> versionCode;
	public static volatile SingularAttribute<Customer, Boolean> enabled;
	public static volatile SingularAttribute<Customer, AdminUser> devUser;
	public static volatile SingularAttribute<Customer, Date> lastLoginTime;
	public static volatile SingularAttribute<Customer, Score> score;
	public static volatile SingularAttribute<Customer, String> password;
	public static volatile SingularAttribute<Customer, AdminUser> adminUser;
	public static volatile SingularAttribute<Customer, Date> createTime;
	public static volatile SingularAttribute<Customer, Block> block;
	public static volatile SingularAttribute<Customer, Long> id;
	public static volatile SingularAttribute<Customer, String> username;

}

