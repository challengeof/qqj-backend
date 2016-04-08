package com.mishu.cgwy.coupon.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.*;
import com.mishu.cgwy.common.repository.CityRepository;
import com.mishu.cgwy.common.repository.WarehouseRepository;
import com.mishu.cgwy.coupon.CouponUtils;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.constant.CouponRuleConstant;
import com.mishu.cgwy.coupon.controller.CouponListRequest;
import com.mishu.cgwy.coupon.controller.CouponRequest;
import com.mishu.cgwy.coupon.controller.CouponStatisticsRequest;
import com.mishu.cgwy.coupon.controller.SendCouponRequest;
import com.mishu.cgwy.coupon.domain.*;
import com.mishu.cgwy.coupon.repository.CouponRepository;
import com.mishu.cgwy.coupon.repository.CustomerCouponRepository;
import com.mishu.cgwy.coupon.vo.CouponStatisticsDiscountVo;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedDetailWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsUsedWrapper;
import com.mishu.cgwy.coupon.wrapper.CouponStatisticsWrapper;
import com.mishu.cgwy.error.CouponCanceledModifyException;
import com.mishu.cgwy.message.CouponSenderEnum;
import com.mishu.cgwy.message.PromotionMessage;
import com.mishu.cgwy.message.PromotionMessageSender;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.repository.OrderRepository;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.CouponWrapper;
import com.mishu.cgwy.order.wrapper.CustomerCouponWrapper;
import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.profile.constants.PromotionConstants;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.promotion.repository.PromotionRepository;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOut_;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.ExpressionUtils;
import com.mishu.cgwy.utils.JpaQueryUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.time.DateUtils;
import org.elasticsearch.common.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by bowen on 15-6-23.
 */
@Service
public class CouponService {

    private static Logger LOG = LoggerFactory.getLogger(CouponService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerCouponRepository customerCouponRepository;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private PromotionRepository promotionRepository;

    @Autowired(required=false)
    private SkuService skuService;

    @Autowired(required=false)
    private CustomerService customerService;

    @Autowired
    private ShareService shareService;

    @Autowired(required=false)
    private PromotionMessageSender promotionMessageSender;

    @Autowired(required=false)
    private RestaurantService restaurantService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WarehouseRepository warehouseRepository;

    @Autowired
    private OrderService orderService;


    public final static String CUSTOMER_COUPON_LIST				    ="/template/customer_coupon-list.xls"           ;
    public final static String CUSTOMER_COUPON_STATISTICS_LIST		="/template/customer_coupon-statistics-list.xls";
    public final static String CUSTOMER_COUPON_SEND_LIST			    ="/template/customer_coupon-send-list.xls"      ;
    public final static String CUSTOMER_COUPON_USEDDETAIL_LIST		="/template/customer_coupon-usedDetail-list.xls";


    /**
     * 优惠券使用明细- 优惠券使用金额合计
     */
    public BigDecimal getSumDiscountByUsedDetail(final CouponStatisticsRequest request){
        return JpaQueryUtils.valSelect(CustomerCoupon.class, new UsedDetailSpecification(request), this.entityManager, new JpaQueryUtils.SelectPathGetting<CustomerCoupon, BigDecimal,UsedDetailSpecification>() {

            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<CustomerCoupon> root, UsedDetailSpecification specification) {
                return new Selection<?>[]{cb.sum(root.get(CustomerCoupon_.coupon).get(Coupon_.discount))};
            }

            @Override
            public BigDecimal resultWrappe(List<Tuple> tuples) {
                BigDecimal val = new BigDecimal(0);
                for(Tuple tuple : tuples){
                    BigDecimal ctupleNumber =tuple.get(0, BigDecimal.class);
                    if(ctupleNumber!=null){
                        val=val.add(ctupleNumber);
                    }
                }
                return val;
            }
        });
    }

