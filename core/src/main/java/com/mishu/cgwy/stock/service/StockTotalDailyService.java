package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.Category_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.domain.StockTotalDaily;
import com.mishu.cgwy.stock.domain.StockTotalDaily_;
import com.mishu.cgwy.stock.dto.StockTotalDailyRequest;
import com.mishu.cgwy.stock.repository.StockTotalDailyRepository;
import com.mishu.cgwy.stock.repository.StockTotalRepository;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.math.BigDecimal;
import java.util.*;

@Service
public class StockTotalDailyService {

    @Autowired
    private StockTotalDailyRepository stockTotalDailyRepository;
    @Autowired
    private CategoryRepository categoryRepository;
    @Autowired
    private StockTotalRepository stockTotalRepository;
    @PersistenceContext
    private EntityManager entityManager;

    @Transactional(readOnly = true)
    public Page<StockTotalDaily> getStockTotalDailyList(final StockTotalDailyRequest request, final AdminUser adminUser) {

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
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<StockTotalDaily> page = stockTotalDailyRepository.findAll(new StockTotalDailySpecification(request, adminUser), pageable);

        return page;
    }

    @Transactional(readOnly = true)
    public BigDecimal getStockTotalDailyAmount(final StockTotalDailyRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        final Root<StockTotalDaily> root = query.from(StockTotalDaily.class);

        final Specification<StockTotalDaily> specification = new StockTotalDailySpecification(request, adminUser);
        query.select(cb.sum(root.get(StockTotalDaily_.totalCost)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional
    public List<StockTotalDaily> saveStockTotalDaily() {
        int batchSize = 100;
        List<StockTotal> stockTotals = stockTotalRepository.findAll();
        List<StockTotalDaily> stockTotalDailies = new ArrayList<>();
        int i = 0;
        for (StockTotal stockTotal : stockTotals) {
            StockTotalDaily stockTotalDaily = new StockTotalDaily();
            stockTotalDaily.setAvgCost(stockTotal.getAvgCost());
            stockTotalDaily.setCity(stockTotal.getCity());
            stockTotalDaily.setCreateDate(DateUtils.addDays(new Date(), -1));
            stockTotalDaily.setQuantity(stockTotal.getQuantity());
            stockTotalDaily.setSku(stockTotal.getSku());
            stockTotalDaily.setTotalCost(stockTotal.getTotalCost());
            stockTotalDailies.add(persistOrMerge(stockTotalDaily));
            i++;
            if (i % batchSize == 0) {
                // Flush a batch of inserts and release memory.
                entityManager.flush();
                entityManager.clear();
            }
        }
        return stockTotalDailies;
    }

    @Transactional
    private StockTotalDaily persistOrMerge(StockTotalDaily t) {
        if (t.getId() == null) {
            entityManager.persist(t);
            return t;
        } else {
            return entityManager.merge(t);
        }
    }

    private static class StockTotalDailySpecification implements Specification<StockTotalDaily> {

        private final StockTotalDailyRequest request;
        private final AdminUser adminUser;

        public StockTotalDailySpecification(StockTotalDailyRequest request, AdminUser adminUser) {
            this.request = request;
            this.adminUser = adminUser;
        }

        @Override
        public Predicate toPredicate(Root<StockTotalDaily> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();
            if (adminUser != null) {
                Set<Long> cityIds = new HashSet<>();

                for (City city : adminUser.getDepotCities()) {
                    cityIds.add(city.getId());
                }

                List<Predicate> depotCondition = new ArrayList<>();
                if (!cityIds.isEmpty()) {
                    depotCondition.add(root.get(StockTotalDaily_.city).get(City_.id).in(cityIds));
                }

                if (!depotCondition.isEmpty()) {
                    predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                } else {
                    predicates.add(cb.or());
                }
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(StockTotalDaily_.city).get(City_.id), request.getCityId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(root.get(StockTotalDaily_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(root.get(StockTotalDaily_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                predicates.add(root.get(StockTotalDaily_.sku).get(Sku_.product).get(Product_.category).get(Category_.id).in(request.getCategoryIds()));
            }
            if (request.getStartCreateDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(StockTotalDaily_.createDate), request.getStartCreateDate()));
            }
            if (request.getEndCreateDate() != null) {
                predicates.add(cb.lessThan(root.get(StockTotalDaily_.createDate), request.getEndCreateDate()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}
