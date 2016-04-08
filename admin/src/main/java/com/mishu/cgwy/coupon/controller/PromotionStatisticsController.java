package com.mishu.cgwy.coupon.controller;

import com.mishu.cgwy.promotion.controller.PromotionStatisticsRequest;
import com.mishu.cgwy.promotion.controller.PromotionStatisticsResponse;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.promotion.vo.PromotionStatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by king-ck on 2015/12/14.
 */
@Controller
public class PromotionStatisticsController {

    @Autowired
    private PromotionService promotionService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }


    @RequestMapping(value = "/api/promotion/fullgift/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportFullgift(PromotionStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<PromotionStatisticsVo> datas =  promotionService.getPromotionStatistics(request);

        return promotionService.exportPromotionFullList(request, datas.getContent(), "promotion-fullcut-list.xls", PromotionService.PROMOTION_FULLGIFT_LIST);
    }
    @RequestMapping(value = "/api/promotion/fullcut/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportSearch(PromotionStatisticsRequest request) throws Exception {
        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<PromotionStatisticsVo> datas =  promotionService.getPromotionStatisticsFullCut(request);

        return promotionService.exportPromotionFullList(request, datas.getContent(), "promotion-fullcut-list.xls", PromotionService.PROMOTION_FULLCUT_LIST);
    }

    /**
     * 活动满赠查询
     */
    @RequestMapping(value = "/api/promotion/fullgift", method = RequestMethod.GET)
    @ResponseBody
    public PromotionStatisticsResponse<PromotionStatisticsVo,PromotionStatisticsVo> queryPromotionByFullgift(PromotionStatisticsRequest request) {
        Page<PromotionStatisticsVo> promotionWrappers = promotionService.getPromotionStatistics(request);
        PromotionStatisticsVo sumLine = promotionService.getPromotionStatisticsSum(request);
        PromotionStatisticsResponse<PromotionStatisticsVo,PromotionStatisticsVo>  result= new PromotionStatisticsResponse<>();

        result.setContent(promotionWrappers.getContent());
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(promotionWrappers.getTotalElements());
        result.setLineTotal(sumLine);
        result.setSuccess(true);
        return result;

    }



    /**
     * 活动满减查询
     */
    @RequestMapping(value = "/api/promotion/fullcut", method = RequestMethod.GET)
    @ResponseBody
    public PromotionStatisticsResponse<PromotionStatisticsVo,PromotionStatisticsVo> queryPromotionByFullcut(PromotionStatisticsRequest request) {

//        request.setPromotionType(PromotionConstant.FULL_MINUS.getType());
        Page<PromotionStatisticsVo> promotionWrappers = promotionService.getPromotionStatisticsFullCut(request);

        PromotionStatisticsVo sumLine = promotionService.getPromotionStatisticsFullCutSum(request);
        PromotionStatisticsResponse<PromotionStatisticsVo,PromotionStatisticsVo>  result= new PromotionStatisticsResponse<>();

        result.setContent(promotionWrappers.getContent());
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(promotionWrappers.getTotalElements());
        result.setLineTotal(sumLine);
        result.setSuccess(true);
        return result;

    }





}
