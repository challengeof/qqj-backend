package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.order.domain.OrderGroup_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.SellCancelQueryRequest;
import com.mishu.cgwy.stock.repository.SellCancelItemRepository;
import com.mishu.cgwy.stock.repository.SellCancelRepository;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangwei on 15/10/15.
 */
@Service
public class SellCancelService {

    @Autowired
    private SellCancelRepository sellCancelRepository;
    @Autowired
    private SellCancelItemRepository sellCancelItemRepository;

    @Transactional(readOnly = true)
    public SellCancel getSellCancel(Long id) {
        return sellCancelRepository.findOne(id);
    }

    @Transactional
    public SellCancel saveSellCancel(SellCancel sellCancel) {
        return sellCancelRepository.save(sellCancel);
    }

    @Transactional(readOnly = true)
    public Page<SellCancel> getSellCancelList(final SellCancelQueryRequest request, final AdminUser operator) {

        PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<SellCancel> page = sellCancelRepository.findAll(new Specification<SellCancel>() {
            @Override
            public Predicate toPredicate(Root<SellCancel> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                query.orderBy(cb.desc(root.get(SellCancel_.id)));
                List<Predicate> predicates = new ArrayList<>();
                if (operator != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();
                    for (City city : operator.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : operator.getDepots()) {
                        depotIds.add(depot.getId());
                    }
                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id).in(depotIds));
                    }
                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.city).get(City_.id), request.getCityId()));
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.join(SellCancel_.order).join(Order_.stockOuts).join(StockOut_.orderGroup).join(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getRestaurantId() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if (request.getRestaurantName() != null) {
                    predicates.add(cb.like(root.get(SellCancel_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                }
                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellCancel_.order).get(Order_.submitDate), request.getStartDate()));
                }
                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellCancel_.order).get(Order_.submitDate), request.getEndDate()));
                }
                if (request.getStartCancelDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellCancel_.createDate), request.getStartCancelDate()));
                }
                if (request.getEndCancelDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellCancel_.createDate), request.getEndCancelDate()));
                }
                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(SellCancel_.type), request.getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    @Transactional(readOnly = true)
    public Page<SellCancelItem> getSellCancelItemList(final SellCancelQueryRequest request, final AdminUser operator) {

        PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Page<SellCancelItem> page = sellCancelItemRepository.findAll(new Specification<SellCancelItem>() {
            @Override
            public Predicate toPredicate(Root<SellCancelItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                query.orderBy(cb.desc(root.get(SellCancelItem_.sellCancel).get(SellCancel_.id)));
                List<Predicate> predicates = new ArrayList<>();
                if (operator != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();
                    for (City city : operator.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : operator.getDepots()) {
                        depotIds.add(depot.getId());
                    }
                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id).in(depotIds));
                    }
                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.city).get(City_.id), request.getCityId()));
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.join(SellCancelItem_.sellCancel).join(SellCancel_.order).join(Order_.stockOuts).join(StockOut_.orderGroup).join(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getRestaurantId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if (request.getRestaurantName() != null) {
                    predicates.add(cb.like(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(SellCancelItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.submitDate), request.getStartDate()));
                }
                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellCancelItem_.sellCancel).get(SellCancel_.order).get(Order_.submitDate), request.getEndDate()));
                }
                if (request.getStartCancelDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellCancelItem_.sellCancel).get(SellCancel_.createDate), request.getStartCancelDate()));
                }
                if (request.getEndCancelDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellCancelItem_.sellCancel).get(SellCancel_.createDate), request.getEndCancelDate()));
                }
                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(SellCancelItem_.sellCancel).get(SellCancel_.type), request.getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        return page;
    }

    public List<SellCancel> getSellCancelByOrderId(Long orderId) {
        return sellCancelRepository.getSellCancelByOrderId(orderId);
    }
}
