package com.mishu.cgwy.job.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.domain.Sku;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(UpdateDynamicSkuPriceJob.class)
public abstract class UpdateDynamicSkuPriceJob_ {

	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, AdminUser> adminUser;
	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, ChangeDetail> changeDetail;
	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, Long> id;
	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, Date> effectTime;
	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, Sku> sku;
	public static volatile SingularAttribute<UpdateDynamicSkuPriceJob, Warehouse> warehouse;

}

