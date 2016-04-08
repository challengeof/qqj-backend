package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by bowen on 15-5-2.
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailResp extends RestError {

    /** 客服姓名 */
    private String adminName;
    /** 客服电话 */
    private String adminTelephone;
    /** 车号 */
    private String carNo;
    /** 车辆电话 */
    private String driverTelephone;
    /** 订单编号 */
    private String orderNumber;
    /** 支付方式 */
    private String payType;
    /** 价格 */
    private BigDecimal price;
    /** 联系人 */
    private String realname;
    /** 餐馆编号 */
    private String restaurantNumber;
    /** 配送时间 */
    private String sendTime;
    /** 运费 */
    private BigDecimal shippingFee;
    /** 状态 */
    private int status;
    /** 联系电话 */
    private String telephone;
    /** 下单时间 */
    private String createTime;
    /** 餐馆名称 */
    private String name;
    /** 地址 */
    private String address;
    /** 物流信息 */
    private List<TraceInfo> traceInfo;
    /** 订单详情 */
    private List<OrderDetail> orderDetailList;
    /** 退货详情 */
    private List<ReturnList> returnList;
    /** 换货详情 */
    private List<ReturnList> changeList;
}
