package com.mishu.cgwy.purchase.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.purchase.domain.ReturnNoteStatus;
import com.mishu.cgwy.purchase.facade.ReturnNoteFacade;
import com.mishu.cgwy.purchase.service.ReturnNoteService;
import com.mishu.cgwy.purchase.vo.ReturnNoteVo;
import com.mishu.cgwy.response.query.QueryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wangguodong on 15/10/10.
 */
@Controller
public class ReturnNoteController {

    @Autowired
    private ReturnNoteService returnNoteService;

    @Autowired
    private ReturnNoteFacade returnNoteFacade;

    @RequestMapping(value = "/api/purchase/order/returnNote/tmp/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ReturnNoteVo getReturnNoteTmp(@PathVariable("id")Long id) {
        return returnNoteFacade.getReturnNoteTmp(id);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/create",method = RequestMethod.POST)
    @ResponseBody
    public void saveReturnNote(@CurrentAdminUser AdminUser adminUser, @RequestBody ReturnNoteData returnNoteData) {
        returnNoteFacade.saveReturnNote(adminUser, returnNoteData);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/statuses", method = RequestMethod.GET)
    @ResponseBody
    public ReturnNoteStatus[] getReturnNoteStatuses() {
        return ReturnNoteStatus.values();
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<ReturnNoteVo> list(ReturnNoteRequest request, @CurrentAdminUser AdminUser operator) {
        return returnNoteService.getReturnNoteList(request, operator);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/{id}",method = RequestMethod.GET)
    @ResponseBody
    public ReturnNoteVo getReturnNote(@PathVariable("id")Long id) {
        return returnNoteFacade.getReturnNote(id);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/audit", method = RequestMethod.POST)
    @ResponseBody
    public void audit(@CurrentAdminUser AdminUser adminUser, @RequestBody ReturnNoteData returnNoteData) {
        returnNoteFacade.audit(adminUser, returnNoteData);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/print/{id}",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> printReturnNote(@PathVariable("id")Long id) throws Exception {
        return returnNoteFacade.printReturnNote(id);
    }

    @RequestMapping(value = "/api/purchase/order/returnNote/list/export",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportReturnNotes(ReturnNoteRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return returnNoteFacade.exportReturnNotes(request, operator);
    }
}