    /**
     * 优惠券使用明细 - 列表数据
     */
    public Page<CouponStatisticsUsedDetailWrapper> getCouponStatisticsByUsedDetail(final CouponStatisticsRequest request){

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField())
        );

        request.setCouponStatus(CouponStatus.USED.getValue());
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<CustomerCoupon> root = query.from(CustomerCoupon.class);
//        final SetJoin<CustomerCoupon, Order> orderJoin = root.join(CustomerCoupon_.orders,JoinType.INNER);
        UsedDetailSpecification spec = new UsedDetailSpecification(request);
        query.where(spec.toPredicate(root, query, cb));
        Path<Restaurant> restaurantPath = spec.getOrderJoin().get(Order_.restaurant);
        Join<Order, StockOut> stockOutJoin = spec.getStockOutJoin();
        query.multiselect(
                root.get(CustomerCoupon_.useDate),
                spec.getOrderJoin().get(Order_.submitDate),
                stockOutJoin.get(StockOut_.receiveDate),

                spec.getOrderJoin().get(Order_.id),
                restaurantPath.get(Restaurant_.id),
                restaurantPath.get(Restaurant_.name),
                root.get(CustomerCoupon_.coupon).get(Coupon_.id),
                root.get(CustomerCoupon_.coupon).get(Coupon_.name),
                root.get(CustomerCoupon_.coupon).get(Coupon_.couponConstants),
                cb.selectCase().when(cb.isNull(cb.sum(root.get(CustomerCoupon_.coupon).get(Coupon_.discount))),new BigDecimal(0))
                        .otherwise(cb.sum(root.get(CustomerCoupon_.coupon).get(Coupon_.discount))),
                spec.getOrderJoin().get(Order_.subTotal),
                root.get(CustomerCoupon_.remark)
        );
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize());
        List<CouponStatisticsUsedDetailWrapper> datas = new ArrayList<>();
        List<Tuple> tuples = typedQuery.getResultList();
        for (Tuple tuple : tuples) {
            CouponStatisticsUsedDetailWrapper csudetail = new CouponStatisticsUsedDetailWrapper(
                    tuple.get(0,Date.class),tuple.get(1,Date.class),tuple.get(2,Date.class),tuple.get(3,Long.class),tuple.get(4,Long.class),
                    tuple.get(5,String.class),tuple.get(6,Long.class),tuple.get(7,String.class),tuple.get(8,Integer.class),
                    tuple.get(9,BigDecimal.class),tuple.get(10,BigDecimal.class),tuple.get(11,String.class)
            );
            datas.add(csudetail);
        }

        long lineCnt = JpaQueryUtils.lineCount(CustomerCoupon.class, new UsedDetailSpecification(request), entityManager);
        Page<CouponStatisticsUsedDetailWrapper> result = new PageImpl<>(datas, pageRequest, lineCnt);
        return result;
    }


    /**
     * 一张发送的优惠券置为作废
     * @param customerCouponId
     */
    @Transactional(rollbackFor=Exception.class)
    public CustomerCouponWrapper customerCouponCancelledSetting(Long customerCouponId, AdminUser operater) {
        try {
            int resultCnt = this.customerCouponRepository.updateStatus(customerCouponId, CouponStatus.UNUSED.getValue(), CouponStatus.CANCELLED.getValue());
            CustomerCoupon ccoupon = this.customerCouponRepository.getOne(customerCouponId);
            ccoupon.setOperater(operater);
            ccoupon.setOperateTime(new Date());
            this.customerCouponRepository.save(ccoupon);
            if(resultCnt==0){
                throw new Exception(String.format("result count = %s",resultCnt));
            }
            return new CustomerCouponWrapper(ccoupon);
        }catch(Exception ex){
            throw new CouponCanceledModifyException(ex);
        }
    }
    @Transactional(rollbackFor=Exception.class)
    public void expirdCustomerCoupon(Date beginDate) {
        this.customerCouponRepository.updateStatusByExpire(CouponStatus.UNUSED.getValue(),CouponStatus.EXPIRED.getValue(),new Date(),beginDate);
    }

    private class UsedDetailSpecification implements  Specification<CustomerCoupon>{
        protected CouponStatisticsRequest request;
        protected SetJoin<CustomerCoupon, Order> orderJoin;
        protected Join<Order, StockOut> stockOutJoin;
        public UsedDetailSpecification(CouponStatisticsRequest request){
            this.request=request;
        }
        public SetJoin<CustomerCoupon, Order> getOrderJoin() {
            return orderJoin;
        }
        public Join<Order, StockOut> getStockOutJoin() {
            return stockOutJoin;
        }

        public Predicate toPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

            orderJoin =root.join(CustomerCoupon_.orders, JoinType.INNER);

            stockOutJoin =orderJoin.join(Order_.stockOuts, JoinType.LEFT);

            query.groupBy(orderJoin.get(Order_.id),root.get(CustomerCoupon_.id) );
            Path<Customer> customerPath =root.get(CustomerCoupon_.customer);
            Path<Coupon> couponPath =root.get(CustomerCoupon_.coupon);
            List<Predicate> predicates = new ArrayList<>();

            if(null != request.getOrderId()){
                predicates.add( cb.equal(orderJoin.get(Order_.id), request.getOrderId()) );
            }
            if(null!=request.getCityId()){
                predicates.add(cb.equal(customerPath.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id), request.getCityId()));
            }
            if(null!=request.getWarehouseId()   ){
                predicates.add(cb.equal(customerPath.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id),request.getWarehouseId()));
            }
            if(null!=request.getRestaurantName()){
                predicates.add(cb.like(orderJoin.get(Order_.restaurant).get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
            }
            if( null!=request.getRestaurantId()){
                predicates.add(cb.equal(orderJoin.get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
            }
            if(null!=request.getCouponType()    ){
                predicates.add(cb.equal(couponPath.get(Coupon_.couponConstants),request.getCouponType()));
            }
            if(null!=request.getCouponStatus()  ){
                predicates.add(cb.equal(root.get(CustomerCoupon_.status), request.getCouponStatus()));
            }
            if(null!=request.getSendFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.sendDate), request.getSendFront()));
            }
            if(null!=request.getSendBack()     ){
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.sendDate),request.getSendBack()));
            }
            if(null!=request.getStartFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.start), request.getStartFront()));
            }
            if(null!=request.getStartBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.start), request.getStartBack()));
            }

            if(null!=request.getUseFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.useDate), request.getUseFront()));
            }
            if(null!=request.getUseBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.useDate), request.getUseBack()));
            }

            if(null!=request.getOrderDateFront()){
                predicates.add(cb.greaterThanOrEqualTo(orderJoin.get(Order_.submitDate), request.getOrderDateFront()));
            }
            if(null!=request.getOrderDateBack()){
                predicates.add(cb.lessThan(orderJoin.get(Order_.submitDate), request.getOrderDateBack()));
            }

            if(null!=request.getStockoutDateFront()){
                predicates.add(cb.greaterThanOrEqualTo(stockOutJoin.get(StockOut_.receiveDate), request.getStockoutDateFront()));
            }
            if(null!=request.getStockoutDateBack()){
                predicates.add(cb.lessThan(stockOutJoin.get(StockOut_.receiveDate), request.getStockoutDateBack()));
            }

            if(null!=request.getEndFront() ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.end),request.getEndFront()));
            }
            if(null!=request.getEndBack()  ){
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.end), request.getEndBack()));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        }
    };


    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportCustomerCoupon(final CouponStatisticsRequest request, List datas,String fileName, String template) throws Exception {

        Map<String, Object> beans = new HashMap<>();
        beans.put("list", datas);
        beans.put("city", request.getCityId() == null ? "全部" : cityRepository.getOne(request.getCityId()).getName());
        beans.put("warehouse", request.getWarehouseId() == null ? "全部" : warehouseRepository.getOne(request.getWarehouseId()).getName());
        beans.put("restaurantId",request.getRestaurantId());
        beans.put("restaurantName",request.getRestaurantName());
        beans.put("couponType",request.getCouponType()==null?"":CouponConstant.getCouponConstantByType(request.getCouponType()).getName());

        beans.put("startFront", request.getStartFront());
        beans.put("startBack",request.getStartBack());
        beans.put("sendFront", request.getSendFront());
        beans.put("sendback", request.getSendBack());
        beans.put("endFront",request.getEndFront());
        beans.put("endBack",request.getEndBack());
        beans.put("couponStatus",request.getCouponStatus()==null?"":CouponStatus.fromInt(request.getCouponStatus()).getName());
        beans.put("now", new Date());

        return ExportExcelUtils.generateExcelBytes(beans, fileName, template);
    }

    /**
     * 优惠券使用统计
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public List<CouponStatisticsUsedWrapper> getCouponStatisticsByUsed(final CouponStatisticsRequest request){
        //查上期优惠券金额统计
        CouponStatisticsRequest preRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),null,request.getStartFront(),null,request.getUseFront(),null);
        Map<String, CouponStatisticsDiscountVo> preSum = this.getCouponStatisticsDiscountSum(preRequest); //上期发送
        CouponStatisticsRequest preUsedRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),null,request.getStartFront(),null,request.getUseFront(),CouponStatus.USED.getValue());
        Map<String, CouponStatisticsDiscountVo> preUsedSum = this.getCouponStatisticsDiscountSum(preUsedRequest); //上期使用
        CouponStatisticsRequest preOverdueRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),null,request.getStartFront(),null,request.getUseFront(),CouponStatus.EXPIRED.getValue());
        Map<String, CouponStatisticsDiscountVo> preOverdueSum = this.getCouponStatisticsDiscountSum(preOverdueRequest); //上期过期

        //查本期优惠券金额统计
        CouponStatisticsRequest crtRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),request.getStartFront(),request.getStartBack(),request.getUseFront(),request.getUseBack(),null);
        Map<String, CouponStatisticsDiscountVo> crtSum =this.getCouponStatisticsDiscountSum(crtRequest); //本期期发送
        CouponStatisticsRequest crtUsedRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),request.getStartFront(),request.getStartBack(),request.getUseFront(),request.getUseBack(),CouponStatus.USED.getValue());
        Map<String, CouponStatisticsDiscountVo> crtUsedSum =this.getCouponStatisticsDiscountSum(crtUsedRequest); //本期使用
        CouponStatisticsRequest crtOverdueRequest =new CouponStatisticsRequest(request.getCityId(),request.getWarehouseId(),request.getRestaurantName(),request.getRestaurantId(),request.getStartFront(),request.getStartBack(),request.getUseFront(),request.getUseBack(),CouponStatus.EXPIRED.getValue());
        Map<String, CouponStatisticsDiscountVo> crtOverdueSum =this.getCouponStatisticsDiscountSum(crtOverdueRequest); //本期过期

        Set<String> allkey = new LinkedHashSet<>();
        allkey.addAll(preSum.keySet());
        allkey.addAll(crtSum.keySet());

        List<CouponStatisticsUsedWrapper> usedWrappers = new ArrayList<>();
        for(String key : allkey){
            String[] kys =key.split("[_]");
            String cityName= preSum.get(key)==null?crtSum.get(key).getCityName():preSum.get(key).getCityName();
            String warehouseName= preSum.get(key)==null?crtSum.get(key).getWarehouseName():preSum.get(key).getWarehouseName();
            BigDecimal cPreSum =preSum.get(key)==null?new BigDecimal(0):preSum.get(key).getDiscountAmount();
            BigDecimal cPreUsedSum =preUsedSum.get(key)==null?new BigDecimal(0):preUsedSum.get(key).getDiscountAmount();
            BigDecimal cPreOverdueSum =preOverdueSum.get(key)==null?new BigDecimal(0):preOverdueSum.get(key).getDiscountAmount();
            BigDecimal cCrtSum =crtSum.get(key)==null?new BigDecimal(0):crtSum.get(key).getDiscountAmount();
            BigDecimal cCrtUsedSum =crtUsedSum.get(key)==null?new BigDecimal(0):crtUsedSum.get(key).getDiscountAmount();
            BigDecimal cCrtOverdueSum =crtOverdueSum.get(key)==null?new BigDecimal(0):crtOverdueSum.get(key).getDiscountAmount();

            CouponStatisticsUsedWrapper usedWrapper = new CouponStatisticsUsedWrapper(
                    Long.parseLong(kys[0]), cityName,
                    Long.parseLong(kys[1]), warehouseName,
                    cPreSum,cPreUsedSum,cPreOverdueSum,cCrtSum,cCrtUsedSum,cCrtOverdueSum
            );
            usedWrappers.add(usedWrapper);
        }

        Collections.sort(usedWrappers,new Comparator<CouponStatisticsUsedWrapper>(){
            @Override
            public int compare(CouponStatisticsUsedWrapper o1, CouponStatisticsUsedWrapper o2) {
                if(o1.getCityId().equals(o2.getCityId())){
                    return 0;
                }
                return o1.getCityId()>o2.getCityId()?1:-1;
            }
        });
        return usedWrappers;
    }


    @Transactional(readOnly = true)
    public Map<String,CouponStatisticsDiscountVo> getCouponStatisticsDiscountSum(final CouponStatisticsRequest request){
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<CustomerCoupon> root = query.from(CustomerCoupon.class);
        query.where(new UsedSpecification(request).toPredicate(root,query,cb));
        query.groupBy(root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id),root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id));

        query.orderBy(cb.asc(root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id)));
        query.multiselect(
                root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id),
                root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.name),
                root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id),
                root.get(CustomerCoupon_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.name),
                cb.selectCase().when(cb.isNull(cb.sum(root.get(CustomerCoupon_.coupon).get(Coupon_.discount))),new BigDecimal(0))
                        .otherwise(cb.sum(root.get(CustomerCoupon_.coupon).get(Coupon_.discount)))
        );
        TypedQuery typedQuery = entityManager.createQuery(query);
        Map<String,CouponStatisticsDiscountVo> discountMp = new HashMap<>();
        List<Tuple> tuples = typedQuery.getResultList();
        for (Tuple tuple : tuples) {
            CouponStatisticsDiscountVo dvo = new CouponStatisticsDiscountVo(
                    tuple.get(0,Long.class),tuple.get(1,String.class),tuple.get(2,Long.class),
                    tuple.get(3,String.class),tuple.get(4,BigDecimal.class)
            );
            discountMp.put(org.apache.commons.lang.StringUtils.join(new Object[]{dvo.getCityId(),dvo.getWarehouseId()},"_"),dvo);
        }
        return discountMp;
    }

    private class UsedSpecification implements Specification<CustomerCoupon> {
        private CouponStatisticsRequest request;

        public UsedSpecification(CouponStatisticsRequest request) {
            this.request = request;
        }
        @Override
        public Predicate toPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            Path<Customer> customerPath = root.get(CustomerCoupon_.customer);
            List<Predicate> predicates = new ArrayList<>();
            if(null!=request.getCityId()){

                predicates.add(cb.equal(customerPath.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id), request.getCityId()));
            }
            if(null!=request.getWarehouseId()   ){
                predicates.add(cb.equal(customerPath.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id),request.getWarehouseId()));
            }

            if(null!=request.getSendFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.sendDate), request.getSendFront()));
            }
            if(null!=request.getSendBack()     ){
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.sendDate),request.getSendBack()));
            }
            if(null!=request.getStartFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.start), request.getStartFront()));
            }
            if(null!=request.getStartBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.start), request.getStartBack()));
            }
            if(null!=request.getUseFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.useDate), request.getUseFront()));
            }
            if(null!=request.getUseBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.useDate), request.getUseBack()));
            }

            if(null!=request.getCouponStatus()  ){
                predicates.add(cb.equal(root.get(CustomerCoupon_.status), request.getCouponStatus()));
            }
            return cb.and(predicates.toArray(new Predicate[]{}));
        }
    };

    /**
     * 发放优惠券金额合计
     * @param request
     * @return
     */
    public CouponStatisticsWrapper getCouponStatisticsSum(final CouponStatisticsRequest request){
        return JpaQueryUtils.valSelect(CustomerCoupon.class, new CouponStatisticsSpecification(request), entityManager, new JpaQueryUtils.SelectPathGetting<CustomerCoupon, CouponStatisticsWrapper, CouponStatisticsSpecification>(){
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<CustomerCoupon> root, CouponStatisticsSpecification specification) {
                return new Selection[]{root.get(CustomerCoupon_.coupon).get(Coupon_.discount)};
            }
            @Override
            public CouponStatisticsWrapper resultWrappe(List<Tuple> tuples) {
                BigDecimal val = new BigDecimal(0);
                for(Tuple tuple : tuples){
                    BigDecimal ctupleNumber =tuple.get(0, BigDecimal.class);
                    if(ctupleNumber!=null){
                        val=val.add(ctupleNumber);
                    }
                }
                return new CouponStatisticsWrapper(val);
            }
        });
    }

    /**
     * 获取优惠券统计信息
     * @return
     */
    @Transactional(readOnly = true)
    public Page<CouponStatisticsWrapper> getCouponStatistics(final CouponStatisticsRequest request){

        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField())
        );
        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery query = cb.createTupleQuery();
        final Root<CustomerCoupon> root = query.from(CustomerCoupon.class);
