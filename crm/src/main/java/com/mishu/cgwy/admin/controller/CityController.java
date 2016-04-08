package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.wrapper.*;
import com.mishu.cgwy.organization.facade.OrganizationFacade;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.stock.facade.DepotFacade;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by wangwei on 15/7/3.
 */
@Controller
public class CityController {

    @Autowired
    private LocationFacade locationFacade;

    @Autowired
    private OrganizationFacade organizationFacade;

    @Autowired
    private DepotFacade depotFacade;

    @RequestMapping(value = "/api/city", method = RequestMethod.GET)
    @ResponseBody
    public List<CityWrapper> getCities() {
        return locationFacade.getAllCityWrappers();
    }

    @RequestMapping(value = "/api/city", method = RequestMethod.POST)
    @ResponseBody
    public CityWrapper createCity(@RequestParam("name") String name) {
        return locationFacade.saveCity(null, name);
    }

    @RequestMapping(value = "/api/city/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CityWrapper getCity(@PathVariable("id") Long id) {
        return locationFacade.getCity(id);
    }

    @RequestMapping(value = "/api/city/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CityWrapper updateCity(@PathVariable("id") Long id, @RequestParam("name") String name) {
        return locationFacade.saveCity(id, name);
    }


    @RequestMapping(value = "/api/city/blocksTree", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeJsonHasChild> getCityBlocksTree(@CurrentAdminUser AdminUser adminUser){
        return locationFacade.getCityBlocksTree(adminUser);
    }

    @RequestMapping(value = "/api/city/depotsTree", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeJsonHasChild> getCityDepotTree() {
        return depotFacade.getCityDepots();
    }


    @RequestMapping(value = "/api/city/{id}/organizations", method = RequestMethod.GET)
    @ResponseBody
    public List<OrganizationVo> getOrganizationsByCityId(@PathVariable(value = "id") Long id,@CurrentAdminUser AdminUser adminUser){
        return organizationFacade.getOrganizationsByCityId(id, adminUser);
    }

    @RequestMapping(value = "/api/city/{id}/warehouses", method = RequestMethod.GET)
    @ResponseBody
    public List<WarehouseWrapper> listCityZones(@PathVariable("id") Long id, @CurrentAdminUser AdminUser adminUser) {
        return locationFacade.getAllWarehouse(id, adminUser);
    }

    @RequestMapping(value = "/api/city/warehouses/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<WarehouseWrapper> getCityWarehouseByWarehouseId(@PathVariable("id") Long warehouseId) {
        return locationFacade.getCityWarehouseByWarehouseId(warehouseId);
    }

    @RequestMapping(value = "/api/city/{id}/blocks", method = RequestMethod.GET)
    @ResponseBody
    public List<BlockWrapper> getBlockByCityId(@PathVariable("id") Long cityId, @RequestParam(value="status", required = false) Boolean status,
                                               @CurrentAdminUser AdminUser adminUser) {
        return  locationFacade.getBlockByCityId(cityId, adminUser, status);
    }
    @RequestMapping(value = "/api/city/{id}/simpleBlocks", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleBlockWrapper> getSimpleBlockByCityId(@PathVariable("id") Long cityId, @RequestParam(value="status", required = false) Boolean status,
                                                           @CurrentAdminUser AdminUser adminUser) {
        return  locationFacade.getSimpleBlockByCityId(cityId, adminUser, status);
    }

    @RequestMapping(value = "/api/city/{id}/zones", method = RequestMethod.GET)
    @ResponseBody
    public List<ZoneWrapper> listZones(@PathVariable(value = "id") Long cityId, @RequestParam(value = "active", required = false) Boolean active) {

        if (active == null) {
            return locationFacade.getZones(cityId);
        } else {
            return locationFacade.getZones(cityId, active);
        }

    }



}
