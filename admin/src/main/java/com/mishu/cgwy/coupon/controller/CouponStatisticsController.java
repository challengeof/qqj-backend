package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedDetailWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedTotalWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsWrapper;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2015/12/14.
 */
@Controller
public class CouponStatisticsController {

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


    @RequestMapping(value = "/api/coupon/statistics/search/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSearch(CouponStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<CouponStatisticsWrapper> datas =  couponService.getCouponStatistics(request);

        return couponService.exportCustomerCoupon(request,datas.getContent(),"couponSearch.xls", CouponService.CUSTOMER_COUPON_LIST);
    }

    @RequestMapping(value = "/api/coupon/statistics/used/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportUsed(CouponStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        List<CouponStatisticsUsedWrapper> datas = couponService.getCouponStatisticsByUsed(request);
        return couponService.exportCustomerCoupon(request,datas,"couponUsex.xls", CouponService.CUSTOMER_COUPON_STATISTICS_LIST);
    }

    @RequestMapping(value = "/api/coupon/statistics/provide/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportProvide(CouponStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<CouponStatisticsWrapper> datas =  couponService.getCouponStatistics(request);
        return couponService.exportCustomerCoupon(request,datas.getContent(),"couponProvide.xls",CouponService.CUSTOMER_COUPON_SEND_LIST);
    }

    @RequestMapping(value = "/api/coupon/statistics/usedDetail/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportUsedDetail(CouponStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<CouponStatisticsUsedDetailWrapper> datas =this.couponService.getCouponStatisticsByUsedDetail(request);
        return couponService.exportCustomerCoupon(request,datas.getContent(),"couponUsedDetail.xls",CouponService.CUSTOMER_COUPON_USEDDETAIL_LIST);
    }


    /**
     * 优惠券使用统计查询
     */
    @RequestMapping(value = "/api/coupon/statistics/used", method = RequestMethod.GET)
    @ResponseBody
    public CouponStatisticsUsedResponse queryUsed(CouponStatisticsRequest request) {
        List<CouponStatisticsUsedWrapper> usedWrappers = couponService.getCouponStatisticsByUsed(request);
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize() );

        CouponStatisticsUsedTotalWrapper usedTotalWrapper = new CouponStatisticsUsedTotalWrapper(usedWrappers);
        CouponStatisticsUsedResponse usedResponse = new CouponStatisticsUsedResponse();

        List<CouponStatisticsUsedWrapper> usedContent = usedWrappers;
        if(usedContent.size()!=0) {
            int fromIndex = pageRequest.getOffset() >= usedWrappers.size() ? usedWrappers.size() - 1 : pageRequest.getOffset();
            int toIndex = pageRequest.getOffset() + pageRequest.getPageSize() > usedWrappers.size() ? usedWrappers.size() : pageRequest.getOffset() + pageRequest.getPageSize();
            usedContent = usedWrappers.subList(fromIndex, toIndex);
        }
        usedResponse.setPage(request.getPage());
        usedResponse.setPageSize(request.getPageSize());
        usedResponse.setTotal(usedWrappers.size());
        usedResponse.setContent(usedContent);
        usedResponse.setLineTotal(usedTotalWrapper);
        usedResponse.setSuccess(true);
        return usedResponse;
    }





    /**
     * 优惠券查询、发放明细
     */
    @RequestMapping(value = "/api/coupon/statistics/provide", method = RequestMethod.GET)
    @ResponseBody
    public CouponStatisticsResponse queryCoupon(CouponStatisticsRequest request) {

        Page<CouponStatisticsWrapper> page = couponService.getCouponStatistics(request);
        CouponStatisticsWrapper couponStatisticsWrapper = couponService.getCouponStatisticsSum(request);
        CouponStatisticsResponse res = new CouponStatisticsResponse();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        res.setLineTotal(couponStatisticsWrapper);

        res.setSuccess(true);
        return res;
    }

//    /**
//     * 优惠券查询、发放明细  — 金额合计
//     */
//    @RequestMapping(value = "/api/coupon/statistics/provideSum", method = RequestMethod.GET)
//    @ResponseBody
//    public BigDecimal queryCouponSum(CouponStatisticsRequest request) {
//        BigDecimal sumVal = couponService.getSumDiscountBycouponStatistic(request);
//        return sumVal;
//    }




    /**
     * 优惠券使用明细查询
     */
    @RequestMapping(value = "/api/coupon/statistics/usedDetail", method = RequestMethod.GET)
    @ResponseBody
    public CouponStatisticsUsedDetailResponse queryUsedDetail(CouponStatisticsRequest request) {
        Page<CouponStatisticsUsedDetailWrapper> page =this.couponService.getCouponStatisticsByUsedDetail(request);

        BigDecimal sum =this.couponService.getSumDiscountByUsedDetail(request);
        CouponStatisticsUsedDetailResponse res = new CouponStatisticsUsedDetailResponse();
        res.setContent(page.getContent());
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        res.setLineTotal(sum);
        res.setSuccess(true);
        return res;
    }




//    /**
//     * 优惠券使用明细——金额合计
//     */
//    @RequestMapping(value = "/api/coupon/statistics/usedDetailSum", method = RequestMethod.GET)
//    @ResponseBody
//    public BigDecimal queryusedDetailSum(CouponStatisticsRequest request) {
//        BigDecimal sum =this.couponService.getSumDiscountByUsedDetail(request);
//        return sum;
//    }


//    /**
//     * 优惠券发放明细查询
//     */
//    @RequestMapping(value = "/api/coupon/statistics/provide", method = RequestMethod.GET)
//    @ResponseBody
//    public void queryProvide(CouponStatisticsRequest reqeust) {
//
////        Page<CouponStatisticsWrapper> page = couponService.getCouponStatistics(request);
////        QueryResponse<CouponStatisticsWrapper> res = new QueryResponse<CouponStatisticsWrapper>();
////        res.setContent(page.getContent());
////        res.setPage(request.getPage());
////        res.setPageSize(request.getPageSize());
////        res.setTotal(page.getTotalElements());
////        res.setSuccess(true);
////        return res;
//    }



}
