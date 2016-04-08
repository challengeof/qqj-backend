package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.profile.constants.CustomerFollowUpStatus;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/3/7.
 */
@Data
public class CustomerSellerChangeRequest {

    private Long[] sellerLogId;

    private Long[] restaurantId;

    private Long allotUser; //分配人
    private Integer sellerType; // 分配类型  开发  维护
    private Integer followUpStatus; //跟进状态
    private Date beginDate;  //跟进日期
    private Date endDate;    //跟进日期

//    private Integer auditType; // 审核 通过，驳回

}
