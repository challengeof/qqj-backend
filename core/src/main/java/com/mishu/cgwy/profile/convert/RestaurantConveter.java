package com.mishu.cgwy.profile.convert;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.constants.RestaurantAuditReviewType;
import com.mishu.cgwy.profile.constants.RestaurantReviewStatus;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.RestaurantAuditReview;
import com.mishu.cgwy.profile.vo.RestaurantAuditReviewVo;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by king-ck on 2016/3/3.
 */
public class RestaurantConveter {


    public static List<RestaurantInfoVo> toRestaurantInfoVo(List<Restaurant> restaurants){
        Collection<RestaurantInfoVo> colls = Collections2.transform(restaurants, new Function<Restaurant, RestaurantInfoVo>() {
            @Override
            public RestaurantInfoVo apply(Restaurant input) {
                return toRestaurantInfoVo(input);
            }
        });

        return new ArrayList<>(colls);
    }

    public static RestaurantInfoVo toRestaurantInfoVo(Restaurant restaurant){

        if(restaurant==null){
            return null;
        }
        RestaurantInfoVo rinfo=new RestaurantInfoVo();
        rinfo.setCustomerId(restaurant.getCustomer().getId());
        rinfo.setRestaurantId(restaurant.getId());
        rinfo.setTelephone(restaurant.getCustomer().getUsername());

//        rinfo.setPassword();
        if(restaurant.getCustomer().getBlock()!=null) {
            rinfo.setCityId(restaurant.getCustomer().getBlock().getCity().getId());
        }
        if(restaurant.getAddress()!=null) {
            rinfo.setRestaurantAddress(restaurant.getAddress().getAddress());
            rinfo.setRestaurantStreetNumber(restaurant.getAddress().getStreetNumber());
            if (restaurant.getAddress().getWgs84Point() != null) {
                rinfo.setLat(String.valueOf(restaurant.getAddress().getWgs84Point().getLatitude()));
                rinfo.setLng(String.valueOf(restaurant.getAddress().getWgs84Point().getLongitude()));
            }
        }
//        rinfo.setLatLng();
        if(restaurant.getCustomer().getBlock()!=null) {
            rinfo.setBlockId(restaurant.getCustomer().getBlock().getId());
            rinfo.setBlockName(restaurant.getCustomer().getBlock().getName());
        }
        rinfo.setReceiver(restaurant.getReceiver());

        if( restaurant.getCustomer().getDevUser()!=null) {
            rinfo.setDevUserId(restaurant.getCustomer().getDevUser().getId());
        }
        if( restaurant.getCustomer().getAdminUser()!=null ) {
            rinfo.setAdminUserId(restaurant.getCustomer().getAdminUser().getId());
        }

        RestaurantStatus rstatus = RestaurantStatus.fromInt(restaurant.getStatus());

        rinfo.setRestaurantStatus(restaurant.getStatus());
        if(rstatus!=null) {
            rinfo.setRestaurantStatusName(rstatus.getName());
        }
        rinfo.setRestaurantReason(restaurant.getRestaurantReason());
        rinfo.setRestaurantName(restaurant.getName());

        rinfo.setRestaurantLicense(restaurant.getLicense());
        if(restaurant.getType()!=null) {
            rinfo.setRestaurantType(restaurant.getType().getId());
            rinfo.setRestaurantTypeName(restaurant.getType().getName());
        }
        rinfo.setCooperatingState(restaurant.getCooperatingState());
        rinfo.setConcern(restaurant.getConcern());
        rinfo.setOpponent(restaurant.getOpponent());
        rinfo.setSpecialReq(restaurant.getSpecialReq());
        rinfo.setStockRate(restaurant.getStockRate());
        rinfo.setReceiver2(restaurant.getReceiver2());
        rinfo.setTelephone2(restaurant.getTelephone2());
        rinfo.setReceiver3(restaurant.getReceiver3());
        rinfo.setTelephone3(restaurant.getTelephone3());

        if(restaurant.getCreateOperater()!=null) {
            AdminUserVo createOperater = new AdminUserVo();
            createOperater.setId(restaurant.getCreateOperater().getId());
            createOperater.setRealname(restaurant.getCreateOperater().getRealname());
            rinfo.setRestaurantCreateOperater(createOperater);

        }
        if(restaurant.getLastOperater()!=null) {
            AdminUserVo lastOperater = new AdminUserVo();
            lastOperater.setId(restaurant.getCreateOperater().getId());
            lastOperater.setRealname(restaurant.getCreateOperater().getRealname());
            rinfo.setRestaurantLastOperater(lastOperater);

        }

        rinfo.setRestaurantCreateTime(restaurant.getCreateTime());
        rinfo.setRestaurantLastOperateTime(restaurant.getLastOperateTime());
        return rinfo;
    }

    public static List<RestaurantAuditReviewVo> toAuditInfoVo(List<RestaurantAuditReview> auditReviews){

        List<RestaurantAuditReviewVo> auditReviewVos = new ArrayList<>();
        for(RestaurantAuditReview auditReview : auditReviews){
            RestaurantAuditReviewVo reviewVo = toAuditInfoVo(auditReview);
            auditReviewVos.add(reviewVo);
        }
        return auditReviewVos;
    }

    public static RestaurantAuditReviewVo toAuditInfoVo(RestaurantAuditReview auditReview){
        return toAuditInfoVo(auditReview,true);
    }

    public static RestaurantAuditReviewVo toAuditInfoVo(RestaurantAuditReview auditReview ,boolean isSetRestaurant){

        if(auditReview==null){
            return null;
        }
        RestaurantAuditReviewVo reviewVo = new RestaurantAuditReviewVo();
        reviewVo.setId(auditReview.getId());
        if(auditReview.getOperater()!=null) {
            AdminUser operater = auditReview.getOperater();
            AdminUserVo operaterVo = new AdminUserVo();
            operaterVo.setId(operater.getId());
            operaterVo.setUsername(operater.getUsername());
            operaterVo.setTelephone(operater.getTelephone());
            operaterVo.setEnabled(operater.isEnabled());
            operaterVo.setRealname(operater.getRealname());
            operaterVo.setGlobalAdmin(operater.isGlobalAdmin());
            reviewVo.setOperater(operaterVo);
        }

        if(auditReview.getCreateUser()!=null) {
            AdminUser createUser = auditReview.getCreateUser();
            AdminUserVo createUserVo = new AdminUserVo();
            createUserVo.setId(createUser.getId());
            createUserVo.setUsername(createUser.getUsername());
            createUserVo.setTelephone(createUser.getTelephone());
            createUserVo.setEnabled(createUser.isEnabled());
            createUserVo.setRealname(createUser.getRealname());
            createUserVo.setGlobalAdmin(createUser.isGlobalAdmin());
            reviewVo.setCreateUser(createUserVo);
        }
        reviewVo.setOperateTime(auditReview.getOperateTime());

        RestaurantAuditReviewType reviewType = RestaurantAuditReviewType.fromInt(auditReview.getReqType());
        reviewVo.setReqType(reviewType);
        RestaurantReviewStatus reviewStatus = RestaurantReviewStatus.fromInt(auditReview.getStatus());
        reviewVo.setStatus(reviewStatus);
        reviewVo.setCreateTime(auditReview.getCreateTime());


        if( isSetRestaurant && auditReview.getRestaurant()!=null){
            reviewVo.setInfoVo(toRestaurantInfoVo(auditReview.getRestaurant()));
        }

        return reviewVo;
    }


}
