package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.stock.domain.TransferItem;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class TransferItemWrapper {

    private Long id;

    private Long skuId;

    private String name;

    private Integer quantity;

    private String singleUnit;

    private TransferWrapper transfer;

    private Integer sourceDepotStock;

    private Integer targetDepotStock;

    private SimpleSkuWrapper sku;

    public TransferItemWrapper(TransferItem item) {
        this.transfer = new TransferWrapper(item.getTransfer());
        this.id = item.getId();
        this.skuId = item.getSku().getId();
        this.name = item.getSku().getName();
        this.quantity = item.getQuantity();
        this.singleUnit = item.getSku().getSingleUnit();
        this.sku = new SimpleSkuWrapper(item.getSku());
    }
}
