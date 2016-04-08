package com.mishu.cgwy.profile.wrapper;

import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.common.wrapper.WarehouseWrapper;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.constants.RestaurantActiveType;
import com.mishu.cgwy.profile.constants.RestaurantAuditShowStatus;
import com.mishu.cgwy.profile.constants.RestaurantCooperatingState;
import com.mishu.cgwy.profile.constants.RestaurantGrade;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.RestaurantReason;
import com.mishu.cgwy.profile.domain.RestaurantType;
import com.mishu.cgwy.utils.DateUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 3/1/15
 * Time: 10:24 PM
 */
@Data
@EqualsAndHashCode(of = {"id"})
public class RestaurantWrapper {
    private Long id;
    private String name;
    private AddressWrapper address;
    private String license;
    private RestaurantStatus status;

    private String receiver; //联系人
    private String telephone; //联系人电话

    private RestaurantTypeWrapper type;//餐馆类型

    private CustomerWrapper customer;

    private Date createTime;

    private SimpleWarehouseWrapper warehouse;

    private String grade;

    private String warning;

    private String blankTime;

    private RestaurantAuditShowStatus auditShowStatus;

    private RestaurantReason restaurantReason;

    private RestaurantActiveType activeType;    //客户类型  潜在，成交，不活跃 CustomerActiveType

    private RestaurantCooperatingState cooperatingState;    //合作状态 正常，跟进，搁置
    private String concern;    //客户关心点
    private String opponent;    //竞争对手
    private String specialReq;    //特殊需求
    private Integer stockRate;//进货频率

    private String receiver2; //联系人2
    private String telephone2; //联系人电话2
    private String receiver3; //联系人3
    private String telephone3; //联系人电话3

    public RestaurantWrapper() {

    }

    public static List<RestaurantWrapper> getWrappers(List<Restaurant> restaurants) {
        List<RestaurantWrapper> rests = new ArrayList<>();
        if(restaurants!=null) {
            for (Restaurant restaurant : restaurants) {

                RestaurantWrapper rwrapper = new RestaurantWrapper();
                rwrapper.id=restaurant.getId();
                rwrapper.name=restaurant.getName();
                rwrapper.license = restaurant.getLicense();
                rwrapper.status = RestaurantStatus.fromInt(restaurant.getStatus());
                rwrapper.receiver = restaurant.getReceiver();
                rwrapper.telephone = restaurant.getTelephone();
                rwrapper.createTime = restaurant.getCreateTime();
                rests.add(rwrapper);
            }
        }
        return rests;
    }

    public RestaurantWrapper(Restaurant restaurant) {
        id = restaurant.getId();
        name = restaurant.getName();
        if (restaurant.getAddress() != null) {
            address = new AddressWrapper(restaurant.getAddress());
        }

        license = restaurant.getLicense();
        status = RestaurantStatus.fromInt(restaurant.getStatus());
        receiver = restaurant.getReceiver();
        telephone = restaurant.getTelephone();
        type = restaurant.getType() != null ? new RestaurantTypeWrapper(restaurant.getType()) : null;
        customer = new CustomerWrapper(restaurant.getCustomer());
        createTime = restaurant.getCreateTime();

        if (restaurant.getCustomer() != null && restaurant.getCustomer().getBlock() != null) {
            warehouse = new SimpleWarehouseWrapper(restaurant.getCustomer().getBlock().getWarehouse());
        }

        grade = RestaurantGrade.getRestaurantGradeByCode(restaurant.getGrade()).getDesc();

        warning = Boolean.TRUE.equals(restaurant.getOpenWarning()) && Boolean.TRUE.equals(restaurant.getWarning()) ? "是" : "否";

        blankTime =  DateUtils.getIntervalDays(restaurant.getLastPurchaseTime() == null ? restaurant.getCreateTime() : restaurant.getLastPurchaseTime(), new Date()).toString();

        if (restaurant.getRestaurantReason() != null) {
            restaurantReason = RestaurantReason.fromInt(restaurant.getRestaurantReason());
        }

        if(restaurant.getCooperatingState()!=null){
            this.cooperatingState=RestaurantCooperatingState.fromInt(restaurant.getCooperatingState());
        }
        if(restaurant.getActiveType()!=null) {
            this.activeType = RestaurantActiveType.fromInt(restaurant.getActiveType());
        }

        auditShowStatus=RestaurantAuditShowStatus.find(restaurant.getAuditShowStatus());

        concern = restaurant.getConcern();
        opponent=restaurant.getOpponent();
        specialReq=restaurant.getSpecialReq();
        stockRate=restaurant.getStockRate();
        receiver2=restaurant.getReceiver2();
        telephone2=restaurant.getTelephone2();
        receiver3=restaurant.getReceiver3();
        telephone3=restaurant.getTelephone3();

        int i = 2;
        while(receiver == null || telephone == null){
            receiver = receiver+i;
            telephone = telephone+i;
        }
    }

}
