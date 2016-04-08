package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.domain.Category;
import com.mishu.cgwy.product.domain.Category_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.domain.StockAdjust;
import com.mishu.cgwy.stock.domain.StockAdjust_;
import com.mishu.cgwy.stock.dto.StockAdjustQueryRequest;
import com.mishu.cgwy.stock.repository.StockAdjustRepository;
import com.mishu.cgwy.utils.SkuCategoryUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StockAdjustService {

    @Autowired
    private StockAdjustRepository stockAdjustRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    @Transactional
    public StockAdjust save(StockAdjust stockAdjust) {
        return stockAdjustRepository.save(stockAdjust);
    }

    @Transactional(readOnly = true)
    public Page<StockAdjust> getStockAdjustList(final StockAdjustQueryRequest request, final AdminUser adminUser) {

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
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, StockAdjust_.id.getName()));
        Page<StockAdjust> page = stockAdjustRepository.findAll(new StockAdjustSpecification(request, adminUser), pageable);
        return page;
    }

    @Transactional(readOnly = true)
    public StockAdjust findOne (Long id) {
        return stockAdjustRepository.findOne(id);
    }

    private static class StockAdjustSpecification implements Specification<StockAdjust> {

        private final StockAdjustQueryRequest request;
        private final AdminUser adminUser;

        public StockAdjustSpecification(StockAdjustQueryRequest request, AdminUser adminUser) {
            this.request = request;
            this.adminUser = adminUser;
        }

        @Override
        public Predicate toPredicate(Root<StockAdjust> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<Predicate> predicates = new ArrayList<>();

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
                    depotCondition.add(root.get(StockAdjust_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                }
                if (!depotIds.isEmpty()) {
                    depotCondition.add(root.get(StockAdjust_.depot).get(Depot_.id).in(depotIds));
                }

                if (!depotCondition.isEmpty()) {
                    predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                } else {
                    predicates.add(cb.or());
                }
            }

            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get(StockAdjust_.status), request.getStatus()));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(StockAdjust_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
            }
            if (request.getDepotId() != null) {
                predicates.add(cb.equal(root.get(StockAdjust_.depot).get(Depot_.id), request.getDepotId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(root.get(StockAdjust_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(root.get(StockAdjust_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getCategoryIds() != null && !request.getCategoryIds().isEmpty()) {
                predicates.add(root.get(StockAdjust_.sku).get(Sku_.product).get(Product_.category).get(Category_.id).in(request.getCategoryIds()));
            }
            if (request.getStartCreateDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(StockAdjust_.createDate), request.getStartCreateDate()));
            }
            if (request.getEndCreateDate() != null) {
                predicates.add(cb.lessThan(root.get(StockAdjust_.createDate), request.getEndCreateDate()));
            }
            if (request.getStartAuditDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(StockAdjust_.auditDate), request.getStartAuditDate()));
            }
            if (request.getEndAuditDate() != null) {
                predicates.add(cb.lessThan(root.get(StockAdjust_.auditDate), request.getEndAuditDate()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }
}
