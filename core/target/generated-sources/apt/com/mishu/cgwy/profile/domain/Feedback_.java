package com.mishu.cgwy.profile.domain;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.inventory.domain.Vendor;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Feedback.class)
public abstract class Feedback_ {

	public static volatile SingularAttribute<Feedback, MediaFile> file;
	public static volatile SingularAttribute<Feedback, Date> submitTime;
	public static volatile SingularAttribute<Feedback, String> feedbackDescription;
	public static volatile SingularAttribute<Feedback, Vendor> vendor;
	public static volatile SingularAttribute<Feedback, Date> updateTime;
	public static volatile SingularAttribute<Feedback, Long> id;
	public static volatile SingularAttribute<Feedback, Short> type;
	public static volatile SingularAttribute<Feedback, Customer> customer;
	public static volatile SingularAttribute<Feedback, Integer> status;

}

