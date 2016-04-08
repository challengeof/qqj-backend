package com.mishu.cgwy.product.wrapper;

import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.order.wrapper.CartSkuStockOutWrapper;
import com.mishu.cgwy.product.domain.Sku;
import lombok.Data;

/**
 * Created by king-ck on 2016/1/18.
 */
@Data
public class CartSimpleSkuWrapper extends SimpleSkuWrapper {

    private Integer cartSkuType ;// 1 普通  2秒杀商品

    private CartSpikeOutOfInfoWrapper spikeOutInfo;


    public CartSimpleSkuWrapper(Sku sku,CartSkuStockOutWrapper outWrapper){
        super(sku);
        this.cartSkuType=outWrapper.getCartSkuType();
        this.spikeOutInfo=outWrapper.getOutOfInfo();
    }

    public CartSimpleSkuWrapper() {
    }
    public CartSimpleSkuWrapper(Sku sku) {
        super(sku);
    }

}
