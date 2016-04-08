package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.domain.CutOrder_;
import com.mishu.cgwy.order.domain.OrderGroup_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.SellReturnQueryRequest;
import com.mishu.cgwy.stock.repository.SellReturnItemRepository;
import com.mishu.cgwy.stock.repository.SellReturnReasonRepository;
import com.mishu.cgwy.stock.repository.SellReturnRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
 * User: Admin
 * Date: 9/22/15
 * Time: 12:02 PM
 */
@Service
public class SellReturnService {

    @Autowired
    private SellReturnRepository sellReturnRepository;
    @Autowired
    private SellReturnItemRepository sellReturnItemRepository;
    @Autowired
    private SellReturnReasonRepository sellReturnReasonRepository;

    @Transactional
    public void complete(StockIn stockIn) {

        if (stockIn == null || stockIn.getSellReturn() == null)
            return;
        SellReturn sellReturn = sellReturnRepository.findOne(stockIn.getSellReturn().getId());
        sellReturn.setStatus(SellReturnStatus.COMPLETED.getValue());
        sellReturnRepository.save(sellReturn);
    }

    @Transactional
    public SellReturn saveSellReturn(SellReturn sellReturn) {
        return sellReturnRepository.save(sellReturn);
    }

    @Transactional(readOnly = true)
    public List<SellReturnReason> getSellReturnReasonList() {
        return sellReturnReasonRepository.findAll();
    }

    @Transactional(readOnly = true)
    public SellReturnReason getSellReturnReason(Long id) {
        return sellReturnReasonRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public List<SellReturnItem> getSellReturnItemBySku(final Sku sku, final Boolean bundle) {
        return sellReturnItemRepository.findAll(new Specification<SellReturnItem>() {
            @Override
            public Predicate toPredicate(Root<SellReturnItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                predicates.add(cb.equal(root.get(SellReturnItem_.sku).get(Sku_.id), sku.getId()));

                if (null != bundle) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.bundle), bundle));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

    }

    @Transactional(readOnly = true)
    public Page<SellReturn> getSellReturn(final SellReturnQueryRequest request, final AdminUser adminUser) {
        Pageable page = new PageRequest(request.getPage(), request.getPageSize());
        return sellReturnRepository.findAll(new Specification<SellReturn>() {
            @Override
            public Predicate toPredicate(Root<SellReturn> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                query.orderBy(cb.desc(root.get(SellReturn_.id)));

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
                        depotCondition.add(root.get(SellReturn_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(SellReturn_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.depot).get(Depot_.id), request.getDepotId()));
                }

                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.join(SellReturn_.order).join(Order_.stockOuts).join(StockOut_.orderGroup).join(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }

                if (request.getRestaurantId() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if (request.getRestaurantName() != null) {
                    predicates.add(cb.like(root.get(SellReturn_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellReturn_.order).get(Order_.submitDate), request.getStartDate()));
                }
                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellReturn_.order).get(Order_.submitDate), request.getEndDate()));
                }
                if (request.getStartReturnDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellReturn_.createDate), request.getStartReturnDate()));
                }
                if (request.getEndReturnDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellReturn_.createDate), request.getEndReturnDate()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.status), request.getStatus()));
                }

                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(SellReturn_.type), request.getType()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, page);

    }

    @Transactional(readOnly = true)
    public SellReturn getSellReturn(Long id) {
        return sellReturnRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<SellReturnItem> getSellReturnItem(final SellReturnQueryRequest request, final AdminUser adminUser) {

        Pageable page = new PageRequest(request.getPage(), request.getPageSize());
        return sellReturnItemRepository.findAll(new Specification<SellReturnItem>() {

            @Override
            public Predicate toPredicate(Root<SellReturnItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                query.orderBy(cb.desc(root.get(SellReturnItem_.sellReturn).get(SellReturn_.id)));
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
                        depotCondition.add(root.get(SellReturnItem_.sellReturn).get(SellReturn_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(SellReturnItem_.sellReturn).get(SellReturn_.depot).get(Depot_.id).in(depotIds));
                    }
                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.depot).get(Depot_.id), request.getDepotId()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.join(SellReturnItem_.sellReturn).join(SellReturn_.order).join(Order_.stockOuts).join(StockOut_.orderGroup).join(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getRestaurantId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if (request.getRestaurantName() != null) {
                    predicates.add(cb.like(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getSkuName() != null) {
                    predicates.add(cb.like(root.get(SellReturnItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
                }
                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.submitDate), request.getStartDate()));
                }
                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellReturnItem_.sellReturn).get(SellReturn_.order).get(Order_.submitDate), request.getEndDate()));
                }
                if (request.getStartReturnDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(SellReturnItem_.sellReturn).get(SellReturn_.createDate), request.getStartReturnDate()));
                }
                if (request.getEndReturnDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(SellReturnItem_.sellReturn).get(SellReturn_.createDate), request.getEndReturnDate()));
                }
                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.status), request.getStatus()));
                }
                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(SellReturnItem_.sellReturn).get(SellReturn_.type), request.getType()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, page);

    }

    public List<SellReturn> getSellReturnByOrderId(Long orderId) {
        return sellReturnRepository.getSellReturnByOrderId(orderId);
    }
}
