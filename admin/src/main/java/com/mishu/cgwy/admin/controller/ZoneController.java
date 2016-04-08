package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.service.LocationService;
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
public class ZoneController {

    @Autowired
    private LocationFacade locationFacade;
    @Autowired
    private LocationService locationService;


    @RequestMapping(value = "/api/zone", method = RequestMethod.GET)
    @ResponseBody
    public List<ZoneWrapper> listZones(@RequestParam(value = "active", required = false) Boolean active) {

        if (active == null) {
            return locationFacade.getZones();
        } else {
            return locationFacade.getZones(active);
        }

    }

    @RequestMapping(value = "/api/zone/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ZoneWrapper updateZone(@PathVariable("id") Long id,
                                  @RequestParam(value = "active", required = false) Boolean active,
                                  @RequestParam(value = "warehouseId", required = false) Long warehouseId) {
        Zone zone = locationService.getZone(id);

        if (active != null) {
            zone.setActive(active);
        }
        if (warehouseId != null) {
            zone.setWarehouse(locationService.getWarehouse(warehouseId));
        }
        return new ZoneWrapper(locationService.saveZone(zone));
    }
}
