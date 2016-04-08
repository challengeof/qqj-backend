package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.purchase.domain.PurchaseOrder;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItemStatus;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockInRequest;
import com.mishu.cgwy.stock.repository.StockInItemRepository;
import com.mishu.cgwy.stock.repository.StockInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: Admin
 * Date: 9/16/15
 * Time: 12:02 PM
 */
@Service
public class StockInService {
    @Autowired
    private StockInRepository stockInRepository;
    @Autowired
    private StockInItemRepository stockInItemRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional
    public StockIn createStockIn(PurchaseOrder purchaseOrder) {

        if (purchaseOrder == null || purchaseOrder.getPurchaseOrderItems() == null || purchaseOrder.getPurchaseOrderItems().size() == 0) {
            return null;
        }

        StockIn stockIn = new StockIn();
        stockIn.setCreateDate(new Date());
        stockIn.setCreator(purchaseOrder.getCreater());
        stockIn.setDepot(purchaseOrder.getDepot());
        stockIn.setPurchaseOrder(purchaseOrder);
        stockIn.setStatus(StockInStatus.UNACCEPTED.getValue());
        stockIn.setType(StockInType.PURCHASE.getValue());

        BigDecimal amount = BigDecimal.ZERO;
        List<StockInItem> stockInItems = new ArrayList<>();

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrder.getPurchaseOrderItems()) {
            if (PurchaseOrderItemStatus.EXECUTION.getValue().equals(purchaseOrderItem.getStatus()) && purchaseOrderItem.getPurchaseQuantity().intValue() > 0) {
                StockInItem stockInItem = new StockInItem();
                stockInItem.setExpectedQuantity(purchaseOrderItem.getPurchaseQuantity());
                stockInItem.setRealQuantity(purchaseOrderItem.getPurchaseQuantity());
                stockInItem.setPrice(purchaseOrderItem.getPrice());
                stockInItem.setSku(purchaseOrderItem.getSku());
                if (purchaseOrderItem.getRate() == null) {
                    stockInItem.setTaxRate(BigDecimal.ZERO);
                } else {
                    stockInItem.setTaxRate(purchaseOrderItem.getRate());
                }

                if (stockInItem.getPrice() != null) {
                    amount = amount.add(stockInItem.getPrice().multiply(new BigDecimal(stockInItem.getExpectedQuantity())));
                }
                stockInItem.setStockIn(stockIn);
                stockInItems.add(stockInItem);
            }
        }

