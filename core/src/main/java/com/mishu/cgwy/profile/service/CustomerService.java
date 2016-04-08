package com.mishu.cgwy.profile.service;

import com.google.common.base.Function;
import com.google.common.collect.Maps;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.repository.BlockRepository;
import com.mishu.cgwy.common.wrapper.PointWrapper;
import com.mishu.cgwy.common.wrapper.SimpleBlockWrapper;
import com.mishu.cgwy.error.CustomerAlreadyExistsException;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.OrderItem_;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.profile.constants.CustomerQueryType;
import com.mishu.cgwy.profile.constants.RestaurantActiveType;
import com.mishu.cgwy.profile.constants.RestaurantAuditReviewType;
import com.mishu.cgwy.profile.constants.RestaurantReviewStatus;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.repository.*;
import org.apache.commons.collections.comparators.ReverseComparator;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 2/28/15
 * Time: 6:02 PM
 */
@Service
@Transactional
public class CustomerService {

    private Logger logger = LoggerFactory.getLogger(CustomerService.class);

    @Autowired
    private CustomerRepository customerRepository;

//    @Autowired
//    private RestaurantRepository restaurantRepository;

    @Autowired
    private RestaurantService restaurantService;


    @Autowired
    private RestaurantAuditReviewRepository restaurantAuditReviewRepository;

    @Autowired(required = false)
    private PasswordEncoder passwordEncoder;

    @Autowired(required = false)
    private RandomCodeValidator randomCodeValidator;

    @Autowired(required = false)
    private ISmsProvider smsProvider;

    @Autowired
    private CustomerHintRepository customerHintRepository;

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private BlockRepository blockRepository;


    public Customer register(Customer customer) {
        if (findCustomerByUsername(customer.getUsername()) != null) {
            throw new CustomerAlreadyExistsException();
        }

        // TODO: check username format, it should be a telephone number

        customer.setPassword(getReformedPassword(customer.getUsername(), customer.getPassword()));
        customer.setCreateTime(new Date());

        return customerRepository.save(customer);
    }



    /**
     * 兼容原有系统密码规则
     *
     * @param username
     * @param password
     * @return
     */
    public String getReformedPassword(String username, String password) {
//        return username + password + "mirror";
        return passwordEncoder.encode(username + password + "mirror");
    }

    public Customer update(Customer customer) {
        assert customer.getId() != null;
        return customerRepository.save(customer);
    }

    public Customer updateCustomerPassword(Customer customer, String password) {
        customer.setPassword(getReformedPassword(customer.getUsername(), password));

        return customerRepository.save(customer);
    }

    public Customer findCustomerByUsername(String username) {
        final List<Customer> list = customerRepository.findByUsername(username);
        if (list.isEmpty()) {
            return null;
        } else {
            return list.get(0);
        }
    }

    public Customer getCustomerById(Long id) {
        return customerRepository.getOne(id);
    }

