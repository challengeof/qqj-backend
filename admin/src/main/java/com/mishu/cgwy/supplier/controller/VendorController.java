package com.mishu.cgwy.supplier.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.VendorQueryRequest;
import com.mishu.cgwy.admin.dto.VendorQueryResponse;
import com.mishu.cgwy.admin.dto.VendorRequest;
import com.mishu.cgwy.admin.facade.VendorFacade;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.vendor.controller.CurrentVendor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bowen on 2015/4/10.
 */
@Controller
public class VendorController {

    @Autowired
    private VendorFacade vendorFacade;

    @RequestMapping(value = "/api/vendor",method = RequestMethod.POST)
    @ResponseBody
    public void createVendor(@RequestBody VendorRequest vendorRequest, @CurrentAdminUser AdminUser submitter){
        vendorFacade.createVendor(vendorRequest, submitter);
    }

    @RequestMapping(value = "/api/vendor/{id}",method = RequestMethod.PUT)
    @ResponseBody
    public void updateSupplier(@PathVariable("id") Long id,@RequestBody VendorRequest vendorRequest){
        vendorFacade.updateVendor(id, vendorRequest);
    }

    @RequestMapping(value = "/vendor-api/vendor/updateVendorPassword", method = RequestMethod.PUT)
    @ResponseBody
    public void updateVendorPassword(@CurrentVendor Vendor vendor, @RequestParam("oldPassword") String oldPassword, @RequestParam("newPassword") String newPassword) {
        vendorFacade.updatePassword(vendor, oldPassword, newPassword);
    }

    @RequestMapping(value = "/api/vendor",method = RequestMethod.GET)
    @ResponseBody
    public VendorQueryResponse listAllVendors(VendorQueryRequest request, @CurrentAdminUser AdminUser adminUser){
        return vendorFacade.getVendors(request, adminUser);
    }

    @RequestMapping(value = "/api/vendor/{id}",method = RequestMethod.GET)
    @ResponseBody
    public VendorVo getSupplier(@PathVariable("id") Long id){
        return vendorFacade.getVendorById(id);
    }

    @RequestMapping(value = "/api/vendor/pwd/init", method = RequestMethod.GET)
    @ResponseBody
    public void pwd() {
        vendorFacade.pwd();
    }

    @RequestMapping(value = "/api/vendor/updateVendorPassword", method = RequestMethod.POST)
    @ResponseBody
    public void updateVendorPassword(@RequestParam("vendorId") Long vendorId, @RequestParam("password") String password) {
        vendorFacade.updatePassword(vendorId, password);
    }
}
