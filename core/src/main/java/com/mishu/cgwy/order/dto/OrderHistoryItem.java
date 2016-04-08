package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.constants.Constants;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by kaicheng on 3/27/15.
 */
@Deprecated
@Data
public class OrderHistoryItem {
    private Long id;
    private String url;
    private String productNumber;
    private String name;
    private BigDecimal price;
    private BigDecimal marketPrice;
    private Integer count;
    private Integer total;
    private Integer maxBuy;
    private Integer last;

    public OrderHistoryItem() {
    }

    public OrderHistoryItem(OrderItem orderItem) {

        this.setId(orderItem.getSku().getProduct().getId());
        this.setProductNumber(String.valueOf(orderItem.getSku().getId()));
        this.setName(orderItem.getSku().getProduct().getName());
        this.setPrice(orderItem.getPrice());
        this.setMarketPrice(orderItem.getSku().getMarketPrice());
        //同一商品的累计，到facade里再合并
        this.setCount(orderItem.getCountQuantity());
        this.setTotal(orderItem.getCountQuantity());
        this.setMaxBuy(Constants.MAX_BUY);
        //上次购买
        this.setLast(orderItem.getCountQuantity());

    }
}
