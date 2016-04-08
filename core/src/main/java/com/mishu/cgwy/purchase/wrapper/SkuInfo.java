package com.mishu.cgwy.purchase.wrapper;

import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.ProductWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderStatus;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.wrapper.StockTotalWrapper;
import lombok.Data;
import org.apache.commons.lang.ArrayUtils;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * admin
 */
@Data
public class SkuInfo {

    private SkuWrapper sku;

    private StockTotalWrapper stockTotal;

    private int[] stocks;

    private BigDecimal fixedPrice;

    private BigDecimal lastPurchasePrice;

    public SkuInfo(Sku sku, StockTotal stockTotal, int[] stocks) {
        this.sku = new SkuWrapper(sku);
        if (stockTotal != null) {
            this.stockTotal = new StockTotalWrapper(stockTotal);
        }
        this.stocks = stocks;
    }
}
