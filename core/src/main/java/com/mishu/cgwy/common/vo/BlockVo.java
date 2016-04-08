package com.mishu.cgwy.common.vo;

import com.mishu.cgwy.common.wrapper.SimpleCityWrapper;
import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import lombok.Data;

/**
 * Created by king-ck on 2016/3/15.
 */
@Data
public class BlockVo {

    private Long id;

    private String name;

    private Long cityId;

    private boolean active;

    private String displayName;

    private String pointStr;

}
