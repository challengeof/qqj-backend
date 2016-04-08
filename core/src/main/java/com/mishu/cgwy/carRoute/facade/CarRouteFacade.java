package com.mishu.cgwy.carRoute.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.carRoute.domain.CarRoute;
import com.mishu.cgwy.carRoute.request.CarRoutePostData;
import com.mishu.cgwy.carRoute.request.CarRouteRequestQuery;
import com.mishu.cgwy.carRoute.service.CarRouteService;
import com.mishu.cgwy.carRoute.vo.CarRouteVo;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.service.DepotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by xgl on 2016/04/05.
 */
@Service
public class CarRouteFacade {

    @Autowired
    private CarRouteService carRouteService;
    @Autowired
    private CityRepository cityRepository;
    @Autowired
    private DepotService depotService;

    @Transactional(readOnly = true)
    public QueryResponse<CarRouteVo> getCarRouteList(CarRouteRequestQuery query){
        List<CarRouteVo> carRouteVoList = new ArrayList<>();

        Page<CarRoute> carRoutePage = carRouteService.getCarRouteList(query);

        if(carRoutePage != null && carRoutePage.getSize() > 0){
            for(CarRoute carRoute : carRoutePage){
                carRouteVoList.add(new CarRouteVo(carRoute));
            }
        }

        QueryResponse<CarRouteVo> res = new QueryResponse<>();

        res.setContent(carRouteVoList);
        res.setTotal(carRoutePage.getTotalElements());
        res.setPage(query.getPage());
        res.setPageSize(query.getPageSize());

        return res;
    }

    @Transactional
    public void deleteCarRoute(Long id){
        carRouteService.deleteCarRoute(id);
    }

    @Transactional
    public CarRouteVo updateCarRoute(CarRoutePostData data, AdminUser adminUser){
        CarRoute carRoute = new CarRoute();
        putCarRoute(carRoute,data,adminUser);
        return new CarRouteVo(carRouteService.updateCarRoute(carRoute));
    }

    @Transactional
    public CarRouteVo findOne(Long id){
        return new CarRouteVo(carRouteService.findOne(id));
    }

    private void putCarRoute(CarRoute carRoute,CarRoutePostData data,AdminUser adminUser){
        if(data.getId() != null){
            carRoute.setId(data.getId());
        }
        carRoute.setName(data.getName());
        carRoute.setCity(cityRepository.findOne(data.getCityId()));
        carRoute.setDepot(depotService.findOne(data.getDepotId()));
        carRoute.setPrice(data.getPrice());
        carRoute.setOperator(adminUser);
    }
}
