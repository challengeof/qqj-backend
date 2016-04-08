package com.mishu.cgwy.message.score;

import lombok.Data;

import java.io.Serializable;

/**
 * Created by king-ck on 2015/11/19.
 */
@Data
public class ScoreMessage implements Serializable {
    private static final long serialVersionUID = -2929601446241923127L;

    private long stockOutId;


    public ScoreMessage(long stockOutId) {
        this.stockOutId = stockOutId;
    }

    

}
