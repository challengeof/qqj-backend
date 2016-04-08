package com.mishu.cgwy.organization.controller;

import com.mishu.cgwy.common.wrapper.BlockWrapper;
import lombok.Data;

import java.util.List;

/**
 * Created by xingdong on 15/7/6.
 */
@Data
public class BlockQueryResponse {

    private long total;
    private int page;
    private int pageSize;

    private List<BlockWrapper> blocks;
}
