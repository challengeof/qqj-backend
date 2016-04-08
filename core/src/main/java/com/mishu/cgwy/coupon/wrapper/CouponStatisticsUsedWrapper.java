package com.mishu.cgwy.coupon.wrapper;

import lombok.Data;

import java.math.BigDecimal;

/**
 * Created by king-ck on 2015/12/16.
 */
@Data
public class CouponStatisticsUsedWrapper {

    private Long cityId;
    private String cityName;
    private Long warehouseId;
    private String warehouseName;
    private BigDecimal prePeriodBalance=new BigDecimal(0);//上期优惠券余额  =  上期发放金额-上期使用金额-上期过期金额
    private BigDecimal crtPeriodSendAmount=new BigDecimal(0);//本期发放金额
    private BigDecimal crtPeriodUsedAmount=new BigDecimal(0);//本期使用金额
    private BigDecimal crtPeriodBalance=new BigDecimal(0);//本期余额  = 本期发放+上期余额-本期使用金额-本期过期金额
    private BigDecimal crtPeriodOverdue=new BigDecimal(0);//本期过期金额

    public CouponStatisticsUsedWrapper(Long cityId, String cityName, Long warehouseId, String warehouseName, BigDecimal cPreSum, BigDecimal cPreUsedSum
            , BigDecimal cPreOverdueSum, BigDecimal cCrtSum, BigDecimal cCrtUsedSum, BigDecimal cCrtOverdueSum) {
        this.cityId=cityId;
        this.cityName=cityName;
        this.warehouseId=warehouseId;
        this.warehouseName=warehouseName;

        this.prePeriodBalance= cPreSum.subtract(cPreUsedSum).subtract(cPreOverdueSum);
        this.crtPeriodBalance=cPreSum.subtract(cPreUsedSum).subtract(cPreOverdueSum);
        this.crtPeriodSendAmount=cCrtSum;
        this.crtPeriodUsedAmount=cCrtUsedSum;
        this.crtPeriodOverdue=cCrtOverdueSum;
        //本期余额  = 本期发放+上期余额-本期使用金额-本期过期金额
        this.crtPeriodBalance=cCrtSum.add(this.prePeriodBalance).subtract(cCrtUsedSum).subtract(cCrtOverdueSum);
    }
}
