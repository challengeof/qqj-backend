package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Zone;
import lombok.Data;

@Data
public class ZoneWrapper {
    private Long id;
    private String name;
    private Boolean active;
    
    private String displayName;
    
    private SimpleWarehouseWrapper warehouse;
    

    public ZoneWrapper() {

    }

    public ZoneWrapper(Zone zone) {
        id = zone.getId();
        name = zone.getName();
        displayName = zone.getDisplayName();
        active = zone.isActive();
        warehouse = new SimpleWarehouseWrapper(zone.getWarehouse());
    }

}
