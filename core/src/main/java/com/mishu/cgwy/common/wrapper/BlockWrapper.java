package com.mishu.cgwy.common.wrapper;

import com.mishu.cgwy.common.domain.Block;
import lombok.Data;


/**
 * Created by wangwei on 15/7/6.
 */
@Data
public class BlockWrapper {

    private Long id;

    private String name;

    private boolean active;

    protected SimpleWarehouseWrapper warehouse;

    private SimpleCityWrapper city;

    private String displayName;

    private String pointStr;


    public BlockWrapper(){}

    public BlockWrapper(Block block) {
        this.id = block.getId();
        this.name = block.getName();
        this.active = block.isActive();
        this.warehouse = new SimpleWarehouseWrapper(block.getWarehouse());
        this.displayName = block.getDisplayName();
        this.city = new SimpleCityWrapper(block.getCity());
        this.pointStr = block.getPointStr();

    }
}
