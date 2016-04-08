package com.mishu.cgwy.product.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.controller.ProductOrDynamicPriceQueryRequest;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.domain.ChangeDetail_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.repository.ChangeDetailRepository;
import com.mishu.cgwy.stock.domain.Depot;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
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
import java.util.*;

/**
 * Created by bowen on 15-6-4.
 */
@Service
public class ChangeDetailService {

    @Autowired
    private ChangeDetailRepository changeDetailRepository;

    @Transactional
    public ChangeDetail saveChangeDetail(ChangeDetail changeDetail) {

        return changeDetailRepository.save(changeDetail);
    }

    @Transactional(readOnly = true)
    public ChangeDetail getChangeDetail(Long id) {
        return changeDetailRepository.getOne(id);
    }
    
    @Transactional(readOnly = true)
    public Page<ChangeDetail> findProductsOrDynamicPrices(final ProductOrDynamicPriceQueryRequest request, final AdminUser adminUser) {


        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, "id"));


        return changeDetailRepository.findAll(new Specification<ChangeDetail>() {
            @Override
            public Predicate toPredicate(Root<ChangeDetail> root, CriteriaQuery<?> query, CriteriaBuilder cb) {


                List<Predicate> predicates = new ArrayList<Predicate>();

                if(adminUser != null && request.getObjectType().equals(Constants.DYNAMIC_PRICE_TYPE)){
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }

                    Set<Long> warehouseIds = new HashSet<>();
                    Set<Long> cityIds = new HashSet<>();

                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }

                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        warehouseIds.add(warehouse.getId());
                    }

                    List<Predicate> blockCondition = new ArrayList<>();
                    if (!warehouseIds.isEmpty()) {
                        blockCondition.add(root.get(ChangeDetail_.warehouseId).in(warehouseIds));
                    }
                    if (!cityIds.isEmpty()) {
                        blockCondition.add(root.get(ChangeDetail_.cityId).in(cityIds));
                    }

                    if (!blockCondition.isEmpty()) {
                        predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }


                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.id), request.getId()));
                }
                if (request.getObjectId() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.objectId), request.getObjectId()));
                }
                if (request.getObjectType() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.objectType), request.getObjectType()));
                }
                if (request.getStatus() == null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.status), Constants.NOT_AUDIT));
                } else {
                    predicates.add(cb.equal(root.get(ChangeDetail_.status), request.getStatus()));
                }
                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(ChangeDetail_.productName), "%" + request.getProductName() + "%"));
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.organizationId), request.getOrganizationId()));
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.cityId), request.getCityId()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(ChangeDetail_.warehouseId), request.getWarehouseId()));
                }
                if (StringUtils.isNotBlank(request.getSubmitRealName())) {
                    predicates.add(cb.like(root.get(ChangeDetail_.submitter).get(AdminUser_.realname), "%" + request.getSubmitRealName() + "%"));
                }
                if (StringUtils.isNotBlank(request.getCheckRealName())) {
                    predicates.add(cb.like(root.get(ChangeDetail_.verifier).get(AdminUser_.realname), "%" + request.getCheckRealName() + "%"));
                }
                if (request.getSubmitDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(ChangeDetail_.submitDate), request.getSubmitDate()));
                    predicates.add(cb.lessThanOrEqualTo(root.get(ChangeDetail_.submitDate), DateUtils.addDays(request.getSubmitDate(), 1)));
                }
                if (request.getPassDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(ChangeDetail_.passDate), request.getPassDate()));
                    predicates.add(cb.lessThanOrEqualTo(root.get(ChangeDetail_.passDate), DateUtils.addDays(request.getPassDate(), 1)));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

}
