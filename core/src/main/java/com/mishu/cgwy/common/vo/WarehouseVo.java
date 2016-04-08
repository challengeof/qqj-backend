package com.mishu.cgwy.common.vo;

import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import lombok.Data;

/**
 * User: xudong
 * Date: 3/6/15
 * Time: 4:43 PM
 */
@Data
public class WarehouseVo {
    private Long id;

    private String name;

    private CityVo city;

    private String displayName;

//    private List<BlockWrapper> blocks;

    private boolean isDefault;

    private boolean active;

//    private DepotWrapper depot;
}