    public List<Customer> findCustomerByWarehouse(final Long warehouseId) {
        return customerRepository.findAll(new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), warehouseId);
            }
        });
    }

    public List<Customer> findCustomerByCity(final Long cityId) {
        return customerRepository.findAll(new Specification<Customer>() {
            @Override
            public Predicate toPredicate(Root<Customer> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.equal(root.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id), cityId);
            }
        });

    }


    public Restaurant saveRestaurant(Restaurant restaurant) {
        if (restaurant.getCreateTime() == null) {
            restaurant.setCreateTime(new Date());
        }

        if(restaurant.getId()==null){
            restaurant.setActiveType(RestaurantActiveType.potential.val);
        }
        return restaurantService.save(restaurant);

    }

    public List<Restaurant> getRestaurantsByCustomer(Long customerId) {
        return restaurantService.findByCustomerId(customerId);

    }


    public List<Customer> getCustomerByAdminUserId(Long adminUserId) {
        return customerRepository.findByAdminUserId(adminUserId);
    }


    public Restaurant getRestaurantById(Long id) {
        return restaurantService.getOne(id);
    }

    public int getRestaurantCount(Long id) {
        List<Restaurant> restaurants = restaurantService.findByCustomerId(id);
        if (null != restaurants && restaurants.size() != 0)
            return restaurants.size();
        return 0;
    }

    public List<CustomerHint> getCustomerHintMsg(Long customerId) {
        return customerHintRepository.findByCustomerId(customerId);
    }

    public void saveComplaint(Complaint complaint) {
        complaintRepository.save(complaint);
    }

    public Complaint getComplaintNumber(Long customerId, Long adminId) {
        return complaintRepository.findByCustomerIdAndAdminId(customerId, adminId);

    }

    @Transactional(readOnly = true)
    public List<Restaurant> findAlarmRestaurant(List<Long> restaurantIds, RestaurantQueryRequest request, final AdminUser adminUser) {

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> root = query.from(Restaurant.class);
        query.select(root);

        query.where(root.get(Restaurant_.id).in(restaurantIds), JpaUtils.getPredicate(cb, root, adminUser, request));

        return entityManager.createQuery(query).getResultList();

    }

    @Transactional(readOnly = true)
    public List<Tuple> findAlarmRestaurantIdAndCount(RestaurantQueryRequest request) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
        final Root<OrderItem> root = query.from(OrderItem.class);
        root.join(OrderItem_.order);

        Date date = new Date();
        if (request.getOrderDate() != null) {
            date = request.getOrderDate();
        }

        query.multiselect(root.get(OrderItem_.order).get(Order_.restaurant).get(Restaurant_.id), cb.count(root.get(OrderItem_.order).get(Order_.id)));

        List<Predicate> predicates = new ArrayList<>();

        predicates.add(cb.greaterThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), DateUtils.truncate(date, Calendar.DATE)));
        predicates.add(cb.lessThanOrEqualTo(root.get(OrderItem_.order).get(Order_.submitDate), DateUtils.addDays(DateUtils.truncate(date, Calendar.DATE), 1)));
        predicates.add(root.get(OrderItem_.order).get(Order_.status).in(OrderStatus.COMMITTED.getValue(), OrderStatus.DEALING.getValue(),
                OrderStatus.SHIPPING.getValue(),
                OrderStatus.COMPLETED.getValue()));


        query.where(predicates.toArray(new Predicate[predicates.size()]));


        query.groupBy(root.get(OrderItem_.order).get(Order_.restaurant).get(Restaurant_.id),
                root.get(OrderItem_.sku).get(Sku_.id));

        query.having(cb.greaterThan(cb.count(root.get(OrderItem_.order).get(Order_.id)), 1L));

        return entityManager.createQuery(query).getResultList();
    }

    @Transactional(readOnly = true)
    public Restaurant findRestaurant(String phone ){

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Restaurant> query = cb.createQuery(Restaurant.class);
        final Root<Restaurant> root = query.from(Restaurant.class);

        query.select(root);
        query.where(cb.equal(root.get(Restaurant_.telephone), phone));

        return entityManager.createQuery(query).getSingleResult();

//        restaurantRepository.findOne(new Specification<Restaurant>() {
//            @Override
//            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//                query.where();
//
//                return null;
//            }
//        });

    }
