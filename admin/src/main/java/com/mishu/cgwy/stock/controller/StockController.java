package com.mishu.cgwy.stock.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.dto.*;
import com.mishu.cgwy.stock.facade.DepotFacade;
import com.mishu.cgwy.stock.facade.ShelfFacade;
import com.mishu.cgwy.stock.facade.StockFacade;
import com.mishu.cgwy.stock.wrapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/15.
 */
@Controller
public class StockController {

    @Autowired
    private StockFacade stockFacade;
    @Autowired
    private DepotFacade depotFacade;
    @Autowired
    private ShelfFacade shelfFacade;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/stock/depot/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockWrapper> depotList(StockQueryRequest request) {
        return stockFacade.findDepotStocks(request);
    }

    @RequestMapping(value = "/api/stockTotal/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockTotalWrapper> list(StockTotalRequest request) {
        return stockFacade.getStockTotalList(request);
    }

    @RequestMapping(value = "/api/stockTotalDaily/list", method = RequestMethod.GET)
    @ResponseBody
    public QuerySummationResponse<StockTotalDailyWrapper> list(StockTotalDailyRequest request, @CurrentAdminUser AdminUser adminUser) {
        return stockFacade.getStockTotalDailyList(request, adminUser);
    }

    @RequestMapping(value = "/api/depot/list/{id}", method = RequestMethod.GET)
    @ResponseBody
    public List<DepotWrapper> list(@PathVariable("id") Long cityId, @CurrentAdminUser AdminUser adminUser) {
        return depotFacade.findDepotsByCityId(cityId, adminUser);
    }

    @RequestMapping(value = "/api/depot/list", method = RequestMethod.GET)
    @ResponseBody
    public List<DepotWrapper> depots(DepotRequest request) {
        //这里查询全部仓库，不添加权限
        return depotFacade.findDepotList(request, null);
    }

    @RequestMapping(value = "/api/depot", method = RequestMethod.POST)
    @ResponseBody
    public DepotWrapper addDepot(@RequestBody DepotData depotData) {
        return depotFacade.addDepot(depotData);
    }

    @RequestMapping(value = "/api/depot/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public DepotWrapper updateDepot(@PathVariable("id") Long id, @RequestBody DepotData depotData) {
        return depotFacade.updateDepot(id, depotData);
    }

    @RequestMapping(value = "/api/depot/main/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public List<DepotWrapper> setMainDepot(@PathVariable("id") Long id) {
        depotFacade.setMainDepot(id);
        return depotFacade.findDepotList(new DepotRequest(), null);
    }

    @RequestMapping(value = "/api/depot/{id}", method = RequestMethod.GET)
    @ResponseBody
    public DepotWrapper findDepot(@PathVariable("id") Long id) {
        return depotFacade.findDepot(id);
    }

    @RequestMapping(value = "/api/stock/avgcost/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<AvgCostHistoryWrapper> avgCostList(AvgCostHistoryRequest request) {
        return stockFacade.getAvgCostList(request);
    }

    @RequestMapping(value = "/api/shelf/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<ShelfWrapper> shelfList(ShelfRequest request, @CurrentAdminUser AdminUser adminUser) {
        return shelfFacade.getShelfList(request, adminUser);
    }

    @RequestMapping(value = "/api/shelf/{id}", method = RequestMethod.GET)
    @ResponseBody
    public ShelfWrapper findShelf(@PathVariable("id") Long id) {
        return shelfFacade.findShelf(id);
    }

    @RequestMapping(value = "/api/shelf", method = RequestMethod.POST)
    @ResponseBody
    public ShelfWrapper addShelf(@RequestBody ShelfData shelfData) {
        return shelfFacade.addShelf(shelfData);
    }

    @RequestMapping(value = "/api/shelf/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public ShelfWrapper updateShelf(@PathVariable("id") Long id, @RequestBody ShelfData shelfData) {
        return shelfFacade.updateShelf(id, shelfData);
    }

    @RequestMapping(value = "/api/shelf/del", method = RequestMethod.DELETE)
    @ResponseBody
    public void deleteShelf(@RequestBody ShelfData shelfData) {
        shelfFacade.deleteShelf(shelfData);
    }

    @RequestMapping(value = "/api/batchShelf", method = RequestMethod.POST)
    @ResponseBody
    public void addBatchShelf(@RequestBody ShelfData shelfData) {
        shelfFacade.addBatchShelf(shelfData);
    }

    @RequestMapping(value = "/api/stock/willShelfList", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockWrapper> findWillOnShelfStocks(StockQueryRequest request) {
        return stockFacade.findWillOnShelfStocks(request);
    }

    @RequestMapping(value = "/api/shelf/code", method = RequestMethod.GET)
    @ResponseBody
    public ShelfWrapper findShelfByCode(ShelfData shelfData) {
        return shelfFacade.findShelfByDepotAndShelfCode(shelfData);
    }

    @RequestMapping(value = "/api/stock/onShelf", method = RequestMethod.POST)
    @ResponseBody
    public void onShelf(@RequestBody StockOnShelfData stockOnShelfData) {
        stockFacade.onShelf(stockOnShelfData);
    }

    @RequestMapping(value = "/api/stockShelf/list", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockShelfWrapper> stockShelfList(StockQueryRequest request) {
        return stockFacade.findStocksByShelf(request);
    }

    @RequestMapping(value = "/api/stock/moveShelf", method = RequestMethod.POST)
    @ResponseBody
    public void moveShelf(@RequestBody StockOnShelfData stockOnShelfData) {
        stockFacade.moveShelf(stockOnShelfData);
    }

    @RequestMapping(value = "/api/stock/batchShelf", method = RequestMethod.POST)
    @ResponseBody
    public void batchShelf(@RequestBody StockOnShelfDatas stockOnShelfDatas) {
        stockFacade.batchShelf(stockOnShelfDatas);
    }

    @RequestMapping(value = "/api/stock/batchMoveShelf", method = RequestMethod.POST)
    @ResponseBody
    public void batchMoveShelf(@RequestBody StockOnShelfDatas stockOnShelfDatas) {
        stockFacade.batchMoveShelf(stockOnShelfDatas);
    }

    @RequestMapping(value = "/api/stock/inputProductionDate", method = RequestMethod.POST)
    @ResponseBody
    public void inputProductionDate(@RequestBody StockProductionDateData stockProductionDateData) {
        stockFacade.inputProductionDate(stockProductionDateData);
    }

    @RequestMapping(value = "/api/stock/batchProductionDate", method = RequestMethod.POST)
    @ResponseBody
    public void batchProductionDate(@RequestBody StockProductionDateDatas stockProductionDateDatas) {
        stockFacade.batchProductionDate(stockProductionDateDatas);
    }

    @RequestMapping(value = "/api/stock/expirationList", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockShelfWrapper> stockExpirationList(StockQueryRequest request) {
        return stockFacade.findExpirationStocks(request);
    }

    @RequestMapping(value = "/api/stock/dullSaleList", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<StockShelfWrapper> stockDullSaleList(StockQueryRequest request) {
        return stockFacade.findDullSaleStocks(request);
    }

}
