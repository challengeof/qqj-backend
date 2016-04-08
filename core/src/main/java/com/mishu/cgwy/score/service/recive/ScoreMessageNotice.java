package com.mishu.cgwy.score.service.recive;

import com.mishu.cgwy.message.score.ScoreMessage;

/**
 * Created by king-ck on 2015/11/17.
 */
public abstract class ScoreMessageNotice<T extends ScoreMessage> {

    public abstract void addScore(T addsp);

}
