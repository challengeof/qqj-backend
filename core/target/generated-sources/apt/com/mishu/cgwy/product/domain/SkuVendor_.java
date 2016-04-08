package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.inventory.domain.Vendor;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(SkuVendor.class)
public abstract class SkuVendor_ {

	public static volatile SingularAttribute<SkuVendor, City> city;
	public static volatile SingularAttribute<SkuVendor, Vendor> vendor;
	public static volatile SingularAttribute<SkuVendor, Long> id;
	public static volatile SingularAttribute<SkuVendor, Sku> sku;

}

