package com.mishu.cgwy.coupon.wrapper;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by king-ck on 2015/12/16.
 */
@Data
public class CouponStatisticsUsedTotalWrapper {

    private BigDecimal prePeriodBalance=new BigDecimal(0);//上期优惠券余额  =  上期发放金额-上期使用金额-上期过期金额
    private BigDecimal crtPeriodSendAmount=new BigDecimal(0);//本期发放金额
    private BigDecimal crtPeriodUsedAmount=new BigDecimal(0);//本期使用金额
    private BigDecimal crtPeriodBalance=new BigDecimal(0);//本期余额  = 本期发放+上期余额-本期使用金额-本期过期金额
    private BigDecimal crtPeriodOverdue=new BigDecimal(0);//本期过期金额

    public CouponStatisticsUsedTotalWrapper(List<CouponStatisticsUsedWrapper> usedWrappers) {
        for(CouponStatisticsUsedWrapper usedWrapper : usedWrappers  ){
            this.prePeriodBalance=this.prePeriodBalance.add(usedWrapper.getPrePeriodBalance());
            this.crtPeriodSendAmount=this.crtPeriodSendAmount.add(usedWrapper.getCrtPeriodSendAmount());
            this.crtPeriodUsedAmount=this.crtPeriodUsedAmount.add(usedWrapper.getCrtPeriodUsedAmount());
            this.crtPeriodBalance=this.crtPeriodBalance.add(usedWrapper.getCrtPeriodBalance());
            this.crtPeriodOverdue=this.crtPeriodOverdue.add(usedWrapper.getCrtPeriodOverdue());
        }
    }
}
