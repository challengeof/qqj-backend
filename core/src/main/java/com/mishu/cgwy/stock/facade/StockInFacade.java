package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.Collectionment;
import com.mishu.cgwy.accounting.facade.AccountPayableFacade;
import com.mishu.cgwy.accounting.service.AccountReceivableService;
import com.mishu.cgwy.accounting.service.CollectionmentService;
import com.mishu.cgwy.accounting.service.RestaurantAccountHistoryService;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.facade.SkuFacade;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItemStatus;
import com.mishu.cgwy.purchase.domain.PurchaseOrderStatus;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockInData;
import com.mishu.cgwy.stock.dto.StockInItemData;
import com.mishu.cgwy.stock.dto.StockInRequest;
import com.mishu.cgwy.stock.service.*;
import com.mishu.cgwy.stock.wrapper.StockInItemWrapper;
import com.mishu.cgwy.stock.wrapper.StockInWrapper;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiao1zhao2 on 15/9/18.
 */
@Service
public class StockInFacade {

    @Autowired
    private StockInService stockInService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockTotalService stockTotalService;
    @Autowired
    private SellReturnService sellReturnService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private AccountPayableFacade accountPayableFacade;
    @Autowired
    private AccountReceivableService accountReceivableService;
    @Autowired
    private RestaurantAccountHistoryService restaurantAccountHistoryService;
    @Autowired
    private CollectionmentService collectionmentService;

    @Autowired
    private SkuFacade skuFacade;

