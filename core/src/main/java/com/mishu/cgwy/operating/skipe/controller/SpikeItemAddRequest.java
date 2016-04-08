package com.mishu.cgwy.operating.skipe.controller;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2016/1/8.
 */
@Data
public class SpikeItemAddRequest {

    private Long spikeItemId;

    private Long skuId;
    private Double originalPrice;
    private Double price;
    private Integer num;
    private Integer perMaxNum;
    private Boolean bundle;

    public static SpikeItem toSpikeItem(SpikeItemAddRequest request){
        SpikeItem spikeItem = new SpikeItem();
        spikeItem.setBundle(request.getBundle());
        spikeItem.setNum(request.getNum());
        spikeItem.setPerMaxNum(request.getPerMaxNum());
        spikeItem.setPrice(new BigDecimal(request.getPrice()));
        spikeItem.setOriginalPrice(new BigDecimal(request.getOriginalPrice()));

        spikeItem.setId(request.getSpikeItemId());

        Sku sku=new Sku();
        sku.setId(request.getSkuId());
        spikeItem.setSku(sku);
        return spikeItem;
    }

    public static SpikeItem[] toSpikeItem(List<SpikeItemAddRequest> request){
        List<SpikeItem> sitems = new ArrayList<>();
        for(SpikeItemAddRequest itemRequest : request){
            SpikeItem sItem = toSpikeItem(itemRequest);
            sitems.add(sItem);
        }
        return sitems.toArray(new SpikeItem[]{});
    }

}
