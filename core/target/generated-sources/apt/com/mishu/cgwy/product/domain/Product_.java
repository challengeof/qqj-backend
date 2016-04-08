package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.organization.domain.Organization;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Product.class)
public abstract class Product_ {

	public static volatile ListAttribute<Product, Sku> skus;
	public static volatile ListAttribute<Product, MediaFile> mediaFiles;
	public static volatile SingularAttribute<Product, String> specification;
	public static volatile SingularAttribute<Product, String> barCode;
	public static volatile SingularAttribute<Product, Boolean> discrete;
	public static volatile SingularAttribute<Product, Organization> organization;
	public static volatile SingularAttribute<Product, String> name;
	public static volatile SingularAttribute<Product, String> details;
	public static volatile SingularAttribute<Product, Long> id;
	public static volatile SingularAttribute<Product, Category> category;
	public static volatile SingularAttribute<Product, Integer> shelfLife;
	public static volatile SingularAttribute<Product, Brand> brand;
	public static volatile SingularAttribute<Product, String> properties;

}

