package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.accounting.service.AccountReceivableService;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.*;
import com.mishu.cgwy.stock.service.*;
import com.mishu.cgwy.stock.wrapper.*;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by admin on 2015/9/15.
 */
@Service
public class StockFacade {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockTotalService stockTotalService;
    @Autowired
    private AvgCostHistoryService avgCostHistoryService;
    @Autowired
    private ShelfService shelfService;
    @Autowired
    private StockTotalDailyService stockTotalDailyService;
    @Autowired
    private AccountReceivableService accountReceivableService;

    @Transactional(readOnly = true)
    public QueryResponse<StockWrapper> findDepotStocks(StockQueryRequest request) {

        QueryResponse<StockWrapper> res = new QueryResponse<>();
        List<Object[]> resultList = stockService.getStock(request);
        for (int step = 0, index = request.getPage() * request.getPageSize(); step < request.getPageSize() && index < resultList.size(); step++, index++) {
            StockWrapper stockWrapper = new StockWrapper((Stock) resultList.get(index)[0]);
            stockWrapper.setAvailableQuantity((Long) resultList.get(index)[1]);
            stockWrapper.setOccupiedQuantity((Long) resultList.get(index)[2]);
            stockWrapper.setOnRoadQuantity((Long) resultList.get(index)[3]);
            stockWrapper.calBundleQuantity();
            res.getContent().add(stockWrapper);
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(resultList.size());
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockTotalWrapper> getStockTotalList(StockTotalRequest request) {

        QueryResponse<StockTotalWrapper> res = new QueryResponse<>();
        Page<StockTotal> page = stockTotalService.getStockTotalList(request);
        for (StockTotal stockTotal : page.getContent()) {
            res.getContent().add(new StockTotalWrapper(stockTotal));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<StockTotalDailyWrapper> getStockTotalDailyList(StockTotalDailyRequest request, AdminUser adminUser) {

        QuerySummationResponse<StockTotalDailyWrapper> res = new QuerySummationResponse<>();
        Page<StockTotalDaily> page = stockTotalDailyService.getStockTotalDailyList(request, adminUser);
        for (StockTotalDaily stockTotalDaily : page.getContent()) {
            res.getContent().add(new StockTotalDailyWrapper(stockTotalDaily));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal totalCost = stockTotalDailyService.getStockTotalDailyAmount(request, adminUser);
        res.setAmount(new BigDecimal[]{totalCost});
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<AvgCostHistoryWrapper> getAvgCostList(AvgCostHistoryRequest request) {

        QueryResponse<AvgCostHistoryWrapper> res = new QueryResponse<AvgCostHistoryWrapper>();
        Page<AvgCostHistory> page = avgCostHistoryService.getAvgCostHistoryList(request);
        for (AvgCostHistory avgCostHistory : page.getContent()) {
            res.getContent().add(new AvgCostHistoryWrapper(avgCostHistory));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockWrapper> findWillOnShelfStocks(StockQueryRequest request) {

        QueryResponse<StockWrapper> res = new QueryResponse<>();
        List<Object[]> resultList = stockService.getWillOnShelfStock(request);
        for (int step = 0, index = request.getPage() * request.getPageSize(); step < request.getPageSize() && index < resultList.size(); step++, index++) {
            StockWrapper stockWrapper = new StockWrapper((Stock) resultList.get(index)[0]);
            stockWrapper.setAvailableQuantity((Long) resultList.get(index)[1]);
            stockWrapper.calBundleQuantity();
            res.getContent().add(stockWrapper);
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(resultList.size());
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockShelfWrapper> findStocksByShelf(StockQueryRequest request) {

        QueryResponse<StockShelfWrapper> res = new QueryResponse<>();
        Page<Stock> page = stockService.findStocksByShelf(request);
        for (Stock stock : page.getContent()) {
            res.getContent().add(new StockShelfWrapper(stock));
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional
    public void onShelf(StockOnShelfData stockOnShelfData) {

        StockQueryRequest request = new StockQueryRequest();
        request.setDepotId(stockOnShelfData.getDepotId());
        request.setSkuId(stockOnShelfData.getSkuId());
        if (stockOnShelfData.getExpirationDate() == null) {
            Calendar c = Calendar.getInstance();
            c.set(1900,1,1,0,0,0);
            c.set(Calendar.MILLISECOND, 0);
            request.setExpirationDate(c.getTime());
        } else {
            request.setExpirationDate(stockOnShelfData.getExpirationDate());
        }
        Integer availQuantity = stockService.findAvailStockByDepotSku(request);
        if (availQuantity == null) {
            availQuantity = 0;
        }

        int shelfQuantity = 0;
        for (StockOnShelfItemData stockOnShelfItemData : stockOnShelfData.getStockShelfs()) {
            shelfQuantity += stockOnShelfItemData.getQuantity();
        }

        if (shelfQuantity > availQuantity.intValue()) {
            throw new UserDefinedException("数据发生变化,请刷新数据重新上架");
        }

        if  (shelfQuantity > 0) {
            List<Stock> stocks = stockService.findAvailStocksByDepotSku(request);
            for (StockOnShelfItemData stockOnShelfItemData : stockOnShelfData.getStockShelfs()) {
                Long shelfId = stockOnShelfItemData.getShelfId();
                Shelf shelf = shelfService.findOne(shelfId);
                int quantity = stockOnShelfItemData.getQuantity();
                Iterator<Stock> iterStock = stocks.iterator();
                while (iterStock.hasNext()) {
                    Stock stock = iterStock.next();
                    if (quantity <= 0) {
                        break;
                    }

                    Stock willMergeStock = null;

                    if (stock.getStock() <= quantity) {
                        stock.setShelf(shelf);
                        stockService.save(stock);
                        quantity -= stock.getStock();
                        willMergeStock = stock;
                    } else {
                        Stock newStock = stockService.split(stock, quantity);
                        newStock.setStockIn(stock.getStockIn());
                        newStock.setStockOut(stock.getStockOut());
                        newStock.setStockAdjust(stock.getStockAdjust());
                        newStock.setShelf(shelf);
                        willMergeStock = stockService.save(newStock);
                        quantity = 0;
                    }

                    if (willMergeStock != null && willMergeStock.getStockIn() == null && willMergeStock.getStockOut() == null && willMergeStock.getStockAdjust() == null) {
                        Stock originStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                                , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());
                        if (originStock != null) {
                            originStock.setStock(originStock.getStock() + willMergeStock.getStock());
                            stockService.save(originStock);
                            stockService.delete(willMergeStock);
                        }
                    }
                }
            }
        }
    }

    @Transactional
    public void moveShelf(StockOnShelfData stockOnShelfData) {
        Stock findStock = stockService.findOne(stockOnShelfData.getId());
        int quantity = stockOnShelfData.getQuantity();
        int moveQuantity = stockOnShelfData.getMoveQuantity();
        if (quantity - findStock.getStock() != 0) {
            throw new UserDefinedException("数据发生变化,请刷新数据重新移位");
        }
        if (moveQuantity > 0) {
            Long shelfId = stockOnShelfData.getShelfId();
            Shelf shelf = shelfService.findOne(shelfId);
            Stock willMergeStock = null;
            if (moveQuantity == findStock.getStock()) {
                findStock.setShelf(shelf);
                stockService.save(findStock);
                willMergeStock = findStock;
            } else if (moveQuantity < findStock.getStock()){
                Stock newStock = stockService.split(findStock, moveQuantity);
                newStock.setStockIn(findStock.getStockIn());
                newStock.setStockOut(findStock.getStockOut());
                newStock.setStockAdjust(findStock.getStockAdjust());
                newStock.setShelf(shelf);
                willMergeStock = stockService.save(newStock);
            }
            if (willMergeStock != null && willMergeStock.getStockIn() == null && willMergeStock.getStockOut() == null && willMergeStock.getStockAdjust() == null) {
                Stock originStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                        , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());
                if (originStock != null) {
                    originStock.setStock(originStock.getStock() + willMergeStock.getStock());
                    stockService.save(originStock);
                    stockService.delete(willMergeStock);
                }
            }
        }
    }

    @Transactional
    public void batchShelf(StockOnShelfDatas stockOnShelfDatas) {
        for (StockOnShelfData stockOnShelfData : stockOnShelfDatas.getStockShelfDatas()) {
            this.onShelf(stockOnShelfData);
        }
    }

    @Transactional
    public void batchMoveShelf(StockOnShelfDatas stockOnShelfDatas) {
        for (StockOnShelfData stockOnShelfData : stockOnShelfDatas.getStockShelfDatas()) {
            this.moveShelf(stockOnShelfData);
        }
    }

    @Transactional
    public void inputProductionDate(StockProductionDateData stockProductionDateData) {

        Stock findStock = stockService.findOne(stockProductionDateData.getId());
        int quantity = stockProductionDateData.getQuantity();
        if (quantity - findStock.getStock() != 0) {
            throw new UserDefinedException("数据发生变化,请刷新数据重新录入生产日期");
        }
        if (findStock.getSku().getProduct().getShelfLife() == null) {
            throw new UserDefinedException("商品 " + findStock.getSku().getName() + " 没有设置保质期,请先设置保质期再录入生产日期");
        }

        for (StockProductionDateItemData stockProductionDateItemData : stockProductionDateData.getStockProductionDates()) {
            Date productionDate = stockProductionDateItemData.getProductionDate();
            int productionQuantity = stockProductionDateItemData.getQuantity();
            Date expirationDate = null;
            Integer shelfLife = findStock.getSku().getProduct() != null && findStock.getSku().getProduct().getShelfLife() != null ? findStock.getSku().getProduct().getShelfLife() : null;

            if (productionDate != null && shelfLife != null) {
                expirationDate = DateUtils.addDays(productionDate, shelfLife);
            }
            if (expirationDate != null) {
                expirationDate = DateUtils.truncate(expirationDate, Calendar.DATE);
            }

            Stock willMergeStock = null;
            if (productionQuantity == findStock.getStock()) {
                findStock.setExpirationDate(expirationDate);
                stockService.save(findStock);
                willMergeStock = findStock;
            } else if (productionQuantity < findStock.getStock()){
                Stock newStock = stockService.split(findStock, productionQuantity);
                newStock.setStockIn(findStock.getStockIn());
                newStock.setStockOut(findStock.getStockOut());
                newStock.setStockAdjust(findStock.getStockAdjust());
                newStock.setExpirationDate(expirationDate);
                willMergeStock = stockService.save(newStock);
            }
            if (willMergeStock != null && willMergeStock.getStockIn() == null && willMergeStock.getStockOut() == null && willMergeStock.getStockAdjust() == null) {
                Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
                Stock originStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                        , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());
                if (originStock != null) {
                    originStock.setStock(originStock.getStock() + willMergeStock.getStock());
                    stockService.save(originStock);
                    stockService.delete(willMergeStock);
                }
            }
        }
    }

    @Transactional
    public void batchProductionDate(StockProductionDateDatas stockProductionDateDatas) {
        for (StockProductionDateData stockProductionDateData : stockProductionDateDatas.getStockProductionDateDatas()) {
            this.inputProductionDate(stockProductionDateData);
        }
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockShelfWrapper> findExpirationStocks(StockQueryRequest request) {

        QueryResponse<StockShelfWrapper> res = new QueryResponse<>();
        Page<Stock> page = stockService.findStocksByShelf(request);
        for (Stock stock : page.getContent()) {
            StockShelfWrapper stockShelfWrapper = new StockShelfWrapper(stock);
            stockShelfWrapper.setShelfLife(stock.getSku().getProduct().getShelfLife());
            res.getContent().add(stockShelfWrapper);
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockShelfWrapper> findDullSaleStocks(StockQueryRequest request) {

        QueryResponse<StockShelfWrapper> res = new QueryResponse<>();
        List<Stock> resultList = stockService.findDullSaleStocks(request);
        for (int step = 0, index = request.getPage() * request.getPageSize(); step < request.getPageSize() && index < resultList.size(); step++, index++) {
            Stock stock = resultList.get(index);
            StockShelfWrapper stockShelfWrapper = new StockShelfWrapper(stock);
            Date lastSaleDate = accountReceivableService.findSkuLastSaleDate(stock.getDepot().getCity().getId(), stock.getSku().getId());
            stockShelfWrapper.setLastSaleDate(lastSaleDate);
            res.getContent().add(stockShelfWrapper);
        }
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(resultList.size());
        return res;
    }
}
