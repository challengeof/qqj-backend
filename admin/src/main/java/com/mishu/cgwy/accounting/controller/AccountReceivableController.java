package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.accounting.dto.AccountReceivableRequest;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableWriteOffStatus;
import com.mishu.cgwy.accounting.facade.AccountReceivableFacade;
import com.mishu.cgwy.accounting.wrapper.AccountReceivableWrapper;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.dto.AdminUserQueryRequest;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/10/13.
 */
@Controller
public class AccountReceivableController {

    @Autowired
    private AccountReceivableFacade accountReceivableFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "api/accounting/receivable/list", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<AccountReceivableWrapper> getAccountReceivableList(AccountReceivableRequest accountReceivableRequest) {
        return accountReceivableFacade.getAccountReceivableList(accountReceivableRequest);
    }

    @RequestMapping(value = "api/accounting/receivable/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportAccountReceivableList(AccountReceivableRequest accountReceivableRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return accountReceivableFacade.exportAccountReceivableList(accountReceivableRequest, operator);
    }

    @RequestMapping(value = "api/accounting/receivable/writeoff", method = RequestMethod.PUT)
    @ResponseBody
    public void accountReceivableWriteoff(@CurrentAdminUser AdminUser user, @RequestBody AccountReceivableRequest accountReceivableRequest) {
        accountReceivableFacade.writeoff(user, accountReceivableRequest);
    }

    @RequestMapping(value = "api/accounting/receivableWriteoff/list", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<AccountReceivableWrapper> getAccountReceivableWriteoffList(AccountReceivableRequest accountReceivableRequest) {
        return accountReceivableFacade.getAccountReceivableWriteoffList(accountReceivableRequest);
    }

    @RequestMapping(value = "api/accounting/receivableWriteoff/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportAccountReceivableWriteoffList(AccountReceivableRequest accountReceivableRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return accountReceivableFacade.exportAccountReceivableWriteoffList(accountReceivableRequest, operator);
    }

    @RequestMapping(value = "api/accounting/receivableWriteoff/cancel", method = RequestMethod.PUT)
    @ResponseBody
    public void accountReceivableWriteoffCancel(@CurrentAdminUser AdminUser user, @RequestBody AccountReceivableRequest accountReceivableRequest) {
        accountReceivableFacade.writeoffCancel(user, accountReceivableRequest);
    }

    @RequestMapping(value = "api/accountReceivable/status/list", method = RequestMethod.GET)
    @ResponseBody
    public AccountReceivableStatus[] getReceivableStatusList() {
        return AccountReceivableStatus.values();
    }

    @RequestMapping(value = "api/accountReceivable/type/list", method = RequestMethod.GET)
    @ResponseBody
    public AccountReceivableType[] getReceivableTypeList() {
        return AccountReceivableType.values();
    }

    @RequestMapping(value = "api/accountReceivableWriteoff/status/list", method = RequestMethod.GET)
    @ResponseBody
    public AccountReceivableWriteOffStatus[] getReceivableWriteoffStatusList() {
        return AccountReceivableWriteOffStatus.values();
    }

    @RequestMapping(value = "api/accounting/tracker/list", method = RequestMethod.GET)
    @ResponseBody
    public List<AdminUserVo> getTrackerList(AdminUserQueryRequest adminUserQueryRequest) {
        return accountReceivableFacade.getTrackerList(adminUserQueryRequest);
    }

}
