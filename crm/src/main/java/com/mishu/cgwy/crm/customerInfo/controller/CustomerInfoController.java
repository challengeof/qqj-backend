package com.mishu.cgwy.crm.customerInfo.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.wrapper.BlockWrapper;
import com.mishu.cgwy.profile.controller.*;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.vo.RestaurantAuditReviewVo;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QueryValueResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2016/3/3.
 */
@Controller
public class CustomerInfoController {
//    @Autowired
//    private CustomerService customerService;

//    @Autowired
//    private CustomerManageFacade customerManageFacade;

    @Autowired
    private CustomerFacade customerFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    /**
     * 增加客户
     */
    @RequestMapping(value = "/api/customerInfo/add", method = RequestMethod.POST)
    @ResponseBody
    public void addRestaurant(@RequestBody RestaurantInfoRequest request , @CurrentAdminUser AdminUser adminUser){
        customerFacade.addRestaurant(request,adminUser);
    }

    /**
     * 获取客户的信息
     * @return
     */
    @RequestMapping(value = "/api/customerInfo/restaurantInfo/{restaurantId}", method = RequestMethod.GET)
    @ResponseBody
    public QueryValueResponse<RestaurantInfoVo> getRestaurant(@PathVariable Long restaurantId){
        RestaurantInfoVo infoVo = customerFacade.getRestaurantInfo(restaurantId);

        return new QueryValueResponse<>(infoVo);
    }

    /**
     * 修改客户信息
     */
    @RequestMapping(value = "/api/customerInfo/modify", method = RequestMethod.POST)
    @ResponseBody
    public void modifyRestaurant(@RequestBody RestaurantInfoRequest request , @CurrentAdminUser AdminUser adminUser){
        customerFacade.updateRestaurant(request,adminUser);
    }

    /**
     * 商户审核
     */
    @RequestMapping(value = "/api/customerInfo/audit", method = RequestMethod.POST)
    @ResponseBody
    public void auditRestaurant(@RequestBody RestaurantInfoRequest request , @CurrentAdminUser AdminUser adminUser){


        //修改审核状态
        customerFacade.updateRestaurant(request,adminUser);
    }

    /**
     * 分配
     */
    @RequestMapping(value = "/api/customerInfo/allot", method = RequestMethod.POST)
    @ResponseBody
    public void customerAllot(@RequestBody CustomerSellerChangeRequest request , @CurrentAdminUser AdminUser adminUser){

        customerFacade.customerAllot(request,adminUser);
    }

    /**
     * 销售员认领客户
     */
    @RequestMapping(value = "/api/customerInfo/claim", method = RequestMethod.POST)
    @ResponseBody
    public void sellerclaim(@RequestBody CustomerSellerChangeRequest request , @CurrentAdminUser AdminUser adminUser){

        customerFacade.sellerClaim(request,adminUser);
    }

    /**
     * 商户投放公海
     */
    @RequestMapping(value = "/api/customerInfo/onto", method = RequestMethod.POST)
    @ResponseBody
    public void ontoSea(@RequestBody CustomerSellerChangeRequest request , @CurrentAdminUser AdminUser adminUser){
        customerFacade.ontoSea(request,adminUser);
    }

    /**
     * 申请审核餐馆
     */
    @RequestMapping(value = "/api/customerInfo/auditInvite/{restaurantId}", method = RequestMethod.POST)
    @ResponseBody
    public void reqAudit( @PathVariable("restaurantId") Long restaurantId , @CurrentAdminUser AdminUser adminUser){
        customerFacade.reqAudit(restaurantId,adminUser);
    }

    /**
     * 各种审核
     */
    @RequestMapping(value = "/api/customerInfo/auditReq", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantAuditReviewVo> auditReq(RestaurantAuditRequest request , @CurrentAdminUser AdminUser adminUser){
        //      customerFacade.customerClaimAudit(request,adminUser);
        List<RestaurantAuditReviewVo> auditReviews  =  customerFacade.claimAudit( request, adminUser);
        return auditReviews;
    }


    /**
     * 审核列表页
     */
    @RequestMapping(value = "/api/customerInfo/audit/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<RestaurantAuditReviewVo> auditList(RestaurantAuditInfoQueryRequest request, @CurrentAdminUser AdminUser adminUser) {
        QueryResponse<RestaurantAuditReviewVo> response = customerFacade.auditList(request, adminUser);
        return response;
    }

    /**
     * 根据坐标计算所在区块
     */
    @RequestMapping(value = "/api/customerInfo/blockAuto", method = RequestMethod.GET)
    @ResponseBody
    public QueryValueResponse getCooperatingStates(BlockAutoRequest blockAutoRequest){
        Block block = this.customerFacade.reckonBlock(blockAutoRequest.getLng(),blockAutoRequest.getLat(),blockAutoRequest.getCityId());
        BlockWrapper blockWrapper = null;
        if(block!=null){
            blockWrapper=new BlockWrapper(block);
        }
        return new QueryValueResponse(blockWrapper,true);
    }


}
