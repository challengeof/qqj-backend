package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.profile.constants.RestaurantAuditReviewType;
import com.mishu.cgwy.profile.constants.RestaurantReviewStatus;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/3/10.
 */
@Data
public class RestaurantAuditReviewVo {
    private Long id;

    private AdminUserVo operater; // 操作人
    private Date operateTime; // 审核时间
    private RestaurantReviewStatus status; // RestaurantReviewStatus  通过 驳回
    private AdminUserVo createUser; // 创建人
    private RestaurantAuditReviewType reqType; //申请审核类型  RestaurantAuditReviewType
    private Date createTime;


    private RestaurantInfoVo infoVo;
}
