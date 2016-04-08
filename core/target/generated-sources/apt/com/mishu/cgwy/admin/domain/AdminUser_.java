package com.mishu.cgwy.admin.domain;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.stock.domain.Depot;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(AdminUser.class)
public abstract class AdminUser_ {

	public static volatile SetAttribute<AdminUser, AdminRole> adminRoles;
	public static volatile SetAttribute<AdminUser, City> cities;
	public static volatile SingularAttribute<AdminUser, Boolean> globalAdmin;
	public static volatile SetAttribute<AdminUser, Block> blocks;
	public static volatile SetAttribute<AdminUser, Warehouse> warehouses;
	public static volatile SingularAttribute<AdminUser, String> telephone;
	public static volatile SingularAttribute<AdminUser, Boolean> enabled;
	public static volatile SingularAttribute<AdminUser, String> realname;
	public static volatile SingularAttribute<AdminUser, String> password;
	public static volatile SetAttribute<AdminUser, Depot> depots;
	public static volatile SetAttribute<AdminUser, Organization> organizations;
	public static volatile SetAttribute<AdminUser, City> depotCities;
	public static volatile SingularAttribute<AdminUser, Long> id;
	public static volatile SingularAttribute<AdminUser, String> username;

}

