package com.mishu.cgwy.product.wrapper;

import lombok.Data;

/**
 * Created by king-ck on 2016/1/22.
 */
@Data
public class CartSpikeOutOfInfoWrapper {
    private Long spikeItemId;
    private Long spikeCityId;
    private String skuName;
    private Integer spikeActivityState; // 对应秒杀活动存活状态  0已失效 1为开始 2进行中 3已结束
    private Integer num;             //总数量
    private Integer takeNum;         // spikeItemId 已卖出的数量
    private Integer perMaxNum;       // 每个用户最大购买量
    private Integer customerTakeNum;// 当前用户已经购买的数量

    private Integer currentBuyQuantity; // 请求中此商品的购买数量

    public CartSpikeOutOfInfoWrapper(){}
    public CartSpikeOutOfInfoWrapper(Long spikeCityId,Long spikeItemId, String skuName, Integer spikeActivityState, Integer num, Integer takeNum, Integer perMaxNum, Integer customerTakeNum , Integer currentBuyQuantity) {
        this.spikeCityId=spikeCityId;
        this.spikeItemId = spikeItemId;
        this.skuName = skuName;
        this.spikeActivityState = spikeActivityState;
        this.num = num;
        this.takeNum = takeNum;
        this.perMaxNum = perMaxNum;
        this.customerTakeNum = customerTakeNum;
        this.currentBuyQuantity = currentBuyQuantity;

    }
}
