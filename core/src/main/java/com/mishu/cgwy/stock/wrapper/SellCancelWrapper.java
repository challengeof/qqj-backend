package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.constants.SellCancelReason;
import com.mishu.cgwy.stock.domain.SellCancel;
import com.mishu.cgwy.stock.domain.SellCancelType;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wangwei on 15/10/13.
 */
@Data
public class SellCancelWrapper {

    private Long sellCancelId;
    private Long orderId;
    private String depotName;
    private String sellCancelType;
    private Long restaurantId;
    private String restaurantName;
    private String receiver;
    private String telephone;
    private BigDecimal amount;
    private Date cancelDate;
    private Date submitDate;
    private String operator;
    private String reason;

    private List<SellCancelItemWrapper> sellCancelItems = new ArrayList<>();

    public SellCancelWrapper() {
    }

    public SellCancelWrapper(SellCancel sellCancel) {
        this.sellCancelId = sellCancel.getId();
        this.orderId = sellCancel.getOrder().getId();
        this.depotName = sellCancel.getOrder().getCustomer().getBlock().getWarehouse().getDepot().getName();
        this.sellCancelType = SellCancelType.fromInt(sellCancel.getType()).getName();
        this.restaurantId = sellCancel.getOrder().getRestaurant().getId();
        this.restaurantName = sellCancel.getOrder().getRestaurant().getName();
        this.receiver = sellCancel.getOrder().getRestaurant().getReceiver();
        this.telephone = sellCancel.getOrder().getRestaurant().getTelephone();
        this.amount = sellCancel.getAmount();
        this.cancelDate = sellCancel.getCreateDate();
        this.submitDate = sellCancel.getOrder().getSubmitDate();
        if (sellCancel.getCreator() != null) {
            operator = sellCancel.getCreator().getRealname();
        }
        if (sellCancel.getCustomer() != null) {
            operator = sellCancel.getOrder().getRestaurant().getReceiver();
        }
        if (!sellCancel.getSellCancelItems().isEmpty() && sellCancel.getSellCancelItems().get(0).getReason() != null) {
            this.reason = SellCancelReason.fromInt(sellCancel.getSellCancelItems().get(0).getReason()).getName();
        }
    }
}
