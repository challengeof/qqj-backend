package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import lombok.Data;

import java.util.List;

/**
 * User: xudong
 * Date: 3/6/15
 * Time: 4:43 PM
 */
@Data
public class WarehouseWrapper {
    private Long id;

    private String name;

    private SimpleCityWrapper city;

    private String displayName;

//    private List<BlockWrapper> blocks;

    private boolean isDefault;

    private boolean active;

    private DepotWrapper depot;

    public WarehouseWrapper() {
    }

    public WarehouseWrapper(Warehouse warehouse) {
        this.id = warehouse.getId();
        this.name = warehouse.getName();
        this.city = new SimpleCityWrapper(warehouse.getCity());
        this.displayName = warehouse.getDisplayName();
        this.isDefault = warehouse.isDefault();
        this.active = warehouse.isActive();
        if (warehouse.getDepot() != null) {
            depot = new DepotWrapper(warehouse.getDepot());
        }
    }


}