//    @Transactional(readOnly = true)
//    public List<RestaurantAuditReview> findRestaurantAuditReview(final List<Long> restaurantIds,final AdminUser adminUser){
//
//        List<RestaurantAuditReview> auditReviews = restaurantAuditReviewRepository.findAll(new Specification<RestaurantAuditReview>() {
//            @Override
//            public Predicate toPredicate(Root<RestaurantAuditReview> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//
//                Predicate predicate = root.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id).in(restaurantIds);
//
//                Subquery<Long> subQuery = query.subquery(Long.class);
//                Root<RestaurantAuditReview> subRoot = subQuery.from(RestaurantAuditReview.class);
//
//                subQuery.where(
//                        cb.equal(root.get(RestaurantAuditReview_.id),subRoot.get(RestaurantAuditReview_.id)),
//
//                );
//                subQuery.groupBy(subRoot.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id));
//                predicates.add(cb.exists(subQuery.select(cb.(subRoot.get(restaurantauditre)))));
//
//
//
//                return null;
//            }
//        });
//        return auditReviews;
//    }

    @Transactional(readOnly = true)
    public Page<Restaurant> findRestaurants(final RestaurantQueryRequest request, final AdminUser adminUser) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        return restaurantService.findAll(new Specification<Restaurant>() {
            @Override
            public Predicate toPredicate(Root<Restaurant> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                final LinkedList<javax.persistence.criteria.Order> orders = new LinkedList<>();
                if (query.getResultType().equals(Restaurant.class)) {
//                    Fetch<Restaurant, Customer> customerFetch = root.fetch(Restaurant_.customer);
//                    customerFetch(Customer_.city, JoinType.LEFT);
//                    customerFetch.fetch(Customer_.block, JoinType.LEFT);

                    Join<Restaurant, Customer> customerJoin = root.join(Restaurant_.customer,JoinType.LEFT);
                    Join<Customer, Block> blockjoin =customerJoin.join(Customer_.block,JoinType.LEFT);
                    Join<Block, City> cityJoin = blockjoin.join(Block_.city,JoinType.LEFT);
                    Join<Block, Warehouse> warehouseJoin = blockjoin.join(Block_.warehouse,JoinType.LEFT);

                    Join<Customer, AdminUser> adminUserJoin = customerJoin.join(Customer_.adminUser,JoinType.LEFT);
                    Join<Customer, AdminUser> devUserJoin = customerJoin.join(Customer_.devUser,JoinType.LEFT);



                    if (request.getSortField() != null) {
                        switch (request.getSortField()) {
                            case "lastPurchaseTime":
                                if (request.isAsc()) {
                                    orders.push(cb.desc(cb.selectCase().when(cb.isNull(root.get(Restaurant_.lastPurchaseTime)), root.get(Restaurant_.createTime)).otherwise(root.get(Restaurant_.lastPurchaseTime))));
//                                    query.orderBy(cb.desc(cb.selectCase().when(cb.isNull(root.get(Restaurant_.lastPurchaseTime)), root.get(Restaurant_.createTime)).otherwise(root.get(Restaurant_.lastPurchaseTime))));
                                } else {
                                    orders.push(cb.asc(cb.selectCase().when(cb.isNull(root.get(Restaurant_.lastPurchaseTime)), root.get(Restaurant_.createTime)).otherwise(root.get(Restaurant_.lastPurchaseTime))));
//                                    query.orderBy(cb.asc(cb.selectCase().when(cb.isNull(root.get(Restaurant_.lastPurchaseTime)), root.get(Restaurant_.createTime)).otherwise(root.get(Restaurant_.lastPurchaseTime))));
                                }

                                break;
                            case "consumption":

                                final ListJoin<Restaurant, Order> join = root.join(Restaurant_.orders, JoinType.LEFT);
                                final Expression<BigDecimal> sum = cb.sum(join.get(Order_.total));

                                join.on(join.get(Order_.status).in(
                                        OrderStatus.COMMITTED.getValue(),
                                        OrderStatus.DEALING.getValue(),
                                        OrderStatus.SHIPPING.getValue(),
                                        OrderStatus.COMPLETED.getValue()));
                                query.groupBy(root);

                                if (request.isAsc()) {
                                    orders.push(cb.asc(sum));
                                } else {
                                    orders.push(cb.desc(sum));
                                }

                                break;
                            case "name":
                                if (request.isAsc()) {
                                    orders.push(cb.asc(root.get(Restaurant_.name)));
                                } else {
                                    orders.push(cb.desc(root.get(Restaurant_.name)));
                                }
                                break;

                            case "address":
                                if (request.isAsc()) {
                                    orders.push(cb.asc(root.get(Restaurant_.address)));
                                } else {
                                    orders.push(cb.desc(root.get(Restaurant_.address)));
                                }
                                break;

                            case "adminUser":
                                if (request.isAsc()) {
                                    orders.push(cb.asc(root.get(Restaurant_.customer).get(Customer_.adminUser)));
                                } else {
                                    orders.push(cb.desc(root.get(Restaurant_.customer).get(Customer_.adminUser)));
                                }
                                break;

                            default:
                                if (request.isAsc()) {
                                    orders.push(cb.asc(root.get(Restaurant_.id)));
                                } else {
                                    orders.push(cb.desc(root.get(Restaurant_.id)));
                                }
                                break;
                        }
                    }

                }

                List<Predicate> predicates = new ArrayList<Predicate>();

                CustomerQueryType customerQueryType = CustomerQueryType.find(request.getQueryType());
                predicates.add(JpaUtils.getPredicate(cb, root, adminUser, request));

                //客户公海
                if(customerQueryType == CustomerQueryType.sea) {

                    //没有销售  且  没有申请认领的记录查询出来
                    Predicate predicate2 = cb.isNull(root.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id));
                    Subquery<RestaurantAuditReview> subQuery = query.subquery(RestaurantAuditReview.class);
                    Root<RestaurantAuditReview> subRoot = subQuery.from(RestaurantAuditReview.class);
                    subQuery.where(
                            cb.equal(root.get(Restaurant_.id), subRoot.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id)),
                            subRoot.get(RestaurantAuditReview_.reqType).in(RestaurantAuditReviewType.claim.val,RestaurantAuditReviewType.seaBack.val),
                            cb.equal(subRoot.get(RestaurantAuditReview_.status), RestaurantReviewStatus.NOT_CHECK.val)
                    );
                    Predicate predicate3 = cb.not(cb.exists(subQuery.select(subRoot)));
                    Predicate predicate4 = cb.not(root.get(Restaurant_.status).in(RestaurantStatus.INACTIVE.getValue()));
                    predicates.add(cb.and(predicate2,predicate3,predicate4));

                }

                //我的客户
                if(customerQueryType == CustomerQueryType.My) {

                    Predicate predicate1 = root.get(Restaurant_.customer).get(Customer_.adminUser).in(adminUser);
                    //同时要查询出
                    Subquery<RestaurantAuditReview> subQuery = query.subquery(RestaurantAuditReview.class);
                    Root<RestaurantAuditReview> subRoot = subQuery.from(RestaurantAuditReview.class);
                    subQuery.where(
                            cb.equal(root.get(Restaurant_.id), subRoot.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id)),
                            subRoot.get(RestaurantAuditReview_.reqType).in(RestaurantAuditReviewType.claim.val),
                            subRoot.get(RestaurantAuditReview_.createUser).in(adminUser),
                            cb.equal(subRoot.get(RestaurantAuditReview_.status), RestaurantReviewStatus.NOT_CHECK.val)
                    );
                    Predicate predicate2= cb.exists(subQuery.select(subRoot));
                    predicates.add(cb.or(predicate1,predicate2));
                    orders.push(cb.asc(root.get(Restaurant_.customer).get(Customer_.adminUser)));
                }

                query.orderBy(orders);
                return cb.and(predicates.toArray(new Predicate[]{}));
            }
        }, pageable);
    }