    @Transactional(readOnly = true)
    public QueryResponse<StockInWrapper> getStockInList(StockInRequest stockInRequest, AdminUser operator) {
        Page<StockIn> page = stockInService.getStockInList(stockInRequest, operator);
        List<StockInWrapper> list = new ArrayList<>();
        for (StockIn stockIn : page.getContent()) {
            list.add(new StockInWrapper(stockIn));
        }
        QueryResponse<StockInWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(stockInRequest.getPage());
        res.setPageSize(stockInRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<StockInItemWrapper> getStockInItemList(StockInRequest stockInRequest, AdminUser operator) {
        Page<StockInItem> page = stockInService.getStockInItemList(stockInRequest, operator);
        List<StockInItemWrapper> list = new ArrayList<>();
        for (StockInItem stockInItem : page.getContent()) {
            list.add(new StockInItemWrapper(stockInItem));
        }
        QuerySummationResponse<StockInItemWrapper> res = new QuerySummationResponse<>();
        res.setContent(list);
        res.setPage(stockInRequest.getPage());
        res.setPageSize(stockInRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal totalCost = stockInService.getStockInItemAmount(stockInRequest, operator);
        res.setAmount(new BigDecimal[]{totalCost});
        return res;
    }

    @Transactional(readOnly = true)
    public StockInWrapper getStockIn(Long stockInId) {
        StockIn stockIn = stockInService.getStockIn(stockInId);
        List<StockInItemWrapper> stockInItemWrappers = new ArrayList<>();
        for (StockInItem stockInItem : stockIn.getStockInItems()) {
            stockInItemWrappers.add(new StockInItemWrapper(stockInItem));
        }
        StockInWrapper stockInWrapper = new StockInWrapper(stockIn);
        stockInWrapper.setStockInItems(stockInItemWrappers);
        return stockInWrapper;
    }

    @Transactional
    public void completeStockIn(StockInData stockInData, AdminUser adminUser) {

        StockIn stockIn = stockInService.findOneStockIn(stockInData.getStockInId());
        if (stockIn == null)
            return;

        if (!StockInStatus.UNACCEPTED.getValue().equals(stockIn.getStatus())) {
            throw new UserDefinedException("入库单" + stockIn.getId() + "状态已改变");
        }

        boolean part = stockInData.isPart();
        BigDecimal amount = BigDecimal.ZERO;
        Boolean allZero = true;
        Set<Long> skuIds = new HashSet<>();
        List<StockInItem> oriStockInItems = new ArrayList<>();
        List<StockInItem> newStockInItems = new ArrayList<>();
        for (StockInItemData stockInItemData : stockInData.getStockInItems()) {
            Long stockInItemId = stockInItemData.getStockInItemId();
            StockInItem stockInItem = stockInService.findOneStockInItem(stockInItemId);

            if (stockInItemData.getRealQuantity() > stockInItem.getExpectedQuantity()) {
                throw new UserDefinedException("入库单" + stockIn.getId() + "实收数量大于应收数量");
            } else if (stockInItemData.getRealQuantity() < 0) {
                throw new UserDefinedException("入库单" + stockIn.getId() + "实收数量小于0");
            } else if (stockInItemData.getRealQuantity() != 0) {
                allZero = false;
                skuIds.add(stockInItem.getSku().getId());
            }

            stockInItem.setProductionDate(stockInItemData.getProductionDate());

            if (stockInItemData.getRealQuantity() < stockInItem.getExpectedQuantity() && part) {

                if (stockInItemData.getRealQuantity() == 0) {
                    oriStockInItems.add(stockInItem);

                } else {
                    StockInItem newStockInItem = stockInService.split(stockInItem, stockInItem.getExpectedQuantity()-stockInItemData.getRealQuantity());
                    newStockInItems.add(newStockInItem);

                    if (StockInType.PURCHASE.getValue().equals(stockIn.getType()) || StockInType.RETURN.getValue().equals(stockIn.getType())) {
                        BigDecimal avgCost = this.saveStock(stockInItem);
                        stockInItem.setAvgCost(avgCost);
                    }

                    if (StockInType.RETURN.getValue().equals(stockIn.getType())) {
                        if (stockInItem.getSalePrice() != null) {
                            amount = amount.add(stockInItem.getSalePrice().multiply(new BigDecimal(stockInItem.getRealQuantity())));
                        }
                    } else {
                        if (stockInItem.getPrice() != null) {
                            amount = amount.add(stockInItem.getPrice().multiply(new BigDecimal(stockInItem.getRealQuantity())));
                        }
                    }
                }

            } else {
                stockInItem.setRealQuantity(stockInItemData.getRealQuantity());

                if (StockInType.PURCHASE.getValue().equals(stockIn.getType()) || StockInType.RETURN.getValue().equals(stockIn.getType())) {
                    BigDecimal avgCost = this.saveStock(stockInItem);
                    stockInItem.setAvgCost(avgCost);
                }

                if (StockInType.RETURN.getValue().equals(stockIn.getType())) {
                    if (stockInItem.getSalePrice() != null) {
                        amount = amount.add(stockInItem.getSalePrice().multiply(new BigDecimal(stockInItem.getRealQuantity())));
                    }
                } else {
                    if (stockInItem.getPrice() != null) {
                        amount = amount.add(stockInItem.getPrice().multiply(new BigDecimal(stockInItem.getRealQuantity())));
                    }
                }
            }
        }

        if (!allZero || !part) {
            if (!StockInType.RETURN.getValue().equals(stockIn.getType())) {
                stockIn.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
            }
            stockIn.setReceiveDate(new Date());
            stockIn.setReceiver(adminUser);
        }

        if (allZero && !part) {
            stockIn.setStatus(StockInStatus.CANCEL.getValue());
        } else if (!allZero){
            stockIn.setStatus(StockInStatus.ACCEPTED.getValue());
        }

        if (!allZero && oriStockInItems.size() > 0) {
            stockIn.getStockInItems().removeAll(oriStockInItems);
        }
        stockIn = stockInService.saveStockIn(stockIn);

        if (StockInType.PURCHASE.getValue().equals(stockIn.getType())) {

            if (!allZero && (newStockInItems.size() > 0 || oriStockInItems.size() > 0)) {

                BigDecimal newAmount = BigDecimal.ZERO;
                StockIn newStockIn = stockIn.clone();
                newStockIn.setStatus(StockInStatus.UNACCEPTED.getValue());
                newStockIn.setReceiver(null);
                newStockIn.setReceiveDate(null);
                for (StockInItem sii : newStockInItems) {
                    if (sii.getPrice() != null) {
                        newAmount = newAmount.add(sii.getPrice().multiply(new BigDecimal(sii.getExpectedQuantity())));
                    }
                }
                for (StockInItem sii : oriStockInItems) {
                    if (sii.getPrice() != null) {
                        newAmount = newAmount.add(sii.getPrice().multiply(new BigDecimal(sii.getExpectedQuantity())));
                    }
                }
                newStockIn.setAmount(newAmount);
                newStockIn.getStockInItems().clear();
                newStockIn.getStockInItems().addAll(oriStockInItems);
                newStockIn.getStockInItems().addAll(newStockInItems);
                stockInService.saveStockIn(newStockIn);
            }
            if (!allZero || !part) {
                purchaseOrderService.complete(stockIn, part);
                if (StockInStatus.ACCEPTED.getValue().equals(stockIn.getStatus())) {
                    accountPayableFacade.saveAccountPayable(stockIn);
                }
                this.matchOrderStockOut(skuIds);
            }

        } else if (StockInType.RETURN.getValue().equals(stockIn.getType())) {
            sellReturnService.complete(stockIn);
            if (SellReturnType.PAST.getValue().equals(stockIn.getSellReturn().getType())) {
                //应收
                AccountReceivable accountReceivable = accountReceivableService.generateAccountReceivableByStockIn(stockIn);
                if (accountReceivable != null && accountReceivable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    restaurantAccountHistoryService.createRestaurantAccountHistory(BigDecimal.ZERO, accountReceivable.getAmount(), accountReceivable.getCreateDate(), accountReceivable.getRestaurant(), accountReceivable, null, null);
                }
                //收款
                if (stockIn.getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    Collectionment collectionment = collectionmentService.generateCollectionment(stockIn);
                    if (collectionment != null) {
                        restaurantAccountHistoryService.createRestaurantAccountHistory(collectionment.getAmount(), BigDecimal.ZERO, collectionment.getCreateDate(), collectionment.getRestaurant(), null, collectionment, null);
                    }
                }
            }
        } else if (StockInType.TRANSFER.getValue().equals(stockIn.getType())) {
            stockService.saveStockTransfer(stockIn);
            transferService.complete(stockIn);
            stockOutService.transferComplete(stockIn);
            this.matchOrderStockOut(skuIds);
        }


        if (StockInType.PURCHASE.getValue().equals(stockIn.getType())) {
            skuFacade.updatePurchasePrice(stockIn);
        }
    }

    @Transactional
    public BigDecimal saveStock(StockInItem stockInItem) {
        if (stockInItem.getRealQuantity() <= 0) {
            StockTotal stockTotal = stockTotalService.findStockTotal(stockInItem.getStockIn().getDepot().getCity().getId(), stockInItem.getSku().getId());
            return stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost();
        }

        Date productionDate = stockInItem.getProductionDate();
        Date expirationDate = null;
        Integer shelfLife = stockInItem.getSku().getProduct() != null && stockInItem.getSku().getProduct().getShelfLife() != null ? stockInItem.getSku().getProduct().getShelfLife() : null;

        if (productionDate != null && shelfLife != null) {
            expirationDate = DateUtils.addDays(productionDate, shelfLife);
        }
        if (expirationDate != null) {
            expirationDate = DateUtils.truncate(expirationDate, Calendar.DATE);
        }

        Stock stock = stockService.findMergeStock(stockInItem.getStockIn().getDepot().getId(), stockInItem.getSku().getId()
                , stockInItem.getTaxRate(), null, expirationDate, null);

        if (stock != null) {
            stock.setStock(stock.getStock() + stockInItem.getRealQuantity());
        } else {
            stock = new Stock();
            stock.setSku(stockInItem.getSku());
            stock.setStock(stockInItem.getRealQuantity());
            stock.setDepot(stockInItem.getStockIn().getDepot());
            stock.setExpirationDate(expirationDate);
            stock.setTaxRate(stockInItem.getTaxRate());
        }

        stockService.save(stock);

        StockTotal stockTotal = stockTotalService.saveStockTotal(stock.getDepot().getCity(), stock.getSku(), stockInItem.getRealQuantity(), stockInItem.getPrice());

        return stockTotal.getAvgCost();
    }

    @Transactional
    public void matchOrderStockOut(Set<Long> skuIds) {

        if (skuIds.size() <= 0) {
            return;
        }
        List<StockOutItem> stockOutItems = stockOutService.findUnDistributedItems(skuIds);
        Iterator<StockOutItem> stockOutItemIterator = stockOutItems.iterator();

        while (stockOutItemIterator.hasNext()) {

            StockOutItem stockOutItem = stockOutItemIterator.next();

            int matchQuantity = stockService.occupySockQuantity(stockOutItem.getStockOut(), stockOutItem.getExpectedQuantity(), stockOutItem.getStockOut().getDepot().getId(), stockOutItem.getSku().getId(), null);
            if (matchQuantity > 0) {//not match

                if (matchQuantity - stockOutItem.getExpectedQuantity() < 0) {

                    StockOutItem newSoi = stockOutService.split(stockOutItem, matchQuantity);
                    newSoi.setStockOut(stockOutItem.getStockOut());
                    stockOutService.saveStockOutItem(newSoi);

                    StockOutItem findMergeItem = stockOutService.findMergeStockOutItem(stockOutItem.getStockOut().getId(), stockOutItem.getSku().getId(), stockOutItem.getPrice(), stockOutItem.isBundle());
                    if (findMergeItem != null) {

                        findMergeItem.setReceiveQuantity(findMergeItem.getReceiveQuantity() + stockOutItem.getReceiveQuantity());
                        findMergeItem.setExpectedQuantity(findMergeItem.getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                        findMergeItem.setRealQuantity(findMergeItem.getRealQuantity() + stockOutItem.getRealQuantity());
                        stockOutService.saveStockOutItem(findMergeItem);
                        stockOutService.deleteItem(stockOutItem);
                        stockOutItemIterator.remove();
                    } else {

                        stockOutItem.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());
                        stockOutService.saveStockOutItem(stockOutItem);
                    }
                }
            } else {

                StockOutItem findMergeItem = stockOutService.findMergeStockOutItem(stockOutItem.getStockOut().getId(), stockOutItem.getSku().getId(), stockOutItem.getPrice(), stockOutItem.isBundle());
                if (findMergeItem != null) {

                    findMergeItem.setReceiveQuantity(findMergeItem.getReceiveQuantity() + stockOutItem.getReceiveQuantity());
                    findMergeItem.setExpectedQuantity(findMergeItem.getExpectedQuantity() + stockOutItem.getExpectedQuantity());
                    findMergeItem.setRealQuantity(findMergeItem.getRealQuantity() + stockOutItem.getRealQuantity());
                    stockOutService.saveStockOutItem(findMergeItem);
                    stockOutService.deleteItem(stockOutItem);
                    stockOutItemIterator.remove();
                } else {

                    stockOutItem.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());
                    stockOutService.saveStockOutItem(stockOutItem);
                }
            }
        }

    }

    @Transactional
    public void AccordingPurchaseOrderReceive(PurchaseOrder purchaseOrder, AdminUser adminUser) {

        StockIn stockIn = stockInService.createStockIn(purchaseOrder);
        if (stockIn != null) {
            StockInData stockInData = new StockInData();
            stockInData.setPart(false);
            stockInData.setStockInId(stockIn.getId());
            stockInData.setStockInType(stockIn.getType());
            List<StockInItemData> stockInItemDatas = new ArrayList<>();
            for (StockInItem stockInItem : stockIn.getStockInItems()) {
                StockInItemData stockInItemData = new StockInItemData();
                stockInItemData.setSkuId(stockInItem.getSku().getId());
                stockInItemData.setExpectedQuantity(stockInItem.getExpectedQuantity());
                stockInItemData.setRealQuantity(stockInItem.getRealQuantity());
                stockInItemData.setStockInItemId(stockInItem.getId());
                stockInItemDatas.add(stockInItemData);
            }
            stockInData.setStockInItems(stockInItemDatas);
            this.completeStockIn(stockInData, adminUser);
        } else {
            purchaseOrder.setReceiver(adminUser);
            purchaseOrder.setReceiveTime(new Date());
            purchaseOrder.setStatus(PurchaseOrderStatus.CANSELED.getValue());

            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                item.setStatus(PurchaseOrderItemStatus.INVALID.getValue());
            }
            purchaseOrderService.save(purchaseOrder);
        }
    }

}
