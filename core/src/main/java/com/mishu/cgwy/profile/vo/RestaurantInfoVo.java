package com.mishu.cgwy.profile.vo;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import lombok.Data;

import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;

/**
 * Created by king-ck on 2016/3/3.
 */
@Data
public class RestaurantInfoVo {

    protected Long restaurantId;
    protected Long customerId;

    protected String telephone; // 用户名 注册电话
    protected String password;

//    protected String recommendNumber;

    protected Long cityId; //城市I
    protected String lat; //纬度
    protected String lng; //经度

    protected String latLng; // 经纬度
    protected Long blockId; // 板块
    protected String blockName;
//    protected boolean containsRestaurant = true;

    protected String receiver;//联系人
    protected String receiverTelephone; // 联系人电话
    protected String restaurantName;
    protected String restaurantAddress;
    protected String restaurantStreetNumber;
    protected String restaurantLicense;
    protected Long restaurantType;
    protected String restaurantTypeName;

    protected Short grade;

    protected Long adminUserId;  //维护人员
    protected String adminUserName;
    protected Long devUserId;
    protected String devUserName;
//    protected Short grade;

    protected Integer cooperatingState;    //合作状态 正常，跟进，搁置
    protected String concern;    //客户关心点
    protected String opponent;    //竞争对手
    protected String specialReq;    //特殊需求
    protected Integer stockRate;//进货频率

    protected String receiver2; //联系人2
    protected String telephone2; //联系人电话2
    protected String receiver3; //联系人3
    protected String telephone3; //联系人电话3


    protected Integer restaurantStatus;//餐馆审核状态
    protected String restaurantStatusName;//餐馆审核状态

    protected Integer restaurantReason;//失效原因

    protected Date customerCreateDate; //客户创建时间

    protected Integer customerActiveType; //活跃状态




    private Date restaurantCreateTime;
    private AdminUserVo restaurantCreateOperater; //创建人

    private Date restaurantLastOperateTime;
    private AdminUserVo restaurantLastOperater; //最后修改人

}
