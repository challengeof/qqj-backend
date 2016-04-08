package com.mishu.cgwy.admin.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AdminRole.class)
public abstract class AdminRole_ {

	public static volatile SingularAttribute<AdminRole, Boolean> organizationRole;
	public static volatile SingularAttribute<AdminRole, String> displayName;
	public static volatile SingularAttribute<AdminRole, String> name;
	public static volatile SingularAttribute<AdminRole, Long> id;
	public static volatile SetAttribute<AdminRole, AdminPermission> adminPermissions;

}

