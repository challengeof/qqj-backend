package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.constants.SellCancelReason;
import com.mishu.cgwy.stock.domain.SellCancelItem;
import com.mishu.cgwy.stock.domain.SellCancelType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangwei on 15/10/16.
 */
@Data
public class SimpleSellCancelItemWrapper {

    private Long orderId;
    private Long skuId;
    private String skuName;
    private String sellCancelType;
    private String restaurantName;
    private String receiver;
    private String telephone;
    private int quantity;
    private BigDecimal price;
    private BigDecimal amount;
    private Date cancelDate;
    private String operator;
    private String reason;

    public SimpleSellCancelItemWrapper() {

    }

    public SimpleSellCancelItemWrapper(SellCancelItem sellCancelItem) {
        this.orderId = sellCancelItem.getSellCancel().getOrder().getId();
        this.skuId = sellCancelItem.getSku().getId();
        this.skuName = sellCancelItem.getSku().getName();
        this.sellCancelType = SellCancelType.fromInt(sellCancelItem.getSellCancel().getType()).getName();
        this.restaurantName = sellCancelItem.getSellCancel().getOrder().getRestaurant().getName();
        this.receiver = sellCancelItem.getSellCancel().getOrder().getRestaurant().getReceiver();
        this.telephone = sellCancelItem.getSellCancel().getOrder().getRestaurant().getTelephone();
        this.quantity = sellCancelItem.getQuantity();
        this.price = sellCancelItem.getPrice() != null ? sellCancelItem.getPrice() : BigDecimal.ZERO;
        this.amount = sellCancelItem.getPrice().multiply(new BigDecimal(this.quantity));
        this.cancelDate = sellCancelItem.getSellCancel().getCreateDate();
        if (sellCancelItem.getSellCancel().getCreator() != null) {
            this.operator = sellCancelItem.getSellCancel().getCreator().getRealname();
        }
        if (sellCancelItem.getSellCancel().getCustomer() != null) {
            operator = sellCancelItem.getSellCancel().getOrder().getRestaurant().getReceiver();
        }
        if (sellCancelItem.getReason() != null) {
            this.reason = SellCancelReason.fromInt(sellCancelItem.getReason()).getName();
        }
    }
}
