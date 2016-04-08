package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.purchase.wrapper.SkuInfo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.TransferStatus;
import com.mishu.cgwy.stock.dto.TransferData;
import com.mishu.cgwy.stock.dto.TransferRequest;
import com.mishu.cgwy.stock.facade.TransferFacade;
import com.mishu.cgwy.stock.service.TransferService;
import com.mishu.cgwy.stock.wrapper.TransferWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * Created by wangguodong on 15/9/22.
 */
@Controller
public class TransferController {

    @Autowired
    private TransferFacade transferFacade;

    @Autowired
    private TransferService transferService;

    @RequestMapping(value = "/api/transfer/sku/{cityId}/{skuId}",method = RequestMethod.GET)
    @ResponseBody
    public SkuInfo getSkuInfo(
            @PathVariable("cityId")Long cityId,
            @PathVariable("skuId")Long skuId,
            @RequestParam("depotIds") String[] depotIds
            ) {
        return transferFacade.getSkuInfo(cityId, skuId, depotIds);
    }

    @RequestMapping(value = "/api/transfer/add",method = RequestMethod.POST)
    @ResponseBody
    public void savePurchaseOrder(@CurrentAdminUser AdminUser adminUser, @RequestBody TransferData transferData) {
        transferService.saveTransfer(adminUser, transferData);
    }

    @RequestMapping(value = "/api/transfer/statuses", method = RequestMethod.GET)
    @ResponseBody
    public TransferStatus[] getStatuses() {
        return TransferStatus.values();
    }

    @RequestMapping(value = "/api/transfer/list",method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<TransferWrapper> list(TransferRequest request, @CurrentAdminUser AdminUser operator) {
        return transferService.getTransfers(request);
    }

    @RequestMapping(value = "/api/transfer/{id}",method = RequestMethod.GET)
    @ResponseBody
    public TransferWrapper getTransfer(@PathVariable("id")Long id) {
        return transferFacade.getTransfer(id);
    }

    @RequestMapping(value = "/api/transfer/submit/{id}",method = RequestMethod.GET)
    @ResponseBody
    public void submit(@PathVariable("id")Long id) {
        transferService.submit(id);
    }

    @RequestMapping(value = "/api/transfer/audit", method = RequestMethod.POST)
    @ResponseBody
    public void audit(@CurrentAdminUser AdminUser adminUser, @RequestBody TransferData transferData) {
        transferFacade.transferAudit(adminUser, transferData);
    }
}
