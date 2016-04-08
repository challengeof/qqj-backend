package com.mishu.cgwy.order.service;


import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.bonus.controller.SalesmanStatisticsRequest;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem_;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.order.controller.OrderEvaluateSearchRequest;
import com.mishu.cgwy.order.controller.OrderItemQueryRequest;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.controller.SkuSalesRequest;
import com.mishu.cgwy.order.domain.*;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.order.repository.*;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.SkuRepository;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.service.JpaUtils;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.score.domain.ScoreLog_;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.utils.OrderUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    private static Logger logger = LoggerFactory.getLogger(OrderService.class);
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private RefundRepository refundRepository;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private OrderGroupRepository orderGroupRepository;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private SkuRepository skuRepository;
    @Autowired
    private EvaluateRepository evaluateRepository;

    public List<Order> getOrderByCustomer(Customer customer) {
        return orderRepository.findByCustomer(customer);
    }

    public Long getOrderCountByRestaurant(Restaurant restaurant) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);

        final Root<Order> root = query.from(Order.class);
        query.multiselect(cb.count(root.get(Order_.id)));
        query.where(cb.equal(root.get(Order_.restaurant).get(Restaurant_.id), restaurant.getId()));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Deprecated
    public List<OrderGroup> getOrderGroupOfEdbByWarehouseId(final Date expectedArrivedDate, final Long warehouseId) {
        return orderGroupRepository.findAll(new Specification<OrderGroup>() {
            @Override
            public Predicate toPredicate(Root<OrderGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                    cb.equal(root.get(OrderGroup_.city).get(City_.id), 1)
                );
            }
        });
    }

    public List<Order> getOrderByCustomerAndSubmitDate(final Customer customer, final Date start, final Date end) {
        return orderRepository.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        cb.equal(root.get(Order_.customer).get(Customer_.id), customer.getId()),
                        cb.greaterThanOrEqualTo(root.get(Order_.submitDate), start),
                        cb.lessThanOrEqualTo(root.get(Order_.submitDate), end),
                        cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()),
                        cb.notEqual(root.get(Order_.status), OrderStatus.CANCEL.getValue()));
            }
        });
    }

    public List<Order> getOrderByCustomerAndSubmitDateAfter(final Customer customer, final Date current) {
        return orderRepository.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(current);

                Date start = null;
                start = DateUtils.truncate(current, Calendar.DAY_OF_MONTH);
                start = DateUtils.addDays(start, -1);

                return cb.and(
                        cb.equal(root.get(Order_.customer).get(Customer_.id), customer.getId()),
                        cb.greaterThanOrEqualTo(root.get(Order_.submitDate), start),
                        cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()),
                        cb.notEqual(root.get(Order_.status), OrderStatus.CANCEL.getValue()));
            }
        });
    }

    public boolean existsOrderCommitted(final Customer customer) {
        long count = orderRepository.count(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        cb.equal(root.get(Order_.customer).get(Customer_.id), customer.getId()),
                        cb.equal(root.get(Order_.status), OrderStatus.COMMITTED.getValue())
                );
            }
        });

        return count > 0;
    }

    @Transactional
    public Order getCartByCustomer(final Customer customer) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Order> query = cb.createQuery(Order.class);

        final Root<Order> root = query.from(Order.class);
        query.select(root);
        query.where(cb.equal(root.get(Order_.customer), customer), cb.equal(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));

        List<Order> orders = entityManager.createQuery(query).setLockMode(LockModeType.PESSIMISTIC_FORCE_INCREMENT).getResultList();
        if (!orders.isEmpty()) {
            return orders.get(0);
        } else {
            Order order = new Order();
            order.setStatus(OrderStatus.UNCOMMITTED.getValue());
            order.setCustomer(customer);
            order = orderRepository.save(order);
            return order;
        }

        /*List<Order> list = orderRepository.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(
                        cb.equal(root.get(Order_.customer), customer),
                        cb.equal(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
            }
        });

        Order cart = null;

        if (list.isEmpty()) {
            cart = new Order();
            cart.setCustomer(customer);
            cart.setStatus(OrderStatus.UNCOMMITTED.getValue());

            cart = orderRepository.save(cart);
        } else if (list.size() > 1) {
            for (int i = 1; i < list.size(); i++) {
                orderRepository.delete(list.get(i));
            }
            cart = list.get(0);
        } else {
            cart = list.get(0);
        }

        return cart;*/
    }

    public List<Refund> findRefundByOrder(Order order) {
        return refundRepository.findByOrder(order);
    }

    public List<Order> findByRestaurantId(Long restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    public List<Order> sortOrder(List<Order> orders) {
        Collections.sort(orders, new OrderComparator());
        return orders;
    }

    public Page<Order> findMaxOrders(final OrderQueryRequest request) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, "subTotal"));

        return orderRepository.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Order_.submitDate), request.getEnd()));
                }
                predicates.add(cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
                predicates.add(cb.notEqual(root.get(Order_.status), OrderStatus.CANCEL.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }


    public Page<Order> findOrders(final OrderQueryRequest request, final AdminUser adminUser) {
        return orderRepository.findAll(new OrderSpecification(adminUser, request), new PageRequest(request.getPage(), request.getPageSize()));
    }

    public Order getOrderById(Long id) {
        return orderRepository.findOne(id);
    }

    public Page<OrderItem> findOrderItems(final OrderItemQueryRequest request, final AdminUser adminUser) {

        assert adminUser != null;
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, "id"));
        return orderItemRepository.findAll(new Specification<OrderItem>() {
            @Override
            public Predicate toPredicate(Root<OrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!query.getResultType().equals(Long.class)) {
                    Fetch<OrderItem, Order> orderFetch = root.fetch(OrderItem_.order, JoinType.INNER);
                    Fetch<Order, Restaurant> restaurantFetch = orderFetch.fetch(Order_.restaurant, JoinType.INNER);
                    restaurantFetch.fetch(Restaurant_.type, JoinType.LEFT);
                    Fetch<Order, Customer> customerFetch = orderFetch.fetch(Order_.customer, JoinType.LEFT);
                    customerFetch.fetch(Customer_.block, JoinType.LEFT);
                    Fetch<Order, Organization> organizationFetch = orderFetch.fetch(Order_.organization, JoinType.INNER);
                    Fetch<OrderItem, Sku> skuFetch = root.fetch(OrderItem_.sku, JoinType.INNER);
                    Fetch<Sku, Product> productFetch = skuFetch.fetch(Sku_.product, JoinType.INNER);
                    productFetch.fetch(Product_.brand, JoinType.LEFT);
                    productFetch.fetch(Product_.category, JoinType.LEFT);
                    /*productFetch.fetch(Product_.mediaFile, JoinType.LEFT);*/
                }

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }
                    Set<Long> blockIds = new HashSet<>();
                    Set<Long> warehouseIds = new HashSet<>();
                    Set<Long> cityIds = new HashSet<>();
                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        warehouseIds.add(warehouse.getId());
                    }
                    for (Block block : adminUser.getBlocks()) {
                        blockIds.add(block.getId());
                    }
                    if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                        List<Predicate> blockCondition = new ArrayList<>();
                        if (!blockIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                        }
                        if (!warehouseIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id).in(warehouseIds));
                        }
                        if (!cityIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.city).get(City_.id).in(cityIds));
                        }
                        if (!blockCondition.isEmpty()) {
                            predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                        } else {
                            predicates.add(cb.or());
                        }
                    } else {
                        predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
                    }
                }
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (request.getRestaurantId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
                }
                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.sku).get(Sku_.id), request.getSkuId()));
                }
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getEnd()));
                }
                if (request.getOrderStatus() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.status), request.getOrderStatus()));
                } else {
                    predicates.add(cb.notEqual(root.get(OrderItem_.order).get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
                    predicates.add(cb.notEqual(root.get(OrderItem_.order).get(Order_.status), OrderStatus.CANCEL.getValue()));
                }
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.id), request.getOrderId()));
                }
                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(OrderItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getProductName() + "%"));
                }
                if (StringUtils.isNotBlank(request.getRestaurantName())) {
                    predicates.add(cb.like(root.get(OrderItem_.order).get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                }
                if (request.getOrderType() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.type), OrderType.find(request.getOrderType(), OrderType.NOMAL).getVal()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Transactional
    public OrderGroup saveOrderGroup(OrderGroup orderGroup) {
        return orderGroupRepository.save(orderGroup);
    }

    @Transactional(readOnly = true)
    public OrderGroup getOrderGroupById(Long id) {
        return orderGroupRepository.getOne(id);
    }

    @Transactional
    public Order saveOrder(Order order) {

        if (order.getId() == null) {
            Date submitDate = order.getSubmitDate();
            if (submitDate == null) {
                order.setSubmitDate(new Date());
            }
            order.setExpectedArrivedDate(OrderUtils.getExpectedArrivedDate(order.getSubmitDate()));
        }
        try {
            if (order.getOrderItems() != null) {
                for (OrderItem orderItem : order.getOrderItems()) {
                    if (orderItem.getPrice() == null) {
                        StackTraceElement[] traces = Thread.currentThread().getStackTrace();
                        String info = "orderItem.price.null " + orderItem;
                        if (traces != null) {
                            for (StackTraceElement trace : traces) {
                                info += "\n" + trace.toString();
                            }
                        }
                        logger.info(info);
                        break;
                    }
                }
            }
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return orderRepository.save(order);
    }

    @Transactional
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Refund saveRefund(Refund refund) {
        return refundRepository.save(refund);
    }

    @Transactional(readOnly = true)
    public List<OrderGroup> findOrderGroups(final Date expectedArrivedDate) {
        return orderGroupRepository.findAll(new Specification<OrderGroup>() {
            @Override
            public Predicate toPredicate(Root<OrderGroup> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                query.distinct(true);
                ListJoin<OrderGroup, com.mishu.cgwy.order.domain.Order> orderListJoin = root.join(OrderGroup_.members, JoinType.LEFT);
                return cb.and(cb.greaterThanOrEqualTo(orderListJoin.get(Order_.submitDate), DateUtils.truncate(DateUtils.addDays(expectedArrivedDate, -1), Calendar.DATE)), cb.lessThan(orderListJoin.get(Order_.submitDate), DateUtils.truncate(expectedArrivedDate, Calendar.DATE)));
            }
        });
    }

    @Transactional(readOnly = true)
    public List<OrderGroup> findOrderGroupsByTracker(final Date expectedArrivedDate, final AdminUser tracker) {

        assert tracker != null;
        if (PermissionCheckUtils.canViewAllTracker(tracker)) {
            final List<Long> cityIds = new ArrayList<Long>();
            final List<Long> organizationIds = new ArrayList<Long>();
            if (tracker.isGlobalAdmin()) {
                for (City city : tracker.getCities()) {
                    cityIds.add(city.getId());
                }
            } else {
                for (Organization organization : tracker.getOrganizations()) {
                    organizationIds.add(organization.getId());
                }
            }
            return new ArrayList<>(Collections2.filter(findOrderGroups(expectedArrivedDate), new com.google.common.base.Predicate<OrderGroup>() {
                @Override
                public boolean apply(OrderGroup input) {
                    if (tracker.isGlobalAdmin() && cityIds.contains(input.getCity().getId())) {
                        return true;
                    } else if (!tracker.isGlobalAdmin() && organizationIds.contains(input.getOrganization().getId())) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }));
        } else {
            return new ArrayList<>(Collections2.filter(findOrderGroups(expectedArrivedDate), new com.google.common.base.Predicate<OrderGroup>() {
                @Override
                public boolean apply(OrderGroup input) {
                    if (input.getTracker() != null && input.getTracker().getId().equals(tracker.getId())) {
                        return true;
                    }
                    return false;
                }
            }));
        }

    }

    @Transactional(readOnly = true)
    public List<Order> findByExpectedArrivedDateAndWarehouseId(final OrderGroupQueryRequest request, final AdminUser adminUser) {
        return orderRepository.findAll(new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                root.fetch(Order_.customer).fetch(Customer_.adminUser, JoinType.LEFT);
                root.fetch(Order_.restaurant);
                List<Predicate> predicates = new ArrayList<Predicate>();
                List<Long> cityIds = new ArrayList<Long>();
                if (!adminUser.isGlobalAdmin()) {
                    request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                }
                /*if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Order_.organization)
                            .get(Organization_.city).get(City_.id), request.getCityId()));
                } else {
                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }

                    predicates.add(root.get(Order_.organization).get(Organization_.city).get(City_.id).in(cityIds));
                }*/
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                predicates.add(cb.equal(root.get(Order_.expectedArrivedDate), request.getExpectedArrivedDate()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    @Transactional(readOnly = true)
    public OrderGroup getOrderGroupByOrder(Order order) {
        final List<OrderGroup> list = orderGroupRepository.findOrderGroupByOrder(order);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    @Transactional
    public void deliverOrder(Date date) {
        final List<Order> orders = orderRepository.findByStatus(OrderStatus.COMMITTED.getValue());
        for (Order order : orders) {
            if (order.getSubmitDate().before(date)) {
                order.setStatus(OrderStatus.SHIPPING.getValue());
                saveOrder(order);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<Object[]> getRestaurantConsumption(List<Long> restaurantIds) {
        if (restaurantIds.isEmpty()) {
            return new ArrayList<>();
        }
        return orderRepository.getRestaurantConsumption(restaurantIds);
    }

    @Transactional
    public BigDecimal sumOrderRealTotal(final OrderQueryRequest request, final AdminUser adminUser) throws DataAccessException {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new OrderSpecification(adminUser, request);
        query.select(cb.sum(root.get(Order_.realTotal)));
        query.where(specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }


    @Transactional(readOnly = true)
    public Page<Order> findOrdersByCustomer(final OrderQueryRequest request, final Customer customer) {

        Pageable page = new PageRequest(request.getPage(), request.getPageSize(), Sort.Direction.DESC, "submitDate");
        return orderRepository.findAll(new Specification<Order>() {

            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final Path<Date> submitDate = root.get(Order_.submitDate);
                List<Predicate> predicates = new ArrayList<>();
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Order_.submitDate), request.getEnd()));
                }
                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(Order_.status), request.getStatus()));
                } else {
                    predicates.add(cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
                }
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.id), customer.getId()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, page);
    }


    @Transactional(readOnly = true)
    public Long firstOrderCount(final OrderQueryRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new OrderSpecification(adminUser, request);
        query.multiselect(cb.count(root.get(Order_.sequence)));
        query.where(cb.equal(root.get(Order_.sequence), Long.valueOf(1)), specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Long restaurantCount(final OrderQueryRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new OrderSpecification(adminUser, request);
        query.select(cb.countDistinct(root.get(Order_.restaurant)));
        query.where(specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    public Evaluate getEvaluateByOrder(Long orderId) {
        return evaluateRepository.findEvaluateByOrderId(orderId);
    }

    @Transactional
    public void relationScorelog(Long orderId,ScoreLog scoreLog){

        Evaluate evaluate = this.getEvaluateByOrder(orderId);
        evaluate.setScoreLog(scoreLog);
    }


    private static class OrderSpecification implements Specification<Order> {

        private final AdminUser adminUser;
        private final OrderQueryRequest request;

        public OrderSpecification(AdminUser adminUser, OrderQueryRequest request) {
            this.adminUser = adminUser;
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (query.getResultType().equals(Order.class)) {
                Fetch<Order, Customer> customerFetch = root.fetch(Order_.customer);
                customerFetch.fetch(Customer_.block, JoinType.LEFT);
                root.fetch(Order_.restaurant);
                root.fetch(Order_.organization);
            }
            if (request.getSortField() != null) {
                switch (request.getSortField()) {
                    case "restaurantName":
                        final Path<String> restaurantName = root.get(Order_.restaurant).get(Restaurant_.name);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(restaurantName));
                        } else {
                            query.orderBy(cb.desc(restaurantName));
                        }
                        break;
                    case "total":
                        final Path<BigDecimal> total = root.get(Order_.total);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(total));
                        } else {
                            query.orderBy(cb.desc(total));
                        }
                        break;
                    case "sequence":
                        final Path<Long> sequence = root.get(Order_.sequence);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(sequence));
                        } else {
                            query.orderBy(cb.desc(sequence));
                        }
                        break;
                    case "telephone":
                        final Path<String> telephone = root.get(Order_.restaurant).get(Restaurant_.telephone);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(telephone));
                        } else {
                            query.orderBy(cb.desc(telephone));
                        }
                        break;
                    case "adminUserId":
                        final Path<Long> adminUserId = root.get(Order_.adminUser).get(AdminUser_.id);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(adminUserId));
                        } else {
                            query.orderBy(cb.desc(adminUserId));
                        }
                        break;
                    default:
                        final Path<Long> id = root.get(Order_.id);
                        if (request.isAsc()) {
                            query.orderBy(cb.asc(id));
                        } else {
                            query.orderBy(cb.desc(id));
                        }
                        break;
                }
            }

            if (adminUser != null) {
                if (!adminUser.isGlobalAdmin()) {
                    request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                }
                Set<Long> blockIds = new HashSet<>();
                Set<Long> warehouseIds = new HashSet<>();
                Set<Long> cityIds = new HashSet<>();
                Set<Long> depotCityIds = new HashSet<>();
                Set<Long> depotIds = new HashSet<>();
                for (City city : adminUser.getCities()) {
                    cityIds.add(city.getId());
                }
                for (Warehouse warehouse : adminUser.getWarehouses()) {
                    warehouseIds.add(warehouse.getId());
                }
                for (Block block : adminUser.getBlocks()) {
                    blockIds.add(block.getId());
                }
                for (City city : adminUser.getDepotCities()) {
                    depotCityIds.add(city.getId());
                }
                for (Depot depot : adminUser.getDepots()) {
                    depotIds.add(depot.getId());
                }
                if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                    List<Predicate> blockCondition = new ArrayList<>();
                    if (!blockIds.isEmpty()) {
                        blockCondition.add(root.get(Order_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                    }
                    if (!warehouseIds.isEmpty()) {
                        blockCondition.add(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id).in(warehouseIds));
                    }
                    if (!cityIds.isEmpty()) {
                        blockCondition.add(root.get(Order_.customer).get(Customer_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotCityIds.isEmpty()) {
                        blockCondition.add(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.city).get(City_.id).in(depotCityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        blockCondition.add(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id).in(depotIds));
                    }
                    if (!blockCondition.isEmpty()) {
                        predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                } else {
                    predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
                }
            }
            if(null!=request.getSpikeItemId()){
                    List<javax.persistence.criteria.Predicate> subPredicate = new ArrayList<>();
                    Subquery<Long> subQuery = query.subquery(Long.class);
                    Root<OrderItem> subRoot = subQuery.from(OrderItem.class);
                    subPredicate.add(cb.equal(root.get(Order_.id), subRoot.get(OrderItem_.order).get(Order_.id)));
                    subPredicate.add(cb.equal(subRoot.get(OrderItem_.spikeItem).get(SpikeItem_.id), request.getSpikeItemId()));

                    subQuery.where(subPredicate.toArray(new Predicate[]{}));
                predicates.add(root.get(Order_.id).in(subQuery.select(subRoot.get(OrderItem_.order).get(Order_.id))));

            }
            if (request.isRefundsIsNotEmpty()) {
                predicates.add(cb.isNotEmpty(root.get(Order_.refunds)));
            }
            if (request.getCityId() != null) {
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
            }
            if (request.getWarehouseId() != null) {
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
            }
            if (request.getOrganizationId() != null) {
                predicates.add(cb.equal(root.get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
            }
            if (request.getDepotId() != null) {
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.depot).get(Depot_.id), request.getDepotId()));
            }
            if (request.getBlockId() != null) {
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.block).get(Block_.id), request.getBlockId()));
            }
            if (request.getAdminId() != null) {
                predicates.add(cb.equal(root.get(Order_.adminUser).get(AdminUser_.id), request.getAdminId()));
            }
            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get(Order_.customer).get(Customer_.id), request.getCustomerId()));
            }
            if (request.getStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), request.getStart()));
            }
            if (request.getEnd() != null) {
                if (request.isPromotionTag()) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Order_.submitDate), request.getEnd()));
                } else {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Order_.submitDate), request.getEnd()));
                }
            }
            if (request.getRestaurantId() != null) {
                predicates.add(cb.equal(root.get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if (StringUtils.isNotBlank(request.getRestaurantName())) {
                predicates.add(cb.like(root.get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if (request.getStatus() != null) {
                predicates.add(cb.equal(root.get(Order_.status), request.getStatus()));
            } else {
                predicates.add(cb.and(root.get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.DEALING.getValue(), OrderStatus.SHIPPING.getValue(), OrderStatus.COMPLETED.getValue())));
            }
            if (request.getExpectedArrivedDate() != null) {
                predicates.add(cb.equal(root.get(Order_.expectedArrivedDate), request.getExpectedArrivedDate()));
            }
            if (request.getOrderId() != null) {
                predicates.add(cb.equal(root.get(Order_.id), request.getOrderId()));
            }
            if (request.getCoordinateLabeled() != null) {
                if (request.getCoordinateLabeled() == 0) {
                    predicates.add(cb.isNull(root.get(Order_.restaurant).get(Restaurant_.address).get(Address_.wgs84Point)));
                } else {
                    predicates.add(cb.isNotNull(root.get(Order_.restaurant).get(Restaurant_.address).get(Address_.wgs84Point)));
                }
            }
            if (request.getTelephone() != null) {
                Predicate restaurantPhone = cb.equal(root.get(Order_.restaurant).get(Restaurant_.telephone), request.getTelephone());
                Predicate customerPhone = cb.equal(root.get(Order_.restaurant).get(Restaurant_.customer).get(Customer_.username), request.getTelephone());
                predicates.add(cb.or(new Predicate[]{restaurantPhone, customerPhone}));
            }
            if (request.getOrderType() != null) {
                predicates.add(cb.equal(root.get(Order_.type), OrderType.find(request.getOrderType(), OrderType.NOMAL).getVal()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    @Transactional(readOnly = true)
    public BigDecimal getUnCancelRestaurantTotal(final RestaurantQueryRequest request, final AdminUser adminUser) throws DataAccessException {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new RestaurantOrderSpecification(adminUser, request);
        query.select(cb.sum(root.get(Order_.total)));
        query.where(JpaUtils.getPredicate(cb, root.get(Order_.restaurant), adminUser, request), specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public Long getAliveCustomers(final RestaurantQueryRequest request, final AdminUser adminUser) throws DataAccessException {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new RestaurantOrderSpecification(adminUser, request);
        query.select(cb.countDistinct(root.get(Order_.restaurant)));
        query.where(JpaUtils.getPredicate(cb, root.join(Order_.restaurant,JoinType.LEFT), adminUser, request), specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional(readOnly = true)
    public BigDecimal getUnCancelRestaurantTotalBetween(final RestaurantQueryRequest request, final AdminUser adminUser) throws DataAccessException {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        final Root<Order> root = query.from(Order.class);
        final Specification<Order> specification = new RestaurantOrderSpecification(adminUser, request);
        query.select(cb.sum(root.get(Order_.total)));
        query.where(JpaUtils.getPredicate(cb, root.get(Order_.restaurant), adminUser, request), specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    private static class RestaurantOrderSpecification implements Specification<Order> {

        private final AdminUser adminUser;
        private final RestaurantQueryRequest request;

        public RestaurantOrderSpecification(AdminUser adminUser, RestaurantQueryRequest request) {
            this.adminUser = adminUser;
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            List<Predicate> predicates = new ArrayList<>();
            if (request.getStart() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), request.getStart()));
            }
            if (request.getEnd() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get(Order_.submitDate), request.getEnd()));
            }
            predicates.add(cb.notEqual(root.get(Order_.status), OrderStatus.CANCEL.getValue()));
            predicates.add(cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        }
    }

    public Map<Long, Tuple> getSkuBoughtCountAndQuantity(Customer customer) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createTupleQuery();
        final Root<Order> root = query.from(Order.class);
        final ListJoin<Order, OrderItem> join = root.join(Order_.orderItems);
        query.multiselect(join.get(OrderItem_.sku).get(Sku_.id),
//                bundle buy counts
                cb.sum(cb.<Boolean, Integer>selectCase(join.get(OrderItem_.bundle)).when(Boolean.TRUE, Integer.valueOf(1)).otherwise(Integer.valueOf(0))),
//                bundle buy sum
                cb.sum(join.get(OrderItem_.bundleQuantity)),
//                single buy counts
                cb.sum(cb.<Boolean, Integer>selectCase(join.get(OrderItem_.bundle)).when(Boolean.FALSE, Integer.valueOf(1)).otherwise(Integer.valueOf(0))),
//                single buy sum
                cb.sum(join.get(OrderItem_.singleQuantity)));
        query.where(cb.equal(root.get(Order_.customer), customer), root.get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.COMPLETED.getValue(), OrderStatus.DEALING.getValue(), OrderStatus.SHIPPING.getValue()));
        query.groupBy(join.get(OrderItem_.sku));

        List<Tuple> list = entityManager.createQuery(query).getResultList();
        Map<Long, Tuple> map = new HashMap<>();
        for (Tuple tuple : list) {
            map.put((Long) tuple.get(0), tuple);
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<AdminUser, Map<Restaurant, BigDecimal>> getRestaurantConsumptionStatistics(Date start, Date end) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createTupleQuery();
        final Root<Order> root = query.from(Order.class);
        query.multiselect(root.get(Order_.restaurant), root.get(Order_.adminUser), cb.sum(root.get(Order_.total)));
        query.where(cb.greaterThanOrEqualTo(root.get(Order_.submitDate), start), cb.lessThanOrEqualTo(root.get(Order_.submitDate), end), cb.notEqual(root.get(Order_.status), OrderStatus.CANCEL.getValue()), cb.notEqual(root.get(Order_.status), OrderStatus.UNCOMMITTED.getValue()));
        query.groupBy(root.get(Order_.restaurant), root.get(Order_.adminUser));
        final List<Tuple> resultList = entityManager.createQuery(query).getResultList();
        Map<AdminUser, Map<Restaurant, BigDecimal>> result = new HashMap<>();
        for (Tuple tuple : resultList) {
            Restaurant restaurant = (Restaurant) tuple.get(0);
            AdminUser adminUser = (AdminUser) tuple.get(1);
            BigDecimal total = (BigDecimal) tuple.get(2);
            if (adminUser != null) {
                if (!result.containsKey(adminUser)) {
                    result.put(adminUser, new HashMap<Restaurant, BigDecimal>());
                }
                result.get(adminUser).put(restaurant, total);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public Map<Long, BigDecimal> getConsumptionGroupByAdminUser(Date start, Date end) {

        final Map<AdminUser, Map<Restaurant, BigDecimal>> restaurantConsumptionStatistics = getRestaurantConsumptionStatistics(start, end);
        Map<Long, BigDecimal> map = new HashMap<>();
        for (Map.Entry<AdminUser, Map<Restaurant, BigDecimal>> entry : restaurantConsumptionStatistics.entrySet()) {
            Long adminUserId = entry.getKey().getId();
            final Map<Restaurant, BigDecimal> value = entry.getValue();
            if (!map.containsKey(adminUserId)) {
                map.put(adminUserId, BigDecimal.ZERO);
            }
            for (BigDecimal money : value.values()) {
                map.put(adminUserId, map.get(adminUserId).add(money));
            }
        }
        return map;
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getRestaurantHavingOrderCountGroupByAdminUser(Date start, Date end) {

        final Map<AdminUser, Map<Restaurant, BigDecimal>> restaurantConsumptionStatistics = getRestaurantConsumptionStatistics(start, end);
        Map<Long, Long> map = new HashMap<>();
        for (Map.Entry<AdminUser, Map<Restaurant, BigDecimal>> entry : restaurantConsumptionStatistics.entrySet()) {
            Long adminUserId = entry.getKey().getId();
            map.put(adminUserId, Long.valueOf(entry.getValue().size()));
        }
        return map;
    }

    @Transactional(readOnly = true)
    public List<OrderItem> findOrderItems(final SkuSalesRequest request, final AdminUser adminUser) {

        assert adminUser != null;
        return orderItemRepository.findAll(new Specification<OrderItem>() {
            @Override
            public Predicate toPredicate(Root<OrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!query.getResultType().equals(Long.class)) {
                    Fetch<OrderItem, Order> orderFetch = root.fetch(OrderItem_.order, JoinType.INNER);
                    orderFetch.fetch(Order_.customer, JoinType.INNER);
                    orderFetch.fetch(Order_.restaurant, JoinType.INNER);
                    Fetch<OrderItem, Sku> skuFetch = root.fetch(OrderItem_.sku, JoinType.INNER);
                    Fetch<Sku, Product> productFetch = skuFetch.fetch(Sku_.product, JoinType.INNER);
                    productFetch.fetch(Product_.brand, JoinType.LEFT);
                    productFetch.fetch(Product_.category, JoinType.LEFT);
                    /*productFetch.fetch(Product_.mediaFile, JoinType.LEFT);*/
                }

                List<Predicate> predicates = new ArrayList<Predicate>();
                /*if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.organization).get(Organization_.city).get(City_.id), request.getCityId()));
                } else {
                    List<Long> cityIds = new ArrayList<>();
                    if (adminUser != null) {
                        for (City city : adminUser.getCities()) {
                            cityIds.add(city.getId());
                        }
                    }
                    predicates.add(root.get(OrderItem_.order).get(Order_.organization).get(Organization_.city).get(City_.id).in(cityIds));
                }*/
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (adminUser != null) {
                    List<Long> blockIds = new ArrayList<>();
                    for (Block block : adminUser.getBlocks()) {
                        blockIds.add(block.getId());
                    }
                    if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                        if (blockIds.isEmpty()) {
                            predicates.add(cb.or());
                        } else {
                            predicates.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                        }
                    } else {
                        predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
                    }
                }
                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.sku).get(Sku_.product).get(Product_.brand).get(Brand_.id), request.getBrandId()));
                }
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getEnd()));
                }
                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.equal(root.get(OrderItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getProductName() + "%"));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    @Transactional(readOnly = true)
    public Page<Sku> findSkus(final SkuSalesRequest request, final AdminUser adminUser) {

        assert adminUser != null;
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, "id"));

        return skuRepository.findAll(new Specification<Sku>() {

            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.brand).get(Brand_.id), request.getBrandId()));
                }
                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(Sku_.product).get(Product_.name), "%" + request.getProductName() + "%"));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    public TypedQuery<Tuple> findSkuSaleStatistics(final SkuSalesRequest request, final AdminUser adminUser) {

        assert adminUser != null;
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<OrderItem> root = query.from(OrderItem.class);
        Specification<OrderItem> orderSpecification = new Specification<OrderItem>() {

            @Override
            public Predicate toPredicate(Root<OrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }
                    Set<Long> blockIds = new HashSet<>();
                    Set<Long> warehouseIds = new HashSet<>();
                    Set<Long> cityIds = new HashSet<>();
                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        warehouseIds.add(warehouse.getId());
                    }
                    for (Block block : adminUser.getBlocks()) {
                        blockIds.add(block.getId());
                    }
                    if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                        List<Predicate> blockCondition = new ArrayList<>();
                        if (!blockIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                        }
                        if (!warehouseIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id).in(warehouseIds));
                        }
                        if (!cityIds.isEmpty()) {
                            blockCondition.add(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.city).get(City_.id).in(cityIds));
                        }
                        if (!blockCondition.isEmpty()) {
                            predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                        } else {
                            predicates.add(cb.or());
                        }
                    } else {
                        predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
                    }
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                predicates.add(root.get(OrderItem_.order).get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.DEALING.getValue(), OrderStatus.SHIPPING.getValue(), OrderStatus.COMPLETED.getValue()));
                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.sku).get(Sku_.product).get(Product_.brand).get(Brand_.id), request.getBrandId()));
                }
                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(OrderItem_.sku).get(Sku_.product).get(Product_.name), "%" + request.getProductName() + "%"));
                }
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), request.getEnd()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(OrderItem_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };

        query.multiselect(root.get(OrderItem_.sku), cb.sum(root.get(OrderItem_.singleQuantity)), cb.sum(root.get(OrderItem_.bundleQuantity)), cb.sum(root.get(OrderItem_.countQuantity)), cb.sum(root.get(OrderItem_.sellCancelQuantity)), cb.sum(root.get(OrderItem_.sellReturnQuantity)));
        query.where(orderSpecification.toPredicate(root, query, cb));
        query.groupBy(root.get(OrderItem_.sku));
        return entityManager.createQuery(query);
    }

    public Evaluate saveEvaluate(Evaluate evaluate) {
        return evaluateRepository.save(evaluate);
    }

    public Page<Evaluate> getEvaluate(final OrderEvaluateSearchRequest request, final AdminUser adminUser) {

        assert adminUser != null;
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort.Direction.DESC, "id"));
        return evaluateRepository.findAll(new Specification<Evaluate>() {

            @Override
            public Predicate toPredicate(Root<Evaluate> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }
                    Set<Long> blockIds = new HashSet<>();
                    Set<Long> warehouseIds = new HashSet<>();
                    Set<Long> cityIds = new HashSet<>();
                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        warehouseIds.add(warehouse.getId());
                    }
                    for (Block block : adminUser.getBlocks()) {
                        blockIds.add(block.getId());
                    }
                    if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {
                        List<Predicate> blockCondition = new ArrayList<>();
                        if (!blockIds.isEmpty()) {
                            blockCondition.add(root.get(Evaluate_.customer).get(Customer_.block).get(Block_.id).in(blockIds));
                        }
                        if (!warehouseIds.isEmpty()) {
                            blockCondition.add(root.get(Evaluate_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id).in(warehouseIds));
                        }
                        if (!cityIds.isEmpty()) {
                            blockCondition.add(root.get(Evaluate_.customer).get(Customer_.city).get(City_.id).in(cityIds));
                        }

                        if (!blockCondition.isEmpty()) {
                            predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                        } else {
                            predicates.add(cb.or());
                        }
                    } else {
                        predicates.add(cb.equal(root.get(Evaluate_.customer).get(Customer_.adminUser).get(AdminUser_.id), adminUser.getId()));
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
                }
                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.order).get(Order_.organization).get(Organization_.id), request.getOrganizationId()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                if (request.getAdminUserId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.adminUser).get(AdminUser_.id), request.getAdminUserId()));
                }
                if (request.getOrderId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.order).get(Order_.id), request.getOrderId()));
                }
                if (request.getCustomerId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.customer).get(Customer_.id), request.getCustomerId()));
                }
                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Evaluate_.order).get(Order_.submitDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Evaluate_.order).get(Order_.submitDate), request.getEnd()));
                }
                if (request.getTrackerId() != null) {
                    predicates.add(cb.equal(root.get(Evaluate_.tracker).get(AdminUser_.id), request.getTrackerId()));
                }
                if (StringUtils.isNotBlank(request.getTrackerName())) {
                    predicates.add(cb.like(root.get(Evaluate_.tracker).get(AdminUser_.realname), "%" + request.getTrackerName() + "%"));
                }
                if (StringUtils.isNotBlank(request.getAdminName())) {
                    predicates.add(cb.like(root.get(Evaluate_.adminUser).get(AdminUser_.username), "%" + request.getAdminName() + "%"));
                }
                if (StringUtils.isNotBlank(request.getCustomerName())) {
                    predicates.add(cb.like(root.get(Evaluate_.customer).get(Customer_.username), "%" + request.getCustomerName() + "%"));
                }

                if(request.isOnlyNoScore()){
                    predicates.add(cb.isNull(root.get(Evaluate_.scoreLog)));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    @Transactional
    public List<Refund> findRefundBySubmitDate(final Date submitDate, final Warehouse warehouse) {
        return refundRepository.findAll(new Specification<Refund>() {
            @Override
            public Predicate toPredicate(Root<Refund> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.and(criteriaBuilder.equal(root.get(Refund_.order).get(Order_.customer).get(Customer_.block).get(Block_.warehouse), warehouse), criteriaBuilder.greaterThanOrEqualTo(root.get(Refund_.submitDate), submitDate), criteriaBuilder.lessThanOrEqualTo(root.get(Refund_.submitDate), DateUtils.addDays(submitDate, 1)));
            }
        });
    }

    @Transactional(readOnly = true)
    public List<Evaluate> getEvaluateByTracker(final AdminUser operator) {
        return evaluateRepository.findAll(new Specification<Evaluate>() {
            @Override
            public Predicate toPredicate(Root<Evaluate> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                return criteriaBuilder.and(criteriaBuilder.equal(root.get(Evaluate_.tracker).get(AdminUser_.id), operator.getId()));
            }
        });
    }

    @Transactional
    public static BigDecimal getOrderAmountByCategories(Order order, Long... categoryIds) {

        BigDecimal total = new BigDecimal(0);
        Map<Long, BigDecimal> subTotalMapByCategory = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            int quantity = orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity();
            BigDecimal singlePrice = orderItem.isBundle() ? orderItem.getPrice().divide(new BigDecimal(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP) : orderItem.getPrice();
            BigDecimal orderItemTotalPrice = singlePrice.multiply(new BigDecimal(quantity)).setScale(2, BigDecimal.ROUND_HALF_UP);
            total = total.add(orderItemTotalPrice);
            setSubTotalByCategory(subTotalMapByCategory, orderItemTotalPrice, orderItem.getSku().getProduct().getCategory());
        }
        if (ArrayUtils.isEmpty(categoryIds)) {
            return total;
        }
        BigDecimal subTotalByCategories = calculateSubTotal(subTotalMapByCategory, categoryIds);
        logger.info("subTotalMapByCategory:" + subTotalMapByCategory);
        logger.info("categoryIds:" + ArrayUtils.toString(categoryIds));
        logger.info("subTotalByCategories:" + subTotalByCategories);
        return subTotalByCategories;
    }

    private static BigDecimal calculateSubTotal(Map<Long, BigDecimal> subTotalMapByCategory, Long... categoryIds) {

        BigDecimal subTotal = new BigDecimal(0);
        for (Long categoryId : categoryIds) {
            if (subTotalMapByCategory.get(categoryId) != null) {
                subTotal = subTotal.add(subTotalMapByCategory.get(categoryId));
            }
        }
        return subTotal;
    }

    private static void setSubTotalByCategory(Map<Long, BigDecimal> subTotalMapByCategory, BigDecimal price, Category category) {

        if (category == null) {
            return;
        }
        Long categoryId = category.getId();
        BigDecimal subTotalByCategory = subTotalMapByCategory.get(categoryId);
        if (subTotalByCategory == null) {
            subTotalMapByCategory.put(categoryId, new BigDecimal(0));
        }
        subTotalMapByCategory.put(categoryId, subTotalMapByCategory.get(categoryId).add(price));
        setSubTotalByCategory(subTotalMapByCategory, price, category.getParentCategory());
    }

    public Order findByRestaurantIdAndSequenceAndStatus(Long restaurantId, Long sequence, Integer status) {
        List<Order> orders = orderRepository.findByRestaurantIdAndSequenceAndStatus(restaurantId, sequence, status);
        return CollectionUtils.isEmpty(orders) ? null : orders.get(0);
    }

    @Transactional(readOnly = true)
    public List<OrderGroup> findOrderGroupsByOperator(final Date expectedArrivedDate, final AdminUser operator) {

        assert operator != null;
        final List<Long> warehouseIds = new ArrayList<>();
        for (Block block : operator.getBlocks()) {
            warehouseIds.add(block.getWarehouse().getId());
        }
        if (PermissionCheckUtils.canViewAllTracker(operator)) {
            return new ArrayList<>(Collections2.filter(findOrderGroups(expectedArrivedDate), new com.google.common.base.Predicate<OrderGroup>() {
                @Override
                public boolean apply(OrderGroup input) {
                            /*if (warehouseIds.contains(input.getWarehouse().getId())) {
                                return true;
                            } else {

                                return false;
                            }*/
                    return true;
                }
            }));

        } else {

            return new ArrayList<>(Collections2.filter(findOrderGroups(expectedArrivedDate), new com.google.common.base.Predicate<OrderGroup>() {
                @Override
                public boolean apply(OrderGroup input) {
                    if (input.getTracker() != null && input.getTracker().getId().equals(operator.getId())) {
                        return true;
                    } else {

                        return false;
                    }
                }
            }));

        }
    }

    @Transactional(readOnly = true)
    public Long getDeliveryCount(final SalesmanStatisticsRequest request) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Long> query = cb.createQuery(Long.class);
        final Root<Order> root = query.from(Order.class);

        final Specification<Order> specification = new Specification<Order>() {
            @Override
            public Predicate toPredicate(Root<Order> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (request.getStart() != null) {
                    predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Order_.expectedArrivedDate), request.getStart()));
                }
                if (request.getEnd() != null) {
                    predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Order_.expectedArrivedDate), request.getEnd()));
                }
                predicates.add(criteriaBuilder.equal(root.get(Order_.adminUser).get(AdminUser_.id), request.getAdminUserId()));
                predicates.add(criteriaBuilder.equal(root.get(Order_.status), OrderStatus.SHIPPING.getValue()));
                return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        query.select(cb.countDistinct(root.get(Order_.restaurant)));
        query.where(specification.toPredicate(root, query, cb));
        return entityManager.createQuery(query).getSingleResult();
    }

    public static Boolean getPromotionBySku(Order order, int buyQuantity, boolean bundle, Long buySkuId) {

        for (OrderItem orderItem : order.getOrderItems()) {
            if (buySkuId.equals(orderItem.getSku().getId())) {
                if (bundle) {
                    return orderItem.getBundleQuantity() >= buyQuantity;
                } else {
                    return orderItem.getSingleQuantity() >= buyQuantity;
                }
            }
        }
        return Boolean.FALSE;
    }


    @Transactional
    public static BigDecimal getOrderAmountByBrands(Order order, Long brandId) {

        BigDecimal total = new BigDecimal(0);
        Map<Long, BigDecimal> subTotalMapByBrand = new HashMap<>();
        for (OrderItem orderItem : order.getOrderItems()) {
            int quantity = orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity();
            BigDecimal singlePrice = orderItem.isBundle() ? orderItem.getPrice().divide(new BigDecimal(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP) : orderItem.getPrice();
            BigDecimal orderItemTotalPrice = singlePrice.multiply(new BigDecimal(quantity)).setScale(2,BigDecimal.ROUND_HALF_UP);
            total = total.add(orderItemTotalPrice);
            setSubTotalByBrand(subTotalMapByBrand, orderItemTotalPrice, orderItem.getSku().getProduct().getBrand().getId());
        }
        if (brandId == null) {
            return total;
        }
        BigDecimal subTotalByBrands = subTotalMapByBrand.get(brandId);
        logger.info("subTotalMapByBrand:" + subTotalMapByBrand);
        logger.info("brandId:" + brandId);
        logger.info("subTotalByCategories:" + subTotalByBrands);
        return subTotalByBrands;
    }

    private static void setSubTotalByBrand(Map<Long, BigDecimal> subTotalMapByBrand, BigDecimal price, Long brandId) {

        BigDecimal subTotalByBrand = subTotalMapByBrand.get(brandId);
        if (subTotalByBrand == null) {
            subTotalMapByBrand.put(brandId, new BigDecimal(0));
        }
        subTotalMapByBrand.put(brandId, subTotalMapByBrand.get(brandId).add(price));
    }

}
