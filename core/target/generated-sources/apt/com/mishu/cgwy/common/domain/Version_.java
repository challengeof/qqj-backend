package com.mishu.cgwy.common.domain;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Version.class)
public abstract class Version_ {

	public static volatile SingularAttribute<Version, MediaFile> file;
	public static volatile SingularAttribute<Version, String> comment;
	public static volatile SingularAttribute<Version, Long> id;
	public static volatile SingularAttribute<Version, Integer> forceUpdate;
	public static volatile SingularAttribute<Version, String> versionName;
	public static volatile SingularAttribute<Version, Integer> type;
	public static volatile SingularAttribute<Version, Integer> versionCode;

}

