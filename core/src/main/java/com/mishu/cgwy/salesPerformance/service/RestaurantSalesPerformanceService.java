package com.mishu.cgwy.salesPerformance.service;

import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem;
import com.mishu.cgwy.accounting.domain.AccountReceivableItem_;
import com.mishu.cgwy.accounting.domain.AccountReceivable_;
import com.mishu.cgwy.accounting.enumeration.AccountReceivableType;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.profile.domain.Address_;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.salesPerformance.domain.RestaurantSalesPerformance;
import com.mishu.cgwy.salesPerformance.domain.RestaurantSalesPerformance_;
import com.mishu.cgwy.salesPerformance.repository.RestaurantSalesPerformanceRepository;
import com.mishu.cgwy.salesPerformance.request.SalesPerformanceRequest;
import com.mishu.cgwy.salesPerformance.response.RestaurantSalesPerformanceWrapper;
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
public class RestaurantSalesPerformanceService {

    @Autowired
    private EntityManager entityManager;
    @Autowired
    private RestaurantSalesPerformanceRepository restaurantSalesPerformanceRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Transactional(readOnly = true)
    public Restaurant getRestaurantById(Long id) {
        return restaurantRepository.findOne(id);
    }

    @Transactional(readOnly = true)
    public Map<Long, Integer> dailyCountOrders(Date start, Date end) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<AccountReceivable> root = query.from(AccountReceivable.class);
        Expression<Long> restaurantId = root.get(AccountReceivable_.restaurant).get(Restaurant_.id);
        Expression<Long> Orders = cb.count(root.get(AccountReceivable_.id));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivable_.createDate), end), cb.equal(root.get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()));
        query.multiselect(restaurantId, Orders).where(predicate).groupBy(restaurantId);
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
        Expression<Long> restaurantId = root.get(AccountReceivable_.restaurant).get(Restaurant_.id);
        Expression salesAmount = cb.sum(root.get(AccountReceivable_.amount));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivable_.createDate), end));
        query.multiselect(restaurantId, salesAmount).where(predicate).groupBy(restaurantId);
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
        Expression<Long> restaurantId = root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.restaurant).get(Restaurant_.id);
        Expression avgCostAmount = cb.sum(cb.prod(root.get(AccountReceivableItem_.avgCost), cb.<Integer>selectCase().when(cb.equal(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.type), AccountReceivableType.SELL.getValue()), root.get(AccountReceivableItem_.quantity)).otherwise(cb.prod(root.get(AccountReceivableItem_.quantity), -1))));
        Predicate predicate = cb.and(cb.greaterThanOrEqualTo(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), start), cb.lessThan(root.get(AccountReceivableItem_.accountReceivable).get(AccountReceivable_.createDate), end));
        query.multiselect(restaurantId, avgCostAmount).where(predicate).groupBy(restaurantId);
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Tuple tuple : entityManager.createQuery(query).getResultList()) {
            map.put(((Number) tuple.get(0)).longValue(), (BigDecimal) tuple.get(1));
        }
        return map;
    }

    @Transactional
    public void saveRestaurantSalesPerformance(RestaurantSalesPerformance restaurantSalesPerformance) {
        restaurantSalesPerformanceRepository.save(restaurantSalesPerformance);
    }

    @Transactional(readOnly = true)
    public List<RestaurantSalesPerformance> getByRestaurantIdAndDate(Long restaurantId, Date date) {
        return restaurantSalesPerformanceRepository.getByRestaurantIdAndDate(restaurantId, date);
    }

    @Transactional(readOnly = true)
    public Page<RestaurantSalesPerformanceWrapper> getRestaurantSalesPerformance(SalesPerformanceRequest request) {

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<RestaurantSalesPerformance> root = query.from(RestaurantSalesPerformance.class);

        Date firstDayOfCurrentMonth = DateUtils.setDays(request.getStartDate(), 1);
        Date firstDayOf_1Month = DateUtils.addMonths(firstDayOfCurrentMonth, -1);
        Date firstDayOf_2Month = DateUtils.addMonths(firstDayOfCurrentMonth, -2);
        Date firstDayOf_3Month = DateUtils.addMonths(firstDayOfCurrentMonth, -3);

        Expression<String> warehouseName = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.name);
        Expression<String> blockName = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.customer).get(Customer_.block).get(Block_.name);
        Expression<Long> restaurantId = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.id);
        Expression<String> restaurantName = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.name);
        Expression<String> address = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.address).get(Address_.address);
        Expression<String> receiver = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.receiver);
        Expression<String> sellerName = root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.realname);
        Expression<Integer> currentOrders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(RestaurantSalesPerformance_.date), request.getEndDate())), root.get(RestaurantSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _1Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(RestaurantSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _2Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month)), root.get(RestaurantSalesPerformance_.orders)).otherwise(0));
        Expression<Integer> _3Orders = cb.sum(cb.<Integer>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month)), root.get(RestaurantSalesPerformance_.orders)).otherwise(0));
        Expression<BigDecimal> currentSalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(RestaurantSalesPerformance_.date), request.getEndDate())), root.get(RestaurantSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _1SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(RestaurantSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _2SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month)), root.get(RestaurantSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _3SalesAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month)), root.get(RestaurantSalesPerformance_.salesAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> currentAvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(RestaurantSalesPerformance_.date), request.getEndDate())), root.get(RestaurantSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _1AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOfCurrentMonth)), root.get(RestaurantSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _2AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month)), root.get(RestaurantSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));
        Expression<BigDecimal> _3AvgCostAmount = cb.sum(cb.<BigDecimal>selectCase().when(cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month)), root.get(RestaurantSalesPerformance_.avgCostAmount)).otherwise(BigDecimal.ZERO));

        query.multiselect(warehouseName, blockName, restaurantId, restaurantName, address, receiver, sellerName, currentOrders, _1Orders, _2Orders, _3Orders, currentSalesAmount, _1SalesAmount, _2SalesAmount, _3SalesAmount, currentAvgCostAmount, _1AvgCostAmount, _2AvgCostAmount, _3AvgCostAmount);
        query.where(new SalesPerformanceSpecification(request).toPredicate(root, query, cb)).groupBy(restaurantId);

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        List<RestaurantSalesPerformanceWrapper> salesPerformances = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList()) {
            salesPerformances.add(new RestaurantSalesPerformanceWrapper(tuple.get(0).toString(), tuple.get(1).toString(), ((Number) tuple.get(2)).longValue(), tuple.get(3).toString(), tuple.get(4).toString(), tuple.get(5).toString(), tuple.get(6).toString(), ((Number) tuple.get(7)).intValue(), ((Number) tuple.get(8)).intValue(), ((Number) tuple.get(9)).intValue(), ((Number) tuple.get(10)).intValue(), (BigDecimal) tuple.get(11), (BigDecimal) tuple.get(12), (BigDecimal) tuple.get(13), (BigDecimal) tuple.get(14), (BigDecimal) tuple.get(15), (BigDecimal) tuple.get(16), (BigDecimal) tuple.get(17), (BigDecimal) tuple.get(18)));
        }
        CriteriaQuery<Long> cntQuery = cb.createQuery(Long.class);
        Root<RestaurantSalesPerformance> cntRoot = cntQuery.from(RestaurantSalesPerformance.class);
        cntQuery.select(cb.countDistinct(cntRoot.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.id))).where(new SalesPerformanceSpecification(request).toPredicate(cntRoot, cntQuery, cb));
        Long total = entityManager.createQuery(cntQuery).getSingleResult();
        return new PageImpl<>(salesPerformances, pageRequest, total == null ? 0 : total);
    }

    private class SalesPerformanceSpecification implements Specification<RestaurantSalesPerformance> {

        private SalesPerformanceRequest request;

        public SalesPerformanceSpecification(SalesPerformanceRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<RestaurantSalesPerformance> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(RestaurantSalesPerformance_.restaurant).get(Restaurant_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
            }
            Date firstDayOfCurrentMonth = DateUtils.setDays(request.getStartDate(), 1);
            Date firstDayOf_1Month = DateUtils.addMonths(firstDayOfCurrentMonth, -1);
            Date firstDayOf_2Month = DateUtils.addMonths(firstDayOfCurrentMonth, -2);
            Date firstDayOf_3Month = DateUtils.addMonths(firstDayOfCurrentMonth, -3);
            predicates.add(cb.or(
                    cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), request.getStartDate()), cb.lessThan(root.get(RestaurantSalesPerformance_.date), request.getEndDate())),
                    cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOfCurrentMonth)),
                    cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_1Month)),
                    cb.and(cb.greaterThanOrEqualTo(root.get(RestaurantSalesPerformance_.date), firstDayOf_3Month), cb.lessThan(root.get(RestaurantSalesPerformance_.date), firstDayOf_2Month))
            ));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

}
