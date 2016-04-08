package com.mishu.cgwy.score.constants;

import com.mishu.cgwy.score.service.recive.OrderReciveNotice;
import com.mishu.cgwy.score.service.recive.ShareFirstOrderNotice;

/**
 * 备注没觉
 * Created by king-ck on 2015/11/30.
 */
public enum ScoreTypeRemark {

    ORDER_COMPLETE("完成订单",0,ScoreTypeEnum.OBTAIN_SCORE),

    BE_SHARE("积分分享注册首单",0,ScoreTypeEnum.SHARE_SCORE),
    SHARER("%s注册首单",1,ScoreTypeEnum.SHARE_SCORE),

    EXCHANGE("兑换积分",0,ScoreTypeEnum.EXCHANGE_SCORE),

    EVALUATE_SEND("评价返积分",0,ScoreTypeEnum.EVALUATE_SCORE);


    private final String remark;
    public final int paramCount;   //参数个数
    public  final ScoreTypeEnum scoreType;

    ScoreTypeRemark(String remark, int paramCount, ScoreTypeEnum scoreType) {
        this.remark = remark;
        this.paramCount = paramCount;
        this.scoreType = scoreType;
    }

    public String getRemark(Object... params){
        if(this.paramCount==0){
            return this.remark;
        }

        if(params!=null && this.paramCount == params.length){
            return  String.format(this.remark,params);
        }

        return null;
    };

    /**
     *
     * @param scoreTypeRemark
     * @param refCustomerId   分享者的customerid
     * @return
     */
    public static String remarkHelp( ScoreTypeRemark scoreTypeRemark, Long refCustomerId ){
        if(scoreTypeRemark==null){
            return null;
        }
        if(scoreTypeRemark==ScoreTypeRemark.SHARER){
            return scoreTypeRemark.getRemark(refCustomerId);
        }
        return scoreTypeRemark.getRemark();
    }




}
