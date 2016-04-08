package com.mishu.cgwy.utils;

import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.dto.OrderDetail;
import com.mishu.cgwy.order.wrapper.OrderItemWrapper;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.order.wrapper.SimpleOrderItemWrapper;
import com.mishu.cgwy.promotion.domain.PromotableItems;
import com.mishu.cgwy.promotion.domain.Promotion;

import java.util.ArrayList;
import java.util.List;

/**
 * User: xudong
 * Date: 5/4/15
 * Time: 8:00 PM
 */
public class LegacyOrderUtils {
    public static List<OrderDetail> getSubItems(Order order) {
        List<OrderDetail> ods = new ArrayList<OrderDetail>();
        for (OrderItem ot : order.getOrderItems()) {
            if (ot.getPrice().intValue() != 0 && ot.getTotalPrice().intValue() != 0) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProductNumber(String.valueOf(ot.getSku().getId()));


                /*final MediaFile mediaFile = ot.getSku().getProduct().getMediaFile();
                if (mediaFile != null) {
                    orderDetail
                            .setUrl(mediaFile.getUrl());
                }*/
                orderDetail.setPrice(ot.getPrice().floatValue());
                orderDetail.setNumber(ot.getCountQuantity());
                orderDetail.setName(ot.getSku().getName());
                orderDetail.setProductId(ot.getSku().getId());
                ods.add(orderDetail);
            }
        }

        for (Promotion promotion : order.getPromotions()) {
            final PromotableItems promotableItems = promotion.getPromotableItems();
            if (promotableItems != null && promotableItems.getSku()!= null) {
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setProductNumber(String.valueOf(promotableItems.getSku().getId()));
                /*final MediaFile mediaFile = promotableItems.getSku().getProduct().getMediaFile();
                if (mediaFile != null) {
                    orderDetail
                            .setUrl(mediaFile.getUrl());
                }*/
                orderDetail.setPrice(0);
                orderDetail.setNumber(promotableItems.getQuantity());
                orderDetail.setName(promotableItems.getSku().getName());
                orderDetail.setProductId(promotableItems.getSku().getId());
                ods.add(orderDetail);
            }
        }

        return ods;
    }

    public static int countTotal(OrderWrapper order) {
        int total = 0;
        for (SimpleOrderItemWrapper ot : order.getOrderItems())
            total += ot.getQuantity();
        return total;
    }

    public static int countType(OrderWrapper orderWrapper) {
        return orderWrapper.getOrderItems().size();
    }
}
