package com.mishu.cgwy.purchase.domain;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.stock.domain.Depot;
import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.ListAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ReturnNote.class)
public abstract class ReturnNote_ {

	public static volatile ListAttribute<ReturnNote, ReturnNoteItem> returnNoteItems;
	public static volatile SingularAttribute<ReturnNote, AdminUser> creator;
	public static volatile SingularAttribute<ReturnNote, Depot> depot;
	public static volatile SingularAttribute<ReturnNote, Date> createTime;
	public static volatile SingularAttribute<ReturnNote, Date> auditTime;
	public static volatile SingularAttribute<ReturnNote, AdminUser> auditor;
	public static volatile SingularAttribute<ReturnNote, PurchaseOrder> purchaseOrder;
	public static volatile SingularAttribute<ReturnNote, String> remark;
	public static volatile SingularAttribute<ReturnNote, Long> id;
	public static volatile SingularAttribute<ReturnNote, Short> type;
	public static volatile SingularAttribute<ReturnNote, Short> status;
	public static volatile SingularAttribute<ReturnNote, String> opinion;

}

