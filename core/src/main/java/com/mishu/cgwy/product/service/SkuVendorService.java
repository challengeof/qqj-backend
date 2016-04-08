package com.mishu.cgwy.product.service;

import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.controller.SkuCandidatesRequest;
import com.mishu.cgwy.product.controller.SkuListRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.SkuRepository;
import com.mishu.cgwy.product.repository.SkuVendorRepository;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import org.apache.commons.collections.CollectionUtils;
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
import javax.persistence.criteria.*;
import java.util.*;

@Service
public class SkuVendorService {
	
    @Autowired
    private SkuVendorRepository skuVendorRepository;

    public List<SkuVendor> findByCityIdAndSkuId(Long cityId, Long skuId) {
        return skuVendorRepository.findByCityIdAndSkuId(cityId, skuId);
    }

    public SkuVendor findOne(Long cityId, Long skuId) {
        List<SkuVendor> list = skuVendorRepository.findByCityIdAndSkuId(cityId, skuId);
        return CollectionUtils.isNotEmpty(list) ? list.get(0) : null;
    }

    public List<SkuVendor> findByCityIdAndVendorId(final Long cityId, final Long vendorId) {
        List<SkuVendor> skuVendors = skuVendorRepository.findAll(new Specification<SkuVendor>() {
            @Override
            public Predicate toPredicate(Root<SkuVendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get(SkuVendor_.city).get(City_.id), cityId));
                predicates.add(cb.equal(root.get(SkuVendor_.vendor).get(Vendor_.id), vendorId));
                predicates.add(cb.equal(root.get(SkuVendor_.sku).get(Sku_.status), SkuStatus.ACTIVE.getValue()));
                query.orderBy(cb.asc(root.get(SkuVendor_.sku).get(Sku_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        return skuVendors;
    }

    public SkuVendor getOne(Long id) {
        return skuVendorRepository.getOne(id);
    }

    public SkuVendor save(SkuVendor skuVendor) {
        return skuVendorRepository.save(skuVendor);
    }

    public Page<SkuVendor> getSkuVendorList(final SkuListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<SkuVendor> page = skuVendorRepository.findAll(new Specification<SkuVendor>() {
            @Override
            public Predicate toPredicate(Root<SkuVendor> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.equal(root.get(SkuVendor_.sku).get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));

                if (request.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(SkuVendor_.sku).get(Sku_.product).get(Product_.category).get(Category_.id), request.getCategoryId()));
                }

                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(SkuVendor_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                }

                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(SkuVendor_.sku).get(Sku_.product).get(Product_.brand).get(Brand_.id), request.getBrandId()));

                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(SkuVendor_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(SkuVendor_.sku).get(Sku_.status), request.getStatus()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    public SkuVendor findOne(Long id) {
        return skuVendorRepository.findOne(id);
    }
}
