package com.mishu.cgwy.admin.dto;

import com.mishu.cgwy.inventory.vo.VendorVo;
import lombok.Data;

import java.util.List;

/**
 * Created by wangwei on 15/7/9.
 */
@Data
public class VendorQueryResponse {

    private long total;
    private int page;
    private int pageSize = Integer.MAX_VALUE;

    private List<VendorVo> vendors;
}