//        Path<Customer> customerPath =root.get(CustomerCoupon_.customer);
        Path<Coupon> couponPath =root.get(CustomerCoupon_.coupon);

        CouponStatisticsSpecification statisticsSpecification = new CouponStatisticsSpecification(request);
        Predicate predicate = statisticsSpecification.toPredicate(root, query, cb);
        ListJoin<Customer, Restaurant> listJoin = statisticsSpecification.getRestaurantListJoin();
        Join<CustomerCoupon, AdminUser> adminUserJoin  = root.join(CustomerCoupon_.sender,JoinType.LEFT);
        Join<CustomerCoupon, AdminUser> operaterJoin  = root.join(CustomerCoupon_.operater,JoinType.LEFT);

        query.multiselect(
                listJoin.get(Restaurant_.id),                       listJoin.get(Restaurant_.name),
                couponPath.get(Coupon_.couponConstants),            root.get(CustomerCoupon_.status),
                root.get(CustomerCoupon_.coupon).get(Coupon_.id),   root.get(CustomerCoupon_.coupon).get(Coupon_.name),
                root.get(CustomerCoupon_.sendDate),                 root.get(CustomerCoupon_.end),
                couponPath.get(Coupon_.discount),                   root.get(CustomerCoupon_.remark),
                adminUserJoin.get(AdminUser_.realname),             operaterJoin.get(AdminUser_.realname),
                root.get(CustomerCoupon_.operateTime),        root.get(CustomerCoupon_.id)
        );
        query.where(predicate);
        query.orderBy(QueryUtils.toOrders(pageRequest.getSort(),root,cb));

        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(pageRequest.getOffset());
        typedQuery.setMaxResults(pageRequest.getPageSize());

        List<Tuple> tuples = typedQuery.getResultList();
        List<CouponStatisticsWrapper> datas = new ArrayList<>();
        for (Tuple tuple : tuples) {
            datas.add(new CouponStatisticsWrapper(
                    tuple.get(0, Long.class),     tuple.get(1, String.class),
                    tuple.get(2, Integer.class),  tuple.get(3, Integer.class),
                    tuple.get(4, Long.class),     tuple.get(5, String.class),
                    tuple.get(6, Date.class),     tuple.get(7, Date.class),
                    tuple.get(8, BigDecimal.class),  tuple.get(9, String.class),
                    tuple.get(10,String.class),   tuple.get(11,String.class),
                    tuple.get(12,Date.class),     tuple.get(13,Long.class)
            ));
        }
        //查结果集数量
        long lineCnt = JpaQueryUtils.lineCount(CustomerCoupon.class, new CouponStatisticsSpecification(request), entityManager);
        Page<CouponStatisticsWrapper> result = new PageImpl<CouponStatisticsWrapper>(datas, pageRequest, lineCnt);
        return result;
    }


    private class CouponStatisticsSpecification implements  Specification<CustomerCoupon> {
        private CouponStatisticsRequest request;
        private ListJoin<Customer, Restaurant> restaurantListJoin;
        public ListJoin<Customer, Restaurant> getRestaurantListJoin() {
            return restaurantListJoin;
        }
        public CouponStatisticsSpecification(CouponStatisticsRequest request ){
            this.request=request;
        }

        //        private Predicate getMainRestaurantSubQueryPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> query, CriteriaBuilder cb){
//
//            //计算出此客户的首个有效餐馆
//            Subquery<Long> isMainRestaurantQuery1 = query.subquery(Long.class);
//            Root<Restaurant> isMainRestaurantQuerySubRoot1 = isMainRestaurantQuery1.from(Restaurant.class);
//            List<Predicate> predicates = new ArrayList<>();
//            predicates.add(cb.equal(root.get(CustomerCoupon_.customer).get(Customer_.id), isMainRestaurantQuerySubRoot1.get(Restaurant_.customer).get(Customer_.id)));
//            predicates.add(cb.equal(isMainRestaurantQuerySubRoot1.get(Restaurant_.status), RestaurantStatus.ACTIVE.getValue()));
//            isMainRestaurantQuery1.where(cb.and(predicates.toArray(new Predicate[]{})));
//            isMainRestaurantQuery1.groupBy(isMainRestaurantQuerySubRoot1.get(Restaurant_.customer).get(Customer_.id));
//            Subquery<Long> isMainRestaurant = isMainRestaurantQuery1.select(isMainRestaurantQuerySubRoot1.get(Restaurant_.id));
//
////            Subquery<Long> isMainRestaurantQuery2 = query.subquery(Long.class);
////            Root<Restaurant> isMainRestaurantQuerySubRoot2 = isMainRestaurantQuery2.from(Restaurant.class);
////            List<Predicate> predicates2 = new ArrayList<>();
////            predicates2.add(cb.equal(root.get(CustomerCoupon_.customer).get(Customer_.id), isMainRestaurantQuerySubRoot2.get(Restaurant_.customer).get(Customer_.id)));
////            predicates2.add(cb.greaterThan(isMainRestaurantQuerySubRoot2.get(Restaurant_.status), RestaurantStatus.UNDEFINED.getValue()));
////            isMainRestaurantQuery2.where(cb.and(predicates2.toArray(new Predicate[]{})));
////            isMainRestaurantQuery2.groupBy(isMainRestaurantQuerySubRoot2.get(Restaurant_.customer).get(Customer_.id));
////            Subquery<Long> isMainRestaurant2 = isMainRestaurantQuery2.select(isMainRestaurantQuerySubRoot2.get(Restaurant_.id));
//
////            return  cb.or(cb.isTrue(cb.and(cb.isNotNull(isMainRestaurant), this.getRestaurantListJoin().get(Restaurant_.id).in(isMainRestaurant))),
////                    this.getRestaurantListJoin().get(Restaurant_.id).in(isMainRestaurant2));
//              return  cb.exists(isMainRestaurant);
//        }
        @Override
        public Predicate toPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            query.groupBy(root.get(CustomerCoupon_.id));

            Join<CustomerCoupon, Customer> customerJoin = root.join(CustomerCoupon_.customer,JoinType.LEFT);
            restaurantListJoin = customerJoin.join(Customer_.restaurant, JoinType.LEFT);
            Path<Customer> customerPath =root.get(CustomerCoupon_.customer);
            Path<Coupon> couponPath =root.get(CustomerCoupon_.coupon);
            List<Predicate> predicates = new ArrayList<>();
            if(null!=request.getCityId()){
                predicates.add(cb.equal(customerPath.get(Customer_.block).get(Block_.warehouse).get(Warehouse_.city).get(City_.id), request.getCityId()));
            }
            if(null!=request.getWarehouseId()   ){
                predicates.add(cb.equal(customerJoin.join(Customer_.block,JoinType.LEFT).join(Block_.warehouse, JoinType.LEFT).get(Warehouse_.id), request.getWarehouseId()));
            }
            if(null!=request.getRestaurantName()){
                predicates.add(cb.like(restaurantListJoin.get(Restaurant_.name), "%"+ request.getRestaurantName() +"%"));
            }
            if( null!=request.getRestaurantId()){
                predicates.add(cb.equal(restaurantListJoin.get(Restaurant_.id), request.getRestaurantId()));
            }
            if(null!=request.getCouponType()    ){
                predicates.add(cb.equal(couponPath.get(Coupon_.couponConstants),request.getCouponType()));
            }
            if(null!=request.getCouponStatus()  ){
                predicates.add(cb.equal(root.get(CustomerCoupon_.status), request.getCouponStatus()));
            }

            if(null!=request.getSendFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.sendDate), request.getSendFront()));
            }
            if(null!=request.getSendBack()     ){
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.sendDate),request.getSendBack()));
            }
            if(null!=request.getStartFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.start), request.getStartFront()));
            }
            if(null!=request.getStartBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.start), request.getStartBack()));
            }
            if(null!=request.getUseFront()    ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.useDate), request.getUseFront()));
            }
            if(null!=request.getUseBack()     ) {
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.useDate), request.getUseBack()));
            }

            if(null!=request.getEndFront() ){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.end),request.getEndFront()));
            }
            if(null!=request.getEndBack()  ){
                predicates.add(cb.lessThan(root.get(CustomerCoupon_.end), request.getEndBack()));
            }
            if(null!=request.getCouponIdFront()){
                predicates.add(cb.greaterThanOrEqualTo(root.get(CustomerCoupon_.coupon).get(Coupon_.id),request.getCouponIdFront()));
            }
            if(null!=request.getCouponIdBack()){
                predicates.add(cb.lessThanOrEqualTo(root.get(CustomerCoupon_.coupon).get(Coupon_.id),request.getCouponIdBack()));
            }

