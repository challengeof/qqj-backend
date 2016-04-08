package com.mishu.cgwy.operating.skipe.controller;

import lombok.Data;

import java.util.List;

/**
 * Created by king-ck on 2016/4/5.
 */
@Data
public class SpikeModifyRequest {

    private Long spikeId;
    private String description;
    private List<SpikeItemAddRequest> items;

}
