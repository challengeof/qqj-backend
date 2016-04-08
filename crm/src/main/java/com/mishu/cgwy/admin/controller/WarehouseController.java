package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.controller.WarehouseRequest;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.WarehouseWrapper;
import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * User: xudong
 * Date: 3/26/15
 * Time: 10:34 AM
 */
@Controller
public class WarehouseController {

    @Autowired
    private LocationFacade locationFacade;

    @RequestMapping(value = "/api/warehouse", method = RequestMethod.GET)
    @ResponseBody
    public List<WarehouseWrapper> listWarehouses(@RequestParam(value = "cityId", required = false) Long cityId) {
        if (cityId != null) {
            return locationFacade.getAllWarehouse(cityId);
        }
        return locationFacade.getAllWarehouses();
    }

    @RequestMapping(value = "/api/warehouse/{id}", method = RequestMethod.GET)
    @ResponseBody
    public WarehouseWrapper getWarehouseById (@PathVariable("id") Long id) {
        return locationFacade.findWarehouseById(id);
    }

    @RequestMapping(value = "/api/warehouse/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public WarehouseWrapper updateWarehouse (@PathVariable("id") Long id, @RequestBody WarehouseRequest request) {
        return locationFacade.updateWarehouse(id, request);
    }

    @RequestMapping(value = "/api/warehouse", method = RequestMethod.POST)
    @ResponseBody
    public WarehouseWrapper createWarehouse (@RequestBody WarehouseRequest request) {
        return locationFacade.createWarehouse(request);
    }

    @RequestMapping(value = "/api/warehouse/{id}/zones", method = RequestMethod.GET)
    @ResponseBody
    public List<ZoneWrapper> listZone(@PathVariable("id") Long warehouseId) {
        return locationFacade.getZonesInWarehouse(warehouseId);
    }
    @RequestMapping(value = "/api/warehouse/isDefault/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public List<WarehouseWrapper> updateWarehouse(@PathVariable("id") Long id){
        return  locationFacade.updateWarehouseDefault(id);

    }

    @RequestMapping(value = "/api/warehouse/depot/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<WarehouseWrapper> getDepotWarehouse(@PathVariable("id") Long depotId, @CurrentAdminUser AdminUser adminUser) {
        return locationFacade.getWarehouseByDepotId(depotId, adminUser);
    }

    @RequestMapping(value = "/api/warehouse/city/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<WarehouseWrapper> getCityWarehouse(@PathVariable("id") Long cityId, @CurrentAdminUser AdminUser adminUser) {
        return locationFacade.getWarehouseByCityId(cityId, adminUser);
    }

}
