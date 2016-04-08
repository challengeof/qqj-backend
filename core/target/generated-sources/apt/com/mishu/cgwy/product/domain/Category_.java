package com.mishu.cgwy.product.domain;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.MediaFile;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Category.class)
public abstract class Category_ {

	public static volatile SingularAttribute<Category, Boolean> showSecond;
	public static volatile SetAttribute<Category, City> cities;
	public static volatile SingularAttribute<Category, String> name;
	public static volatile SingularAttribute<Category, Category> parentCategory;
	public static volatile ListAttribute<Category, Category> childrenCategories;
	public static volatile SingularAttribute<Category, Long> id;
	public static volatile SingularAttribute<Category, MediaFile> mediaFile;
	public static volatile SingularAttribute<Category, Integer> status;

}

