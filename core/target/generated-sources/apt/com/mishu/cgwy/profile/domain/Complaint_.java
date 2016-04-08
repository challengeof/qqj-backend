package com.mishu.cgwy.profile.domain;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Complaint.class)
public abstract class Complaint_ {

	public static volatile SingularAttribute<Complaint, Date> createTime;
	public static volatile SingularAttribute<Complaint, Long> customerId;
	public static volatile SingularAttribute<Complaint, Long> adminId;
	public static volatile SingularAttribute<Complaint, Long> id;
	public static volatile SingularAttribute<Complaint, Integer> complaintNumber;

}