        if (stockInItems.size() > 0) {
            stockIn.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
            stockIn.setStockInItems(stockInItems);
            stockInRepository.save(stockIn);
            return stockIn;
        } else {
            return null;
        }
    }

    @Transactional
    public StockIn createStockIn(StockOut stockOut) {

        if (stockOut == null || stockOut.getStockOutItems() == null
                || stockOut.getStockOutItems().size() == 0 || stockOut.getTransfer() == null) {
            return null;
        }

        StockIn stockIn = new StockIn();
        stockIn.setCreateDate(new Date());
        stockIn.setCreator(stockOut.getTransfer().getCreator());
        stockIn.setDepot(stockOut.getTransfer().getTargetDepot());
        stockIn.setTransfer(stockOut.getTransfer());
        stockIn.setStatus(StockInStatus.UNACCEPTED.getValue());
        stockIn.setType(StockInType.TRANSFER.getValue());

        BigDecimal amount = BigDecimal.ZERO;
        List<StockInItem> stockInItems = new ArrayList<>();

        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus()) && stockOutItem.getRealQuantity() > 0) {
                StockInItem stockInItem = new StockInItem();
                stockInItem.setExpectedQuantity(stockOutItem.getRealQuantity());
                stockInItem.setRealQuantity(stockOutItem.getRealQuantity());
                stockInItem.setPrice(stockOutItem.getAvgCost());
                stockInItem.setSku(stockOutItem.getSku());
                if (stockOutItem.getTaxRate() == null) {
                    stockInItem.setTaxRate(BigDecimal.ZERO);
                } else {
                    stockInItem.setTaxRate(stockOutItem.getTaxRate());
                }
                stockInItem.setAvgCost(stockOutItem.getAvgCost());

                if (stockInItem.getPrice() != null) {
                    amount = amount.add(stockInItem.getPrice().multiply(new BigDecimal(stockInItem.getExpectedQuantity())));
                }
                stockInItems.add(stockInItem);
            }
        }

        if (stockInItems.size() > 0) {
            stockIn.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
            stockIn.setStockInItems(stockInItems);
            stockInRepository.save(stockIn);
            return stockIn;
        } else {
            return null;
        }
    }

    @Transactional
    public StockIn createStockIn(SellReturn sellReturn) {

        if (sellReturn == null || sellReturn.getSellReturnItems() == null
                || sellReturn.getSellReturnItems().size() == 0) {
            return null;
        }

        StockIn stockIn = new StockIn();
        stockIn.setCreateDate(new Date());
        stockIn.setCreator(sellReturn.getCreator());
        stockIn.setDepot(sellReturn.getDepot());
        stockIn.setSellReturn(sellReturn);
        stockIn.setStatus(StockInStatus.UNACCEPTED.getValue());
        stockIn.setType(StockInType.RETURN.getValue());

//        BigDecimal amount = BigDecimal.ZERO;
        List<StockInItem> stockInItems = new ArrayList<>();

        for (SellReturnItem sellReturnItem : sellReturn.getSellReturnItems()) {
            if (sellReturnItem.getQuantity() > 0) {
                StockInItem stockInItem = new StockInItem();
                stockInItem.setExpectedQuantity(sellReturnItem.getQuantity());
                stockInItem.setRealQuantity(sellReturnItem.getQuantity());
                stockInItem.setPrice(sellReturnItem.getAvgCost());
                stockInItem.setSalePrice(sellReturnItem.getPrice());
                stockInItem.setSku(sellReturnItem.getSku());
                if (sellReturnItem.getTaxRate() == null) {
                    stockInItem.setTaxRate(BigDecimal.ZERO);
                } else {
                    stockInItem.setTaxRate(sellReturnItem.getTaxRate());
                }

                /*if (stockInItem.getSalePrice() != null) {
                    amount = amount.add(stockInItem.getSalePrice().multiply(new BigDecimal(stockInItem.getExpectedQuantity())));
                }*/
                stockInItems.add(stockInItem);
            }
        }

        if (stockInItems.size() > 0) {
            //这里取退货单上金额，部分优惠会减掉
            stockIn.setAmount(sellReturn.getAmount());
            stockIn.setStockInItems(stockInItems);
            stockInRepository.save(stockIn);
            return stockIn;
        } else {
            return null;
        }
    }

    public Page<StockIn> getStockInList(final StockInRequest request, final AdminUser adminUser) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<StockIn> page = stockInRepository.findAll(new Specification<StockIn>() {

            @Override
            public Predicate toPredicate(Root<StockIn> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                query.orderBy(cb.desc(root.get(StockIn_.id)));

                if (adminUser != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(StockIn_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(StockIn_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                final ListJoin<StockIn, StockInItem> join = root.join(StockIn_.stockInItems);
                query.distinct(true);

                if (request.getStockInType() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockIn_.type), request.getStockInType()));
                }
                if (request.getStockInStatus() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockIn_.status), request.getStockInStatus()));
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(StockIn_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockIn_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getStockInId() != null) {
                    predicates.add(cb.equal(root.get(StockIn_.id), request.getStockInId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(join.get(StockInItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(join.get(StockInItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (request.getSourceId() != null) {
                    Predicate p1 = cb.equal(root.get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getSourceId());
                    Predicate p2 = cb.equal(root.get(StockIn_.sellReturn).get(SellReturn_.id), request.getSourceId());
                    Predicate p3 = cb.equal(root.get(StockIn_.transfer).get(Transfer_.id), request.getSourceId());
                    predicates.add(cb.or(p1, p2, p3));
                }
                if (request.getStartCreateDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockIn_.createDate), request.getStartCreateDate()));
                }
                if (request.getEndCreateDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockIn_.createDate), request.getEndCreateDate()));
                }
                if (request.getStartReceiveDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockIn_.receiveDate), request.getStartReceiveDate()));
                }
                if (request.getEndReceiveDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockIn_.receiveDate), request.getEndReceiveDate()));
                }
                if (request.getOutPrint() != null) {
                    predicates.add(cb.equal(root.get(StockIn_.outPrint), request.getOutPrint()));
                }

                if (request.getStockInType() == StockInType.PURCHASE.getValue()) {
                    if (request.getPurchaseOrderType() != Integer.MAX_VALUE) {
                        predicates.add(cb.equal(root.get(StockIn_.purchaseOrder).get(PurchaseOrder_.type), request.getPurchaseOrderType()));
                    }
                    if (request.getPurchaseOrderId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseOrderId()));
                    }
                    if (request.getVendorId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                    }
                    if (request.getVendorName() != null) {
                        predicates.add(cb.like(root.get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.name), "%" + request.getVendorName() + "%"));
                    }
                } else if (request.getStockInType() == StockInType.RETURN.getValue()) {
                    if (request.getSellReturnType() != Integer.MAX_VALUE) {
                        predicates.add(cb.equal(root.get(StockIn_.sellReturn).get(SellReturn_.type), request.getSellReturnType()));
                    }
                    if (request.getSellReturnId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.sellReturn).get(SellReturn_.id), request.getSellReturnId()));
                    }
                    if (request.getOrderId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.sellReturn).get(SellReturn_.order).get(Order_.id), request.getOrderId()));
                    }
                } else if (request.getStockInType() == StockInType.TRANSFER.getValue()) {
                    if (request.getTransferId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.transfer).get(Transfer_.id), request.getTransferId()));
                    }
                    if (request.getSourceDepotId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.transfer).get(Transfer_.sourceDepot).get(Depot_.id), request.getSourceDepotId()));
                    }
                    if (request.getTargetDepotId() != null) {
                        predicates.add(cb.equal(root.get(StockIn_.depot).get(Depot_.id), request.getTargetDepotId()));
                    }
                }
                if (request.getSaleReturn() != null) {
                    if (request.getSaleReturn().intValue() == 1) {
                        predicates.add(cb.equal(root.get(StockIn_.type), StockInType.RETURN.getValue()));
                    } else {
                        predicates.add(cb.notEqual(root.get(StockIn_.type), StockInType.RETURN.getValue()));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    public Page<StockInItem> getStockInItemList(final StockInRequest request, final AdminUser adminUser) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<StockInItem> page = stockInItemRepository.findAll(new Specification<StockInItem>() {

            @Override
            public Predicate toPredicate(Root<StockInItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                query.orderBy(cb.desc(root.get(StockInItem_.stockIn).get(StockIn_.id)));

                if (adminUser != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getStockInType() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.type), request.getStockInType()));
                }
                if (request.getStockInStatus() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.status), request.getStockInStatus()));
                }
                if (request.getStockInType() == StockInType.RETURN.getValue() && request.getSellReturnType() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.sellReturn).get(SellReturn_.type), request.getSellReturnType()));
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getStockInId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.id), request.getStockInId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(StockInItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (request.getSourceId() != null) {
                    Predicate p1 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getSourceId());
                    Predicate p2 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.sellReturn).get(SellReturn_.id), request.getSourceId());
                    Predicate p3 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.transfer).get(Transfer_.id), request.getSourceId());
                    predicates.add(cb.or(p1, p2, p3));
                }
                if (request.getStartReceiveDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockInItem_.stockIn).get(StockIn_.receiveDate), request.getStartReceiveDate()));
                }
                if (request.getEndReceiveDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockInItem_.stockIn).get(StockIn_.receiveDate), request.getEndReceiveDate()));
                }
                if (request.getStockInType() == StockInType.PURCHASE.getValue()) {
                    if (request.getVendorId() != null) {
                        predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                    }
                }
                if (request.getSaleReturn() != null) {
                    if (request.getSaleReturn().intValue() == 1) {
                        predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.type), StockInType.RETURN.getValue()));
                    } else {
                        predicates.add(cb.notEqual(root.get(StockInItem_.stockIn).get(StockIn_.type), StockInType.RETURN.getValue()));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    @Transactional(readOnly = true)
    public BigDecimal getStockInItemAmount(final StockInRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Number> query = cb.createQuery(Number.class);
        final Root<StockInItem> root = query.from(StockInItem.class);

        final Specification<StockInItem> specification = new Specification<StockInItem>() {

            @Override
            public Predicate toPredicate(Root<StockInItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                query.orderBy(cb.desc(root.get(StockInItem_.stockIn).get(StockIn_.id)));

                if (adminUser != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getStockInType() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.type), request.getStockInType()));
                }
                if (request.getStockInStatus() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.status), request.getStockInStatus()));
                }
                if (request.getStockInType() == StockInType.RETURN.getValue() && request.getSellReturnType() != Integer.MAX_VALUE) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.sellReturn).get(SellReturn_.type), request.getSellReturnType()));
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getStockInId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.id), request.getStockInId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(StockInItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(StockInItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (request.getSourceId() != null) {
                    Predicate p1 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), request.getSourceId());
                    Predicate p2 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.sellReturn).get(SellReturn_.id), request.getSourceId());
                    Predicate p3 = cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.transfer).get(Transfer_.id), request.getSourceId());
                    predicates.add(cb.or(p1, p2, p3));
                }
                if (request.getStartReceiveDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockInItem_.stockIn).get(StockIn_.receiveDate), request.getStartReceiveDate()));
                }
                if (request.getEndReceiveDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockInItem_.stockIn).get(StockIn_.receiveDate), request.getEndReceiveDate()));
                }
                if (request.getStockInType() == StockInType.PURCHASE.getValue()) {
                    if (request.getVendorId() != null) {
                        predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                    }
                }
                if (request.getSaleReturn() != null) {
                    if (request.getSaleReturn().intValue() == 1) {
                        predicates.add(cb.equal(root.get(StockInItem_.stockIn).get(StockIn_.type), StockInType.RETURN.getValue()));
                    } else {
                        predicates.add(cb.notEqual(root.get(StockInItem_.stockIn).get(StockIn_.type), StockInType.RETURN.getValue()));
                    }
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        query.select(cb.sum(cb.prod(cb.<Integer>selectCase().when(cb.isNull(root.get(StockInItem_.realQuantity)), 0).otherwise(root.get(StockInItem_.realQuantity)),
                root.get(StockInItem_.price))));
        query.where(specification.toPredicate(root, query, cb));

        return (BigDecimal)entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public StockIn getStockIn(Long stockInId) {
        return stockInRepository.findOne(stockInId);
    }

    @Transactional
    public StockIn saveStockIn(StockIn stockIn) {
        return stockInRepository.save(stockIn);
    }

    @Transactional(readOnly = true)
    public StockIn findOneStockIn(Long stockInId) {
        return stockInRepository.getOne(stockInId);
    }

    @Transactional(readOnly = true)
    public StockInItem findOneStockInItem(Long stockInItemId) {
        return stockInItemRepository.getOne(stockInItemId);
    }

    @Transactional
    public void saveStockInItem(StockInItem stockInItem) {
        stockInItemRepository.save(stockInItem);
    }

    @Transactional(readOnly = true)
    public List<StockIn> getStockInByIds(final Long[] ids) {
        return stockInRepository.findAll(new Specification<StockIn>() {
            @Override
            public Predicate toPredicate(Root<StockIn> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get(StockIn_.id).in(ids);
            }
        });
    }

    @Transactional
    public void updateStockInPrintStatus(Long[] ids) {
        for (Long id : ids) {
            StockIn stockIn = stockInRepository.getOne(id);
            stockIn.setOutPrint(true);
            stockInRepository.save(stockIn);
        }
    }

    @Transactional
    public StockInItem split(StockInItem sourceStockInItem, int quantity) {
        if (sourceStockInItem.getExpectedQuantity() - quantity == 0) {
            return sourceStockInItem;
        }
        StockInItem newStockInItem = sourceStockInItem.clone();
        newStockInItem.setRealQuantity(quantity);
        newStockInItem.setExpectedQuantity(quantity);
        stockInItemRepository.save(newStockInItem);

        sourceStockInItem.setExpectedQuantity(sourceStockInItem.getExpectedQuantity() - quantity);
        sourceStockInItem.setRealQuantity(sourceStockInItem.getExpectedQuantity());
        stockInItemRepository.save(sourceStockInItem);

        return newStockInItem;
    }

    public List<StockIn> findByPurchaseOrderId(Long purchaseOrderId) {
        return stockInRepository.findByPurchaseOrderId(purchaseOrderId);
    }

    public List<StockInItem> findStockInItemBySkuId(Long skuId) {
        return stockInItemRepository.findBySkuId(skuId);
    }
}
