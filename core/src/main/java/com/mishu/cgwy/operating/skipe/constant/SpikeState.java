package com.mishu.cgwy.operating.skipe.constant;

import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * Created by king-ck on 2016/1/11.
 */
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SpikeState {

    EFFECTIVE(1,"生效"),  INVALID(0,"失效");

    private Integer val;

    private String name;

    public Integer getVal() {
        return val;
    }

    public String getName() {
        return name;
    }

    private SpikeState(Integer val, String name) {
        this.val = val;
        this.name = name;
    }

    public static SpikeState fromInt(Integer val){

        for(SpikeState state : SpikeState.values()){
            if(state.getVal()==val){
                return state;
            }
        }
        return null;
    }
}