package com.mishu.cgwy.order.dto;

import com.mishu.cgwy.error.RestError;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class OrderDetailResponse extends RestError {
    private String orderNumber;
    private float price;
    private String address;
    /** 餐馆名称 */
    private String name;
    private String realname;
    private String telephone;
    private int status;
    private String restaurantNumber;
    private String createTime;
    private String payTime;
    /** 子订单信息 */
    private List<OrderDetail> orderDetail = new ArrayList<OrderDetail>();
    private int shippingFee;
    /** 退换货状态 0：尚未申请 1：待审核 2：已审核 */
    private int orderReturnStatus;
    /** 退换货清单list */
    private List<OrderReturnDetail> orderReturnDetail = new ArrayList<OrderReturnDetail>();
    private String sendTime;
    private String payType = "";
    private List<TraceInfo> traceInfo = new ArrayList<TraceInfo>();

}
