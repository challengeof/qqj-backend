package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.admin.vo.AdminUserVo;
import lombok.Data;

/**
 * User: xudong
 * Date: 5/7/15
 * Time: 7:35 PM
 */
@Data
public class Fulfillment {
    private Long orderGroupId;
    private AdminUserVo tracker;
}