//    /**
//     * 查客户公海列表
//     * @param request
//     * @param operater
//     * @return
//     */
//    public Page<Restaurant> findAllBySeaReqeust(final RestaurantQueryRequest request, final AdminUser operater) {
//        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), request.isAsc()? Sort.Direction.ASC:Sort.Direction.DESC,request.getSortField());
//
////      final RestaurantInfoQuerySpecification infoQuerySpecification = new RestaurantInfoQuerySpecification(request);
//        Specification<Restaurant> specification = new Specification() {
//            @Override
//            public Predicate toPredicate(Root root, CriteriaQuery query, CriteriaBuilder cb) {
//                Join<Restaurant, Customer> customerJoin = root.join(Restaurant_.customer,JoinType.LEFT);
//                Join<Customer, Block> blockjoin =customerJoin.join(Customer_.block,JoinType.LEFT);
//                Join<Block, City> cityJoin = blockjoin.join(Block_.city,JoinType.LEFT);
//                Join<Block, Warehouse> warehouseJoin = blockjoin.join(Block_.warehouse,JoinType.LEFT);
//
//                Join<Customer, AdminUser> adminUserJoin = customerJoin.join(Customer_.adminUser,JoinType.LEFT);
//                Join<Customer, AdminUser> devUserJoin = customerJoin.join(Customer_.devUser,JoinType.LEFT);
//
//                Predicate predicate =JpaUtils.getPredicate(cb,root,operater,request);
//
//                //没有销售  且  没有申请认领的记录查询出来
//                Predicate predicate2 = cb.isNull(root.get(Restaurant_.customer).get(Customer_.adminUser).get(AdminUser_.id));
//                Subquery<RestaurantAuditReview> subQuery = query.subquery(RestaurantAuditReview.class);
//                Root<RestaurantAuditReview> subRoot = subQuery.from(RestaurantAuditReview.class);
//                subQuery.where(
//                        cb.equal(root.get(Restaurant_.id), subRoot.get(RestaurantAuditReview_.restaurant).get(Restaurant_.id)),
//                        subRoot.get(RestaurantAuditReview_.reqType).in(RestaurantAuditReviewType.claim.val,RestaurantAuditReviewType.seaBack.val),
//                        cb.equal(subRoot.get(RestaurantAuditReview_.status), RestaurantReviewStatus.NOT_CHECK.val)
//                );
//                Predicate predicate3 = cb.not(cb.exists(subQuery.select(subRoot)));
//                Predicate predicate4 = cb.not(root.get(Restaurant_.status).in(RestaurantStatus.INACTIVE.getValue()));
//
//                return cb.and( predicate, predicate2, predicate3, predicate4);
//            }
//        };
//
//        return this.restaurantService.findAll(specification,pageable);
//
////        return this.findRestaurantInfoVos(request, specification, operater);
//    }

    @Transactional
    public Favorite addFavorite(Customer customer, Sku sku) {
        List<Favorite> favorites = favoriteRepository.findByCustomerId(customer.getId());
        for (Favorite favorite : favorites) {
            if (favorite.getSku().getId().equals(sku.getId())) {
                favorite.setUpdateTime(new Date());
                return favorite;
            }
        }

        Favorite favorite = new Favorite();
        favorite.setSku(sku);
        favorite.setCustomer(customer);
        favorite.setUpdateTime(new Date());

        return favoriteRepository.save(favorite);
    }

    @Transactional
    public void deleteFavorite(Customer customer, Sku sku) {
        List<Favorite> favorites = favoriteRepository.findByCustomerId(customer.getId());
        for (Favorite favorite : favorites) {
            if (favorite.getSku().getId().equals(sku.getId())) {
                favoriteRepository.delete(favorite);
            }
        }
    }

    @Transactional
    public List<Favorite> getFavorites(Customer customer) {
        final List<Favorite> list = favoriteRepository.findByCustomerId(customer.getId());
        Collections.sort(list, new ReverseComparator(new Comparator<Favorite>() {
            @Override
            public int compare(Favorite o1, Favorite o2) {
                if (o1.getUpdateTime() == null)
                    return 1;
                if (o2.getUpdateTime() == null)
                    return -1;

                return o1.getUpdateTime().compareTo(o2.getUpdateTime());
            }
        }));

        return list;
    }

    @Transactional(readOnly = true)
    public Map<Long, Long> getComplaintCountGroupByAdminUser(Date start, Date end) {
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<Tuple> query = cb.createTupleQuery();
        final Root<Complaint> root = query.from(Complaint.class);

        query.multiselect(root.get(Complaint_.adminId), cb.count(root.get(Complaint_.id)));

        query.where(cb.greaterThanOrEqualTo(root.get(Complaint_.createTime), start),
                cb.lessThanOrEqualTo(root.get(Complaint_.createTime), end));

        query.groupBy(root.get(Complaint_.adminId));

        final List<Tuple> resultList = entityManager.createQuery(query).getResultList();
        Map<Long, Long> result = new HashMap<>();
        for (Tuple tuple : resultList) {
            result.put(((Long) tuple.get(0)), (Long) tuple.get(1));
        }

        return result;

    }


    public Map<Long, Long> getNewRestaurantCountGroupByAdminUser(Date start, Date end) {
        RestaurantQueryRequest request = new RestaurantQueryRequest();
        request.setStart(start);
        request.setEnd(end);
        request.setStatus(RestaurantStatus.ACTIVE.getValue());
        request.setPageSize(Integer.MAX_VALUE);
        final Page<Restaurant> restaurants = findRestaurants(request, null);

        Map<Long, Set<Long>> map = new HashMap<>();
        for (Restaurant r : restaurants) {
            if (r.getStatus() == RestaurantStatus.ACTIVE.getValue()) {
                final AdminUser adminUser = r.getCustomer().getAdminUser();
                if (adminUser != null) {
                    if (map.containsKey(adminUser.getId())) {
                        map.get(adminUser.getId()).add(r.getId());
                    } else {
                        Set<Long> set = new HashSet<>();
                        set.add(r.getId());
                        map.put(adminUser.getId(), set);
                    }
                }
            }
        }

        return Maps.transformValues(map, new Function<Set<Long>, Long>() {
            @Override
            public Long apply(Set<Long> input) {
                return Long.valueOf(input.size());
            }
        });

    }

