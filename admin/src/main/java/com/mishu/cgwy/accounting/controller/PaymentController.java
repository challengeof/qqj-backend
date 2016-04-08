package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.accounting.dto.*;
import com.mishu.cgwy.accounting.enumeration.*;
import com.mishu.cgwy.accounting.facade.*;
import com.mishu.cgwy.accounting.service.CollectionPaymentMethodService;
import com.mishu.cgwy.accounting.service.PaymentService;
import com.mishu.cgwy.accounting.vo.PaymentVo;
import com.mishu.cgwy.accounting.vo.VendorAccountHistoryVo;
import com.mishu.cgwy.accounting.wrapper.*;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
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
 * Created by wangguodong on 15/10/12.
 */
@Controller
public class PaymentController {

    @Autowired
    private PaymentFacade paymentFacade;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private CollectionPaymentMethodService collectionPaymentMethodService;

    @Autowired
    private AccountPayableFacade accountPayableFacade;

    @Autowired
    private AccountPayableItemFacade accountPayableItemFacade;

    @Autowired
    private VendorAccountFacade vendorAccountFacade;

    @Autowired
    private VendorAccountHistoryFacade vendorAccountHistroyFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/accounting/payment/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<PaymentVo> list(PaymentListRequest request) {
        return paymentService.getPaymentList(request);
    }

    @RequestMapping(value = "/api/accounting/payment/cancel",method = RequestMethod.POST)
    @ResponseBody
    public void cancelPayment(@CurrentAdminUser AdminUser adminUser, @RequestParam(value = "id",required = true) Long id) {
        paymentFacade.cancelPayment(adminUser, id);
    }

    @RequestMapping(value = "/api/accounting/payment/methods/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public List<CollectionPaymentMethodWrapper> getPaymentMethods(@PathVariable("cityId")Long cityId) {
        return collectionPaymentMethodService.getCollectionPaymentMethods(cityId, true);
    }

    @RequestMapping(value = "/api/accounting/payment/statuses", method = RequestMethod.GET)
    @ResponseBody
    public PaymentStatus[] getPaymentStatuses() {
        return PaymentStatus.values();
    }

    @RequestMapping(value = "/api/accounting/payable/statuses", method = RequestMethod.GET)
    @ResponseBody
    public AccountPayableStatus[] getPayableStatuses() {
        return AccountPayableStatus.values();
    }


    @RequestMapping(value = "/api/accounting/payable/writeOff/statuses", method = RequestMethod.GET)
    @ResponseBody
    public AccountPayableWriteOffStatus[] getPaymentWriteOffStatus() {
        return AccountPayableWriteOffStatus.values();
    }

    @RequestMapping(value = "/api/accounting/payment/add",method = RequestMethod.POST)
    @ResponseBody
    public void savePayment(@CurrentAdminUser AdminUser adminUser, @RequestBody PaymentData paymentData) {
        paymentFacade.savePaymentData(adminUser, paymentData);
    }

    @RequestMapping(value = "/api/accounting/payable/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<AccountPayableWrapper> list(AccountPayableListRequest request) {
        return accountPayableFacade.getAccountPayables(request);
    }

    @RequestMapping(value = "/api/accounting/payable/exportAccountPayables",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportAccountPayables(AccountPayableListRequest request) throws Exception {
        return accountPayableFacade.exportAccountPayables(request);
    }

    @RequestMapping(value = "/api/accounting/payable/writeOff/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<AccountPayableWriteOffWrapper> list(AccountPaymentWriteOffListRequest request) {
        return accountPayableFacade.getAccountPayableWriteOffs(request);
    }

    @RequestMapping(value = "/api/accounting/payable/writeOff/exportAccountPayableWriteOffs",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportAccountPayableWriteOffs(AccountPaymentWriteOffListRequest request) throws Exception {
        return accountPayableFacade.exportAccountPayableWriteOffs(request);
    }

    @RequestMapping(value = "/api/accounting/payable/writeOff",method = RequestMethod.POST)
    @ResponseBody
    public void writeOff(@CurrentAdminUser AdminUser adminUser, @RequestBody List<AccountPayableData> accountPayableDataList) {
        accountPayableFacade.writeOff(adminUser, accountPayableDataList);
    }

    @RequestMapping(value = "/api/accounting/payable/writeOff/cancel",method = RequestMethod.POST)
    @ResponseBody
    public void writeOffCancel(@CurrentAdminUser AdminUser adminUser, @RequestParam(value = "writeOffId",required = true) Long writeOffId, @RequestParam(value = "cancelDate") Date cancelDate) {
        accountPayableFacade.writeOffCancel(adminUser, writeOffId, cancelDate);
    }

    @RequestMapping(value = "/api/accounting/vendorAccount/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VendorAccountWrapper> getVendorAccountList(VendorAccountListRequest request) {
        return vendorAccountFacade.getVendorAccountList(request);
    }

    @RequestMapping(value = "/api/accounting/vendorAccount/list/export",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportVendorAccounts(VendorAccountListRequest request) throws Exception {
        return vendorAccountFacade.exportVendorAccounts(request);
    }

    @RequestMapping(value = "/api/accounting/payable/types", method = RequestMethod.GET)
    @ResponseBody
    public AccountPayableType[] getAccountPayableTypes() {
        return AccountPayableType.values();
    }

    @RequestMapping(value = "/api/accounting/payableItem/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<AccountPayableItemWrapper> getAccountPayableItems(AccountPayableItemListRequest request) {
        return accountPayableItemFacade.getAccountPayableItems(request);
    }

    @RequestMapping(value = "/api/accounting/payableItem/list/export",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportPayableItems(AccountPayableItemListRequest request) throws Exception {
        return accountPayableItemFacade.exportPayableItems(request);
    }

    @RequestMapping(value = "/api/accounting/vendorAccountHistory/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<VendorAccountHistoryVo> getVendorAccountHistories(VendorAccountHistoryListRequest request) {
        if (request.getVendorAccountOperationType() != null) {
            request.setTypes(new Short[]{request.getVendorAccountOperationType()});
        } else {
            request.setTypes(new Short[]{VendorAccountOperationType.PAYABLE.getValue(), VendorAccountOperationType.WRITEOFF.getValue()});
        }
        return vendorAccountHistroyFacade.getVendorAccountHistories(request);
    }

    @RequestMapping(value = "/api/accounting/vendorAccountOperation/types", method = RequestMethod.GET)
    @ResponseBody
    public VendorAccountOperationType[] getVendorAccountOperationTypes() {
        return VendorAccountOperationType.values();
    }

    @RequestMapping(value = "/api/accounting/vendorAccountHistory/list/export",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportVendorAccountHistoryList(VendorAccountHistoryListRequest request) throws Exception {
        if (request.getVendorAccountOperationType() != null) {
            request.setTypes(new Short[]{request.getVendorAccountOperationType()});
        } else {
            request.setTypes(new Short[]{VendorAccountOperationType.PAYABLE.getValue(), VendorAccountOperationType.WRITEOFF.getValue()});
        }
        return vendorAccountHistroyFacade.exportVendorAccountHistoryList(request);
    }

    @RequestMapping(value = "/api/accounting/payment/export",method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportPayment(PaymentListRequest request) throws Exception {
        return paymentFacade.exportPayment(request);
    }
}
