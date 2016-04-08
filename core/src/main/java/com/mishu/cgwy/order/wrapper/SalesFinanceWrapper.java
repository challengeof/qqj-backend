package com.mishu.cgwy.order.wrapper;

import lombok.Data;

import java.math.BigDecimal;

import com.mishu.cgwy.order.domain.SalesFinance;
import com.mishu.cgwy.utils.NumberUtils;

@Data
public class SalesFinanceWrapper {
	
	private Long sku;
	
	private String name; 
	
	private Long orderQuantity = 0l;
	
	private BigDecimal salesUnitPrice = BigDecimal.ZERO;
	
	private Long returnedQuantity = 0l;
	
	private BigDecimal salesTotal = BigDecimal.ZERO;
	
	private Integer stock = 0;
	
	private Long stockUsed = 0l;
	
	private BigDecimal avgPrice = BigDecimal.ZERO;
	
	private Long purchaseUsed = 0l;
	
	private BigDecimal purchasePrice = BigDecimal.ZERO;
	
	private BigDecimal spendingTotal = BigDecimal.ZERO;
	
	private String grossMargins = "--.--%";
	
	public SalesFinanceWrapper(SalesFinance salesFinance) {
		this.sku = salesFinance.getSku().getId();
		this.name = salesFinance.getSku().getName();
		this.orderQuantity = salesFinance.getOrderQuantity();
		this.salesUnitPrice = salesFinance.getSalesUnitPrice();
		this.returnedQuantity = salesFinance.getReturnedQuantity();
		this.salesTotal = salesFinance.getSalesTotal();
		this.stock = salesFinance.getStock();
		this.stockUsed = salesFinance.getStockUsed();
		this.avgPrice = salesFinance.getAvgPrice();
		this.purchaseUsed = salesFinance.getPurchaseUsed();
		this.purchasePrice = salesFinance.getPurchasePrice();
		this.spendingTotal = salesFinance.getSpendingTotal();
		this.grossMargins = NumberUtils.numberFormat(salesFinance.getGrossMargins());
	}
}
