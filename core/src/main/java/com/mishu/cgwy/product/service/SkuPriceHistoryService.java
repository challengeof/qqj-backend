package com.mishu.cgwy.product.service;

import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.product.controller.SkuPriceListRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.SkuPriceHistoryRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.util.ArrayList;
import java.util.List;

@Service
public class SkuPriceHistoryService {
	
    @Autowired
    private SkuPriceHistoryRepository skuPriceHistoryRepository;

    public SkuPriceHistory save(SkuPriceHistory history) {
        return skuPriceHistoryRepository.save(history);
    }

    public List<SkuPriceHistory> finaAll() {
        return skuPriceHistoryRepository.findAll();
    }

    public Page<SkuPriceHistory> findAll(Specification<SkuPriceHistory> skuPriceHistoryListSpecification, PageRequest pageable) {
        return skuPriceHistoryRepository.findAll(skuPriceHistoryListSpecification, pageable);
    }

    public SkuPriceHistory findByCityIdAndSkuIdAndTypeOrderByCreateDateDesc(Long cityId, Long skuId, Integer type) {
        Page<SkuPriceHistory> page = skuPriceHistoryRepository.findAll(getSkuPriceHistoryListSpecification(cityId, skuId, type), new PageRequest(0, 1));
        List<SkuPriceHistory> historyList = page.getContent();
        return CollectionUtils.isNotEmpty(historyList) ? historyList.get(0) : null;
    }

    private Specification<SkuPriceHistory> getSkuPriceHistoryListSpecification(final Long cityId, final Long skuId, final Integer type) {
        return new Specification<SkuPriceHistory>() {
            @Override
            public javax.persistence.criteria.Predicate toPredicate(Root<SkuPriceHistory> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(SkuPriceHistory_.city).get(City_.id), cityId));
                predicates.add(cb.equal(root.get(SkuPriceHistory_.sku).get(Sku_.id), skuId));
                predicates.add(cb.equal(root.get(SkuPriceHistory_.type), type));

                query.orderBy(cb.desc(root.get(SkuPriceHistory_.createDate)));

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[predicates.size()]));
            }
        };
    }
}
