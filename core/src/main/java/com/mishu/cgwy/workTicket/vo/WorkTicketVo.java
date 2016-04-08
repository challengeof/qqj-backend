package com.mishu.cgwy.workTicket.vo;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import com.mishu.cgwy.profile.vo.RestaurantVo;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.purchase.vo.CutOrderVo;
import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 16/2/29.
 */
@Data
public class WorkTicketVo {

    private Long id;

    private String consultants;

    private String consultantsTelephone;

    private RestaurantVo restaurant;

    private Long orderId;

    private AdminUserVo followUp;

    private String followUpTelephone;

    private Integer problemSources;

    private Integer process;

    private String content;

    private int status;

    private Date createTime;

    private String username;
}
