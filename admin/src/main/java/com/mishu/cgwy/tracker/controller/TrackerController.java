package com.mishu.cgwy.tracker.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.order.controller.EvaluateResponse;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.facade.OrderGroupFacade;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.SellCancelType;
import com.mishu.cgwy.stock.domain.StockOutType;
import com.mishu.cgwy.stock.dto.SellCancelQueryRequest;
import com.mishu.cgwy.stock.dto.SellReturnQueryRequest;
import com.mishu.cgwy.stock.dto.StockOutData;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.facade.DepotFacade;
import com.mishu.cgwy.stock.facade.SellCancelFacade;
import com.mishu.cgwy.stock.facade.SellReturnFacade;
import com.mishu.cgwy.stock.facade.StockOutFacade;
import com.mishu.cgwy.stock.vo.DepotVo;
import com.mishu.cgwy.stock.wrapper.*;
import com.mishu.cgwy.utils.LineUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by bowen on 15/8/13.
 */

@Controller
public class TrackerController {

    @Autowired
    private OrderFacade orderFacade;

    @Autowired
    private OrderGroupFacade orderGroupFacade;
    @Autowired
    private StockOutFacade stockOutFacade;
    @Autowired
    private SellCancelFacade sellCancelFacade;
    @Autowired
    private SellReturnFacade sellReturnFacade;
    @Autowired
    private DepotFacade depotFacade;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/v2/orderGroupPoint", method = RequestMethod.GET)
    @ResponseBody
    public DepotWrapper findOrderGroupPointByTracker(@CurrentAdminUser AdminUser tracker) {

        return stockOutFacade.findOrderGroupPointByTracker(tracker);
    }

    @RequestMapping(value = "/api/v2/orderGroups", method = RequestMethod.GET)
    @ResponseBody
    public List<StockOutOrderWrapper> findOrderGroupsByTracker(@CurrentAdminUser AdminUser tracker) {

        return stockOutFacade.findStockOutOrdersByTracker(tracker);
        //return orderGroupFacade.findOrderGroupsByTracker(tracker, expectedArrivedDate);
    }


    @RequestMapping(value = "/api/v2/orderGroupsApp", method = RequestMethod.GET)
    @ResponseBody
    public List<StockOutOrderWrapper> findOrderGroupsByTrackerApp(@CurrentAdminUser AdminUser tracker , @RequestParam(value = "lat")double lat , @RequestParam(value = "lon")double lon) {
        Map<String,Object> resMap = new HashMap<String,Object>();
        List<StockOutOrderWrapper> orderGroups = stockOutFacade.findStockOutOrdersByTracker(tracker);
//        List<double[]> latAndLon = new ArrayList<double[]>();
//        for(StockOutOrderWrapper orderGroup : orderGroups){
//            double mLat = orderGroup.getRestaurant().getAddress().getWgs84Point().getLatitude();
//            double mLon = orderGroup.getRestaurant().getAddress().getWgs84Point().getLongitude();
//            latAndLon.add(new double[]{mLat,mLon});
//        }

        List<StockOutOrderWrapper> rsLatAndLon = new ArrayList<StockOutOrderWrapper>();
        LineUtils.lineSortByOrder(new double[]{lat, lon}, orderGroups, rsLatAndLon);
        return rsLatAndLon;
    }



    @RequestMapping(value = "/api/v2/orderGroups/{id}", method = RequestMethod.GET)
    @ResponseBody
    public StockOutOrderWrapper getStockOutOrder(@PathVariable("id") Long id) {
        return stockOutFacade.getStockOutOrderById(id);
    }

