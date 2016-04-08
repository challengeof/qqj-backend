package com.mishu.cgwy.organization.domain;

import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Organization.class)
public abstract class Organization_ {

	public static volatile SingularAttribute<Organization, Boolean> selfSupport;
	public static volatile SetAttribute<Organization, City> cities;
	public static volatile SetAttribute<Organization, Block> blocks;
	public static volatile SingularAttribute<Organization, String> name;
	public static volatile SetAttribute<Organization, Warehouse> warehouses;
	public static volatile SingularAttribute<Organization, String> telephone;
	public static volatile SingularAttribute<Organization, Long> id;
	public static volatile SingularAttribute<Organization, Boolean> enabled;
	public static volatile SingularAttribute<Organization, Date> createDate;

}

