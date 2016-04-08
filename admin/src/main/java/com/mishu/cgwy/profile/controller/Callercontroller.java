package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.controller.CallerRequest;
import com.mishu.cgwy.profile.controller.caller.*;
import com.mishu.cgwy.profile.facade.CallerFacade;
import com.mishu.cgwy.profile.wrapper.CallerWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by king-ck on 2015/9/29.
 */
@Controller
public class Callercontroller {
    @Autowired
    private CallerFacade callerFacade;

//    @Autowired
//    private AdminUserFacade adminUserFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }
    @RequestMapping(value = "/api/caller/update", method = RequestMethod.GET)
    @ResponseBody
    public CallerWrapper callerUpdate(CallerRequest callerRequest, @CurrentAdminUser AdminUser operator) throws InvocationTargetException, IllegalAccessException {
        return callerFacade.updateInfo(callerRequest);
    }
    @RequestMapping(value = "/api/caller/add", method = RequestMethod.GET)
    @ResponseBody
    public RestError callerSave(CallerRequest callerRequest, @CurrentAdminUser AdminUser operator) throws InvocationTargetException, IllegalAccessException {
        return callerFacade.saveInfo(callerRequest);
    }

    @RequestMapping(value = "/api/caller/delete/{callerid}", method = RequestMethod.GET)
    @ResponseBody
    public RestError findcaller(@PathVariable("callerid") Long callerid, @CurrentAdminUser AdminUser operator) {

        return callerFacade.remove(callerid);
    }

//    @RequestMapping(value = "/api/caller/relationRestaurant/{phone}/{restaurantid}", method = RequestMethod.GET)
//    @ResponseBody
//    public CallerQueryResponse relationRestaurant(@PathVariable("phone") String phone,@PathVariable("restaurantid") Long restaurantid, @CurrentAdminUser AdminUser operator){
//
//        callerFacade.relationRestaurant(phone,restaurantid);
//        return callerFacade.findByPhone(phone);
//    }

    @RequestMapping(value = "/api/caller/{phone}", method = RequestMethod.GET)
    @ResponseBody
    public CallerQueryResponse findcaller(@PathVariable("phone") String phone, @CurrentAdminUser AdminUser operator) {

        return callerFacade.findByPhone(phone);
    }

    //显示弹出页
    @RequestMapping(value = "/api/caller/pop/show", method = {RequestMethod.GET,RequestMethod.POST})
    public String toCallerPop(@RequestParam("customerNumber") String phone,HttpServletRequest request, @CurrentAdminUser AdminUser operator) throws InvocationTargetException, IllegalAccessException {

        CallerRequest callerRequest=new CallerRequest();
        callerRequest.setPhone(phone);
        CallerQueryResponse queryresult =callerFacade.findByPhone(phone);
        if(queryresult.getCaller()==null){
            callerFacade.updateInfo(callerRequest); //更新修改时间
        }else{
            callerFacade.updateModifyTime(phone);
        }

//      AdminUserWrapper adminuser = adminUserFacade.getAdminUserByUsername(operator.getUsername());
        String iswatch = request.getParameter("iswatch");

        request.setAttribute("userRealname",operator.getRealname());
        request.setAttribute("phone", phone);
        request.setAttribute("iswatch",iswatch);

        return "/caller/callerpop.jsp";
    }


    // 显示列表页
    @RequestMapping(value = "/api/caller/listindex", method = {RequestMethod.GET,RequestMethod.POST})
    public String toCallerPop( CcicShowParam showParam,HttpSession session, @CurrentAdminUser AdminUser operator) throws InvocationTargetException, IllegalAccessException {

        session.setAttribute("ccicuser",showParam);
        return "/caller/list/callerlist.jsp";
    }

    //--- 客户列表查询

    @RequestMapping(value = "/api/caller/list", method = RequestMethod.GET)
    @ResponseBody
    public CallerListResponse findcaller(CallerListRequest listRequest, @CurrentAdminUser AdminUser operator) {



        return callerFacade.queryCallerPage(listRequest);
    }



    @RequestMapping(value = "/api/caller/outcall", method = RequestMethod.GET)
    @ResponseBody
    public String outcall(CallerOutCallRequest request, @CurrentAdminUser AdminUser operator) throws Exception {



        return callerFacade.outcall(request);

    }

    @RequestMapping(value = "/api/caller/sendSms", method = RequestMethod.GET)
    @ResponseBody
    public String sendSms(CallerSendSmsRequest request, @CurrentAdminUser AdminUser operator) throws Exception {




        return callerFacade.sendSms(request);

    }



}
