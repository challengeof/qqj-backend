package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Warehouse;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 3/6/15
 * Time: 4:43 PM
 */
@Data
public class SimpleWarehouseWrapper {
    private Long id;

    private String name;

    private SimpleCityWrapper city;

    private String displayName;

    private boolean isDefault;

    public SimpleWarehouseWrapper() {
    }

    public SimpleWarehouseWrapper(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.city = new SimpleCityWrapper(warehouse.getCity());
        this.displayName = warehouse.getDisplayName();
        isDefault = warehouse.isDefault();

    }


}
