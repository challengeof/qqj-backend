package com.mishu.cgwy.score.domain;

import com.mishu.cgwy.profile.domain.Customer;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Score.class)
public abstract class Score_ {

	public static volatile ListAttribute<Score, ScoreLog> scoreLogs;
	public static volatile SingularAttribute<Score, Date> createTime;
	public static volatile SingularAttribute<Score, Long> exchangeScore;
	public static volatile SingularAttribute<Score, Date> updateTime;
	public static volatile SingularAttribute<Score, Long> id;
	public static volatile SingularAttribute<Score, Long> totalScore;
	public static volatile SingularAttribute<Score, Customer> customer;

}

