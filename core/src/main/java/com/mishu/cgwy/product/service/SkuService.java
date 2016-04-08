package com.mishu.cgwy.product.service;

import java.util.*;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;

import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.controller.SkuCandidatesRequest;
import com.mishu.cgwy.product.controller.SkuListRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.wrapper.CandidateSkuWrapper;
import com.mishu.cgwy.purchase.domain.PurchaseOrderItem;
import com.mishu.cgwy.purchase.repository.PurchaseOrderItemRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.product.repository.SkuRepository;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.profile.domain.Customer_;

@Service
public class SkuService {
	
    @Autowired
    private EntityManager entityManager;
	
    @Autowired
    private SkuRepository skuRepository;

    @Transactional
    public List<Sku> findAll(final Date date, final Long warehouseId) {
        return skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            	Subquery<OrderItem> sq = query.subquery(OrderItem.class);
            	Root<OrderItem> sqr = sq.from(OrderItem.class);
            	sq.select(sqr);
            	
            	Predicate subPredicate1 = cb.equal(root.get(Sku_.id), sqr.get(OrderItem_.sku).get(Sku_.id));
            	Predicate subPredicate2 = cb.equal(sqr.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), warehouseId);
            	Predicate subPredicate3 = cb.lessThanOrEqualTo(sqr.get(OrderItem_.order).get(Order_.submitDate), DateUtils.addDays(date, 1));
            	Predicate subPredicate4 = cb.greaterThanOrEqualTo(sqr.get(OrderItem_.order).get(Order_.submitDate), date);

            	sq.where(cb.and(subPredicate1, subPredicate2, subPredicate3, subPredicate4));
            	
                List<Predicate> predicates = new ArrayList<Predicate>();
                
                predicates.add(cb.exists(sq));

                query.distinct(true);

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));

            }
        });
    }

    public Sku getOne(Long skuId) {
        return skuRepository.getOne(skuId);
    }

    public Sku findOne(Long skuId) {
        return skuRepository.findOne(skuId);
    }


    public List<Sku> getSkuList(final List<Long> skuIds) {

        return skuRepository.findByIdIn(skuIds);
    }

    @Transactional
    public Page<Sku> getSkuList(final SkuListRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<Sku> page = skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.equal(root.get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));

                if (request.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.category).get(Category_.id), request.getCategoryId()));
                }

                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                }

                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.brand).get(Brand_.id), request.getBrandId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Sku_.status), request.getStatus()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    public List<CandidateSkuWrapper> getSkuCandidates(final SkuCandidatesRequest request) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<Sku> page = skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.like(root.get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getName())));

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                predicates.add(cb.equal(root.get(Sku_.status), SkuStatus.ACTIVE.getValue()));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        List<CandidateSkuWrapper> wrappers = new ArrayList<>();

        for (Sku sku : page.getContent()) {
            wrappers.add(new CandidateSkuWrapper(sku));
        }
        System.out.println(request);
        System.out.println(wrappers);

        return wrappers;
    }

    public Sku saveSku(Sku sku) {
        return skuRepository.save(sku);
    }
}
