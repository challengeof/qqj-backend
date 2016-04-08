package com.mishu.cgwy.salesPerformance.service;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.admin.repository.AdminUserRepository;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.salesPerformance.domain.SellerSalesPerformance;
import com.mishu.cgwy.salesPerformance.domain.SellerSalesPerformance_;
import com.mishu.cgwy.salesPerformance.repository.SellerSalesPerformanceRepository;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.SellerSalesPerformanceWrapper;
import com.mishu.cgwy.stock.domain.SellReturn_;
import com.mishu.cgwy.stock.domain.StockIn_;
import com.mishu.cgwy.stock.domain.StockOut_;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by xiao1zhao2 on 15/12/15.
 */
@Service
public class SellerSalesPerformanceService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SellerSalesPerformanceRepository sellerSalesPerformanceRepository;
    @Autowired
    private AdminUserRepository adminUserRepository;

    //默认销售ID
    private static final Long DEFAULT_SELLER = 1L;
    //首单
    private static final Long FIRST_ORDER = 1L;

    @Transactional(readOnly = true)
    public AdminUser getAdminUserById(Long id) {
        return adminUserRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> dailyCountNewCustomers(Date start, Date end) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<Order> root = query.from(Order.class);
        Path<Long> sellerPath = root.join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id);
        Expression<Long> sellerId = cb.<Long>selectCase().when(cb.isNotNull(sellerPath), sellerPath).otherwise(DEFAULT_SELLER);
        Expression<Long> newCustomers = cb.count(root.get(Order_.restaurant).get(Restaurant_.id));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), start), cb.lessThan(root.get(Order_.submitDate), end), cb.equal(root.get(Order_.sequence), FIRST_ORDER));
        query.multiselect(sellerId, newCustomers).where(predicate).groupBy(sellerId);
        Map<Long, Integer> map = new HashMap<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            map.put(((Number) tuple.get(0)).longValue(), ((Number) tuple.get(1)).intValue());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> dailyCountOrders(Date start, Date end) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivable> root = query.from(AccountReceivable.class);
        Path<Long> sellerPath = root.join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id);
        Expression<Long> sellerId = cb.<Long>selectCase().when(cb.isNotNull(sellerPath), sellerPath).otherwise(DEFAULT_SELLER);
        Expression<Long> Orders = cb.count(root.get(AccountReceivable_.id));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivable_.createDate), end), cb.equal(root.get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()));
        query.multiselect(sellerId, Orders).where(predicate).groupBy(sellerId);
        Map<Long, Integer> map = new HashMap<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            map.put(((Number) tuple.get(0)).longValue(), ((Number) tuple.get(1)).intValue());
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> dailyCountSalesAmount(Date start, Date end) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivable> root = query.from(AccountReceivable.class);
        Expression<Long> sellerId = cb.<Long>selectCase().when(cb.and(cb.isNotNull(root.join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)), cb.equal(root.get(AccountReceivable_.type), AccountReceivableType.SELL.getValue())), root.join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)).when(cb.and(cb.isNotNull(root.join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)), cb.equal(root.get(AccountReceivable_.type), AccountReceivableType.RETURN.getValue())), root.join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)).otherwise(DEFAULT_SELLER);
        Expression salesAmount = cb.sum(root.get(AccountReceivable_.amount));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivable_.createDate), end));
        query.multiselect(sellerId, salesAmount).where(predicate).groupBy(sellerId);
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            map.put(((Number) tuple.get(0)).longValue(), (BigDecimal) tuple.get(1));
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> dailyCountAvgCostAmount(Date start, Date end) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivableItem> root = query.from(AccountReceivableItem.class);
        Expression<Long> sellerId = cb.<Long>selectCase().when(cb.and(cb.isNotNull(root.join(AccountReceivableItem_.accountReceivable, JoinType.LEFT).join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)), cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue())), root.join(AccountReceivableItem_.accountReceivable, JoinType.LEFT).join(AccountReceivable_.stockOut, JoinType.LEFT).join(StockOut_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)).when(cb.and(cb.isNotNull(root.join(AccountReceivableItem_.accountReceivable).join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)), cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.RETURN.getValue())), root.join(AccountReceivableItem_.accountReceivable, JoinType.LEFT).join(AccountReceivable_.stockIn, JoinType.LEFT).join(StockIn_.sellReturn, JoinType.LEFT).join(SellReturn_.order, JoinType.LEFT).join(Order_.adminUser, JoinType.LEFT).get(AdminUser_.id)).otherwise(DEFAULT_SELLER);
        Expression avgCostAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.avgCost), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), end));
        query.multiselect(sellerId, avgCostAmount).where(predicate).groupBy(sellerId);
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            map.put(((Number) tuple.get(0)).longValue(), (BigDecimal) tuple.get(1));
        }
        return map;
    }

    @Transactional
    public void saveSellerSalesPerformance(SellerSalesPerformance sellerSalesPerformance) {
        sellerSalesPerformanceRepository.save(sellerSalesPerformance);
    }

    @Transactional(readOnly = true)
    public List<SellerSalesPerformance> getByAdminUserIdAndDate(Long sellerId, Date date) {
        return sellerSalesPerformanceRepository.getByAdminUserIdAndDate(sellerId, date);
    }

    @Transactional(readOnly = true)
    public Page<SellerSalesPerformanceWrapper> getSellerSalesPerformance(SalesPerformanceRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<SellerSalesPerformance> root = query.from(SellerSalesPerformance.class);

        Date firstDayOfCurrentMonth = DateUtils.setDays(request.getStartDate(), 1);
        Date firstDayOf_1Month = DateUtils.addMonths(firstDayOfCurrentMonth, -1);
        Date firstDayOf_2Month = DateUtils.addMonths(firstDayOfCurrentMonth, -2);
        Date firstDayOf_3Month = DateUtils.addMonths(firstDayOfCurrentMonth, -3);

        Expression<Long> sellerId = root.get(SellerSalesPerformance_.adminUser).get(AdminUser_.id);
        Expression<String> sellerName = root.get(SellerSalesPerformance_.adminUser).get(AdminUser_.realname);
        Expression<Integer> currentNewCustomers = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(SellerSalesPerformance_.date), request.getEndDate())), root.get(SellerSalesPerformance_.newCustomers)).otherwise(0));
        Expression<Integer> _1NewCustomers = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(SellerSalesPerformance_.newCustomers)).otherwise(0));
        Expression<Integer> _2NewCustomers = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_1Month)), root.get(SellerSalesPerformance_.newCustomers)).otherwise(0));
        Expression<Integer> _3NewCustomers = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_2Month)), root.get(SellerSalesPerformance_.newCustomers)).otherwise(0));
        Expression<Integer> currentOrders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(SellerSalesPerformance_.date), request.getEndDate())), root.get(SellerSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _1Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(SellerSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _2Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_1Month)), root.get(SellerSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _3Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_2Month)), root.get(SellerSalesPerformance_.orders)).otherwise(0));
        Expression<BigDecimal> currentSalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(SellerSalesPerformance_.date), request.getEndDate())), root.get(SellerSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _1SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(SellerSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _2SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_1Month)), root.get(SellerSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _3SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_2Month)), root.get(SellerSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> currentAvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(SellerSalesPerformance_.date), request.getEndDate())), root.get(SellerSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _1AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(SellerSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _2AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_1Month)), root.get(SellerSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _3AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_2Month)), root.get(SellerSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));

        query.multiselect(sellerId, sellerName, currentNewCustomers, _1NewCustomers, _2NewCustomers, _3NewCustomers, currentOrders, _1Orders, _2Orders, _3Orders, currentSalesAmount, _1SalesAmount, _2SalesAmount, _3SalesAmount, currentAvgCostAmount, _1AvgCostAmount, _2AvgCostAmount, _3AvgCostAmount);
        query.where(new SalesPerformanceSpecification(request).toPredicate(root, query, cb)).groupBy(sellerId);

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        List<SellerSalesPerformanceWrapper> salesPerformances = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList()) {
            salesPerformances.add(new SellerSalesPerformanceWrapper(((Number) tuple.get(0)).longValue(), tuple.get(1).toString(), ((Number) tuple.get(2)).intValue(), ((Number) tuple.get(3)).intValue(), ((Number) tuple.get(4)).intValue(), ((Number) tuple.get(5)).intValue(), ((Number) tuple.get(6)).intValue(), ((Number) tuple.get(7)).intValue(), ((Number) tuple.get(8)).intValue(), ((Number) tuple.get(9)).intValue(), (BigDecimal) tuple.get(10), (BigDecimal) tuple.get(11), (BigDecimal) tuple.get(12), (BigDecimal) tuple.get(13), (BigDecimal) tuple.get(14), (BigDecimal) tuple.get(15), (BigDecimal) tuple.get(16), (BigDecimal) tuple.get(17)));
        }
        CriteriaQuery<Long> cntQuery = cb.createQuery(Long.class);
        Root<SellerSalesPerformance> cntRoot = cntQuery.from(SellerSalesPerformance.class);
        cntQuery.select(cb.countDistinct(cntRoot.get(SellerSalesPerformance_.adminUser).get(AdminUser_.id))).where(new SalesPerformanceSpecification(request).toPredicate(cntRoot, cntQuery, cb));
        Long total = entityManager.createQuery(cntQuery).getSingleResult();
        return new PageImpl<>(salesPerformances, pageRequest, total == null ? 0 : total);
    }

    private class SalesPerformanceSpecification implements Specification<SellerSalesPerformance> {

        private SalesPerformanceRequest request;

        public SalesPerformanceSpecification(SalesPerformanceRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<SellerSalesPerformance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.join(SellerSalesPerformance_.adminUser, JoinType.LEFT).join(AdminUser_.cities, JoinType.LEFT).get(City_.id), request.getCityId()));
            }
            Date firstDayOfCurrentMonth = DateUtils.setDays(request.getStartDate(), 1);
            Date firstDayOf_1Month = DateUtils.addMonths(firstDayOfCurrentMonth, -1);
            Date firstDayOf_2Month = DateUtils.addMonths(firstDayOfCurrentMonth, -2);
            Date firstDayOf_3Month = DateUtils.addMonths(firstDayOfCurrentMonth, -3);
            predicates.add(cb.or(
                    cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(SellerSalesPerformance_.date), request.getEndDate())),
                    cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOfCurrentMonth)),
                    cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_1Month)),
                    cb.and(cb.greaterThanOrEqualTo(root.get(SellerSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(SellerSalesPerformance_.date), firstDayOf_2Month))
            ));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

}
