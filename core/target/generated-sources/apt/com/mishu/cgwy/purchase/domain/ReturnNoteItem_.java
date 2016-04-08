package com.mishu.cgwy.purchase.domain;

import java.math.BigDecimal;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(ReturnNoteItem.class)
public abstract class ReturnNoteItem_ {

	public static volatile SingularAttribute<ReturnNoteItem, ReturnNote> returnNote;
	public static volatile SingularAttribute<ReturnNoteItem, Integer> returnQuantity;
	public static volatile SingularAttribute<ReturnNoteItem, Long> id;
	public static volatile SingularAttribute<ReturnNoteItem, BigDecimal> returnPrice;
	public static volatile SingularAttribute<ReturnNoteItem, PurchaseOrderItem> purchaseOrderItem;

}

