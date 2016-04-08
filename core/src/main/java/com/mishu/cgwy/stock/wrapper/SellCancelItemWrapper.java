package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.constants.SellCancelReason;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.stock.domain.SellCancelItem;
import com.mishu.cgwy.stock.domain.SellCancelType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by wangwei on 15/10/16.
 */
@Data
public class SellCancelItemWrapper {

    private Long id;

    private int quantity;

    private SimpleSkuWrapper sku;

    private BigDecimal price;

    private boolean bundle;

    private SellCancelType type;

    private String memo;

    private String operator;

    private String reason;

    private Date createDate;

    public SellCancelItemWrapper() {

    }

    public SellCancelItemWrapper(SellCancelItem sellCancelItem) {
        this.id = sellCancelItem.getId();
        this.quantity = sellCancelItem.getQuantity();
        this.sku = new SimpleSkuWrapper(sellCancelItem.getSku());
        this.price = sellCancelItem.getPrice();
        this.bundle = sellCancelItem.isBundle();
        this.type = SellCancelType.fromInt(sellCancelItem.getSellCancel().getType());
        this.memo = sellCancelItem.getMemo();
        if (sellCancelItem.getSellCancel().getCreator() != null) {
            this.operator = sellCancelItem.getSellCancel().getCreator().getRealname();
        }
        if (sellCancelItem.getSellCancel().getCustomer() != null) {
            operator = sellCancelItem.getSellCancel().getOrder().getRestaurant().getReceiver();
        }
        if (sellCancelItem.getReason() != null) {
            this.reason = SellCancelReason.fromInt(sellCancelItem.getReason()).getName();
        }
        createDate = sellCancelItem.getSellCancel().getCreateDate();
    }
}
