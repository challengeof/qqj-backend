package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.*;
import com.mishu.cgwy.accounting.dto.AccountReceivableRequest;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableStatus;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableWriteOffStatus;
import com.mishu.cgwy.accounting.repository.AccountReceivableItemRepository;
import com.mishu.cgwy.accounting.repository.AccountReceivableRepository;
import com.mishu.cgwy.accounting.repository.AccountReceivableWriteoffRepository;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.domain.OrderGroup_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.stock.domain.*;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiao1zhao2 on 15/10/12.
 */
@Service
public class AccountReceivableService {

    @Autowired
    private AccountReceivableRepository accountReceivableRepository;
    @Autowired
    private AccountReceivableItemRepository accountReceivableItemRepository;
    @Autowired
    private AccountReceivableWriteoffRepository accountReceivableWriteoffRepository;
    @Autowired
    private EntityManager entityManager;

    @Transactional
    public AccountReceivable generateAccountReceivableByStockOut(StockOut stockOut) {

        AccountReceivable accountReceivable = new AccountReceivable();
        accountReceivable.setAmount(stockOut.getReceiveAmount());
        accountReceivable.setWriteOffAmount(BigDecimal.ZERO);
        accountReceivable.setCreateDate(stockOut.getReceiveDate());
        accountReceivable.setWriteOffDate(null);
        accountReceivable.setWriteOffer(null);
        if (stockOut.getReceiveAmount().compareTo(BigDecimal.ZERO) > 0) {
            accountReceivable.setStatus(AccountReceivableStatus.UNWRITEOFF.getValue());
        } else {
            accountReceivable.setStatus(AccountReceivableStatus.WRITEOFF.getValue());
            accountReceivable.setWriteOffer(stockOut.getReceiver());
            accountReceivable.setWriteOffDate(accountReceivable.getCreateDate());
        }
        accountReceivable.setType(AccountReceivableType.SELL.getValue());
        accountReceivable.setRestaurant(stockOut.getOrder().getRestaurant());
        accountReceivable.setStockOut(stockOut);

        List<AccountReceivableItem> accountReceivableItems = accountReceivable.getAccountReceivableItems();
        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus()) && stockOutItem.getReceiveQuantity() > 0) {
                AccountReceivableItem accountReceivableItem = new AccountReceivableItem();
                accountReceivableItem.setSku(stockOutItem.getSku());
                accountReceivableItem.setQuantity(stockOutItem.getReceiveQuantity());
                accountReceivableItem.setAvgCost(stockOutItem.getAvgCost());
                accountReceivableItem.setTaxRate(stockOutItem.getTaxRate());
                accountReceivableItem.setPrice(stockOutItem.getPrice());
                accountReceivableItem.setAccountReceivable(accountReceivable);
                accountReceivableItems.add(accountReceivableItem);
            }
        }
        accountReceivable.setAccountReceivableItems(accountReceivableItems);

        return accountReceivableRepository.save(accountReceivable);
    }

    @Transactional
    public AccountReceivable generateAccountReceivableByStockIn(StockIn stockIn) {

        AccountReceivable accountReceivable = new AccountReceivable();
        accountReceivable.setAmount(stockIn.getAmount().multiply(new BigDecimal(-1)));
        accountReceivable.setWriteOffAmount(BigDecimal.ZERO);
        accountReceivable.setCreateDate(stockIn.getReceiveDate());
        accountReceivable.setWriteOffDate(null);
        accountReceivable.setWriteOffer(null);
        if (stockIn.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            accountReceivable.setStatus(AccountReceivableStatus.UNWRITEOFF.getValue());
        } else {
            accountReceivable.setStatus(AccountReceivableStatus.WRITEOFF.getValue());
            accountReceivable.setWriteOffer(stockIn.getReceiver());
            accountReceivable.setWriteOffDate(accountReceivable.getCreateDate());
        }
        accountReceivable.setType(AccountReceivableType.RETURN.getValue());
        accountReceivable.setRestaurant(stockIn.getSellReturn().getOrder().getRestaurant());
        accountReceivable.setStockIn(stockIn);

        List<AccountReceivableItem> accountReceivableItems = accountReceivable.getAccountReceivableItems();
        for (StockInItem stockInItem : stockIn.getStockInItems()) {
            if (stockInItem.getRealQuantity() > 0) {
                AccountReceivableItem accountReceivableItem = new AccountReceivableItem();
                accountReceivableItem.setSku(stockInItem.getSku());
                accountReceivableItem.setQuantity(stockInItem.getRealQuantity());
                accountReceivableItem.setAvgCost(stockInItem.getAvgCost());
                accountReceivableItem.setTaxRate(stockInItem.getTaxRate());
                accountReceivableItem.setPrice(stockInItem.getSalePrice());
                accountReceivableItem.setAccountReceivable(accountReceivable);
                accountReceivableItems.add(accountReceivableItem);
            }
        }
        accountReceivable.setAccountReceivableItems(accountReceivableItems);

        return accountReceivableRepository.save(accountReceivable);
    }

    @Transactional
    public AccountReceivable save(AccountReceivable accountReceivable) {
        return accountReceivableRepository.save(accountReceivable);
    }

    @Transactional
    public AccountReceivableWriteoff writeoff(AccountReceivable accountReceivable, AdminUser adminUser, Date writeoffDate) {

        if (accountReceivable != null) {
            AccountReceivableWriteoff accountReceivableWriteoff = new AccountReceivableWriteoff();
            accountReceivableWriteoff.setStatus(AccountReceivableWriteOffStatus.VALID.getValue());
            accountReceivableWriteoff.setAccountReceivable(accountReceivable);
            accountReceivableWriteoff.setRealWriteOffDate(new Date());
            accountReceivableWriteoff.setWriteOffAmount(accountReceivable.getAmount());
            accountReceivableWriteoff.setWriteOffDate(writeoffDate);
            accountReceivableWriteoff.setWriteOffer(adminUser);
            return accountReceivableWriteoffRepository.save(accountReceivableWriteoff);
        }
        return null;
    }

    @Transactional
    public AccountReceivableWriteoff writeoffCancel(AccountReceivableWriteoff accountReceivableWriteoff, AdminUser adminUser, Date cancelDate) {

        if (accountReceivableWriteoff != null) {
            accountReceivableWriteoff.setStatus(AccountReceivableWriteOffStatus.INVALID.getValue());
            accountReceivableWriteoff.setCancelDate(cancelDate);
            accountReceivableWriteoff.setCanceler(adminUser);
            accountReceivableWriteoff.setRealCancelDate(new Date());
            return accountReceivableWriteoffRepository.save(accountReceivableWriteoff);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public AccountReceivable findOne(Long id) {
        return accountReceivableRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public AccountReceivableWriteoff findOneWriteoff(Long id) {
        return accountReceivableWriteoffRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Page<AccountReceivable> getAccountReceivableList(final AccountReceivableRequest request) {

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, AccountReceivable_.id.getName()));
        Page<AccountReceivable> page = accountReceivableRepository.findAll(new AccountReceivableSpecification(request), pageRequest);

        return page;
    }

    @Transactional(readOnly = true)
    public Page<AccountReceivableWriteoff> getAccountReceivableWriteoffList(final AccountReceivableRequest request) {

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, AccountReceivableWriteoff_.id.getName()));
        Page<AccountReceivableWriteoff> page = accountReceivableWriteoffRepository.findAll(new AccountReceivableWriteoffSpecification(request), pageRequest);

        return page;
    }

    @Transactional(readOnly = true)
    public BigDecimal[] getAccountReceivableAmounts(final AccountReceivableRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<AccountReceivable> root = query.from(AccountReceivable.class);

        final Specification<AccountReceivable> specification = new AccountReceivableSpecification(request);
        query.multiselect(root.get(AccountReceivable_.id), root.get(AccountReceivable_.amount)
                , root.get(AccountReceivable_.writeOffAmount));
        query.where(specification.toPredicate(root, query, cb));
        List<Object[]> amountsList = entityManager.createQuery(query).getResultList();
        BigDecimal[] amountArray = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Object[] amounts : amountsList) {
            amountArray[0] = amountArray[0].add(amounts[1] != null ? (BigDecimal) amounts[1] : BigDecimal.ZERO);
            amountArray[1] = amountArray[1].add(amounts[2] != null ? (BigDecimal) amounts[2] : BigDecimal.ZERO);
        }
        amountArray[2] = amountArray[0].subtract(amountArray[1]);
        return amountArray;
    }

    @Transactional(readOnly = true)
    public BigDecimal[] getAccountReceivableByWriteoffAmount(final AccountReceivableRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Object[]> query = cb.createQuery(Object[].class);
        final Root<AccountReceivableWriteoff> root = query.from(AccountReceivableWriteoff.class);

        final Specification<AccountReceivableWriteoff> specification = new AccountReceivableWriteoffSpecification(request);

        query.multiselect(root.get(AccountReceivableWriteoff_.id),
                root.get(AccountReceivableWriteoff_.accountReceivable).get(AccountReceivable_.id),
                root.get(AccountReceivableWriteoff_.accountReceivable).get(AccountReceivable_.amount),
                root.get(AccountReceivableWriteoff_.writeOffAmount));
        query.where(specification.toPredicate(root, query, cb));

        List<Object[]> amountsList = entityManager.createQuery(query).getResultList();
        Set<Long> receivableIds = new HashSet<>();

        BigDecimal[] amountArray = new BigDecimal[]{BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO};
        for (Object[] amounts : amountsList) {
            if (!receivableIds.contains((Long) amounts[1])) {
                amountArray[0] = amountArray[0].add(amounts[2] != null ? (BigDecimal) amounts[2] : BigDecimal.ZERO);
                receivableIds.add((Long) amounts[1]);
            }
            amountArray[1] = amountArray[1].add(amounts[3] != null ? (BigDecimal) amounts[3] : BigDecimal.ZERO);
        }
        amountArray[2] = amountArray[0].subtract(amountArray[1]);
        return amountArray;
    }

    @Transactional(readOnly = true)
    public Date findSkuLastSaleDate(Long cityId, Long skuId) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Date> query = cb.createQuery(Date.class);
        final Root<AccountReceivable> root = query.from(AccountReceivable.class);

        final AccountReceivableRequest request = new AccountReceivableRequest();
        request.setAccountReceivableType(AccountReceivableType.SELL.getValue());
        request.setCityId(cityId);
        request.setSkuId(skuId);
        final Specification<AccountReceivable> specification = new AccountReceivableSpecification(request);
        query.select(cb.greatest(root.get(AccountReceivable_.createDate)));
        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    private static class AccountReceivableSpecification implements Specification<AccountReceivable> {

        private final AccountReceivableRequest request;

        public AccountReceivableSpecification(AccountReceivableRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<AccountReceivable> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicateList = new ArrayList<>();
            final Join<AccountReceivable, StockOut> stockOutJoin = root.join(AccountReceivable_.stockOut, JoinType.LEFT);
            final Join<AccountReceivable, StockIn> stockInJoin = root.join(AccountReceivable_.stockIn, JoinType.LEFT);
            final Join<StockOut, Depot> outDepotJoin = stockOutJoin.join(StockOut_.depot, JoinType.LEFT);
            final Join<StockIn, Depot> inDepotJoin = stockInJoin.join(StockIn_.depot, JoinType.LEFT);
            final Join<StockIn, SellReturn> sellReturnJoin = stockInJoin.join(StockIn_.sellReturn, JoinType.LEFT);
            final Join<SellReturn, Order> orderJoin = sellReturnJoin.join(SellReturn_.order, JoinType.LEFT);
            final Join<StockOut, Order> outOrderJoin = stockOutJoin.join(StockOut_.order, JoinType.LEFT);
            ListJoin<AccountReceivable, AccountReceivableItem> listJoin = null;
            query.distinct(true);

            if(request.getRestaurantId()!=null){
                predicateList.add(cb.equal(root.get(AccountReceivable_.restaurant).get(Restaurant_.id),request.getRestaurantId()));
            }
            if (request.getCityId() != null) {
                predicateList.add(cb.or(cb.equal(outDepotJoin.get(Depot_.city).get(City_.id), request.getCityId()),
                        cb.equal(inDepotJoin.get(Depot_.city).get(City_.id), request.getCityId())));
            }
            if (request.getDepotId() != null) {
                predicateList.add(cb.or(cb.equal(stockOutJoin.get(StockOut_.depot).get(Depot_.id), request.getDepotId()),
                        cb.equal(stockInJoin.get(StockIn_.depot).get(Depot_.id), request.getDepotId())));
            }
            if (request.getTrackerId() != null) {
                Join<StockOut, OrderGroup> orderGroupJoin = stockOutJoin.join(StockOut_.orderGroup, JoinType.LEFT);
                predicateList.add(cb.equal(orderGroupJoin.get(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
            }
            if (request.getAccountReceivableStatus() != Integer.MAX_VALUE) {
                predicateList.add(cb.equal(root.get(AccountReceivable_.status), request.getAccountReceivableStatus()));
                if (request.getType().equals("writeoff") && AccountReceivableStatus.UNWRITEOFF.getValue().equals(request.getAccountReceivableStatus())) {
                    predicateList.add(cb.or(cb.and(cb.isNotNull(root.get(AccountReceivable_.stockOut).get(StockOut_.id)), cb.isTrue(stockOutJoin.get(StockOut_.settle))),
                            cb.isNull(root.get(AccountReceivable_.stockOut).get(StockOut_.id))));
                }
            }
            if (request.getAccountReceivableType() != Integer.MAX_VALUE) {
                predicateList.add(cb.equal(root.get(AccountReceivable_.type), request.getAccountReceivableType()));
            }
            if (request.getSourceId() != null) {
                Predicate p1 = cb.equal(root.get(AccountReceivable_.stockOut).get(StockOut_.id), request.getSourceId());
                Predicate p2 = cb.equal(root.get(AccountReceivable_.stockIn).get(StockIn_.id), request.getSourceId());
                predicateList.add(cb.or(p1, p2));
            }
            if (request.getOrderId() != null) {
                predicateList.add(cb.or(cb.equal(stockOutJoin.get(StockOut_.order).get(Order_.id), request.getOrderId()),
                        cb.equal(sellReturnJoin.get(SellReturn_.order).get(Order_.id), request.getOrderId())));
            }
            if (request.getCustomerName() != null) {
                Join<AccountReceivable, Restaurant> restaurantJoin = root.join(AccountReceivable_.restaurant, JoinType.LEFT);
                predicateList.add(cb.like(restaurantJoin.get(Restaurant_.name), "%" + request.getCustomerName() + "%"));
            }
            if (request.getSkuId() != null) {
                if (listJoin == null) {
                    listJoin = root.join(AccountReceivable_.accountReceivableItems);
                }
                predicateList.add(cb.equal(listJoin.get(AccountReceivableItem_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (StringUtils.isNotBlank(request.getSkuName())) {
                if (listJoin == null) {
                    listJoin = root.join(AccountReceivable_.accountReceivableItems);
                }
                predicateList.add(cb.like(listJoin.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getStartOrderDate() != null) {
                predicateList.add(cb.or(cb.greaterThanOrEqualTo(outOrderJoin.get(Order_.submitDate), request.getStartOrderDate()),
                        cb.greaterThanOrEqualTo(orderJoin.get(Order_.submitDate), request.getStartOrderDate())));
            }
            if (request.getEndOrderDate() != null) {
                predicateList.add(cb.or(cb.lessThanOrEqualTo(outOrderJoin.get(Order_.submitDate), request.getEndOrderDate()),
                        cb.lessThanOrEqualTo(orderJoin.get(Order_.submitDate), request.getEndOrderDate())));
            }
            if (request.getStartSendDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(stockOutJoin.get(StockOut_.finishDate), request.getStartSendDate()));
            }
            if (request.getEndSendDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(stockOutJoin.get(StockOut_.finishDate), request.getEndSendDate()));
            }
            if (request.getStartReceiveDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.createDate), request.getStartReceiveDate()));
            }
            if (request.getEndReceiveDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get(AccountReceivable_.createDate), request.getEndReceiveDate()));
            }
            if (request.getStartWriteoffDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.writeOffDate), request.getStartWriteoffDate()));
            }
            if (request.getEndWriteoffDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get(AccountReceivable_.writeOffDate), request.getEndWriteoffDate()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }
    }

    private static class AccountReceivableWriteoffSpecification implements Specification<AccountReceivableWriteoff> {

        private final AccountReceivableRequest request;

        public AccountReceivableWriteoffSpecification(AccountReceivableRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<AccountReceivableWriteoff> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicateList = new ArrayList<>();
            final Join<AccountReceivableWriteoff, AccountReceivable> accountReceivableJoin = root.join(AccountReceivableWriteoff_.accountReceivable, JoinType.INNER);
            final Join<AccountReceivable, StockOut> stockOutJoin = accountReceivableJoin.join(AccountReceivable_.stockOut, JoinType.LEFT);
            final Join<AccountReceivable, StockIn> stockInJoin = accountReceivableJoin.join(AccountReceivable_.stockIn, JoinType.LEFT);
            final Join<StockOut, Depot> outDepotJoin = stockOutJoin.join(StockOut_.depot, JoinType.LEFT);
            final Join<StockIn, Depot> inDepotJoin = stockInJoin.join(StockIn_.depot, JoinType.LEFT);
            final Join<StockIn, SellReturn> sellReturnJoin = stockInJoin.join(StockIn_.sellReturn, JoinType.LEFT);
            final Join<SellReturn, Order> orderJoin = sellReturnJoin.join(SellReturn_.order, JoinType.LEFT);
            final Join<StockOut, Order> outOrderJoin = stockOutJoin.join(StockOut_.order, JoinType.LEFT);
            ListJoin<AccountReceivable, AccountReceivableItem> listJoin = null;
            query.distinct(true);

            if (request.getCityId() != null) {
                predicateList.add(cb.or(cb.equal(outDepotJoin.get(Depot_.city).get(City_.id), request.getCityId()),
                        cb.equal(inDepotJoin.get(Depot_.city).get(City_.id), request.getCityId())));
            }
            if (request.getDepotId() != null) {
                predicateList.add(cb.or(cb.equal(stockOutJoin.get(StockOut_.depot).get(Depot_.id), request.getDepotId()),
                        cb.equal(stockInJoin.get(StockIn_.depot).get(Depot_.id), request.getDepotId())));
            }
            if (request.getTrackerId() != null) {
                Join<StockOut, OrderGroup> orderGroupJoin = stockOutJoin.join(StockOut_.orderGroup, JoinType.LEFT);
                predicateList.add(cb.equal(orderGroupJoin.get(OrderGroup_.tracker).get(AdminUser_.id), request.getTrackerId()));
            }
            if (request.getAccountReceivableType() != Integer.MAX_VALUE) {
                predicateList.add(cb.equal(accountReceivableJoin.get(AccountReceivable_.type), request.getAccountReceivableType()));
            }
            if (request.getAccountReceivableWriteoffStatus() != Integer.MAX_VALUE) {
                predicateList.add(cb.equal(root.get(AccountReceivableWriteoff_.status), request.getAccountReceivableWriteoffStatus()));
            }
            if (request.getSourceId() != null) {
                Predicate p1 = cb.equal(accountReceivableJoin.get(AccountReceivable_.stockOut).get(StockOut_.id), request.getSourceId());
                Predicate p2 = cb.equal(accountReceivableJoin.get(AccountReceivable_.stockIn).get(StockIn_.id), request.getSourceId());
                predicateList.add(cb.or(p1, p2));
            }
            if (request.getOrderId() != null) {
                predicateList.add(cb.or(cb.equal(stockOutJoin.get(StockOut_.order).get(Order_.id), request.getOrderId()),
                        cb.equal(sellReturnJoin.get(SellReturn_.order).get(Order_.id), request.getOrderId())));
            }
            if (request.getCustomerName() != null) {
                Join<AccountReceivable, Restaurant> restaurantJoin = accountReceivableJoin.join(AccountReceivable_.restaurant, JoinType.LEFT);
                predicateList.add(cb.like(restaurantJoin.get(Restaurant_.name), "%" + request.getCustomerName() + "%"));
            }
            if (request.getSkuId() != null) {
                if (listJoin == null) {
                    listJoin = accountReceivableJoin.join(AccountReceivable_.accountReceivableItems);
                }
                predicateList.add(cb.equal(listJoin.get(AccountReceivableItem_.sku).get(Sku_.id), request.getSkuId()));
            }
            if (StringUtils.isNotBlank(request.getSkuName())) {
                if (listJoin == null) {
                    listJoin = accountReceivableJoin.join(AccountReceivable_.accountReceivableItems);
                }
                predicateList.add(cb.like(listJoin.get(AccountReceivableItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getSkuName() + "%"));
            }
            if (request.getStartOrderDate() != null) {
                predicateList.add(cb.or(cb.greaterThanOrEqualTo(outOrderJoin.get(Order_.submitDate), request.getStartOrderDate()),
                        cb.greaterThanOrEqualTo(orderJoin.get(Order_.submitDate), request.getStartOrderDate())));

            }
            if (request.getEndOrderDate() != null) {
                predicateList.add(cb.or(cb.lessThanOrEqualTo(outOrderJoin.get(Order_.submitDate), request.getEndOrderDate()),
                        cb.lessThanOrEqualTo(orderJoin.get(Order_.submitDate), request.getEndOrderDate())));
            }
            if (request.getStartSendDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(stockOutJoin.get(StockOut_.finishDate), request.getStartSendDate()));
            }
            if (request.getEndSendDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(stockOutJoin.get(StockOut_.finishDate), request.getEndSendDate()));
            }
            if (request.getStartReceiveDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(accountReceivableJoin.get(AccountReceivable_.createDate), request.getStartReceiveDate()));
            }
            if (request.getEndReceiveDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(accountReceivableJoin.get(AccountReceivable_.createDate), request.getEndReceiveDate()));
            }
            if (request.getStartWriteoffDate() != null) {
                predicateList.add(cb.greaterThanOrEqualTo(root.get(AccountReceivableWriteoff_.writeOffDate), request.getStartWriteoffDate()));
            }
            if (request.getEndWriteoffDate() != null) {
                predicateList.add(cb.lessThanOrEqualTo(root.get(AccountReceivableWriteoff_.writeOffDate), request.getEndWriteoffDate()));
            }
            return cb.and(predicateList.toArray(new Predicate[predicateList.size()]));
        }
    }

}
