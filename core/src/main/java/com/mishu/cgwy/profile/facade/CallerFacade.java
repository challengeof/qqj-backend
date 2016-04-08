package com.mishu.cgwy.profile.facade;

import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.controller.CallerRequest;
import com.mishu.cgwy.profile.constants.CallerResultcode;
import com.mishu.cgwy.profile.constants.Ccic2RequestConstants;
import com.mishu.cgwy.profile.controller.caller.*;
import com.mishu.cgwy.profile.domain.Caller;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.repository.CallerRepository;
import com.mishu.cgwy.profile.service.CallerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.profile.wrapper.CallerListQueryWrapper;
import com.mishu.cgwy.profile.wrapper.CallerWrapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2015/9/29.
 */
@Service
public class CallerFacade {
    private static Logger logger = LoggerFactory.getLogger(CallerFacade.class);

    @Autowired
    private CallerRepository callerRepository;

    @Autowired
    private CallerService callerService;

    @Autowired
    private RestaurantService restaurantService;

    @Transactional
    public CallerWrapper updateInfo(CallerRequest callerRequest) throws InvocationTargetException, IllegalAccessException {

        Caller caller = callerService.saveCaller(callerRequest);
        CallerWrapper callerWrapper =new CallerWrapper(caller);
        return callerWrapper;
    }


    @Transactional
    public void updateModifyTime(String phone) throws InvocationTargetException, IllegalAccessException {

        Caller caller = callerService.saveCallerModifyTime(phone);

    }

    @Transactional
    public RestError saveInfo(CallerRequest callerRequest){
        RestError result = new RestError();
        try {

            Caller caller = this.callerRepository.findByPhone(callerRequest.getPhone());
            if (caller != null) {
                result.setErrno(CallerResultcode.existsCaller.getNo());
                result.setErrmsg(CallerResultcode.existsCaller.getDetail());
                return result;
            }
            callerService.saveCaller(callerRequest);
        }catch (Exception ex){
            result.setErrno(CallerResultcode.errorSave.getNo());
            result.setErrmsg(CallerResultcode.errorSave.getDetail());
        }

        return result;
    }

    @Transactional(readOnly = true)
    public CallerQueryResponse findByPhone(String phone) {

        Caller caller = this.callerService.findByPhone(phone);

//        List<Restaurant> callerRestaurant =new ArrayList<Restaurant>();
//        if(caller!=null){
//            callerRestaurant = caller.getRestaurants();
//        }

        List<Restaurant> relationRestaurants = restaurantService.findByTelephone(phone);

//        if( otherRestaurants!=null ) {
//            begin:
//            for (Restaurant restaurant : callerRestaurant) {
//                for (Restaurant other : otherRestaurants) {
//                    if (other.getId().equals(restaurant.getId())) {
//                        otherRestaurants.remove(other);
//                        continue begin;
//                    }
//                }
//            }
//        }

        return new CallerQueryResponse(phone,caller,relationRestaurants);

    }

//    @Transactional
//    public void relationRestaurant(String phone, Long restaurantid) {
//        callerService.relationRestaurant(phone,restaurantid);
//    }


    @Transactional
    public CallerListResponse queryCallerPage(CallerListRequest request){

        CallerListResponse listResponse=new CallerListResponse();

        Page<Object[]> callerPage = callerService.findCaller(request);

        List<CallerListQueryWrapper> queryWrapper = new ArrayList<>();

        for(Object[] obj : callerPage) {
            CallerListQueryWrapper wrapper = new CallerListQueryWrapper((BigInteger)obj[0],(String)obj[1],(String)obj[2],(String)obj[3],(Date)obj[4],(Date)obj[5],(BigInteger)obj[6],(String)obj[7],(String)obj[8]);
            queryWrapper.add(wrapper);
        }
//        List<CallerWrapper> callerWrappers = new ArrayList<>();
//        for(Caller wrapper : callerPage){
//            CallerWrapper callerWrapper = new CallerWrapper(wrapper);
//            callerWrappers.add(callerWrapper);
//        }
//        listResponse.setCallerWrappers(callerWrappers);
        listResponse.setQueryWrappers(queryWrapper);
        listResponse.setTotal(callerPage.getTotalElements());
        listResponse.setPage(request.getPage());
        listResponse.setPageSize(request.getPageSize());
        return listResponse;
    }



    @Transactional
    public RestError remove(Long callerid) {
        RestError restError = new RestError();
        try {
            this.callerRepository.delete(callerid);
        }catch (Exception ex){
            restError.setErrno(CallerResultcode.errorSave.getNo());
            restError.setErrmsg(CallerResultcode.errorSave.getDetail());
        }
        return restError;
    }

    public String outcall(CallerOutCallRequest request) throws Exception {

        String result = Ccic2RequestConstants.OUT_CALL.request(request);

        return result;
    }

    public String sendSms(CallerSendSmsRequest request) throws Exception {

        String pwd = DigestUtils.md5Hex(request.getPwd()+request.getSeed());  //对应username的密码，md5(md5(登录密码)+seed)，例如：md5(md5(123456)seed)
        request.setPwd(pwd);

        request.setCustomerName(URLEncoder.encode(request.getCustomerName(),"utf8"));

        request.setMsg(URLEncoder.encode(request.getMsg(),"utf8"));

        String result = Ccic2RequestConstants.SEND_SMS.request(request);

        return result;
//        return null;
    }


}
