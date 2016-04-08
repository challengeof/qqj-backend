package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.order.domain.OrderItem;
import lombok.Data;

@Data
public class CartListData {
    private Long id;
    private String url;
    private float price;
    private float marketPrice;
    private String productNumber;
    private int maxBuy;
    private String name;
    private int number;

    public CartListData(OrderItem oi) {
        setId(oi.getId());
        setName(oi.getSku().getProduct().getName());
        setNumber(oi.getCountQuantity());
        setPrice(oi.getPrice().floatValue());
        setMarketPrice(oi.getSku().getMarketPrice().floatValue());
        setProductNumber(String.valueOf(oi.getSku().getId()));


//        final MediaFile mediaFile = oi.getSku().getProduct().getMediaFile();
//        if (mediaFile != null) {
//            setUrl(mediaFile.getUrl());
//
//        }
    }

    public CartListData() {
    }

}
