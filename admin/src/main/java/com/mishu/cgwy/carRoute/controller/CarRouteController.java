package com.mishu.cgwy.carRoute.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.carRoute.facade.CarRouteFacade;
import com.mishu.cgwy.carRoute.request.CarRoutePostData;
import com.mishu.cgwy.carRoute.request.CarRouteRequestQuery;
import com.mishu.cgwy.carRoute.vo.CarRouteVo;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by xgl on 2016/04/06.
 */
@Controller
public class CarRouteController {
    @Autowired
    private CarRouteFacade carRouteFacade;


    @RequestMapping(value = "/api/carRoute/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<CarRouteVo> getCarRouteList(CarRouteRequestQuery query){
        return carRouteFacade.getCarRouteList(query);
    }

    @RequestMapping(value = "/api/carRoute/update",method = RequestMethod.POST)
    @ResponseBody
    public CarRouteVo updateCarRoute(@RequestBody CarRoutePostData data, @CurrentAdminUser AdminUser adminUsr){
        return carRouteFacade.updateCarRoute(data,adminUsr);
    }

    @RequestMapping(value = "/api/carRoute/{id}")
    @ResponseBody
    public CarRouteVo findOne(@PathVariable("id") Long id){
        return carRouteFacade.findOne(id);
    }

    @RequestMapping(value = "/api/carRoute/delete/{id}",method = RequestMethod.GET)
    public void deleteCarRoute(@PathVariable("id") Long id){
        carRouteFacade.deleteCarRoute(id);
    }

}
