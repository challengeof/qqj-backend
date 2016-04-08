package com.mishu.cgwy.vendor.wrapper;

import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.vendor.domain.VendorOrderItem;
import lombok.Data;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by wangguodong on 15/12/15.
 */
@Data
public class VendorOrderItemWrapper {

    private Long id;

    private Short status;

    private Long skuId;

    private String name;

    private Integer singleQuantity;

    private String singleUnit;

    private Integer bundleQuantity;

    private String bundleUnit;

    public VendorOrderItemWrapper(VendorOrderItem item, VendorOrderItemCalculateType vendorOrderItemCalculateType) {
        this.id = item.getId();
        Sku sku = item.getSku();
        Integer quantity = vendorOrderItemCalculateType == VendorOrderItemCalculateType.NOT_READY ? item.getQuantityNeed() : item.getQuantityReady();
        Integer capacityInBundle = sku.getCapacityInBundle();
        this.skuId = sku.getId();
        this.name = sku.getName();
        this.singleUnit = sku.getSingleUnit();
        this.bundleUnit = sku.getBundleUnit();
        this.singleQuantity = quantity % capacityInBundle;
        this.bundleQuantity = quantity / capacityInBundle;
    }

    public enum VendorOrderItemCalculateType {
        NOT_READY(1, "未备货"),
        READY(2, "已备货");

        private Integer value;
        private String name;

        public Integer getValue() {
            return value;
        }

        public String getName() {
            return name;
        }

        private VendorOrderItemCalculateType(Integer value, String name) {
            this.value = value;
            this.name = name;
        }

        public static VendorOrderItemCalculateType get(Integer value) {
            for (VendorOrderItemCalculateType i : VendorOrderItemCalculateType.values()) {
                if (i.value.equals(value)) {
                    return i;
                }
            }
            return null;
        }
    }
}
