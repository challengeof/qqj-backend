package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.profile.domain.FeedbackStatus;
import com.mishu.cgwy.profile.domain.FeedbackType;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import lombok.Data;

import java.util.Date;

/**
 * Created by king-ck on 2016/1/28.
 */
@Data
public class FeedbackShowVo {

    private Long id;
    //城市id
    private Long cityId;
    private String cityName;

    private String feedbackDescription;  //内容
//    private MediaFileWrapper file;
    private String mediaFileurl; //图片地址
    //客户注册号
    private String customerUsername;
    //餐馆id
    private Long restaurantId;
    //餐馆名称
    private String restaurantName;
    //联系人
    private String receiver;
    //联系电话
    private String telephone;
    //提交时间
    private Date submitTime;
    //状态
    private FeedbackStatus status;

//    private CustomerWrapper customer;
//    private Date submitTime;
//    private FeedbackStatus status;
//    private Date updateTime;
//    private FeedbackType type;
//    private VendorVo vendor;
}
