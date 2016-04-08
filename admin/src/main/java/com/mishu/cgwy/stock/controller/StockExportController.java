package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.stock.domain.StockPrintStatus;
import com.mishu.cgwy.stock.dto.*;
import com.mishu.cgwy.stock.facade.StockExportFacade;
import com.mishu.cgwy.utils.UserDateEditor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.util.Date;

/**
 * Created by xiao1zhao2 on 15/11/6.
 */
@Controller
public class StockExportController {

    @Autowired
    private StockExportFacade stockExportFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Date.class, new UserDateEditor());
    }

    @RequestMapping(value = "/api/stockTotal/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockTotalList(StockTotalRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockTotalList(request, operator);
    }

    @RequestMapping(value = "/api/stockTotalDaily/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockTotalDailyList(StockTotalDailyRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockTotalDailyList(request, operator);
    }

    @RequestMapping(value = "/api/stockDepot/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockDepotList(StockQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockDepotList(request, operator);
    }

    @RequestMapping(value = "/api/stockIn/export/bills", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockInBills(@RequestParam(value = "stockInIds") Long[] stockInIds, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockInBills(stockInIds, operator);
    }

    @RequestMapping(value = "/api/stockIn/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockInList(StockInRequest stockInRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockInList(stockInRequest, operator);
    }

    @RequestMapping(value = "/api/stockInItem/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockInItemList(StockInRequest stockInRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockInItemList(stockInRequest, operator);
    }

    @RequestMapping(value = "/api/stockOut/export/bills", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutBills(@RequestParam("stockOutIds") Long[] stockOutIds, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutBills(stockOutIds, operator);
    }

    @RequestMapping(value = "/api/stockOut/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutList(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockOutItem/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutItemList(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutItemList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockOut/total/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutTotalList(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutTotalList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockOut/excel-sku-pick", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportPickExcel(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator)
            throws Exception {
        File file = stockExportFacade.exportPickExcel(stockOutRequest, operator);
        return stockExportFacade.getHttpEntityXls(file.getPath(), "按品类拣选单.xls");
    }

    @RequestMapping(value = "/api/stockOut/excel-tracker-pick", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportPickTrackerExcel(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator)
            throws Exception {
        File file = stockExportFacade.exportPickTrackerExcel(stockOutRequest, operator);
        return stockExportFacade.getHttpEntityXls(file.getPath(), "按线路拣选单.xls");
    }

    @RequestMapping(value = "/api/stockOut/excel-associate", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportAssociateTrackerExcel(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator)
            throws Exception {
        File file = stockExportFacade.exportAssociateTrackerExcel(stockOutRequest, operator);
        return stockExportFacade.getHttpEntityXls(file.getPath(), "交接单.xls");
    }

    @RequestMapping(value = "/api/stockOut/excel-barcode/{type}", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportBarcodeExcel(@PathVariable(value = "type") int type, StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator)
            throws Exception {
        String fileName = type == 0 ? "在库品条码.xls" : ( type == 1 ? "外采品条码.xls" : "调拨条码.xls");
        File file = stockExportFacade.exportBarcodeExcel(stockOutRequest, type, operator);
        return stockExportFacade.getHttpEntityXls(file.getPath(), fileName);
    }

    @RequestMapping(value = "/api/stockOut/excel-notmatch", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportNotMatchExcel(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator)
            throws Exception {
        File file = stockExportFacade.exportNotMatchExcel(stockOutRequest, operator);
        return stockExportFacade.getHttpEntityXls(file.getPath(), "未配货销售出库一览.xls");
    }

    @RequestMapping(value = "/api/stockOut/out/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutOutList(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutOutList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/incomeDailyReport/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportIncomeDailyReport(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportIncomeDailyReport(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockShelf/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockShelfExcel(StockQueryRequest request, @CurrentAdminUser AdminUser operator)
            throws Exception {
        return stockExportFacade.exportStockShelfExcel(request, operator);
    }

    @RequestMapping(value = "/api/stockAdjust/export/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockAdjustExcel(StockAdjustQueryRequest request, @CurrentAdminUser AdminUser operator)
            throws Exception {
        return stockExportFacade.exportStockAdjustExcel(request, operator);
    }

    @RequestMapping(value = "/api/stock/export/willShelfList", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOnShelfExcel(StockQueryRequest request, @CurrentAdminUser AdminUser operator)
            throws Exception {
        return stockExportFacade.exportStockOnShelfExcel(request, operator);
    }

    @RequestMapping(value = "/api/stock/exportExpiration/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockExpirationExcel(StockQueryRequest request, @CurrentAdminUser AdminUser operator)
            throws Exception {
        return stockExportFacade.exportStockExpirationExcel(request, operator);
    }

    @RequestMapping(value = "/api/stock/exportDullSale/list", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockDullSaleExcel(StockQueryRequest request, @CurrentAdminUser AdminUser operator)
            throws Exception {
        return stockExportFacade.exportStockDullSaleExcel(request, operator);
    }

    @RequestMapping(value = "/api/stockOut/receive/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> exportStockOutReceiveList(StockOutRequest stockOutRequest, @CurrentAdminUser AdminUser operator) throws Exception {
        return stockExportFacade.exportStockOutReceiveList(stockOutRequest, operator);
    }

    @RequestMapping(value = "/api/stockPrint/status/list", method = RequestMethod.GET)
    @ResponseBody
    public StockPrintStatus[] getStockPrintStatus() {
        return StockPrintStatus.values();
    }

}
