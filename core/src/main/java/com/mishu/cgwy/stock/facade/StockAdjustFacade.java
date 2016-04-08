package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Stock;
import com.mishu.cgwy.stock.domain.StockAdjust;
import com.mishu.cgwy.stock.domain.StockAdjustStatus;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.dto.StockAdjustData;
import com.mishu.cgwy.stock.dto.StockAdjustQueryRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.StockAdjustService;
import com.mishu.cgwy.stock.service.StockService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.stock.wrapper.StockAdjustWrapper;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class StockAdjustFacade {

    @Autowired
    private StockService stockService;
    @Autowired
    private StockAdjustService stockAdjustService;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SkuService skuService;
    @Autowired
    private DepotService depotService;
    @Autowired
    private StockTotalService stockTotalService;

    @Transactional
    public void adjustStock(StockAdjustData stockAdjustData, AdminUser adminUser) {

        Stock findStock = stockService.findOne(stockAdjustData.getStockId());
        int quantity = stockAdjustData.getQuantity();
        int adjustQuantity = stockAdjustData.getAdjustQuantity();
        if (quantity - findStock.getStock() != 0 || findStock.getStockAdjust() != null
                || findStock.getStockOut() != null || findStock.getStockIn() != null) {
            throw new UserDefinedException("数据发生变化,请刷新数据重新调整");
        }
        if (quantity - adjustQuantity == 0) {
            throw new UserDefinedException("原数量和调整后数量相等,没必要做调整");
        }

        StockAdjust stockAdjust = new StockAdjust();
        stockAdjust.setDepot(findStock.getDepot());
        stockAdjust.setAdjustQuantity(adjustQuantity);
        stockAdjust.setComment(stockAdjustData.getComment());
        stockAdjust.setCreateDate(new Date());
        stockAdjust.setCreator(adminUser);
        stockAdjust.setExpirationDate(findStock.getExpirationDate());
        stockAdjust.setQuantity(findStock.getStock());
        if (findStock.getShelf() != null) {
            stockAdjust.setShelfName(findStock.getShelf().getName());
        }
        stockAdjust.setSku(findStock.getSku());
        stockAdjust.setStatus(StockAdjustStatus.PENDINGAUDIT.getValue());
        stockAdjust.setTaxRate(findStock.getTaxRate());
        stockAdjust = stockAdjustService.save(stockAdjust);

        int balance = quantity-adjustQuantity;
        if (balance < 0) {//盘盈不去占库存
            //findStock.setStockAdjust(stockAdjust);
           // stockService.save(findStock);
        } else {
            if (quantity-balance == 0) {
                findStock.setStockAdjust(stockAdjust);
                stockService.save(findStock);
            } else {
                Stock newStock = stockService.split(findStock, balance);
                newStock.setStockAdjust(stockAdjust);
                stockService.save(newStock);
            }
        }
    }

    @Transactional
    public void createAdjust(StockAdjustData stockAdjustData, AdminUser adminUser) {

        StockAdjust stockAdjust = new StockAdjust();
        stockAdjust.setDepot(depotService.findOne(stockAdjustData.getDepotId()));
        Sku sku = skuService.findOne(stockAdjustData.getSkuId());
        stockAdjust.setQuantity(0);
        stockAdjust.setSku(sku);
        stockAdjust.setStatus(StockAdjustStatus.PENDINGAUDIT.getValue());
        stockAdjust.setTaxRate(sku.getRate());
        stockAdjust.setAdjustQuantity(stockAdjustData.getAdjustQuantity());
        stockAdjust.setComment(stockAdjustData.getComment());
        stockAdjust.setCreateDate(new Date());
        stockAdjust.setCreator(adminUser);
        stockAdjust.setAvgCost(stockAdjustData.getAvgCost());
        Date productionDate = stockAdjustData.getProductionDate();
        Date expirationDate = null;
        Integer shelfLife = sku.getProduct() != null && sku.getProduct().getShelfLife() != null ? sku.getProduct().getShelfLife() : null;
        if (productionDate != null && shelfLife != null) {
            expirationDate = DateUtils.addDays(productionDate, shelfLife);
        }
        if (expirationDate != null) {
            expirationDate = DateUtils.truncate(expirationDate, Calendar.DATE);
            stockAdjust.setExpirationDate(expirationDate);
        }

        stockAdjustService.save(stockAdjust);
    }

    @Transactional(readOnly = true)
    public QueryResponse<StockAdjustWrapper> getStockAdjustList(StockAdjustQueryRequest request, AdminUser operator) {
        List<StockAdjustWrapper> list = new ArrayList<>();
        Page<StockAdjust> page = stockAdjustService.getStockAdjustList(request, operator);
        for (StockAdjust stockAdjust : page.getContent()) {
            StockAdjustWrapper stockAdjustWrapper = new StockAdjustWrapper();
            stockAdjustWrapper.setId(stockAdjust.getId());
            stockAdjustWrapper.setDepotId(stockAdjust.getDepot().getId());
            stockAdjustWrapper.setDepotName(stockAdjust.getDepot().getName());
            stockAdjustWrapper.setSkuId(stockAdjust.getSku().getId());
            stockAdjustWrapper.setSkuName(stockAdjust.getSku().getName());
            stockAdjustWrapper.setSkuSingleUnit(stockAdjust.getSku().getSingleUnit());
            stockAdjustWrapper.setSkuBundleUnit(stockAdjust.getSku().getBundleUnit());
            stockAdjustWrapper.setSkuCapacityInBundle(stockAdjust.getSku().getCapacityInBundle());
            stockAdjustWrapper.setTaxRate(stockAdjust.getTaxRate());
            stockAdjustWrapper.setQuantity(stockAdjust.getQuantity());
            stockAdjustWrapper.setAdjustQuantity(stockAdjust.getAdjustQuantity());
            stockAdjustWrapper.setExpirationDate(stockAdjust.getExpirationDate());
            if (stockAdjustWrapper.getExpirationDate() != null && stockAdjust.getSku().getProduct().getShelfLife() != null) {
                stockAdjustWrapper.setProductionDate(DateUtils.truncate(DateUtils.addDays(stockAdjustWrapper.getExpirationDate(), stockAdjust.getSku().getProduct().getShelfLife() * (-1)), Calendar.DATE));
            }
            stockAdjustWrapper.setShelfName(stockAdjust.getShelfName());
            stockAdjustWrapper.setStockAdjustStatus(StockAdjustStatus.fromInt(stockAdjust.getStatus()));
            stockAdjustWrapper.setAuditDate(stockAdjust.getAuditDate());
            stockAdjustWrapper.setAvgCost(stockAdjust.getAvgCost());
            stockAdjustWrapper.setComment(stockAdjust.getComment());
            stockAdjustWrapper.setCreateDate(stockAdjust.getCreateDate());
            if (stockAdjust.getCreator() != null) {
                stockAdjustWrapper.setCreatorId(stockAdjust.getCreator().getId());
                stockAdjustWrapper.setCreatorName(stockAdjust.getCreator().getRealname());
            }
            if (stockAdjust.getAuditor() != null) {
                stockAdjustWrapper.setAuditorId(stockAdjust.getAuditor().getId());
                stockAdjustWrapper.setAuditorName(stockAdjust.getAuditor().getRealname());
            }
            list.add(stockAdjustWrapper);
        }
        QueryResponse<StockAdjustWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional
    public void adjustReject(StockAdjustData stockAdjustData, AdminUser adminUser) {

        for (Long id : stockAdjustData.getAdjustIds()) {
            StockAdjust stockAdjust = stockAdjustService.findOne(id);
            if (stockAdjust == null) {
                continue;
            }
            if (!StockAdjustStatus.PENDINGAUDIT.getValue().equals(stockAdjust.getStatus())) {
                throw new UserDefinedException("SKU " + stockAdjust.getSku().getName() + "的状态已改变,请刷新数据再审核");
            }

            stockAdjust.setStatus(StockAdjustStatus.REFUSED.getValue());
            stockAdjust.setAuditDate(new Date());
            stockAdjust.setAuditor(adminUser);
            stockAdjustService.save(stockAdjust);

            List<Stock> occupiedStocks = stockService.findAdjustOccupiedSocks(stockAdjust.getId());
            //释放库存后，合并库存
            Iterator<Stock> unMatchIterator = occupiedStocks.iterator();
            while (unMatchIterator.hasNext()) {
                Stock willMergeStock = unMatchIterator.next();
                Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
                Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                        , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

                if (mergeStock != null) {
                    mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                    stockService.save(mergeStock);
                    stockService.delete(willMergeStock);

                } else {
                    willMergeStock.setStockAdjust(null);
                    stockService.save(willMergeStock);
                }
            }
        }
    }

    @Transactional
    public void adjustConfirm(StockAdjustData stockAdjustData, AdminUser adminUser) {

        for (Long id : stockAdjustData.getAdjustIds()) {
            StockAdjust stockAdjust = stockAdjustService.findOne(id);
            if (stockAdjust == null) {
                continue;
            }
            if (!StockAdjustStatus.PENDINGAUDIT.getValue().equals(stockAdjust.getStatus())) {
                throw new UserDefinedException("SKU " + stockAdjust.getSku().getName() + "的状态已改变,请刷新数据再审核");
            }

            stockAdjust.setStatus(StockAdjustStatus.APPROVE.getValue());
            stockAdjust.setAuditDate(new Date());
            stockAdjust.setAuditor(adminUser);
            stockAdjustService.save(stockAdjust);

            if (stockAdjust.getQuantity() == 0) {//新商品盘盈
                this.saveStock(stockAdjust);
            } else {
                StockTotal stockTotal = stockTotalService.findStockTotal(stockAdjust.getDepot().getCity().getId(), stockAdjust.getSku().getId());
                BigDecimal avgCost = stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost();
                stockAdjust.setAvgCost(avgCost);
                stockAdjust = stockAdjustService.save(stockAdjust);

                int balance = stockAdjust.getAdjustQuantity()-stockAdjust.getQuantity();
                List<Stock> occupiedStocks = stockService.findAdjustOccupiedSocks(stockAdjust.getId());
                if (occupiedStocks != null && occupiedStocks.size() > 0) {
                    Stock occupiedStock = occupiedStocks.get(0);
                    occupiedStock.setStock(occupiedStock.getStock() + balance);
                    occupiedStock.setStockAdjust(null);
                    occupiedStock = stockService.save(occupiedStock);
                    stockTotalService.saveStockTotal(occupiedStock.getDepot().getCity(), occupiedStock.getSku(), balance, stockAdjust.getAvgCost());
                    if (occupiedStock.getStock() - 0 == 0) {
                        stockService.delete(occupiedStock);
                    }
                } else {
                    this.saveStock(stockAdjust);
                }
            }
        }
    }

    @Transactional
    public void adjustCancel(StockAdjustData stockAdjustData, AdminUser adminUser) {

        for (Long id : stockAdjustData.getAdjustIds()) {
            StockAdjust stockAdjust = stockAdjustService.findOne(id);
            if (stockAdjust == null) {
                continue;
            }
            if (!StockAdjustStatus.PENDINGAUDIT.getValue().equals(stockAdjust.getStatus())) {
                throw new UserDefinedException("SKU " + stockAdjust.getSku().getName() + "的状态已改变,请刷新数据再审核");
            }

            stockAdjust.setStatus(StockAdjustStatus.CANCEL.getValue());
            stockAdjustService.save(stockAdjust);

            List<Stock> occupiedStocks = stockService.findAdjustOccupiedSocks(stockAdjust.getId());
            //释放库存后，合并库存
            Iterator<Stock> unMatchIterator = occupiedStocks.iterator();
            while (unMatchIterator.hasNext()) {
                Stock willMergeStock = unMatchIterator.next();
                Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
                Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                        , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

                if (mergeStock != null) {
                    mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                    stockService.save(mergeStock);
                    stockService.delete(willMergeStock);

                } else {
                    willMergeStock.setStockAdjust(null);
                    stockService.save(willMergeStock);
                }
            }
        }
    }

    @Transactional
    private void saveStock(StockAdjust stockAdjust) {
        int balance = stockAdjust.getAdjustQuantity()-stockAdjust.getQuantity();
        if (balance <= 0) {
            return;
        }
        Stock stock = stockService.findMergeStock(stockAdjust.getDepot().getId(), stockAdjust.getSku().getId()
                , stockAdjust.getTaxRate(), null, stockAdjust.getExpirationDate(), null);

        if (stock != null) {
            stock.setStock(stock.getStock() + balance);
        } else {
            stock = new Stock();
            stock.setSku(stockAdjust.getSku());
            stock.setStock(balance);
            stock.setDepot(stockAdjust.getDepot());
            stock.setExpirationDate(stockAdjust.getExpirationDate());
            stock.setTaxRate(stockAdjust.getTaxRate());
        }
        stockService.save(stock);
        stockTotalService.saveStockTotal(stock.getDepot().getCity(), stock.getSku(), balance, stockAdjust.getAvgCost());
    }

    @Transactional(readOnly = true)
    public OrganizationVo getDefaultOrganization () {
        Organization organization = organizationService.getDefaultOrganization();
        if (organization == null) {
            return null;
        }
        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(organization.getId());
        organizationVo.setName(organization.getName());
        return organizationVo;
    }

    @Transactional(readOnly = true)
    public CandidateSkuWrapper getSku(Long id) {
        Sku sku = skuService.findOne(id);
        if (sku == null) {
            return null;
        }
        return new CandidateSkuWrapper(sku);
    }
}
