package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.order.constants.CartSkuType;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.wrapper.CartSpikeOutOfInfoWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import lombok.Data;

/**
 * Created by king-ck on 2016/1/12.
 */
@Data
public class CartSkuStockOutWrapper {

    private Integer cartSkuType ;// 1 请求  2秒杀
    private CartSpikeOutOfInfoWrapper outOfInfo;

    /*******以上字段只针对秒杀的数据********/

    private SimpleSkuWrapper sku;

    public CartSkuStockOutWrapper() {}

    public CartSkuStockOutWrapper( Sku sku) {
        this.cartSkuType = CartSkuType.normal.val;

        this.sku = new SimpleSkuWrapper(sku);
    }


    public static CartSkuStockOutWrapper createCartSkuStockOutWrapper(Integer buyQuantity ,Integer customerTakeNum, SpikeItemWrapper item, SpikeWrapper spike, SimpleSkuWrapper sku){
        SpikeActivityState activityState = SpikeActivityState.parseSpikeActivity(spike);
        return new CartSkuStockOutWrapper(spike.getCity().getId(), item.getId(),item.getSku().getName(),activityState.val,item.getNum(),item.getTakeNum(),item.getPerMaxNum(), customerTakeNum, buyQuantity, sku);
    }

    public CartSkuStockOutWrapper(Long spikeCityId,Long spikeItemId, String skuName, Integer spikeActivityState, Integer num, Integer takeNum, Integer perMaxNum, Integer customerTakeNum,Integer buyQuantity, SimpleSkuWrapper sku) {
        this.cartSkuType = CartSkuType.spike.val;
        this.outOfInfo = new CartSpikeOutOfInfoWrapper(spikeCityId,spikeItemId,skuName,spikeActivityState,num, takeNum, perMaxNum,customerTakeNum,buyQuantity);
        this.sku = sku;
    }

}
