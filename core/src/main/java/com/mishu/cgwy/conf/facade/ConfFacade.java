package com.mishu.cgwy.conf.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.controller.WarehouseRequest;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.vo.CityVo;
import com.mishu.cgwy.common.wrapper.*;
import com.mishu.cgwy.conf.domain.ConfEnum;
import com.mishu.cgwy.conf.service.ConfService;
import com.mishu.cgwy.conf.vo.OrderLimit;
import com.mishu.cgwy.organization.controller.AddBlockRequest;
import com.mishu.cgwy.organization.controller.BlockQueryRequest;
import com.mishu.cgwy.organization.controller.BlockQueryResponse;
import com.mishu.cgwy.organization.controller.UpdateBlockQueryRequest;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.elasticsearch.common.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

@Service
public class ConfFacade {

    @Autowired
    private ConfService confService;

    @Autowired
    private LocationService locationService;

    public List<OrderLimit> getOrderLimitList(AdminUser adminUser) throws Exception {

        Map<String, String> confMap = confService.getConfMap(ConfEnum.ORDER_LIMIT.getName());
        List<City> cities = locationService.getAllCities();

        List<OrderLimit> orderLimitList = new ArrayList<>();

        for (City city : cities) {
            String limit = confMap.get(String.valueOf(city.getId()));
            Long limitValue = limit == null ? 0 : Long.valueOf(limit);

            OrderLimit orderLimit = new OrderLimit();
            CityVo cityVo = new CityVo();
            cityVo.setId(city.getId());
            cityVo.setName(city.getName());

            orderLimit.setCity(cityVo);
            orderLimit.setLimit(limitValue);

            orderLimitList.add(orderLimit);
        }

        return orderLimitList;
    }
}
