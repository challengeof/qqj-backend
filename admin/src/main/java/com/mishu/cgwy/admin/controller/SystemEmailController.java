package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.controller.SystemEmailRequest;
import com.mishu.cgwy.common.domain.SystemEmailType;
import com.mishu.cgwy.common.dto.SystemEmailData;
import com.mishu.cgwy.common.facade.SystemEmailFacade;
import com.mishu.cgwy.common.wrapper.SystemEmailWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class SystemEmailController {

    @Autowired
    private SystemEmailFacade systemEmailFacade;
    @RequestMapping(value = "/api/systemEmail/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SystemEmailWrapper> systemEmailList(SystemEmailRequest request, @CurrentAdminUser AdminUser adminUser) {
        return systemEmailFacade.getSystemEmailList(request, adminUser);
    }

    @RequestMapping(value = "/api/systemEmail/{id}", method = RequestMethod.GET)
    @ResponseBody
    public SystemEmailWrapper findSystemEmail(@PathVariable("id") Long id) {
        return systemEmailFacade.findSystemEmail(id);
    }

    @RequestMapping(value = "/api/systemEmail", method = RequestMethod.POST)
    @ResponseBody
    public SystemEmailWrapper addSystemEmail(@RequestBody SystemEmailData systemEmailData) {
        return systemEmailFacade.addSystemEmail(systemEmailData);
    }

    @RequestMapping(value = "/api/systemEmail/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public SystemEmailWrapper updateSystemEmail(@PathVariable("id") Long id, @RequestBody SystemEmailData systemEmailData) {
        return systemEmailFacade.updateSystemEmail(id, systemEmailData);
    }

    @RequestMapping(value = "/api/systemEmail/del", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteSystemEmail(@RequestBody SystemEmailData systemEmailData) {
        systemEmailFacade.deleteSystemEmail(systemEmailData);
    }

    @RequestMapping(value = "/api/systemEmail/type/list", method = RequestMethod.GET)
    @ResponseBody
    public SystemEmailType[] getStockOutType() {
        return SystemEmailType.values();
    }

}
