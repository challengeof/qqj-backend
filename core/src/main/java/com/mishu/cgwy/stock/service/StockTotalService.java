package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.stock.domain.AvgCostHistory;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.domain.StockTotal_;
import com.mishu.cgwy.stock.dto.StockTotalChange;
import com.mishu.cgwy.stock.dto.StockTotalChangeItem;
import com.mishu.cgwy.stock.dto.StockTotalRequest;
import com.mishu.cgwy.stock.repository.AvgCostHistoryRepository;
import com.mishu.cgwy.stock.repository.StockTotalRepository;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Service
public class StockTotalService {

    @Autowired
    private StockTotalRepository stockTotalRepository;
    @Autowired
    private AvgCostHistoryRepository avgCostHistoryRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public Page<StockTotal> getStockTotalList(final StockTotalRequest request) {

        final List<Long> categoryIds = new ArrayList<>();
        if (request.getCategoryId() != null) {
            Category category = categoryRepository.findOne(request.getCategoryId());
            if (category != null) {
                categoryIds.addAll(SkuCategoryUtils.getChildrenCategoryIds(category));
            }
        }
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<StockTotal> page = stockTotalRepository.findAll(new Specification<StockTotal>() {
            @Override
            public Predicate toPredicate(Root<StockTotal> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(StockTotal_.city).get(City_.id), request.getCityId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(StockTotal_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(StockTotal_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (!categoryIds.isEmpty()) {
                    predicates.add(root.get(StockTotal_.sku).get(Sku_.product).get(Product_.category).get(Category_.id).in(categoryIds));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    @Transactional(readOnly = true)
    public StockTotal findStockTotal(Long cityId, Long skuId) {
        return stockTotalRepository.findByCityIdAndSkuId(cityId, skuId);
    }

    @Transactional
    public void save (StockTotal stockTotal) {
        stockTotalRepository.save(stockTotal);
    }

    @Transactional
    public StockTotal saveStockTotal(City city, Sku sku, int quantity, BigDecimal price) {
        StockTotal stockTotal = this.findStockTotal(city.getId(), sku.getId());
        if (stockTotal == null) {
            stockTotal = new StockTotal();
            stockTotal.setQuantity(0);
            stockTotal.setAvgCost(BigDecimal.ZERO);
            stockTotal.setCity(city);
            stockTotal.setSku(sku);
            stockTotal.setTotalCost(BigDecimal.ZERO);
        }
        if (quantity != 0) {
            if (price == null) {
                price = stockTotal.getAvgCost();
            }

            stockTotal.setQuantity(stockTotal.getQuantity() + quantity);
            BigDecimal cost = price.multiply(new BigDecimal(quantity));
            stockTotal.setTotalCost(stockTotal.getTotalCost().add(cost));

            if (stockTotal.getQuantity() != 0) {
                stockTotal.setAvgCost(stockTotal.getTotalCost().divide(new BigDecimal(stockTotal.getQuantity()), 6, BigDecimal.ROUND_HALF_UP));
            }
            this.save(stockTotal);

            AvgCostHistory avgCostHistory = new AvgCostHistory();
            avgCostHistory.setAmount(stockTotal.getTotalCost());
            avgCostHistory.setAvgCost(stockTotal.getAvgCost());
            avgCostHistory.setSku(stockTotal.getSku());
            avgCostHistory.setCity(stockTotal.getCity());
            avgCostHistory.setQuantity(stockTotal.getQuantity());
            avgCostHistory.setDate(new Date());
            avgCostHistoryRepository.save(avgCostHistory);
        }
        return stockTotal;
    }

    @Transactional
    public void saveStockTotal(StockTotalChange stockTotalChange) {
        List<StockTotalChangeItem> stockTotalChangeItems = stockTotalChange.getStockTotalChangeItems();
        for (StockTotalChangeItem item : stockTotalChangeItems) {
            this.saveStockTotal(item.getCity(), item.getSku(), item.getQuantity(), item.getPrice());
        }
    }

    public List<StockTotal> findStockTotalBySkuId(Long skuId) {
        return stockTotalRepository.findBySkuId(skuId);
    }
}
