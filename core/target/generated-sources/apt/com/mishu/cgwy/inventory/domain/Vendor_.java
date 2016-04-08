package com.mishu.cgwy.inventory.domain;

import com.mishu.cgwy.accounting.domain.VendorAccount;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.organization.domain.Organization;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Vendor.class)
public abstract class Vendor_ {

	public static volatile SingularAttribute<Vendor, String> address;
	public static volatile SingularAttribute<Vendor, City> city;
	public static volatile SingularAttribute<Vendor, String> telephone;
	public static volatile SingularAttribute<Vendor, String> password;
	public static volatile SingularAttribute<Vendor, String> contact;
	public static volatile SingularAttribute<Vendor, Organization> organization;
	public static volatile SingularAttribute<Vendor, String> name;
	public static volatile SingularAttribute<Vendor, Vendor> paymentVendor;
	public static volatile SingularAttribute<Vendor, Boolean> defaultVendor;
	public static volatile SingularAttribute<Vendor, Long> id;
	public static volatile SingularAttribute<Vendor, String> brand;
	public static volatile SingularAttribute<Vendor, String> email;
	public static volatile SingularAttribute<Vendor, VendorAccount> account;
	public static volatile SingularAttribute<Vendor, String> username;

}