    @RequestMapping(value = "/api/v2/order-group", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleStockOutGroupWrapper> findOrderGroupsByOperator(@CurrentAdminUser AdminUser tracker,
                                                                      @RequestParam(value = "trackerName", required = false) String trackerName) {

        return stockOutFacade.findStockOutGroupsByOperator(tracker, trackerName);
        //return orderGroupFacade.findOrderGroupsByOperator(tracker, expectedArrivedDate);
    }

    @RequestMapping(value = "/api/v2/tracker/evaluate", method = RequestMethod.GET)
    @ResponseBody
    public EvaluateResponse getEvaluateByTracker(@CurrentAdminUser AdminUser operator) {

        return orderFacade.getEvaluateByTracker(operator);
    }


    @RequestMapping(value = "/api/v2/order-group/{id}", method = RequestMethod.GET)
    @ResponseBody
    public StockOutGroupsSku getOrderGroup(@PathVariable("id") Long id,
                                           @CurrentAdminUser AdminUser operator) {
        return stockOutFacade.getStockOutGroupById(id);
        // return orderGroupFacade.getOrderGroupById(id);
    }

    @RequestMapping(value = "/api/v2/update/checkResult/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void updateOrderGroupCheckResult(@PathVariable("id") Long id,
                                            @RequestBody StockOutData stockOutData,
                                            @CurrentAdminUser AdminUser operator) {

        stockOutFacade.orderStockOutConfirmOut(id, stockOutData, operator);
        //return orderGroupFacade.updateOrderGroupCheckResult(id , checkResult);
    }

    //收货查询(司机版)
    @RequestMapping(value = "/api/v2/tracker/stockOutReceive/list", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<StockOutWrapper> getStockOutReceiveByTracker(StockOutRequest request, @CurrentAdminUser AdminUser tracker) {
        if (tracker == null) {
            return null;
        } else {
            request.setTrackerId(tracker.getId());
            request.setStockOutType(StockOutType.ORDER.getValue());
            return stockOutFacade.getStockOutList(request, tracker);
        }
    }

    //订单取消(司机版)
//    @RequestMapping(value = "/api/v2/tracker/sellCancel/list", method = RequestMethod.GET)
//    @ResponseBody
//    public QueryResponse<SellCancelWrapper> getSellCancelByTracker(SellCancelQueryRequest request, @CurrentAdminUser AdminUser tracker) {
//        if (tracker == null) {
//            return null;
//        } else {
//            request.setTrackerId(tracker.getId());
//            return sellCancelFacade.getSellCancelList(request, tracker);
//        }
//    }

    //订单取消明细(司机版)
    @RequestMapping(value = "/api/v2/tracker/sellCancelItem/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleSellCancelItemWrapper> getSellCancelItemByTracker(SellCancelQueryRequest request, @CurrentAdminUser AdminUser tracker) {
        if (tracker == null) {
            return null;
        } else {
            request.setTrackerId(tracker.getId());
            request.setType(SellCancelType.DEPOT_CANCEL.getValue());
            request.setStartCancelDate(DateUtils.truncate(new Date(), Calendar.DATE));
//            request.setEndCancelDate(DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DATE), 1));
            return sellCancelFacade.getSellCancelItemList(request, tracker);
        }
    }

    //订单退货(司机版)
//    @RequestMapping(value = "/api/v2/tracker/sellReturn/list", method = RequestMethod.GET)
//    @ResponseBody
//    public QueryResponse<SimpleSellReturnWrapper> getSellReturnByTracker(SellReturnQueryRequest request, @CurrentAdminUser AdminUser tracker) {
//        if (tracker == null) {
//            return null;
//        } else {
//            request.setTrackerId(tracker.getId());
//            return sellReturnFacade.getSellReturn(request, tracker);
//        }
//    }

    //订单退货明细(司机版)
    @RequestMapping(value = "/api/v2/tracker/sellReturnItem/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleSellReturnItemWrapper> getSellReturnItemByTracker(SellReturnQueryRequest request, @CurrentAdminUser AdminUser tracker) {
        if (tracker == null) {
            return null;
        } else {
            request.setTrackerId(tracker.getId());
            return sellReturnFacade.getSellReturnItem(request, tracker);
        }
    }

    //操作员城市
    @RequestMapping(value = "/api/v2/cities", method = RequestMethod.GET)
    @ResponseBody
    public List<CityVo> findOperatorCities(@CurrentAdminUser AdminUser operator) {
        List<CityVo> cityVos = new ArrayList<>();
        Set<City> cities = operator.getCities();
        for (City city : cities) {
            CityVo cityVo = new CityVo();
            cityVo.setId(city.getId());
            cityVo.setName(city.getName());
            cityVos.add(cityVo);
        }
        return cityVos;
    }

    @RequestMapping(value = "/api/v2/depotList/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<DepotVo> findOperatorDepots(@PathVariable("id") Long cityId, @CurrentAdminUser AdminUser adminUser) {
        return depotFacade.findDepotsVoByCityId(cityId, adminUser);
    }

}
