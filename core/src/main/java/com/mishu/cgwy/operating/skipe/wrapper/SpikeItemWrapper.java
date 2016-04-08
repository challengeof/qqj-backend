package com.mishu.cgwy.operating.skipe.wrapper;

import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.domain.Spike;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.wrapper.CompleteSkuWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2016/1/8.
 */
@Data
public class SpikeItemWrapper {

    private Long id;   //秒杀活动子项的id

    private Long spikeId;   // 对应的活动id

    private CompleteSkuWrapper sku;  // 商品信息

    private BigDecimal originalPrice;//商品原价

    private BigDecimal price; //活动价格

    private Integer num; //总数量

    private Boolean bundle; //true 为打包， false 为单品

    private String unit; //单位

    private Integer takeNum; // 已经卖出的数量

    private Integer perMaxNum; // 每个客户限量

    private Integer spikeActiveStatus; //活动存活状态


    public static List<SpikeItemWrapper> toWrappers(List<SpikeItem> spikeItems){
        List<SpikeItemWrapper> itemWrappers= new ArrayList<>();
        for(SpikeItem item : spikeItems){
            SpikeItemWrapper itemWrapper = new SpikeItemWrapper(item,null);
            itemWrappers.add(itemWrapper);
        }
        return itemWrappers;
    }
    public SpikeItemWrapper() {}
    public SpikeItemWrapper(SpikeItem spikeItem, Sku sku, SpikeActivityState activityState) {
        this.id = spikeItem.getId();
        this.price = spikeItem.getPrice();
        this.num = spikeItem.getNum();
        this.takeNum = spikeItem.getTakeNum();

        this.bundle=spikeItem.isBundle();
        this.perMaxNum=spikeItem.getPerMaxNum();
        this.originalPrice=spikeItem.getOriginalPrice();

        if(null!=spikeItem.getSpike()) {
            this.spikeId = spikeItem.getSpike().getId();
        }
        if(null!=sku){
            this.sku=new CompleteSkuWrapper(spikeItem.getSku());
            this.unit=this.bundle?this.sku.getBundleUnit():this.sku.getSingleUnit();
        }
        if (activityState!=null) {
            this.spikeActiveStatus = activityState.val;
        }

    }

    public SpikeItemWrapper(SpikeItem spikeItem,SpikeActivityState activityState) {
        this.id = spikeItem.getId();
        this.price = spikeItem.getPrice();
        this.num = spikeItem.getNum();
        this.takeNum = spikeItem.getTakeNum();

        this.bundle = spikeItem.isBundle();
        this.perMaxNum = spikeItem.getPerMaxNum();

        this.originalPrice = spikeItem.getOriginalPrice();

        if (null != spikeItem.getSpike()) {
            this.spikeId = spikeItem.getSpike().getId();
        }
        if (null != spikeItem.getSku()) {
            this.sku = new CompleteSkuWrapper(spikeItem.getSku());
            this.unit = this.bundle ? this.sku.getBundleUnit() : this.sku.getSingleUnit();
        }

        if (activityState!=null){
            this.spikeActiveStatus = activityState.val;
        }

    }

}
