package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ChangeDetail.class)
public abstract class ChangeDetail_ {

	public static volatile SingularAttribute<ChangeDetail, AdminUser> submitter;
	public static volatile SingularAttribute<ChangeDetail, Date> submitDate;
	public static volatile SingularAttribute<ChangeDetail, Long> cityId;
	public static volatile SingularAttribute<ChangeDetail, Date> passDate;
	public static volatile SingularAttribute<ChangeDetail, String> content;
	public static volatile SingularAttribute<ChangeDetail, String> productName;
	public static volatile SingularAttribute<ChangeDetail, Long> objectType;
	public static volatile SingularAttribute<ChangeDetail, Long> organizationId;
	public static volatile SingularAttribute<ChangeDetail, Long> warehouseId;
	public static volatile SingularAttribute<ChangeDetail, AdminUser> verifier;
	public static volatile SingularAttribute<ChangeDetail, Long> id;
	public static volatile SingularAttribute<ChangeDetail, Long> objectId;
	public static volatile SingularAttribute<ChangeDetail, Long> status;

}

