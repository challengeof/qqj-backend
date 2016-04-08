package com.mishu.cgwy.stock.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.domain.OrderGroup_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.repository.OrderGroupRepository;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.purchase.domain.PurchaseOrder_;
import com.mishu.cgwy.purchase.domain.ReturnNote_;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.repository.StockOutItemRepository;
import com.mishu.cgwy.stock.repository.StockOutRepository;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Service
public class StockOutService {
    @Autowired
    private StockOutRepository stockOutRepository;
    @Autowired
    private StockOutItemRepository stockOutItemRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderGroupRepository orderGroupRepository;

    @Transactional(readOnly = true)
    public Page<StockOut> getStockOutList(final StockOutRequest request, final AdminUser adminUser) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, StockOut_.id.getName()));
        Page<StockOut> page = stockOutRepository.findAll(new StockOutSpecification(request, adminUser), pageable);
        return page;
    }

    @Transactional(readOnly = true)
    public Page<StockOutItem> getStockOutItemList(final StockOutRequest request, final AdminUser adminUser) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, StockOutItem_.stockOut.getName()));
        Page<StockOutItem> page = stockOutItemRepository.findAll(new StockOutItemSpecification(request, adminUser), pageable);
        return page;
    }

    @Transactional(readOnly = true)
    public BigDecimal[] getStockOutAmounts(final StockOutRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<StockOut> root = query.from(StockOut.class);

        final Specification<StockOut> specification = new StockOutSpecification(request, adminUser);
        query.multiselect(root.get(StockOut_.id), root.get(StockOut_.amount)
                , root.get(StockOut_.receiveAmount)
                , cb.<BigDecimal>selectCase().when(cb.isTrue(root.get(StockOut_.settle)), root.get(StockOut_.receiveAmount)).otherwise(BigDecimal.ZERO));
        query.where(specification.toPredicate(root, query, cb));
        query.groupBy(root.get(StockOut_.id));

        List<Object[]> amountsList = entityManager.createQuery(query).getResultList();
        BigDecimal[] amountArray = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Object[] amounts : amountsList) {
            amountArray[0] = amountArray[0].add(amounts[1] != null ? (BigDecimal) amounts[1] : BigDecimal.ZERO);
            amountArray[1] = amountArray[1].add(amounts[2] != null ? (BigDecimal) amounts[2] : BigDecimal.ZERO);
            amountArray[2] = amountArray[2].add(amounts[3] != null ? (BigDecimal) amounts[3] : BigDecimal.ZERO);
        }

        return amountArray;
    }

    @Transactional(readOnly = true)
    public BigDecimal[] getStockOutItemAmounts(final StockOutRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
        final Root<StockOutItem> root = query.from(StockOutItem.class);
        query.multiselect(cb.sum(cb.prod(root.get(StockOutItem_.realQuantity), root.get(StockOutItem_.price))));
        query.where(new StockOutItemSpecification(request, adminUser).toPredicate(root, query, cb));

        return new BigDecimal[]{(BigDecimal) entityManager.createQuery(query).getSingleResult().get(0)};
    }

    @Transactional
    public StockOut saveStockOut(StockOut stockOut) {
        return stockOutRepository.save(stockOut);
    }

    @Transactional(readOnly = true)
    public StockOut getOneStockOut(Long stockOutId) {
        return stockOutRepository.getOne(stockOutId);
    }

    @Transactional(readOnly = true)
    public StockOut getStockOutByOrderId(Long orderId) {
        return stockOutRepository.getStockOutByOrderId(orderId);
    }

    public StockOut getOneStockOutByType(Integer type, Long id) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<StockOut> query = cb.createQuery(StockOut.class);
        final Root<StockOut> root = query.from(StockOut.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(StockOut_.type), type));

        if (StockOutType.ORDER.getValue().equals(type)) {

            predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.id), id));
        } else if (StockOutType.TRANSFER.getValue().equals(type)) {

            predicates.add(cb.equal(root.get(StockOut_.transfer).get(Transfer_.id), id));
        } else if (StockOutType.PURCHASERETURN.getValue().equals(type)) {

            predicates.add(cb.equal(root.get(StockOut_.returnNote).get(ReturnNote_.id), id));
        } else {
            predicates.add(cb.equal(root.get(StockOut_.id), -1));
        }

        query.where(predicates.toArray(new Predicate[predicates.size()]));

        List<StockOut> stockOuts = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (stockOuts != null && stockOuts.size() > 0) {
            return stockOuts.get(0);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public StockOutItem getOneStockOutItem(Long stockOutItemId) {
        return stockOutItemRepository.findOne(stockOutItemId);
    }

    @Transactional
    public StockOutItem split(StockOutItem sourceStockOutItem, int quantity) {
        if (sourceStockOutItem.getRealQuantity() - quantity == 0) {
            return sourceStockOutItem;
        }
        StockOutItem newStockOutItem = sourceStockOutItem.clone();
        newStockOutItem.setRealQuantity(quantity);
        newStockOutItem.setExpectedQuantity(quantity);
        newStockOutItem.setReceiveQuantity(quantity);
        stockOutItemRepository.save(newStockOutItem);

        sourceStockOutItem.setRealQuantity(sourceStockOutItem.getRealQuantity() - quantity);
        sourceStockOutItem.setExpectedQuantity(sourceStockOutItem.getExpectedQuantity() - quantity);
        sourceStockOutItem.setReceiveQuantity(sourceStockOutItem.getReceiveQuantity() - quantity);
        stockOutItemRepository.save(sourceStockOutItem);

        return newStockOutItem;
    }

    @Transactional
    public void delete(StockOut stockOut) {
        stockOutRepository.delete(stockOut);
    }

    @Transactional
    public void deleteItem(StockOutItem stockOutItem) {
        stockOutItemRepository.delete(stockOutItem);
    }

    @Transactional(readOnly = true)
    public List<StockOutItem> findUnDistributedItems(Set<Long> skuIds) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<StockOutItem> query = cb.createQuery(StockOutItem.class);
        final Root<StockOutItem> root = query.from(StockOutItem.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(StockOutItem_.status), StockOutItemStatus.UNDISTRIBUTED.getValue()));
        predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.status), StockOutStatus.IN_STOCK.getValue()));
        predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.type), StockOutType.ORDER.getValue()));
        predicates.add(root.get(StockOutItem_.sku).get(Sku_.id).in(skuIds));

        query.where(predicates.toArray(new Predicate[predicates.size()]));
        query.orderBy(cb.asc(root.get(StockOutItem_.stockOut).get(StockOut_.id)));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public StockOutItem findMergeStockOutItem(Long stockOutId, Long skuId, BigDecimal price, boolean bundle) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<StockOutItem> query = cb.createQuery(StockOutItem.class);
        final Root<StockOutItem> root = query.from(StockOutItem.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.id), stockOutId));
        predicates.add(cb.equal(root.get(StockOutItem_.sku).get(Sku_.id), skuId));
        predicates.add(cb.equal(root.get(StockOutItem_.status), StockOutItemStatus.DISTRIBUTED.getValue()));
        predicates.add(cb.equal(root.get(StockOutItem_.price), price));
        predicates.add(cb.equal(root.get(StockOutItem_.bundle), bundle));

        query.where(predicates.toArray(new Predicate[predicates.size()]));
        query.orderBy(cb.asc(root.get(StockOutItem_.id)));

        List<StockOutItem> stockOutItems = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (stockOutItems != null && stockOutItems.size() > 0) {
            return stockOutItems.get(0);
        } else {
            return null;
        }
    }

    @Transactional
    public void saveStockOutItem(StockOutItem stockOutItem) {
        stockOutItemRepository.save(stockOutItem);
    }

    @Transactional
    public void transferComplete(StockIn stockIn) {

        if (stockIn == null || stockIn.getTransfer() == null)
            return;

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<StockOut> query = cb.createQuery(StockOut.class);
        final Root<StockOut> root = query.from(StockOut.class);

        List<Predicate> predicates = new ArrayList<>();
        predicates.add(cb.equal(root.get(StockOut_.status), StockOutStatus.HAVE_OUTBOUND.getValue()));
        predicates.add(cb.equal(root.get(StockOut_.type), StockOutType.TRANSFER.getValue()));
        predicates.add(cb.equal(root.get(StockOut_.transfer).get(Transfer_.id), stockIn.getTransfer().getId()));

        query.where(predicates.toArray(new Predicate[predicates.size()]));

        List<StockOut> stockOuts = entityManager.createQuery(query).setMaxResults(1).getResultList();
        if (stockOuts != null && stockOuts.size() > 0) {
            stockOuts.get(0).setStatus(StockOutStatus.FINISHED.getValue());
            stockOutRepository.save(stockOuts.get(0));
        }
    }

    @Transactional(readOnly = true)
    public List<StockOut> getStockOutByIds(final Long[] ids) {
        return stockOutRepository.findAll(new Specification<StockOut>() {
            @Override
            public Predicate toPredicate(Root<StockOut> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get(StockOut_.id).in(ids);
            }
        });
    }

    @Transactional
    public void updateStockOutPrintStatus(Long[] ids) {
        for (Long id : ids) {
            StockOut stockOut = stockOutRepository.getOne(id);
            stockOut.setOutPrint(true);
            stockOutRepository.save(stockOut);
        }
    }

    @Transactional(readOnly = true)
    public List<OrderGroup> findStockOutGroups(final int type, final List<Long> depots, final String tracker, final Long operaterId) {//0 未出库;1 已出库和已收货
        return orderGroupRepository.findAll(new Specification<OrderGroup>() {
            @Override
            public Predicate toPredicate(Root<OrderGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                query.distinct(true);
                ListJoin<OrderGroup, StockOut> stockOutListJoin = root.join(OrderGroup_.stockOuts, JoinType.INNER);
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(stockOutListJoin.get(StockOut_.type), StockOutType.ORDER.getValue()));
                if (type == 0) {
                    predicates.add(cb.or(cb.equal(stockOutListJoin.get(StockOut_.status), StockOutStatus.IN_STOCK.getValue()),
                            cb.equal(stockOutListJoin.get(StockOut_.status), StockOutStatus.HAVE_OUTBOUND.getValue())));
                } else {
                    predicates.add(cb.or(cb.equal(stockOutListJoin.get(StockOut_.status), StockOutStatus.HAVE_OUTBOUND.getValue()),
                            cb.and(cb.equal(stockOutListJoin.get(StockOut_.status), StockOutStatus.FINISHED.getValue()),
                                    cb.greaterThanOrEqualTo(stockOutListJoin.get(StockOut_.receiveDate), DateUtils.truncate(new Date(), Calendar.DATE)),
                                    cb.lessThan(stockOutListJoin.get(StockOut_.receiveDate), DateUtils.truncate(DateUtils.addDays(new Date(), 1), Calendar.DATE)))));
                }
                if (depots != null) {
                    predicates.add(root.get(OrderGroup_.depot).get(Depot_.id).in(depots));
                }
                if (StringUtils.isNotBlank(tracker)) {
                    Join<OrderGroup, AdminUser> adminUserJoin = root.join(OrderGroup_.tracker, JoinType.LEFT);
                    predicates.add(cb.like(adminUserJoin.get(AdminUser_.realname), "%" + tracker + "%"));
                }
                if (operaterId != null) {
                    predicates.add(cb.equal(root.get(OrderGroup_.tracker).get(AdminUser_.id), operaterId));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    /**
     * 检查是否是注册后完成的首单
     */
    @Transactional(readOnly = true, propagation = Propagation.REQUIRES_NEW)
    public boolean checkIsFirstByThisCustomer(final long stockOutId) {

        final StockOut stockOut = this.stockOutRepository.findOne(stockOutId);

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Integer> query = cb.createQuery(Integer.class);
        final Root<StockOut> root = query.from(StockOut.class);

        Expression cntExpr = cb.selectCase().when(cb.isNull(cb.count(root.get(StockOut_.id))), 0).otherwise(cb.count(root.get(StockOut_.id)));
        query.multiselect(cntExpr);
        Specification<StockOut> specification = new Specification<StockOut>() {
            @Override
            public Predicate toPredicate(Root<StockOut> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.customer).get(Customer_.id), stockOut.getOrder().getCustomer().getId()));
                predicates.add(cb.isNotNull(root.get(StockOut_.receiveDate)));
                predicates.add(cb.lessThanOrEqualTo(root.get(StockOut_.receiveDate), stockOut.getReceiveDate()));
                predicates.add(cb.notEqual(root.get(StockOut_.id), stockOutId));
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
        };
        query.where(specification.toPredicate(root, query, cb));

        Integer cnt = entityManager.createQuery(query).getSingleResult();
        return cnt == 0;

    }

    @Transactional(readOnly = true)
    public List<StockOut> getNotReceiveStockOuts(final int days) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<StockOut> query = cb.createQuery(StockOut.class);
        final Root<StockOut> root = query.from(StockOut.class);
        StockOutRequest request = new StockOutRequest();
        request.setStockOutType(StockOutType.ORDER.getValue());
        request.setStockOutStatus(StockOutStatus.HAVE_OUTBOUND.getValue());
        Date finishDate = DateUtils.addDays(new Date(), days);
        finishDate = DateUtils.truncate(finishDate, Calendar.DATE);
        request.setEndSendDate(finishDate);

        final Specification<StockOut> specification = new StockOutSpecification(request, null);
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getResultList();
    }

    private static class StockOutSpecification implements Specification<StockOut> {

        private final StockOutRequest request;
        private final AdminUser adminUser;

        public StockOutSpecification(StockOutRequest request, AdminUser adminUser) {
            this.request = request;
            this.adminUser = adminUser;
        }

        @Override
        public Predicate toPredicate(Root<StockOut> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            final ListJoin<StockOut, StockOutItem> join = root.join(StockOut_.stockOutItems);
            query.distinct(true);

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
                    depotCondition.add(root.get(StockOut_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                }
                if (!depotIds.isEmpty()) {
                    depotCondition.add(root.get(StockOut_.depot).get(Depot_.id).in(depotIds));
                }

                if (!depotCondition.isEmpty()) {
                    predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                } else {
                    predicates.add(cb.or());
                }
            }

            if (request.getStockOutType() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(StockOut_.type), request.getStockOutType()));
            }
            if (request.getStockOutStatus() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(StockOut_.status), request.getStockOutStatus()));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(StockOut_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
            }
            if (request.getDepotId() != null) {
                predicates.add(cb.equal(root.get(StockOut_.depot).get(Depot_.id), request.getDepotId()));
            }
            if (request.getStockOutId() != null) {
                predicates.add(cb.equal(root.get(StockOut_.id), request.getStockOutId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(join.get(StockOutItem_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(join.get(StockOutItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getSourceId() != null) {
                Predicate p1 = cb.equal(root.get(StockOut_.order).get(Order_.id), request.getSourceId());
                Predicate p2 = cb.equal(root.get(StockOut_.transfer).get(Transfer_.id), request.getSourceId());
                Predicate p3 = cb.equal(root.get(StockOut_.returnNote).get(ReturnNote_.id), request.getSourceId());
                predicates.add(cb.or(p1, p2, p3));
            }
            if (request.getStartSendDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(StockOut_.finishDate), request.getStartSendDate()));
            }
            if (request.getEndSendDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(StockOut_.finishDate), request.getEndSendDate()));
            }
            if (request.getOutPrint() != null) {
                predicates.add(cb.equal(root.get(StockOut_.outPrint), request.getOutPrint()));
            }
            if (request.getPickPrint() != null) {
                predicates.add(cb.equal(root.get(StockOut_.pickPrint), request.getPickPrint()));
            }

            if (request.getStockOutType() == StockOutType.ORDER.getValue()) {
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getOrderStatus() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.status), request.getOrderStatus()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                if (request.getBlockId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.customer).get(Customer_.block).get(Block_.id), request.getBlockId()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.orderGroup).get(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (request.getCustomerName() != null) {
                    predicates.add(cb.like(root.get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getCustomerName() + "%"));
                }
                if (request.getStartOrderDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOut_.order).get(Order_.submitDate), request.getStartOrderDate()));
                }
                if (request.getEndOrderDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOut_.order).get(Order_.submitDate), request.getEndOrderDate()));
                }
                if (request.getExpectedArrivedDate() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.order).get(Order_.expectedArrivedDate), request.getExpectedArrivedDate()));
                }
                if (request.getStartReceiveDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOut_.receiveDate), request.getStartReceiveDate()));
                }
                if (request.getEndReceiveDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOut_.receiveDate), request.getEndReceiveDate()));
                }
                if (request.getStartSettleDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOut_.settleDate), request.getStartSettleDate()));
                }
                if (request.getEndSettleDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOut_.settleDate), request.getEndSettleDate()));
                }

                if (request.getOrderGroupIsNull() != null) {
                    if (request.getOrderGroupIsNull().booleanValue() == Boolean.TRUE) {
                        predicates.add(cb.isNull(root.get(StockOut_.orderGroup)));
                    } else {
                        predicates.add(cb.isNotNull(root.get(StockOut_.orderGroup)));
                    }
                }
                if (request.getSettle() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.settle), request.getSettle()));
                }
            } else if (request.getStockOutType() == StockOutType.TRANSFER.getValue()) {
                if (request.getTransferId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.transfer).get(Transfer_.id), request.getTransferId()));
                }
                if (request.getSourceDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.depot).get(Depot_.id), request.getSourceDepotId()));
                }
                if (request.getTargetDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.transfer).get(Transfer_.targetDepot).get(Depot_.id), request.getTargetDepotId()));
                }
            } else if (request.getStockOutType() == StockOutType.PURCHASERETURN.getValue()) {
                if (request.getPurchaseId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseId()));
                }
                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                }
                if (request.getVendorName() != null) {
                    predicates.add(cb.like(root.get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.name), "%" + request.getVendorName() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    private static class StockOutItemSpecification implements Specification<StockOutItem> {

        private final StockOutRequest request;
        private final AdminUser adminUser;

        public StockOutItemSpecification(StockOutRequest request, AdminUser adminUser) {
            this.request = request;
            this.adminUser = adminUser;
        }

        @Override
        public Predicate toPredicate(Root<StockOutItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
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
                    depotCondition.add(root.get(StockOutItem_.stockOut).get(StockOut_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                }
                if (!depotIds.isEmpty()) {
                    depotCondition.add(root.get(StockOutItem_.stockOut).get(StockOut_.depot).get(Depot_.id).in(depotIds));
                }

                if (!depotCondition.isEmpty()) {
                    predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                } else {
                    predicates.add(cb.or());
                }
            }

            if (request.getStockOutType() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.type), request.getStockOutType()));
            }
            if (request.getStockOutStatus() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.status), request.getStockOutStatus()));
            }
            if (request.getStockOutItemStatus() != Integer.MAX_VALUE) {
                predicates.add(cb.equal(root.get(StockOutItem_.status), request.getStockOutItemStatus()));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
            }
            if (request.getDepotId() != null) {
                predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.depot).get(Depot_.id), request.getDepotId()));
            }
            if (request.getStockOutId() != null) {
                predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.id), request.getStockOutId()));
            }
            if (request.getSkuId() != null) {
                predicates.add(cb.equal(root.get(StockOutItem_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (request.getSkuName() != null) {
                predicates.add(cb.like(root.get(StockOutItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getStartSendDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.finishDate), request.getStartSendDate()));
            }
            if (request.getEndSendDate() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.finishDate), request.getEndSendDate()));
            }

            if (request.getStockOutType() == StockOutType.ORDER.getValue()) {
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.orderGroup).get(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (request.getCustomerName() != null) {
                    predicates.add(cb.like(root.get(StockOutItem_.stockOut).get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getCustomerName() + "%"));
                }
                if (request.getStartReceiveDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.receiveDate), request.getStartReceiveDate()));
                }
                if (request.getEndReceiveDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.receiveDate), request.getEndReceiveDate()));
                }
            } else if (request.getStockOutType() == StockOutType.TRANSFER.getValue()) {
                if (request.getTransferId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.id), request.getTransferId()));
                }
                if (request.getSourceDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.depot).get(Depot_.id), request.getSourceDepotId()));
                }
                if (request.getTargetDepotId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.targetDepot).get(Depot_.id), request.getTargetDepotId()));
                }
                if (request.getStartTransferDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.createDate), request.getStartTransferDate()));
                }
                if (request.getEndTransferDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.createDate), request.getEndTransferDate()));
                }
                if (request.getStartAuditDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.auditDate), request.getStartAuditDate()));
                }
                if (request.getEndAuditDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(StockOutItem_.stockOut).get(StockOut_.transfer).get(Transfer_.auditDate), request.getEndAuditDate()));
                }
            } else if (request.getStockOutType() == StockOutType.PURCHASERETURN.getValue()) {
                if (request.getPurchaseId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.id), request.getPurchaseId()));
                }
                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(StockOutItem_.stockOut).get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                }
                if (request.getVendorName() != null) {
                    predicates.add(cb.like(root.get(StockOutItem_.stockOut).get(StockOut_.returnNote).get(ReturnNote_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.name), "%" + request.getVendorName() + "%"));
                }
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    public List<StockOutItem> findStockOutItemBySkuId(Long skuId) {
        return stockOutItemRepository.findBySkuId(skuId);
    }
}
