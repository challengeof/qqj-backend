package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.Category_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockQueryRequest;
import com.mishu.cgwy.stock.repository.StockRepository;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 9/14/15
 * Time: 12:02 PM
 */
@Service
public class StockService {

    @Autowired
    private StockRepository stockRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Object[]> getStock(final StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(null);
        final Specification<Stock> specification = new StockSpecification(request);
        query.multiselect(root, cb.sum(cb.<Integer>selectCase().when(cb.and(cb.isNull(root.get(Stock_.stockIn).get(StockIn_.id)), cb.isNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))), root.get(Stock_.stock)).otherwise(0)), cb.sum(cb.<Integer>selectCase().when(cb.or(cb.isNotNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNotNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))), root.get(Stock_.stock)).otherwise(0)), cb.sum(cb.<Integer>selectCase().when(cb.isNotNull(root.get(Stock_.stockIn).get(StockIn_.id)), root.get(Stock_.stock)).otherwise(0)));

        if (request.getCategoryId() != null) {
            final List<Long> categoryIds = new ArrayList<>();
            Category category = categoryRepository.findOne(request.getCategoryId());
            if (category != null) {
                categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
            }
            if (!categoryIds.isEmpty()) {
                request.setCategoryIds(categoryIds);
            }
        }
        query.where(specification.toPredicate(root, query, cb));
        query.groupBy(root.get(Stock_.depot).get(Depot_.id), root.get(Stock_.sku).get(Sku_.id));
        query.orderBy(cb.asc(root.get(Stock_.depot).get(Depot_.id)), cb.asc(root.get(Stock_.sku).get(Sku_.id)));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public Integer findDepotStockTotal(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.AVAILABLE.getValue());
        final Specification<Stock> specification = new StockSpecification(request);
        query.select(cb.sum(root.get(Stock_.stock)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Integer findDepotOnRoadStockTotal(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.ONROAD.getValue());
        final Specification<Stock> specification = new StockSpecification(request);
        query.select(cb.sum(root.get(Stock_.stock)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Integer findDepotOccupiedStockTotal(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.OCCUPIED.getValue());
        final Specification<Stock> specification = new StockSpecification(request);
        query.select(cb.sum(root.get(Stock_.stock)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Object[] findDepotAllStockTotal(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(null);
        final Specification<Stock> specification = new StockSpecification(request);

        query.multiselect(cb.sum(cb.<Integer>selectCase().when(cb.and(cb.isNull(root.get(Stock_.stockIn).get(StockIn_.id)), cb.isNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))), root.get(Stock_.stock)).otherwise(0))
                , cb.sum(cb.<Integer>selectCase().when(cb.or(cb.isNotNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNotNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))), root.get(Stock_.stock)).otherwise(0))
                , cb.sum(cb.<Integer>selectCase().when(cb.isNotNull(root.get(Stock_.stockIn).get(StockIn_.id)), root.get(Stock_.stock)).otherwise(0)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional
    public void saveStockTransfer(StockIn stockin) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
        final Root<Stock> root = query.from(Stock.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Stock_.depot).get(Depot_.id), stockin.getTransfer().getSourceDepot().getId()));
        predicates.add(cb.equal(root.get(Stock_.stockIn).get(StockIn_.id), stockin.getId()));

        query.where(predicates.toArray(new Predicate[predicates.size()]));
        List<Stock> stocks = entityManager.createQuery(query).getResultList();

        if (stocks != null) {
            Iterator<Stock> stockIterator = stocks.iterator();
            while (stockIterator.hasNext()) {
                Stock stock = stockIterator.next();
                Stock mergeStock = this.findMergeStock(stockin.getDepot().getId(), stock.getSku().getId()
                        , stock.getTaxRate(), null, stock.getExpirationDate(), null);
                if (mergeStock != null) {
                    mergeStock.setStock(mergeStock.getStock() + stock.getStock());
                    stockRepository.save(mergeStock);
                    stockRepository.delete(stock);
                } else {
                    stock.setDepot(stockin.getDepot());
                    stock.setShelf(null);
                    stock.setStockIn(null);
                    stockRepository.save(stock);
                }
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Stock> findOccupiedSocks(Long stockOutId) {
        return stockRepository.findByStockOutId(stockOutId);
    }

    @Transactional
    public Stock split(Stock sourceStock, int quantity) {
        if (sourceStock.getStock() - quantity == 0) {
            return sourceStock;
        }
        Stock newStock = sourceStock.clone();
        newStock.setStock(quantity);
        stockRepository.save(newStock);

        sourceStock.setStock(sourceStock.getStock() - quantity);
        stockRepository.save(sourceStock);

        return newStock;
    }

    @Transactional
    public Stock save(Stock stock) {
        return stockRepository.save(stock);
    }

    @Transactional
    public void delete(Stock stock) {
        stockRepository.delete(stock);
    }

    @Transactional(readOnly = true)
    public Stock findMergeStock(Long depotId, Long skuId, BigDecimal taxRate, Long shelfId, Date expirationDate, Long stockId) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
        final Root<Stock> root = query.from(Stock.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(Stock_.depot).get(Depot_.id), depotId));
        predicates.add(cb.equal(root.get(Stock_.sku).get(Sku_.id), skuId));
        predicates.add(cb.isNull(root.get(Stock_.stockIn).get(StockIn_.id)));
        predicates.add(cb.isNull(root.get(Stock_.stockOut).get(StockOut_.id)));
        predicates.add(cb.isNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id)));
        if (taxRate == null) {
            predicates.add(cb.isNull(root.get(Stock_.taxRate)));
        } else {
            predicates.add(cb.equal(root.get(Stock_.taxRate), taxRate));
        }
        if (shelfId == null) {
            predicates.add(cb.isNull(root.get(Stock_.shelf)));
        } else {
            predicates.add(cb.equal(root.get(Stock_.shelf).get(Shelf_.id), shelfId));
        }
        if (expirationDate != null) {
            predicates.add(cb.equal(root.get(Stock_.expirationDate), expirationDate));
        } else {
            predicates.add(cb.isNull(root.get(Stock_.expirationDate)));
        }
        if (stockId != null) {
            predicates.add(cb.notEqual(root.get(Stock_.id), stockId));
        }

        query.where(predicates.toArray(new Predicate[predicates.size()]));
        query.orderBy(cb.asc(root.get(Stock_.id)));

        List<Stock> stocks = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (stocks != null && stocks.size() > 0) {
            return stocks.get(0);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public List<Stock> findDepotAvailableStock(Long depotId, Long skuId, BigDecimal taxRate) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
        final Root<Stock> root = query.from(Stock.class);
        StockQueryRequest request = new StockQueryRequest();
        request.setDepotId(depotId);
        request.setSkuId(skuId);
        request.setStatus(StockStatus.AVAILABLE.getValue());
        if (taxRate != null) {
            request.setTaxRate(taxRate);
        }
        final Specification<Stock> specification = new StockSpecification(request);
        query.where(specification.toPredicate(root, query, cb));
        query.orderBy(cb.asc(root.get(Stock_.expirationDate)), cb.asc(root.get(Stock_.id)));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional
    public int occupySockQuantity(StockOut stockOut, int outQuantity, Long depotId, Long skuId, BigDecimal taxRate) {

        List<Stock> stocks = this.findDepotAvailableStock(depotId, skuId, taxRate);
        for (Stock stock : stocks) {
            if (outQuantity <= 0) {
                break;
            }
            if (outQuantity >= stock.getStock()) {
                outQuantity -= stock.getStock();
                stock.setStockOut(stockOut);
                this.save(stock);
            } else {
                Stock newStock = this.split(stock, outQuantity);
                newStock.setStockOut(stockOut);
                this.save(newStock);
                outQuantity = 0;
                break;
            }
        }

        return outQuantity;
    }

    @Transactional(readOnly = true)
    public List<Stock> findSockByShelf(Long shelfId) {
        return stockRepository.findByShelfId(shelfId);
    }

    @Transactional(readOnly = true)
    public List<Object[]> getWillOnShelfStock(final StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.AVAILABLE.getValue());
        request.setShelfIsNull(true);
        final Specification<Stock> specification = new StockSpecification(request);
        query.multiselect(root, cb.sum(cb.<Integer>selectCase().when(cb.and(cb.isNull(root.get(Stock_.stockIn).get(StockIn_.id)), cb.isNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))), root.get(Stock_.stock)).otherwise(0)));

        if (request.getCategoryId() != null) {
            final List<Long> categoryIds = new ArrayList<>();
            Category category = categoryRepository.findOne(request.getCategoryId());
            if (category != null) {
                categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
            }
            if (!categoryIds.isEmpty()) {
                request.setCategoryIds(categoryIds);
            }
        }
        query.where(specification.toPredicate(root, query, cb));
        query.groupBy(root.get(Stock_.depot).get(Depot_.id), root.get(Stock_.sku).get(Sku_.id), root.get(Stock_.expirationDate));
        query.orderBy(cb.asc(root.get(Stock_.depot).get(Depot_.id)), cb.asc(root.get(Stock_.sku).get(Sku_.id)));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public Integer findAvailStockByDepotSku(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.AVAILABLE.getValue());
        request.setShelfIsNull(true);
        final Specification<Stock> specification = new StockSpecification(request);
        query.select(cb.sum(root.get(Stock_.stock)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public List<Stock> findAvailStocksByDepotSku(StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
        final Root<Stock> root = query.from(Stock.class);

        request.setStatus(StockStatus.AVAILABLE.getValue());
        request.setShelfIsNull(true);
        final Specification<Stock> specification = new StockSpecification(request);
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public Page<Stock> findStocksByShelf(final StockQueryRequest request) {

        PageRequest pageRequest;
        if (request.getSortField() != null) {
            switch (request.getSortField()) {
                case "skuId":
                    if (request.isAsc()) {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.ASC, Stock_.sku.getName()));
                    } else {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, Stock_.sku.getName()));
                    }
                    break;
                case "shelfId":
                    if (request.isAsc()) {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.ASC, Stock_.shelf.getName()));
                    } else {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, Stock_.shelf.getName()));
                    }
                    break;
                default:
                    if (request.isAsc()) {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.ASC, Stock_.shelf.getName()));
                    } else {
                        pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, Stock_.shelf.getName()));
                    }
                    break;
            }
        } else {
            pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        }
        if (request.getCategoryId() != null) {
            final List<Long> categoryIds = new ArrayList<>();
            Category category = categoryRepository.findOne(request.getCategoryId());
            if (category != null) {
                categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
            }
            if (!categoryIds.isEmpty()) {
                request.setCategoryIds(categoryIds);
            }
        }

        Page<Stock> page = stockRepository.findAll(new StockSpecification(request), pageRequest);

        return page;
    }

    @Transactional(readOnly = true)
    public Stock findOne (Long id) {
        return stockRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<Stock> findAdjustOccupiedSocks(Long stockAdjustId) {
        return stockRepository.findByStockAdjustId(stockAdjustId);
    }

    @Transactional(readOnly = true)
    public List<Stock> findDullSaleStocks(final StockQueryRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Stock> query = cb.createQuery(Stock.class);
        final Root<Stock> root = query.from(Stock.class);

        final Specification<Stock> specification = new StockSpecification(request);

        if (request.getCategoryId() != null) {
            final List<Long> categoryIds = new ArrayList<>();
            Category category = categoryRepository.findOne(request.getCategoryId());
            if (category != null) {
                categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
            }
            if (!categoryIds.isEmpty()) {
                request.setCategoryIds(categoryIds);
            }
        }
        query.where(specification.toPredicate(root, query, cb));

        List<Stock> stockList = entityManager.createQuery(query).getResultList();
        List<Object[]> salesList = new ArrayList<>();

        if (request.getDullSaleDays() != null) {

            final CriteriaQuery<Object[]> queryAr = cb.createQuery(Object[].class);
            final Root<AccountReceivableItem> rootAr = queryAr.from(AccountReceivableItem.class);
            queryAr.multiselect(rootAr.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.stockOut).get(StockOut_.depot).get(Depot_.city).get(City_.id),rootAr.get(AccountReceivableItem_.sku).get(Sku_.id));
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(rootAr.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()));
            Date dullSaleDate = DateUtils.addDays(new Date(), request.getDullSaleDays()*(-1));
            dullSaleDate = DateUtils.truncate(dullSaleDate, Calendar.DATE);
            predicates.add(cb.greaterThanOrEqualTo(rootAr.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), dullSaleDate));
            queryAr.where(predicates.toArray(new Predicate[predicates.size()]));
            queryAr.groupBy(rootAr.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.stockOut).get(StockOut_.depot).get(Depot_.city).get(City_.id), rootAr.get(AccountReceivableItem_.sku).get(Sku_.id));
            salesList = entityManager.createQuery(queryAr).getResultList();
        }
        Iterator<Stock> iteratorStock = stockList.iterator();
        while (iteratorStock.hasNext()) {

            Stock stock = iteratorStock.next();
            for (Object[] obj : salesList) {
                if (stock.getDepot().getCity().getId().equals((Long)obj[0])
                        && stock.getSku().getId().equals((Long)obj[1])) {
                    iteratorStock.remove();
                    break;
                }
            }
        }

        return stockList;
    }

    private static class StockSpecification implements Specification<Stock> {

        private final StockQueryRequest request;

        public StockSpecification(StockQueryRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<Stock> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

            if (request.getStatus() != null && StockStatus.ONROAD.getValue().equals(request.getStatus())) {
                predicates.add(cb.isNotNull(root.get(Stock_.stockIn).get(StockIn_.id)));
            } else if (request.getStatus() != null && StockStatus.AVAILABLE.getValue().equals(request.getStatus())) {
                predicates.add(cb.and(cb.isNull(root.get(Stock_.stockIn).get(StockIn_.id)), cb.isNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))));
            } else if (request.getStatus() != null && StockStatus.OCCUPIED.getValue().equals(request.getStatus())) {
                predicates.add(cb.or(cb.isNotNull(root.get(Stock_.stockOut).get(StockOut_.id)), cb.isNotNull(root.get(Stock_.stockAdjust).get(StockAdjust_.id))));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(Stock_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
            }
            if (request.getDepotId() != null) {
                predicates.add(cb.equal(root.get(Stock_.depot).get(Depot_.id), request.getDepotId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(root.get(Stock_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(root.get(Stock_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getTaxRate() != null) {
                predicates.add(cb.equal(root.get(Stock_.taxRate), request.getTaxRate()));
            }
            if (request.getShelfIsNull() != null) {
                if (request.getShelfIsNull()) {
                    predicates.add(cb.isNull(root.get(Stock_.shelf).get(Shelf_.id)));
                } else {
                    predicates.add(cb.isNotNull(root.get(Stock_.shelf).get(Shelf_.id)));
                }
            }
            if (request.getShelfCode() != null && StringUtils.isNotBlank(request.getShelfCode())) {
                predicates.add(cb.like(root.get(Stock_.shelf).get(Shelf_.shelfCode), request.getShelfCode() + "%"));
            }
            if (request.getExpirationDate() != null) {
                Calendar c = Calendar.getInstance();
                c.set(1900,1,1,0,0,0);
                c.set(Calendar.MILLISECOND, 0);
                if (request.getExpirationDate().compareTo(c.getTime()) == 0) {
                    predicates.add(cb.isNull(root.get(Stock_.expirationDate)));
                } else {
                    predicates.add(cb.equal(root.get(Stock_.expirationDate), DateUtils.truncate(request.getExpirationDate(),Calendar.DATE)));
                }

            }
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                predicates.add(root.get(Stock_.sku).get(Sku_.product).get(Product_.category).get(Category_.id).in(request.getCategoryIds()));
            }
            if (request.getProductionDate() != null && StringUtils.isNotBlank(request.getProductionDate())) {
                if (request.getProductionDate().equals("noDate")) {
                    predicates.add(cb.isNull(root.get(Stock_.expirationDate)));
                }
            }
            if (request.getExpireDays() != null) {
                Date expirationDate = DateUtils.addDays(new Date(), request.getExpireDays());
                expirationDate = DateUtils.truncate(expirationDate, Calendar.DATE);
                predicates.add(cb.lessThanOrEqualTo(root.get(Stock_.expirationDate), expirationDate));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    public List<Stock> findStockBySkuId(Long skuId) {
        return stockRepository.findBySkuId(skuId);
    }
}
