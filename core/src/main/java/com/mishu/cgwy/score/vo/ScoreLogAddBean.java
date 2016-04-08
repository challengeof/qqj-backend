package com.mishu.cgwy.score.vo;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.score.constants.ScoreTypeEnum;
import com.mishu.cgwy.score.constants.ScoreTypeRemark;
import lombok.Data;

/**
 * Created by king-ck on 2015/11/18.
 */
@Data
public class ScoreLogAddBean {

    private long score;
    private long customerId;
    private Long stockOutId;
    private Long orderId;

    private Long couponId;
    private Long beSharedCustomerId;
    private AdminUser sender;
    private ScoreTypeRemark scoreTypeRemark; //取自 ScoreTypeEnum

}
