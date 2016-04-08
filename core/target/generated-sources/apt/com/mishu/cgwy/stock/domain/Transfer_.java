package com.mishu.cgwy.stock.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.order.domain.CutOrder;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Transfer.class)
public abstract class Transfer_ {

	public static volatile SingularAttribute<Transfer, AdminUser> creator;
	public static volatile SingularAttribute<Transfer, CutOrder> cutOrder;
	public static volatile SingularAttribute<Transfer, Depot> sourceDepot;
	public static volatile SingularAttribute<Transfer, AdminUser> auditor;
	public static volatile SingularAttribute<Transfer, String> remark;
	public static volatile ListAttribute<Transfer, TransferItem> transferItems;
	public static volatile SingularAttribute<Transfer, Long> id;
	public static volatile SingularAttribute<Transfer, Depot> targetDepot;
	public static volatile SingularAttribute<Transfer, Date> createDate;
	public static volatile SingularAttribute<Transfer, Date> auditDate;
	public static volatile SingularAttribute<Transfer, Short> status;
	public static volatile SingularAttribute<Transfer, String> opinion;

}

