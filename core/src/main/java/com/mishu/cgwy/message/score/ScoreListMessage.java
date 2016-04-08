package com.mishu.cgwy.message.score;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by king-ck on 2015/12/10.
 */
@Data
public class ScoreListMessage implements Serializable {
    private static final long serialVersionUID = 3111601576461736453L;

    private List<Long> stockOutIds=new ArrayList<>();

    public ScoreListMessage(List<Long> stockOutIds){

        if(null != stockOutIds){
            this.stockOutIds=stockOutIds;
        }

    }

}