//            predicates.add(this.getMainRestaurantSubQueryPredicate(root,query,cb));
            return cb.and(predicates.toArray(new Predicate[]{}));
        }
    };


    @Transactional
    public CustomerCoupon saveCustomerCoupon(CustomerCoupon customerCoupon) {
        return customerCouponRepository.save(customerCoupon);
    }

    public List<CustomerCoupon> findAll(final Customer customer, final int status, final Date date) {
        return customerCouponRepository.findAll(new Specification<CustomerCoupon>() {
            @Override
            public Predicate toPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder
                    criteriaBuilder) {
                return criteriaBuilder.and(
                        criteriaBuilder.equal(root.get(CustomerCoupon_.status), status),
                        criteriaBuilder.equal(root.get(CustomerCoupon_.customer), customer),
                        criteriaBuilder.lessThanOrEqualTo(root.get(CustomerCoupon_.start), date),
                        criteriaBuilder.greaterThanOrEqualTo(root.get(CustomerCoupon_.end), date));
            }
        });
    }

    @Transactional
    public CustomerCoupon getCustomerCoupon(Long id) {
        return customerCouponRepository.getOne(id);
    }

    @Transactional(readOnly = true)
    public List<CustomerCoupon> findByCustomer(Customer customer) {
        return customerCouponRepository.findByCustomerOrderByStatusAsc(customer);
    }

    private boolean couldOfferApplyToOrder(Coupon coupon, Order order, Warehouse warehouse, Block block) {
        HashMap<String, Object> vars = new HashMap<>();
        vars.put("order", order);
        vars.put("warehouse", warehouse);
        vars.put("block", block);
        LOG.info("MVEL vars:" + vars);
        LOG.info("coupon.getUseRule()" + coupon.getUseRule());
        return ExpressionUtils.executeExpression(CouponUtils.getRealRule(coupon.getUseRule(), coupon.getRuleValue()), vars);
    }

    public boolean couldOfferApplyToOrder(CustomerCoupon coupon, Order order, Warehouse warehouse, Block block) {
        if (CouponStatus.USED.getValue().equals(coupon.getStatus())) {
            return false;
        }

        if (coupon.getStart().after(order.getSubmitDate())) {
            return false;
        }

        if (coupon.getEnd().before(order.getSubmitDate())) {
            return false;
        }

        return couldOfferApplyToOrder(coupon.getCoupon(), order, warehouse, block);
    }

    public boolean couldOfferApplyToTempOrder(CustomerCoupon coupon, Order order, Warehouse warehouse, Block block) {
        if (coupon.getStart().after(order.getSubmitDate())) {
            return false;
        }

        if (coupon.getEnd().before(order.getSubmitDate())) {
            return false;
        }

        return couldOfferApplyToOrder(coupon.getCoupon(), order, warehouse, block);
    }

    public List<CustomerCoupon> findAvailableCoupons(Customer customer, Order order) {
        List<CustomerCoupon> result = new ArrayList<>();

        for (CustomerCoupon coupon : findAll(customer, CouponStatus.UNUSED.getValue(), order.getSubmitDate())) {
            if (couldOfferApplyToOrder(coupon.getCoupon(), order, order.getCustomer().getBlock().getWarehouse(), order.getCustomer().getBlock())) {
                result.add(coupon);
            }
        }

        return result;
    }

    @Transactional(readOnly = true)
    public List<Coupon> getAvailableActivityCouponForNewCustomer(final Customer customer, final Date date) {
        return new ArrayList<>(Collections2.filter(getAvailableCouponCandidate(CouponConstant.ACTIVITY_SEND.getType(), false, date),
                new com.google.common.base.Predicate<Coupon>() {
                    @Override
                    public boolean apply(Coupon input) {
                        Map<String, Object> context = new HashMap<>();
                        context.put("city", customer.getBlock().getCity());
                        context.put("warehouse", customer.getBlock().getWarehouse());
                        context.put("block", customer.getBlock());

                        return ExpressionUtils.executeExpression(CouponUtils.getRealRule(input.getSendRule(), input.getRuleValue()), context);
                    }
                }));
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Coupon> getAvailableRegisterCouponForNewCustomer(final Customer customer, final Date date) {
        return new ArrayList<>(Collections2.filter(getAvailableCouponCandidate(CouponConstant.REGISTER_SEND.getType(), true, date),
                new com.google.common.base.Predicate<Coupon>() {
                    @Override
                    public boolean apply(Coupon input) {
                        Map<String, Object> context = new HashMap<>();
                        context.put("city", customer.getBlock().getCity());
                        context.put("warehouse", customer.getBlock().getWarehouse());
                        context.put("block", customer.getBlock());

                        return ExpressionUtils.executeExpression(CouponUtils.getRealRule(input.getSendRule(), input.getRuleValue()), context);
                    }
                }));
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Coupon> getAvailableOrderGift(final Integer promotionType, final Order order,  final Date date) {
        return new ArrayList<>(Collections2.filter(getAvailableCouponCandidate(promotionType, true, date),
                new com.google.common.base.Predicate<Coupon>() {
                    @Override
                    public boolean apply(Coupon input) {
                        Map<String, Object> context = new HashMap<>();
                        context.put("order", order);
                        context.put("city", order.getCustomer().getBlock().getCity());
                        context.put("warehouse", order.getCustomer().getBlock().getWarehouse());
                        context.put("block", order.getCustomer().getBlock());
                        return checkCouponType(input, order, date) && ExpressionUtils.executeExpression(CouponUtils.getRealRule(input.getSendRule(), input.getRuleValue()), context);
                    }
                }));
    }

    public boolean checkCouponType(Coupon coupon, final Order order, Date current) {
        Date start = DateUtils.truncate(current, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addDays(start, +1);

        if (coupon.getCouponRestriction() == null) {
            return true;
        } else if (!anyOrderBefore(order.getCustomer(), current, null) && coupon.getCouponRestriction().equals(PromotionConstants.RESTAURANT_FIRST_SINGLE)) {
            return true;
        } else if (coupon.getCouponRestriction().equals(PromotionConstants.TODAY_FIRST_SINGLE)) {
            // 每天的首单
            final List<Order> orders = orderService.getOrderByCustomerAndSubmitDate(order.getCustomer(), start, end);
            boolean existsOrdersToday = false;
            for (Order o : orders) {
                if (o.getStatus() != OrderStatus.CANCEL.getValue() && !o.getId().equals(order.getId())) {
                    existsOrdersToday = true;
                }
            }
            if (!existsOrdersToday) {
                return true;
            }
        } else if (coupon.getCouponRestriction().equals(PromotionConstants.ORDER_WITH_A_GIFT_SEND)) {
            return true;
        }

        return false;
    }

    private boolean anyOrderBefore(Customer customer, Date end, AdminUser adminUser) {
        OrderQueryRequest request = new OrderQueryRequest();
        request.setEnd(end);
        request.setPage(0);
        request.setPageSize(1);
        request.setCustomerId(customer.getId());
        request.setPromotionTag(true);

        Page<Order> page = orderService.findOrders(request, adminUser);

        return page.getTotalElements() > 0;
    }

    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public List<Coupon> getAvailableShareCoupon(final Customer customer, final Date date) {
        return new ArrayList<>(Collections2.filter(getAvailableCouponCandidate(CouponConstant.SHARE_SEND.getType(), true, date),
                new com.google.common.base.Predicate<Coupon>() {
                    @Override
                    public boolean apply(Coupon input) {
                        Map<String, Object> context = new HashMap<>();
                        context.put("city", customer.getBlock().getCity());
                        context.put("warehouse", customer.getBlock().getWarehouse());
                        context.put("block", customer.getBlock());
                        return ExpressionUtils.executeExpression(CouponUtils.getRealRule(input.getSendRule(), input.getRuleValue()), context);
                    }
                }));
    }

    private List<Coupon> getAvailableCouponCandidate(final Integer couponType, final boolean
            between, final Date date) {
        return couponRepository.findAll(new Specification<Coupon>() {
            @Override
            public Predicate toPredicate(Root<Coupon> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(Coupon_.couponConstants), couponType));

                predicates.add(cb.greaterThanOrEqualTo(root.get(Coupon_.end), date));
                if (between) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Coupon_.start), date));
                }


                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

    }

    public boolean existsRegisterCustomerCoupon(final Long customerId) {
        List<CustomerCoupon> customerCoupons = customerCouponRepository.findAll(new Specification<CustomerCoupon>() {
            @Override
            public Predicate toPredicate(Root<CustomerCoupon> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();

                predicates.add(cb.equal(root.get(CustomerCoupon_.coupon).get(Coupon_.couponConstants), CouponConstant.REGISTER_SEND.getType()));

                predicates.add(cb.equal(root.get(CustomerCoupon_.customer).get(Customer_.id), customerId));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        return CollectionUtils.isNotEmpty(customerCoupons);
    }

    private Coupon formCoupon(CouponRequest couponRequest) {
        Coupon coupon = new Coupon();
        coupon.setCouponConstants(couponRequest.getCouponType());
        coupon.setCouponRestriction(couponRequest.getCouponRestriction());
        coupon.setName(couponRequest.getName());

        if (!CouponConstant.ORDER_WITH_A_GIFT_SEND.getType().equals(couponRequest.getCouponType())) {
            coupon.setDiscount(couponRequest.getDiscount());
        }

        if (CouponConstant.ORDER_WITH_A_GIFT_SEND.getType().equals(couponRequest.getCouponType())) {
            coupon.setSku(skuService.getOne(couponRequest.getSkuId()));
            coupon.setQuantity(couponRequest.getQuantity());
        }

        if (CouponConstant.EXCHANGE_COUPON.getType().equals(couponRequest.getCouponType())) {
            coupon.setScore(couponRequest.getScore());
        }

        if (CouponConstant.TWO_FOR_ONE.getType().equals(couponRequest.getCouponType())) {

            coupon.setSendCouponQuantity(couponRequest.getSendCouponQuantity());
        }

        coupon.setStart(couponRequest.getStart());
        if (couponRequest.getEnd() != null) {

            coupon.setEnd(DateUtils.addSeconds(DateUtils.addDays(couponRequest.getEnd(), 1), -1));
        }
        coupon.setDescription(couponRequest.getDescription());
        coupon.setRemark(couponRequest.getRemark());
        coupon.setSendRule(getSendRule(couponRequest));
        coupon.setUseRule(getUseRule(couponRequest));
        if (couponRequest.getDeadline() != null) {
            coupon.setDeadline(DateUtils.addSeconds(DateUtils.addDays(couponRequest.getDeadline(), 1), -1));
        }
        coupon.setPeriodOfValidity(couponRequest.getPeriodOfValidity());
        coupon.setBeginningDays(couponRequest.getBeginningDays());

        try {
            coupon.setRuleValue(new ObjectMapper().writeValueAsString(couponRequest));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return coupon;
    }

    public void createCoupon(CouponRequest couponRequest) throws Exception {
        Coupon coupon = formCoupon(couponRequest);
        coupon.setCreateTime(new Date());
        couponRepository.save(coupon);
    }

    private String append(String str, String appendStr) {
        return str += StringUtils.isEmpty(str) ? appendStr : "&&" + appendStr;
    }

    private String getUseRule(CouponRequest couponRequest) {
        String useRule = "";

        if (couponRequest.getUseRestrictionsTotal() != null) {
            if (ArrayUtils.isNotEmpty(couponRequest.getUseRestrictionsCategoryIds())) {
                useRule = append(useRule, String.format("OrderService.getOrderAmountByCategories(order,%s)>=%s", CouponRuleConstant.useRestrictionsCategoryIdsRule, CouponRuleConstant.useRestrictionsTotalRule));
            } else {
                useRule = append(useRule, String.format("OrderService.getOrderAmountByCategories(order)>=%s", CouponRuleConstant.useRestrictionsTotalRule));
            }

            if (couponRequest.getBrandId() != null) {
                useRule = append(useRule, String.format("OrderService.getOrderAmountByBrands(order,%s)>=%s", CouponRuleConstant.brandIdRule, CouponRuleConstant.useRestrictionsTotalRule));
            }
        }

        if (couponRequest.getWarehouseId() != null) {
            useRule = append(useRule, String.format("warehouse.id==%s", CouponRuleConstant.warehouseIdRule));
        }

        return StringUtils.isEmpty(useRule) ? Boolean.TRUE.toString() : useRule;
    }

    private String getSendRule(CouponRequest couponRequest) {
        String sendRule = "";

        if (couponRequest.getSendRestrictionsTotalMin() != null && couponRequest.getSendRestrictionsTotalMax() != null) {
            BigDecimal min = couponRequest.getSendRestrictionsTotalMin();
            BigDecimal max = couponRequest.getSendRestrictionsTotalMax();
//            if (!couponRequest.getCouponType().equals(CouponConstant.EXCHANGE_COUPON.getType())
//                    && !couponRequest.getCouponType().equals(CouponConstant.REGISTER_SEND.getType())
//                    && !couponRequest.getCouponType().equals(CouponConstant.SHARE_SEND.getType())) {
//                if (ArrayUtils.isNotEmpty(couponRequest.getSendRestrictionsCategoryIds())) {
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order,%s)>=%s", StringUtils.join(couponRequest.getSendRestrictionsCategoryIds(), ","), min));
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order,%s)<%s", StringUtils.join(couponRequest.getSendRestrictionsCategoryIds(), ","), max));
//                } else {
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order)>=%s", min));
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order)<%s", max));
//                }
//                if (couponRequest.getBrandId() != null) {
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByBrands(order,%s)>=%s", couponRequest.getBrandId(), min));
//                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByBrands(order,%s)<%s", couponRequest.getBrandId(), max));
//                }
//            }

            if (!couponRequest.getCouponType().equals(CouponConstant.EXCHANGE_COUPON.getType())
                    && !couponRequest.getCouponType().equals(CouponConstant.REGISTER_SEND.getType())
                    && !couponRequest.getCouponType().equals(CouponConstant.SHARE_SEND.getType())) {
                if (ArrayUtils.isNotEmpty(couponRequest.getSendRestrictionsCategoryIds())) {
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order,%s)>=%s", CouponRuleConstant.sendRestrictionsCategoryIdsRule, CouponRuleConstant.sendRestrictionsTotalMinRule));
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order,%s)<%s", CouponRuleConstant.sendRestrictionsCategoryIdsRule, CouponRuleConstant.sendRestrictionsTotalMaxRule));
                } else {
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order)>=%s", CouponRuleConstant.sendRestrictionsTotalMinRule));
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByCategories(order)>=%s", CouponRuleConstant.sendRestrictionsTotalMaxRule));
                }
                if (couponRequest.getBrandId() != null) {
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByBrands(order,%s)>=%s", CouponRuleConstant.brandIdRule, CouponRuleConstant.sendRestrictionsTotalMinRule));
                    sendRule = append(sendRule, String.format("OrderService.getOrderAmountByBrands(order,%s)<%s", CouponRuleConstant.brandIdRule, CouponRuleConstant.sendRestrictionsTotalMaxRule));
                }
            }
        }

        if (couponRequest.getCouponType().equals(CouponConstant.TWO_FOR_ONE.getType())) {
            sendRule = append(sendRule,String.format("true==OrderService.getPromotionBySku(order,%s,%s,%s)", CouponRuleConstant.buyQuantityRule, CouponRuleConstant.buySkuUnitRule, CouponRuleConstant.buySkuIdRule));
        }

        if (couponRequest.getCityId() != null) {
            sendRule = append(sendRule, String.format("city.id==%s", CouponRuleConstant.cityIdRule));
        }

        if (couponRequest.getWarehouseId() != null) {
            sendRule = append(sendRule, String.format("warehouse.id==%s", CouponRuleConstant.warehouseIdRule));
        }

        return StringUtils.isEmpty(sendRule) ? Boolean.FALSE.toString() : sendRule;
    }

    @Transactional
    public QueryResponse<SimpleCouponWrapper> getCouponList(final CouponListRequest request) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        List<SimpleCouponWrapper> list = new ArrayList<>();
        Page<Coupon> page = couponRepository.findAll(new Specification<Coupon>() {
            @Override
            public Predicate toPredicate(Root<Coupon> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();
                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(Coupon_.createTime), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(Coupon_.createTime), request.getEndDate()));
                }

                if (request.getCouponType() != null) {
                    predicates.add(cb.equal(root.get(Coupon_.couponConstants), request.getCouponType()));
                }
                criteriaQuery.orderBy(cb.desc(root.get(Coupon_.id)));
                return CollectionUtils.isEmpty(predicates) ? cb.and() : cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        for (Coupon coupon : page.getContent()){
            list.add(new SimpleCouponWrapper(coupon));
        }

        QueryResponse<SimpleCouponWrapper> res = new QueryResponse<SimpleCouponWrapper>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional
    public CouponWrapper getCoupon(Long id) throws Exception {
        return new CouponWrapper(couponRepository.getOne(id));
    }

    public void updateCoupon(Long id, CouponRequest couponRequest) throws Exception {
        Coupon coupon = couponRepository.getOne(id);
        BeanUtils.copyProperties(formCoupon(couponRequest), coupon, "id", "createTime");
        couponRepository.save(coupon);
    }


    public String sendCoupon(Long couponId, Long cityId, Long warehouseId, Long customerId) {

        if (cityId == null && warehouseId == null && customerId == null) {
            return "None of the parameters \"cityId\", \"warehouseId\", \"customerId\" are provided.";
        }

        if (!existOnlyOne(cityId, warehouseId, customerId)) {
            return "Only one of the parameters \"cityId\", \"warehouseId\", \"customerId\" can exist.";
        }

        PromotionMessage message = new PromotionMessage(CouponSenderEnum.ACTIVITY_SEND);
        message.setCouponId(couponId);
        message.setCityId(cityId);
        message.setWarehouseId(warehouseId);
        message.setCustomerId(customerId);
        promotionMessageSender.sendMessage(message);
        return "success";
    }

    private boolean existOnlyOne(Object... parameters) {
        int notNullParametersCount = 0;
        for (Object parameter : parameters) {
            if (parameter != null) {
                notNullParametersCount++;
            }
        }
        return notNullParametersCount == 1;
    }

    public void sendCoupon(SendCouponRequest sendCouponRequest, AdminUser adminUser) {

        Long couponId = sendCouponRequest.getCouponId();
        Coupon coupon = couponRepository.getOne(couponId);

        Date start;
        Date end;

        if (CouponConstant.PRECISE_SEND.getType().equals(coupon.getCouponConstants()) || CouponConstant.ORDER_WITH_A_COUPON_SEND.getType().equals(coupon.getCouponConstants())) {
            start = new Date();
            if (coupon.getPeriodOfValidity() != null) {

                end = DateUtils.truncate(DateUtils.addDays(start, coupon.getPeriodOfValidity()), Calendar.DAY_OF_MONTH);

            } else {

                end = DateUtils.truncate(DateUtils.addDays(start, 8), Calendar.DAY_OF_MONTH);
            }
        } else if (!CouponConstant.ACTIVITY_SEND.getType().equals(coupon.getCouponConstants())) {
            start = new Date();
            if (coupon.getDeadline() != null) {
                end = coupon.getDeadline();
            } else {
                end = DateUtils.truncate(DateUtils.addDays(start, 8), Calendar.DAY_OF_MONTH);
            }
        } else {
            start = coupon.getStart();
            end = coupon.getEnd();
        }

        for (Long restaurantId : sendCouponRequest.getRestaurantIds()) {
            Customer customer = restaurantService.getOne(restaurantId).getCustomer();
            CustomerCoupon customerCoupon = new CustomerCoupon();
            customerCoupon.setSendDate(new Date());
            customerCoupon.setStatus(CouponStatus.UNUSED.getValue());
            customerCoupon.setCoupon(coupon);
            customerCoupon.setCustomer(customer);
            customerCoupon.setStart(start);
            customerCoupon.setEnd(end);
            customerCoupon.setSender(adminUser);
            customerCoupon.setReason(sendCouponRequest.getReason());
            customerCoupon.setRemark(sendCouponRequest.getReason().equals(SendCouponReason.REASON10.getValue()) ? sendCouponRequest.getRemark() : SendCouponReason.from(sendCouponRequest.getReason()).getName());

            customerCouponRepository.save(customerCoupon);
        }
    }

    public Coupon getCouponById(Long couponId) {
        return couponRepository.getOne(couponId);
    }

    public List<Coupon> getAvailableExchangeCouponCandidate(final Integer couponType, final boolean between, final Date date ,final Customer customer) {

        return new ArrayList<>(Collections2.filter(getAvailableCouponCandidate(couponType, true, date),
                new com.google.common.base.Predicate<Coupon>() {
                    @Override
                    public boolean apply(Coupon input) {
                        Map<String, Object> context = new HashMap<>();
                        context.put("city", customer.getBlock().getCity());
                        context.put("warehouse", customer.getBlock().getWarehouse());
                        return ExpressionUtils.executeExpression(CouponUtils.getRealRule(input.getSendRule(), input.getRuleValue()), context);
                    }
                }));
    }




}
