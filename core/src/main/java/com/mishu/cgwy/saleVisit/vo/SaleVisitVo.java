package com.mishu.cgwy.saleVisit.vo;

import com.mishu.cgwy.saleVisit.domain.SaleVisit;
import lombok.Data;
import org.apache.commons.lang.time.DateFormatUtils;


/**
 * Created by xgl on 2016/03/21.
 */
@Data
public class SaleVisitVo {

    private Long id;
    private Long restaurantId;
    private String visitTime;
    private Integer visitStage;
    private Integer[] visitPurposes;
    private Integer[] intentionProductions;
    private Integer[] visitTroubles;
    private Integer[] visitSolutions;
    private String nextVisitTime;
    private Integer nextVisitStage;
    private String remark;

    public SaleVisitVo(SaleVisit saleVisit){
        this.id = saleVisit.getId();
        this.restaurantId = saleVisit.getRestaurant().getId();
        this.visitTime = DateFormatUtils.format(saleVisit.getVisitTime(),"yyyy-MM-dd HH:mm:ss") ;
        this.visitStage = saleVisit.getVisitStage();
        this.visitPurposes = toIntArr(saleVisit.getVisitPurposes().split(","));
        this.intentionProductions = toIntArr(saleVisit.getIntentionProductions().split(","));
        this.visitTroubles = toIntArr(saleVisit.getVisitTroubles().split(","));
        this.visitSolutions = toIntArr(saleVisit.getVisitSolutions().split(","));
        this.nextVisitTime = DateFormatUtils.format(saleVisit.getNextVisitTime(),"yyyy-MM-dd HH:mm:ss");
        this.nextVisitStage = saleVisit.getNextVisitStage();
        this.remark = saleVisit.getRemark();
    }


    private Integer[] toIntArr(String[] strArr){
        Integer[] intArr = new Integer[strArr.length];

        for(int i = 0;i < strArr.length;i ++){
            intArr[i] = Integer.parseInt(strArr[i]);
        }
        return intArr;
    }

}
