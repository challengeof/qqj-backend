package com.mishu.cgwy.inventory.exception;

import com.mishu.cgwy.product.domain.Sku;

/**
 * User: xudong
 * Date: 5/31/15
 * Time: 11:28 PM
 */
public class SkuNotAvailableException extends RuntimeException {
    public SkuNotAvailableException(Sku sku) {
    }
}
