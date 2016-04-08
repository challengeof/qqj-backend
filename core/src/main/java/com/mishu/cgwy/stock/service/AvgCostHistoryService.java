package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.stock.domain.AvgCostHistory;
import com.mishu.cgwy.stock.domain.AvgCostHistory_;
import com.mishu.cgwy.stock.dto.AvgCostHistoryRequest;
import com.mishu.cgwy.stock.repository.AvgCostHistoryRepository;
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
import java.util.List;

/**
 * User: Admin
 * Date: 9/18/15
 * Time: 12:02 PM
 */
@Service
public class AvgCostHistoryService {
    @Autowired
    private AvgCostHistoryRepository avgCostHistoryRepository;

    @Transactional(readOnly = true)
    public Page<AvgCostHistory> getAvgCostHistoryList(final AvgCostHistoryRequest request) {

        Sort sort = new Sort(new Sort.Order(Sort.Direction.ASC, AvgCostHistory_.city.getName()),
                new Sort.Order(Sort.Direction.ASC, AvgCostHistory_.sku.getName()),
                new Sort.Order(Sort.Direction.DESC, AvgCostHistory_.id.getName()));
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), sort);
        Page<AvgCostHistory> page = avgCostHistoryRepository.findAll(new Specification<AvgCostHistory>() {
            @Override
            public Predicate toPredicate(Root<AvgCostHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(AvgCostHistory_.city).get(City_.id), request.getCityId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(AvgCostHistory_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(AvgCostHistory_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }
}
