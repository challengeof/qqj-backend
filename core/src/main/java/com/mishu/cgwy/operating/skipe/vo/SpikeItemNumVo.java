package com.mishu.cgwy.operating.skipe.vo;

import lombok.Data;

/**
 * Created by king-ck on 2016/1/13.
 */
@Data
public class SpikeItemNumVo {

    private Long spikeId;

    private Long spikeItemId;

    private Integer num; //总数量

    private Integer takeNum; //已参与数量

}
