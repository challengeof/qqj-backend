package com.mishu.cgwy.vendor.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.facade.DepotFacade;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import com.mishu.cgwy.vendor.facade.VendorOrderFacade;
import com.mishu.cgwy.vendor.wrapper.VendorOrderHistory;
import com.mishu.cgwy.vendor.wrapper.VendorOrderItemWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class VendorOrderController {

    @Autowired
    private VendorOrderFacade vendorOrderItemFacade;

    @Autowired
    private DepotFacade depotFacade;

    @RequestMapping(value = "/vendor-api/vendor/order/notReady",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VendorOrderItemWrapper> getVendorOrderNotReady(@CurrentVendor Vendor vendor, @RequestParam(value="depotId", required=false) Long depotId) {
        return vendorOrderItemFacade.getVendorOrderItemsNotReady(vendor, depotId);
    }

    @RequestMapping(value = "/vendor-api/vendor/order/ready",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VendorOrderItemWrapper> getVendorOrderReady(@CurrentVendor Vendor vendor, @RequestParam(value="depotId", required=false) Long depotId) {
        return vendorOrderItemFacade.getVendorOrderItemsReady(vendor, depotId);
    }

    @RequestMapping(value = "/vendor-api/vendor/order/submit",method = RequestMethod.POST)
    @ResponseBody
    public Response submit(@CurrentVendor Vendor vendor, @RequestBody Long[] ids) {
        return vendorOrderItemFacade.submit(vendor, ids);
    }

    @RequestMapping(value = "/vendor-api/vendor/order/history",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VendorOrderHistory> getVendorOrderHistory(@CurrentVendor Vendor vendor, VendorOrderHistoryListRequest request) {
        return vendorOrderItemFacade.getVendorOrderHistory(vendor, request);
    }

    @RequestMapping(value = "/vendor-api/vendor/depot/list/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<DepotWrapper> vendorDepots(@CurrentVendor Vendor vendor, @PathVariable("id") Long cityId) {
        return depotFacade.findDepotsByCityId(cityId, vendor);
    }
}
