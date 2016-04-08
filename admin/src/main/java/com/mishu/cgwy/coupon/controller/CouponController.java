package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.constant.PromotionConstant;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.SendCouponReason;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.coupon.wrapper.CouponConstantWrapper;
import com.mishu.cgwy.coupon.wrapper.PromotionConstantWrapper;
import com.mishu.cgwy.coupon.wrapper.PromotionFullWrapper;
import com.mishu.cgwy.order.wrapper.CouponWrapper;
import com.mishu.cgwy.order.wrapper.CustomerCouponWrapper;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.promotion.controller.PromotionListRequest;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Controller
public class CouponController {

    @Autowired
    private CouponService couponService;

    @Autowired
    private PromotionService promotionService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }


    @RequestMapping(value = "/api/coupon/customer/expird", method = RequestMethod.GET)
    @ResponseBody
    public void expirdCustomerCoupon(@RequestParam(value = "begindate",required = true) Date begindate){
         couponService.expirdCustomerCoupon(begindate);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/customer/cancelled/{customerCouponId}", method = RequestMethod.GET)
    @ResponseBody
    public CustomerCouponWrapper cancelledCustomerCoupon(@PathVariable("customerCouponId") Long customerCouponId , @CurrentAdminUser AdminUser operater){
        return couponService.customerCouponCancelledSetting(customerCouponId, operater);

    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleCouponWrapper> listCoupons(CouponListRequest request) {
        return couponService.getCouponList(request);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CouponWrapper getCoupon(@PathVariable("id")Long id) throws Exception {
        return couponService.getCoupon(id);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/create", method = RequestMethod.POST)
    @ResponseBody
    public void createCoupon(@RequestBody CouponRequest couponRequest) throws Exception {
        couponService.createCoupon(couponRequest);
    }

    @RequestMapping(value = "/api/coupon/couponStatus", method = RequestMethod.GET)
    @ResponseBody
    public List<CouponStatus> getCouponStatus(){
        return Arrays.asList(CouponStatus.values());
    }

    @RequestMapping(value = "/api/coupon/couponEnums", method = RequestMethod.GET)
    @ResponseBody
    public List<CouponConstantWrapper> getOrganizationsByCityId() {
        return CouponConstant.getCouponConstants();
    }

    @RequestMapping(value = "/api/coupon/sendCouponReasons", method = RequestMethod.GET)
    @ResponseBody
    public SendCouponReason[] getSendCouponReasons() {
        return SendCouponReason.values();
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/send", method = RequestMethod.PUT)
    @ResponseBody
    public void sendCoupon(@RequestBody SendCouponRequest sendCouponRequest, @CurrentAdminUser AdminUser adminUser) throws Exception {
        couponService.sendCoupon(sendCouponRequest, adminUser);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/edit/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateCoupon(@PathVariable("id")Long id, @RequestBody CouponRequest couponRequest) throws Exception {
        couponService.updateCoupon(id, couponRequest);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/coupon/del", method = RequestMethod.GET)
    @ResponseBody
    public void delCoupon() {
        return;
    }


    @RequestMapping(value = "/api/promotion/promotionEnums", method = RequestMethod.GET)
    @ResponseBody
    public List<PromotionConstantWrapper> getActivityType() {
        return PromotionConstant.getPromotionConstants();
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/promotion/edit/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updatePromotion(@PathVariable("id")Long id, @RequestBody PromotionRequest promotionRequest) throws Exception {
        promotionService.updatePromotion(id, promotionRequest);
    }


    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/promotion/create", method = RequestMethod.POST)
    @ResponseBody
    public void createPromotion(@RequestBody PromotionRequest promotionRequest) throws Exception {
        promotionService.createPromotion(promotionRequest);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/promotion",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<PromotionWrapper> listPromotions(PromotionListRequest request) {
        return promotionService.getPromotionList(request);
    }

    @Secured("PRODUCT_ADMIN")
    @RequestMapping(value = "/api/promotion/{id}", method = RequestMethod.GET)
    @ResponseBody
    public PromotionFullWrapper getPromotion(@PathVariable("id")Long id) throws Exception {
        return promotionService.getPromotion(id);
    }

    @RequestMapping(value = "/api/promotion/convert", method = RequestMethod.GET)
    @ResponseBody
    public void convert() throws Exception {
        promotionService.convert();
    }
}
