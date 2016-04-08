package com.mishu.cgwy.car.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.stock.domain.Depot;
import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Car.class)
public abstract class Car_ {

	public static volatile SingularAttribute<Car, BigDecimal> cubic;
	public static volatile SingularAttribute<Car, Depot> depot;
	public static volatile SingularAttribute<Car, City> city;
	public static volatile SingularAttribute<Car, BigDecimal> weight;
	public static volatile SingularAttribute<Car, String> taxingPoint;
	public static volatile SingularAttribute<Car, String> source;
	public static volatile SingularAttribute<Car, String> licencePlateNumber;
	public static volatile SingularAttribute<Car, BigDecimal> vehicleLength;
	public static volatile SingularAttribute<Car, AdminUser> adminUser;
	public static volatile SingularAttribute<Car, BigDecimal> vehicleWidth;
	public static volatile SingularAttribute<Car, String> name;
	public static volatile SingularAttribute<Car, BigDecimal> vehicleHeight;
	public static volatile SingularAttribute<Car, Integer> vehicleModel;
	public static volatile SingularAttribute<Car, Long> id;
	public static volatile SingularAttribute<Car, String> expenses;
	public static volatile SingularAttribute<Car, Integer> status;

}

