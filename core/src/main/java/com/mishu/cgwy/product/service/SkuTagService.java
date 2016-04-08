package com.mishu.cgwy.product.service;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.product.controller.SkuTagQueryRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.SkuTagRepository;
import com.mishu.cgwy.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangwei on 15/12/1.
 */
@Service
public class SkuTagService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SkuTagRepository skuTagRepository;

    public SkuTag getSkuTag(Sku sku, City city) {
        List<SkuTag> tags = skuTagRepository.findBySkuIdAndCityId(sku.getId(), city.getId());
        if (!tags.isEmpty()) {
            if (tags.size() > 1) {
                for (int i = 1; i < tags.size(); i ++) {
                    skuTagRepository.delete(tags.get(i));
                }
            }
            return tags.get(0);
        }
        return null;
    }

    public boolean checkSkuCityDiscount(Sku sku, City city) {
        SkuTag skuTag = getSkuTag(sku, city);
        if (null != skuTag) {
            return skuTag.getInDiscount();
        }
        return false;
    }

    @Transactional(readOnly = true)
    public Page<SkuTag> getSkuTag(final SkuTagQueryRequest request) {
        Pageable page = new PageRequest(request.getPage(), request.getPageSize());

        return skuTagRepository.findAll(new Specification<SkuTag>() {
            @Override
            public Predicate toPredicate(Root<SkuTag> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                if (request.getTagCityId() != null) {
                    predicates.add(cb.equal(root.get(SkuTag_.city).get(City_.id), request.getTagCityId()));
                }

                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(SkuTag_.sku).get(Sku_.id), request.getSkuId()));
                }

                if (request.getProductName() != null) {
                    predicates.add(cb.like(root.get(SkuTag_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, page);

    }
}