//    public RestaurantStatus autoDefined(Restaurant restaurant){
//        //根据数据，计算出在自动审核时应该表现的状态
//        Block block =restaurant.getCustomer().getBlock();
//        if(block!=null){
//           return RestaurantStatus.UNDEFINED;
//        }
//        List<Block> blocks = this.blockRepository.findByCityId(restaurant.getCustomer().getCity().getId());
//        if(blocks==null || blocks.size()==0){
//            //该城市还没有划定区块时 不做区块判断
//            return RestaurantStatus.UNDEFINED;
//        }
//
//        return RestaurantStatus.OUTSIDE;
//    }

    /**
     * 检查是否在服务区内，
     * @return
     */
    public boolean checkInServiceArea(Double xp, Double yp, Long cityId){
        List<Block> blocks = this.blockRepository.findByCityId(cityId);

        if(blocks==null || blocks.size()==0 || this.blockIsEmpty(blocks)){
            return true;
        }

        return this.reckonBlock(xp,yp,blocks)!=null;
    }

    /**
     * 检查区块是不是都是空的
     */
    public boolean blockIsEmpty(List<Block> blocks){
        for(Block block : blocks){
            if(StringUtils.isNotBlank(block.getPointStr())){
                return false;
            }
        }
        return true;
    }

    /**
     * 计算出坐标对应的区块
     */
    public Block reckonBlock(Double lng, Double lat, List<Block> blocks) {

        if(blocks==null || blocks.size()==0 || this.blockIsEmpty(blocks)){
            return null;
        }
        for(Block block : blocks){
            boolean isthis =  this.checkpoint(lng,lat,block);
            if(isthis){
                return block;
            }
        }
        return null;
    }

    /**
     * 计算出坐标对应的区块
     */
    public Block reckonBlock(Double lng, Double lat, Long cityId) {


        List<Block> blocks = this.blockRepository.findByCityIdAndActive(cityId, true);

        return this.reckonBlock(lng,lat,blocks);
    }

    /**
     * 检查坐标是否在区块内
     * @param lng
     * @param lat
     * @param block
     * @return
     */
    public boolean checkpoint(  Double lng, Double lat, Block block) {
        try {
            SimpleBlockWrapper sbwrapper = new SimpleBlockWrapper(block);
            List<PointWrapper> points = sbwrapper.getPoints();

            if (points == null || points.size() == 0) {
                return false;
            }

            //PNPoly 算法 计算一点是否在多边形内部
            boolean result = false;
            for (int i = 0, j = points.size() - 1; i < points.size(); j = i++) {
                Double ilng = Double.valueOf(points.get(i).getLongitude());
                Double ilat = Double.valueOf(points.get(i).getLatitude());
                Double jlng = Double.valueOf(points.get(j).getLongitude());
                Double jlat = Double.valueOf(points.get(j).getLatitude());

                if (((ilat > lat) != (jlat > lat)) && (lng < (jlng - ilng) * (lat - ilat) / (jlat - ilat) + ilng)) {
                    result = !result;
                }
            }

            return result;
        }catch(Exception ex){
            logger.error(String.format("坐标点区块判别失败 blockid:%s",block.getId()),ex);
            return false;
        }
    }

//    public static void main(String[] args) {
//
//        CustomerService customerService=new CustomerService();
//        Block block =new Block();
//        block.setPointStr("116.222883,40.174429;116.248179,39.836638;116.282674,40.017238;116.482745,39.813586;116.505741,39.997785;116.413755,40.1709;116.298772,40.084398;116.220583,40.1709");
//
//        int cnt = 0;
//        while(true) {
//            long begin = System.currentTimeMillis();
//            boolean cpoint = customerService.checkpoint(116.397657, 40.084398, block);
//
//
//            System.out.println(cpoint + "---" + (System.currentTimeMillis() - begin));
//
//            if(cnt++>10){
//                break;
//            }
//        }
//    }

    public static void main(String[] args) {
        System.out.println(";".split(";").length);
    }


}