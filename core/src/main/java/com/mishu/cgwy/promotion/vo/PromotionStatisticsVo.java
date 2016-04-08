package com.mishu.cgwy.promotion.vo;

import com.mishu.cgwy.coupon.constant.PromotionConstant;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by king-ck on 2015/12/24.
 */
@Data
public class PromotionStatisticsVo {

    private Long orderId;
    private Date submitDate;
    private Date receiveDate;
    private Long restaurantId;
    private String restaurantName;
    private PromotionConstant promotionType;
    private Long promotionId;
    private Long skuId;
    private String skuName;
    private Boolean bundle;//true打包  false单品
    private Integer quantity;//赠品
    private Integer receiveQuantity; //接收数量
    private String promotionDes;
    private BigDecimal avgCost=new BigDecimal(0);  //成本价

    private BigDecimal saleCost=new BigDecimal(0); //销售价(单品)
    private BigDecimal saleCostBundle=new BigDecimal(0); //销售价(打包)
    private Integer capacityInBundle=1; //转化率

    private BigDecimal avgCostAmount=new BigDecimal(0);     //成本额 = 成本单价*数量
    private BigDecimal saleCostAmount=new BigDecimal(0);    //销售额 = 销售单价*数量

    private BigDecimal discount=new BigDecimal(0);  //满减额度

    private String skuUnit; //商品单位

//    public PromotionStatisticsVo(BigDecimal avgCostAmount, BigDecimal saleCostAmount) {
//        this.avgCostAmount = avgCostAmount;
//        this.saleCostAmount = saleCostAmount;
//    }
//
//    public PromotionStatisticsVo(BigDecimal discount) {
//        this.discount=discount;
//    }
//
//    public PromotionStatisticsVo(Long orderId, Date submitDate, Date receiveDate, Long restaurantId, String restaurantName,
//                                 Integer promotionType, Long promotionId, BigDecimal discount, String promotionDes) {
//        this.orderId = orderId;
//        this.submitDate = submitDate;
//        this.receiveDate = receiveDate;
//        this.restaurantId = restaurantId;
//        this.restaurantName = restaurantName;
//        this.promotionType = PromotionConstant.getPromotionConstantByType(promotionType);
//        this.promotionId = promotionId;
//        this.discount = discount;
//        this.promotionDes = promotionDes;
//    }
//    public PromotionStatisticsVo(Long orderId, Date submitDate, Date receiveDate, Long restaurantId, String restaurantName, Integer promotionType,
//                                 Long promotionId, Long skuId, String skuName, Boolean bundle, Integer quantity, Integer receiveQuantity, String promotionDes,
//                                 BigDecimal avgCost, BigDecimal saleCost, BigDecimal saleCostBundle, Integer capacityInBundle, String skuUnit) {
//        this.orderId = orderId;
//        this.submitDate = submitDate;
//        this.receiveDate = receiveDate;
//        this.restaurantId = restaurantId;
//        this.restaurantName = restaurantName;
//        this.promotionType = PromotionConstant.getPromotionConstantByType(promotionType);
//        this.promotionId = promotionId;
//        this.skuId = skuId;
//        this.skuName = skuName;
//        this.bundle = bundle;
//        this.quantity = quantity;
//        this.receiveQuantity = receiveQuantity==null?0:receiveQuantity;
//        this.promotionDes = promotionDes;
//        this.avgCost = avgCost==null?new BigDecimal(0):avgCost;
//        this.saleCost = saleCost==null?new BigDecimal(0):saleCost;
//        this.capacityInBundle = capacityInBundle;
//        this.saleCostBundle = saleCostBundle==null || (this.saleCost.compareTo(new BigDecimal(0))==0) ? this.saleCost.multiply(new BigDecimal(this.capacityInBundle)) : new BigDecimal(0);
//
//        this.avgCostAmount = this.avgCost.multiply(new BigDecimal(this.receiveQuantity));
//        this.saleCostAmount = (this.bundle? this.saleCostBundle : this.saleCost).multiply(new BigDecimal(this.receiveQuantity));
//
//        this.skuUnit=skuUnit;
//    }
}
