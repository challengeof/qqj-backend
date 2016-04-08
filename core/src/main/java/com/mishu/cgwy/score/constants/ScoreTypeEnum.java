package com.mishu.cgwy.score.constants;

import com.mishu.cgwy.score.service.recive.OrderReciveNotice;
import com.mishu.cgwy.score.service.recive.ScoreMessageNotice;
import com.mishu.cgwy.score.service.recive.ShareFirstOrderNotice;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bowen on 15/11/11.
 */
public enum ScoreTypeEnum {

    OBTAIN_SCORE(1,true,"订单确认收货", OrderReciveNotice.class),
    SHARE_SCORE(2,true,"积分分享", ShareFirstOrderNotice.class),
    EXCHANGE_SCORE(3,false,"积分兑换", null),
    EVALUATE_SCORE(4,true,"评价返积分", null);

    public final int val;
    public final boolean isAddScore; //是否是增加积分
    public final String description;
    public final Class<? extends ScoreMessageNotice> scoreMessageReciveClass;

    public static ScoreTypeEnum[] find(boolean isAddScore){
        List<ScoreTypeEnum> lts = new ArrayList<>();
        for(ScoreTypeEnum ste : ScoreTypeEnum.values()){
            if(ste.isAddScore==isAddScore){
                lts.add(ste);
            }
        }
        return lts.toArray(new ScoreTypeEnum[]{});
    }

    public static Integer[] findVal(boolean isAddScore){
        List<Integer> lts = new ArrayList<>();
        for(ScoreTypeEnum ste : ScoreTypeEnum.values()){
            if(ste.isAddScore==isAddScore){
                lts.add(ste.val);
            }
        }
        return lts.toArray(new Integer[]{});
    }



    ScoreTypeEnum(int val, boolean isAddScore,String description, Class<? extends ScoreMessageNotice> scoreMessageReciveClass) {
        this.val = val;
        this.isAddScore=isAddScore;
        this.description = description;
        this.scoreMessageReciveClass = scoreMessageReciveClass;
    }
}
