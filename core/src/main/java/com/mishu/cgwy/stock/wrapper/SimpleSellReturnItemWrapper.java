package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.SellReturnItem;
import com.mishu.cgwy.stock.domain.SellReturnStatus;
import com.mishu.cgwy.stock.domain.SellReturnType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
public class SimpleSellReturnItemWrapper {

    private Long id;
    private Long sellReturnId;
    private Long orderId;
    private Long skuId;
    private String skuName;
    private String restaurantName;
    private String receiver;
    private String telephone;
    private String depotName;
    private int capacityInBundle;
    private int quantity;
    private BigDecimal avgCost;
    private BigDecimal price;
    private BigDecimal amount;
    private Date returnDate;
    private String memo;
    private SellReturnReasonWrapper sellReturnReason;
    private SellReturnStatus sellReturnStatus;
    private SellReturnType sellReturnType;
    private String operator;

    public SimpleSellReturnItemWrapper() {
    }

    public SimpleSellReturnItemWrapper(SellReturnItem sellReturnItem) {
        this.id = sellReturnItem.getId();
        this.sellReturnId = sellReturnItem.getSellReturn().getId();
        this.orderId = sellReturnItem.getSellReturn().getOrder().getId();
        this.skuId = sellReturnItem.getSku().getId();
        this.skuName = sellReturnItem.getSku().getName();
        this.restaurantName = sellReturnItem.getSellReturn().getOrder().getRestaurant().getName();
        this.receiver = sellReturnItem.getSellReturn().getOrder().getRestaurant().getReceiver();
        this.telephone = sellReturnItem.getSellReturn().getOrder().getRestaurant().getTelephone();
        this.depotName = sellReturnItem.getSellReturn().getDepot().getName();
        this.capacityInBundle = sellReturnItem.getSku().getCapacityInBundle();
        this.quantity = sellReturnItem.getQuantity();
        this.avgCost = sellReturnItem.getAvgCost();
        this.price = sellReturnItem.getPrice() != null ? sellReturnItem.getPrice() : BigDecimal.ZERO;
        this.amount = this.price.multiply(new BigDecimal(this.quantity));
        this.returnDate = sellReturnItem.getSellReturn().getCreateDate();
        this.memo=sellReturnItem.getMemo();
        this.sellReturnReason = sellReturnItem.getSellReturnReason() != null ? new SellReturnReasonWrapper(sellReturnItem.getSellReturnReason()) : null;
        this.sellReturnStatus = SellReturnStatus.fromInt(sellReturnItem.getSellReturn().getStatus());
        this.sellReturnType = SellReturnType.fromInt(sellReturnItem.getSellReturn().getType());
        if (sellReturnItem.getSellReturn().getCreator() != null) {

            operator = sellReturnItem.getSellReturn().getCreator().getRealname();
        }
    }
}
