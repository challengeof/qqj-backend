package com.mishu.cgwy.order.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.controller.OrderGroupRequest;
import com.mishu.cgwy.order.domain.*;
import com.mishu.cgwy.order.repository.OrderGroupRepository;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.domain.SellReturn_;
import com.mishu.cgwy.stock.dto.DepotData;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by wangwei on 15/10/27.
 */
@Service
public class OrderGroupService {

    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Transactional
    public Page<OrderGroup> getOrderGroup(final OrderGroupQueryRequest request, final AdminUser adminUser) {
        Pageable page = new PageRequest(request.getPage(), request.getPageSize(), Sort.Direction.DESC, "id");
        return orderGroupRepository.findAll(new Specification<OrderGroup>() {
            @Override
            public Predicate toPredicate(Root<OrderGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (adminUser != null){
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
                        depotCondition.add(root.get(OrderGroup_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(OrderGroup_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(OrderGroup_.city).get(City_.id), request.getCityId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(OrderGroup_.depot).get(Depot_.id), request.getDepotId()));
                }

                if(request.getTrackerId() != null){
                    predicates.add(cb.equal(root.get(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }

                if (request.getStartOrderDate() != null || request.getEndOrderDate() != null) {
                    query.distinct(true);
                    if(request.getQueryDateType() == 1){
                        predicates.add(cb.greaterThanOrEqualTo(root.get(OrderGroup_.createDate) , request.getStartOrderDate()));
                        predicates.add(cb.lessThanOrEqualTo(root.get(OrderGroup_.createDate) , request.getEndOrderDate()));
                    }else{
                        ListJoin<OrderGroup, com.mishu.cgwy.order.domain.Order> orderListJoin = root.join(OrderGroup_.members, JoinType.LEFT);
                        if (request.getStartOrderDate() != null) {
                            predicates.add(cb.greaterThanOrEqualTo(orderListJoin.get(Order_.submitDate), request.getStartOrderDate()));
                        }
                        if (request.getEndOrderDate() != null) {
                            predicates.add(cb.lessThanOrEqualTo(orderListJoin.get(Order_.submitDate), request.getEndOrderDate()));
                        }
                    }
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, page);
    }
}
