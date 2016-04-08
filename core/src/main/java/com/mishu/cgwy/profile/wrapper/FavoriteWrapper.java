package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.domain.Favorite;
import lombok.Data;

import java.math.BigDecimal;

/**
 * web
 * User: xudong
 * Date: 5/20/15
 * Time: 4:35 PM
 */
@Data
public class FavoriteWrapper {
    private Long id;
    private SkuWrapper sku;

    // quantity this sku is bought
    private long singleQuantity = 0;
    private long bundleQuantity = 0;

    // how many times this sku is bought
    private long singleCount = 0;
    private long bundleCount = 0;

    private long maxBuy = 0;

    public FavoriteWrapper() {

    }

    public FavoriteWrapper(Favorite favorite) {
        id = favorite.getId();
        sku = new SkuWrapper(favorite.getSku());
    }
}
