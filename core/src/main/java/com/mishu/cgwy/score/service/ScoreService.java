package com.mishu.cgwy.score.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.domain.AdminUser_;
import com.mishu.cgwy.common.domain.Block_;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.order.domain.Order_;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Customer_;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Restaurant_;
import com.mishu.cgwy.profile.repository.CustomerRepository;
import com.mishu.cgwy.profile.repository.RestaurantRepository;
import com.mishu.cgwy.score.constants.ScoreTypeEnum;
import com.mishu.cgwy.score.constants.ScoreTypeRemark;
import com.mishu.cgwy.score.controller.ScoreLogQueryRequest;
import com.mishu.cgwy.score.controller.ScoreQueryRequest;
import com.mishu.cgwy.score.controller.ScoreSumRequest;
import com.mishu.cgwy.score.controller.ScoreSumResponse;
import com.mishu.cgwy.score.domain.Score;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.score.domain.ScoreLog_;
import com.mishu.cgwy.score.domain.Score_;
import com.mishu.cgwy.score.repository.ScoreLogRepository;
import com.mishu.cgwy.score.repository.ScoreRepository;
import com.mishu.cgwy.score.vo.ScoreLogAddBean;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOut_;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.utils.JpaQueryUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.hibernate.jpa.criteria.CriteriaQueryImpl;
import org.hibernate.jpa.criteria.OrderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.MapAttribute;
import javax.persistence.metamodel.PluralAttribute;
import javax.persistence.metamodel.SingularAttribute;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by bowen on 15/11/10.
 */
@Service
public class ScoreService{
    private Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private ScoreRepository scoreRepository;
    @Autowired
    private ScoreLogRepository scoreLogRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private OrderService orderService;

    @Autowired
    private StockOutService stockOutService;


    @Transactional(propagation= Propagation.REQUIRED)
    public void addScoreLog(ScoreLogAddBean... scorelogAddbeans){
        if(scorelogAddbeans==null){
            return ;
        }
        for(ScoreLogAddBean scorelog: scorelogAddbeans){
            this.addScoreLog(scorelog);
        }
    }

    /**
     * 增减积分
     */
    @Transactional(propagation= Propagation.REQUIRED)
    public ScoreLog addScoreLog(ScoreLogAddBean addBean){
        Customer customer= customerRepository.getOne(addBean.getCustomerId());
        Score cScore = customer.getScore();
        Date cdate=new Date();
        if(cScore==null){
            cScore=new Score();
            cScore.setCreateTime(cdate);
            cScore.setCustomer(customer);
            cScore.setUpdateTime(cdate);
            scoreRepository.save(cScore);
        }
        if(addBean.getScoreTypeRemark().scoreType.isAddScore) {
            scoreRepository.increaseTotalScore(addBean.getCustomerId(),addBean.getScore());
        }else{
            scoreRepository.increaseExchangeScore(addBean.getCustomerId(),addBean.getScore());
        }
        ScoreLog scoreLog = new ScoreLog();
        scoreLog.setCreateTime(cdate);
        scoreLog.setCustomer(customer);
        scoreLog.setIntegral(addBean.getScore());
        scoreLog.setScore(cScore);
        scoreLog.setStatus(addBean.getScoreTypeRemark().scoreType.val);
        scoreLog.setRemark(ScoreTypeRemark.remarkHelp( addBean.getScoreTypeRemark(), addBean.getBeSharedCustomerId()) );
        scoreLog.setSender(addBean.getSender());
        if(addBean.getCouponId()!=null){
            Coupon coupon=new Coupon();
            coupon.setId(addBean.getCouponId());
            scoreLog.setCoupon(coupon);
        }
        if(addBean.getOrderId()!=null){
            com.mishu.cgwy.order.domain.Order order=new com.mishu.cgwy.order.domain.Order();
            order.setId(addBean.getOrderId());
            scoreLog.setOrder(order);
        }
        if(addBean.getStockOutId()!=null){
            StockOut stockOut =stockOutService.getOneStockOut(addBean.getStockOutId());
            scoreLog.setStockOut(stockOut);
            scoreLog.setOrder(stockOut.getOrder());
        }


        scoreLogRepository.save(scoreLog);
        return scoreLog;
    }


    /**
     * 获取 订单实收金额合计， 积分合计，
     * @param request
     * @return
     */
    public ScoreSumResponse getOrderScoreSum(final ScoreSumRequest request) {

        ScoreSumResponse ssResponse = new ScoreSumResponse();
        Long scoreSum = this.getSumByScore(request);
        BigDecimal amountSum = this.getSumByAmount(request);

        ssResponse.setScoreSum(scoreSum);
        ssResponse.setReciveAmount(amountSum);
        return ssResponse;
    }


    public Long getSumByScore(final ScoreSumRequest request) {

        Long result =JpaQueryUtils.valSelect(ScoreLog.class, new SumSpecification(request), entityManager, new JpaQueryUtils.SelectPathGetting<ScoreLog, Long, SumSpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<ScoreLog> root, SumSpecification specification) {
                Expression scoreSum = cb.selectCase().when(cb.isNull(cb.sum(root.get(ScoreLog_.integral))), 0L).otherwise(cb.sum(root.get(ScoreLog_.integral)));
                return new Selection<?>[]{
                        scoreSum
                };
            }
            @Override
            public Long resultWrappe(List<Tuple> tuples) {
                Long result =0L;
                for(Tuple tuple:tuples){
                    result+=tuple.get(0,Long.class);
                }
                return result;
            }
        });
        return result;

    }

    public BigDecimal getSumByAmount(final ScoreSumRequest request) {
        BigDecimal result =JpaQueryUtils.valSelect(ScoreLog.class, new SumSpecification(request), entityManager, new JpaQueryUtils.SelectPathGetting<ScoreLog, BigDecimal, SumSpecification>() {
            @Override
            public Selection<?>[] getSelectPath(CriteriaBuilder cb, CriteriaQuery query, Root<ScoreLog> root, SumSpecification specification) {

                CriteriaQueryImpl cqImpl = (CriteriaQueryImpl) query;
                Predicate nEPredicate =cb.equal(root.get(ScoreLog_.status), ScoreTypeEnum.OBTAIN_SCORE.val);
                Predicate restriction = cqImpl.getRestriction();
                if(null!=restriction){
                    nEPredicate= cb.and(nEPredicate,restriction);
                }
                query.where(nEPredicate);
                query.groupBy(root.get(ScoreLog_.order).get(Order_.id));
                Join<com.mishu.cgwy.order.domain.Order, StockOut> stockOutJoin = root.join(ScoreLog_.order, JoinType.LEFT).join(Order_.stockOuts, JoinType.LEFT);
                Expression receiveAmount = cb.selectCase().when(cb.isNull(stockOutJoin.get(StockOut_.receiveAmount)), new BigDecimal(0)).otherwise(stockOutJoin.get(StockOut_.receiveAmount));
                return new Selection<?>[]{receiveAmount};
            }
            @Override
            public BigDecimal resultWrappe(List<Tuple> tuples) {
                BigDecimal result = new BigDecimal(0);
                for (Tuple tuple : tuples) {
                    result = result.add(tuple.get(0, BigDecimal.class));
                }
                return result;
            }
        });
        return result;
    }

    private static class SumSpecification implements Specification<ScoreLog>{
        private final ScoreSumRequest request;
        public SumSpecification(ScoreSumRequest request) {
            this.request = request;
        }

        @Override
        public Predicate toPredicate(Root<ScoreLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
            List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();

            if (request.getCustomerId() != null) {
                predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.id), request.getCustomerId()));
            }
            if (request.getOrderBeginDate() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get(ScoreLog_.order).get(Order_.completeDate), request.getOrderBeginDate()));
            }
            if (request.getOrderEndDate() != null) {
                predicates.add(cb.lessThan(root.get(ScoreLog_.order).get(Order_.completeDate), DateUtils.addDays(request.getOrderEndDate(), 1)));
            }
            if (request.getScoreLogStatus() != null) {
                if (request.getScoreLogStatus() == ScoreTypeEnum.OBTAIN_SCORE.val) {
                    predicates.add(cb.and(root.get(ScoreLog_.status).in(ScoreTypeEnum.findVal(true))));
                }else if (request.getScoreLogStatus() == ScoreTypeEnum.EXCHANGE_SCORE.val) {
                    predicates.add(cb.and(root.get(ScoreLog_.status).in(ScoreTypeEnum.findVal(false))));
                }
            }

            if (request.getRestaurantId() != null ) {
                List<javax.persistence.criteria.Predicate> subPredicate = new ArrayList<>();
                Subquery<Restaurant> subQuery = query.subquery(Restaurant.class);
                Root<Restaurant> subRoot = subQuery.from(Restaurant.class);
                subPredicate.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.id), subRoot.get(Restaurant_.customer).get(Customer_.id)));
                subPredicate.add(cb.equal(subRoot.get(Restaurant_.id), request.getRestaurantId()));

                subQuery.where(subPredicate.toArray(new Predicate[]{}));
                predicates.add(cb.exists(subQuery.select(subRoot)));
            }

            return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[]{}));
        }
    };

    /**
     * 积分列表查询
     * @return
     */
    public Page<Score> getScores(final ScoreQueryRequest request){

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<Score> scorePage = this.scoreRepository.findAll(new Specification<Score>() {
            @Override
            public Predicate toPredicate(Root<Score> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Order order=null;
                if("availableScore".equals(request.getSortField())){ //可用积分
                    Expression exp=cb.diff(root.get(Score_.totalScore), root.get(Score_.exchangeScore));
                    order=new OrderImpl(exp,request.isAsc());
                }else{
                    Attribute attr= root.getModel().getAttribute(request.getSortField());

                    if(attr instanceof SingularAttribute){
                        order=new OrderImpl(root.get((SingularAttribute<? super Score, Object>) attr),request.isAsc());
                    }
                    if(attr instanceof PluralAttribute){
                        order=new OrderImpl(root.get((PluralAttribute<Score, Collection<Object>, Object>) attr),request.isAsc());
                    }
                    if(attr instanceof MapAttribute){
                        order=new OrderImpl(root.get((MapAttribute<Score, Object, Object>) attr),request.isAsc());
                    }
                }

                if(order!=null){
                    query.orderBy(order);
                }


                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(Score_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
                }

                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(Score_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }

                if (request.getAdminUserId() != null) {
                    predicates.add(cb.equal(root.get(Score_.customer).get(Customer_.adminUser).get(AdminUser_.id), request.getAdminUserId()));
                }

                if(request.getRestaurantId() != null || request.getStatus() != null || request.getGrade() != null || StringUtils.isNotBlank(request.getRestaurantName())){
                    List<javax.persistence.criteria.Predicate> subPredicate = new ArrayList<>();
                    Subquery<Restaurant> subQuery = query.subquery(Restaurant.class);
                    Root<Restaurant> subRoot = subQuery.from(Restaurant.class);
                    subPredicate.add(cb.equal(root.get(Score_.customer).get(Customer_.id), subRoot.get(Restaurant_.customer).get(Customer_.id)));

                    if (request.getRestaurantId() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.id), request.getRestaurantId()));
                    }
                    if (request.getStatus() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.status), request.getStatus()));
                    }
                    if (request.getGrade() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.grade), request.getGrade()));
                    }
                    if (StringUtils.isNotBlank(request.getRestaurantName())) {
                        subPredicate.add(cb.like(subRoot.get(Restaurant_.name), "%"+request.getRestaurantName()+"%"));
                    }

                    subQuery.where(subPredicate.toArray(new Predicate[]{}) );
                    predicates.add(cb.exists(subQuery.select(subRoot)));
                }

                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[]{}));
            }


        },pageable);

        return scorePage;
    }


    /**
     * 积分日志查询
     * @return
     */
    public Page<ScoreLog> getScoreLogs(final ScoreLogQueryRequest request){

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(),
                new Sort(request.isAsc()?Sort.Direction.ASC:Sort.Direction.DESC, request.getSortField()));

        Page<ScoreLog> scorelogs = this.scoreLogRepository.findAll(new Specification<ScoreLog>() {
            @Override
            public Predicate toPredicate(Root<ScoreLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                root.join(ScoreLog_.stockOut,JoinType.LEFT);
                root.join(ScoreLog_.order,JoinType.LEFT);
                List<javax.persistence.criteria.Predicate> predicates = new ArrayList<>();
                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.city).get(City_.id), request.getCityId()));
                }
                if (request.getCustomerId() != null) {
                    predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.id), request.getCustomerId()));
                }
                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.block).get(Block_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }
                if (request.getAdminUserId() != null) {
                    predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.adminUser).get(AdminUser_.id), request.getAdminUserId()));
                }
                if (request.getOrderBeginDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(ScoreLog_.order).get(Order_.completeDate), request.getOrderBeginDate()));
                }
                if (request.getOrderEndDate() != null) {
                    predicates.add(cb.lessThan(root.get(ScoreLog_.order).get(Order_.completeDate), DateUtils.addDays(request.getOrderEndDate(), 1)));
                }
//                if (request.getScoreLogStatus() != null) {
//                    if (request.getScoreLogStatus() == ScoreTypeEnum.OBTAIN_SCORE.val) {
//                        predicates.add(cb.and(root.get(ScoreLog_.status).in(request.getScoreLogStatus(), ScoreTypeEnum.SHARE_SCORE.val)));
//                    }else if (request.getScoreLogStatus() == ScoreTypeEnum.EXCHANGE_SCORE.val) {
//                        predicates.add(cb.equal(root.get(ScoreLog_.status), request.getScoreLogStatus()));
//                    }
//                }
                if (request.getScoreLogStatus() != null) {
                    if (request.getScoreLogStatus() == ScoreTypeEnum.OBTAIN_SCORE.val) {
                        predicates.add(cb.and(root.get(ScoreLog_.status).in(ScoreTypeEnum.findVal(true))));
                    }else if (request.getScoreLogStatus() == ScoreTypeEnum.EXCHANGE_SCORE.val) {
                        predicates.add(cb.and(root.get(ScoreLog_.status).in(ScoreTypeEnum.findVal(false))));
                    }
                }
//                if (ScoreTypeEnum.EXCHANGE_SCORE.val == request.getScoreLogStatus()) {

                if (request.getRestaurantId() != null || request.getStatus() != null || request.getGrade() != null || StringUtils.isNotBlank(request.getRestaurantName())) {
                    List<javax.persistence.criteria.Predicate> subPredicate = new ArrayList<>();
                    Subquery<Restaurant> subQuery = query.subquery(Restaurant.class);
                    Root<Restaurant> subRoot = subQuery.from(Restaurant.class);
                    subPredicate.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.id), subRoot.get(Restaurant_.customer).get(Customer_.id)));

                    if (request.getRestaurantId() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.id), request.getRestaurantId()));
                    }
                    if (request.getStatus() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.status), request.getStatus()));
                    }
                    if (request.getGrade() != null) {
                        subPredicate.add(cb.equal(subRoot.get(Restaurant_.grade), request.getGrade()));
                    }
                    if (StringUtils.isNotBlank(request.getRestaurantName())) {
                        subPredicate.add(cb.like(subRoot.get(Restaurant_.name), "%" + request.getRestaurantName() + "%"));
                    }
                    subQuery.where(subPredicate.toArray(new Predicate[]{}));
                    predicates.add(cb.exists(subQuery.select(subRoot)));

                }
//                } else if (ScoreTypeEnum.OBTAIN_SCORE.val == request.getScoreLogStatus()) {
//                    if (request.getRestaurantId() != null) {
//                        predicates.add(cb.equal(root.get(ScoreLog_.stockOut).get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.id), request.getRestaurantId()));
//                    }
//                    if (request.getStatus() != null) {
//                        predicates.add(cb.equal(root.get(ScoreLog_.stockOut).get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.status), request.getStatus()));
//                    }
//                    if (request.getGrade() != null) {
//                        predicates.add(cb.equal(root.get(ScoreLog_.stockOut).get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.grade), request.getGrade()));
//                    }
//                    if (StringUtils.isNotBlank(request.getRestaurantName())) {
//                        predicates.add(cb.equal(root.get(ScoreLog_.stockOut).get(StockOut_.order).get(Order_.restaurant).get(Restaurant_.name), request.getRestaurantName()));
//                    }
//                }
                return cb.and(predicates.toArray(new javax.persistence.criteria.Predicate[]{}));
            }
        }, pageable);

        return scorelogs;
    }




    public Long getLastMonthObtainScore(final Customer customer) {

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.MONTH, -1);
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        final Date start = calendar.getTime();
        calendar.roll(Calendar.DAY_OF_MONTH, -1);
        final Date end = calendar.getTime();

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        final CriteriaQuery<Long> query = cb.createQuery(Long.class);

        final Root<ScoreLog> root = query.from(ScoreLog.class);

        final Specification<ScoreLog> specification = new Specification<ScoreLog>() {
            @Override
            public Predicate toPredicate(Root<ScoreLog> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(ScoreLog_.customer).get(Customer_.id), customer.getId()));
                predicates.add(cb.greaterThanOrEqualTo(root.get(ScoreLog_.createTime), start));
                predicates.add(cb.lessThanOrEqualTo(root.get(ScoreLog_.createTime), end));
                predicates.add(cb.equal(root.get(ScoreLog_.status), ScoreTypeEnum.OBTAIN_SCORE.val));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));

            }
        };

        query.select(cb.sum(root.get(ScoreLog_.integral)));

        query.where(specification.toPredicate(root, query, cb));

        return entityManager.createQuery(query).getSingleResult();
    }

    @Transactional
    public Score saveScore(Score score) {

        return scoreRepository.save(score);
    }

    @Transactional
    public ScoreLog saveScoreLog(ScoreLog scoreLog) {

        return scoreLogRepository.save(scoreLog);
    }

    public Score getOne(Customer customer) {

        return scoreRepository.getOne(customer.getId());
    }

    @Transactional
    public void increaseSalary(Long id , Long score) {

        scoreRepository.increaseExchangeScore(id, score);
    }

    @Transactional
    public void increaseTotalScore(Long id, Long score) {

        scoreRepository.increaseTotalScore(id, score);
    }


    private final static Long scoreVal=10L; //评论一次所返的固定积分

    /**
     * 评价返积分
     * @param orderId
     * @param user
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public ScoreLog evaluateBakScore(Long orderId, AdminUser user) {
//        StockOut stockOut = stockOutService.getStockOutByOrderId(orderId);
//        if(stockOut==null){
//        }
        com.mishu.cgwy.order.domain.Order corder = orderService.getOrderById(orderId);
        ScoreTypeRemark remarkType = ScoreTypeRemark.EVALUATE_SEND;
        ScoreLog scoreLog = this.scoreLogRepository.findByOrderAndStatus(corder, remarkType.scoreType.val);
        if(scoreLog!=null){
            this.logger.info(String.format("orderId:%s status:%s 积分重复派送",orderId,remarkType.scoreType.description));
            return scoreLog;
        }

        ScoreLogAddBean scoreLogAddBean = new ScoreLogAddBean();
        scoreLogAddBean.setScore(scoreVal);
        scoreLogAddBean.setSender(user);
        scoreLogAddBean.setCustomerId(corder.getCustomer().getId());
        scoreLogAddBean.setOrderId(corder.getId());
//        scoreLogAddBean.setStockOutId(stockOut.getId());
        scoreLogAddBean.setScoreTypeRemark(ScoreTypeRemark.EVALUATE_SEND);
        scoreLog =this.addScoreLog(scoreLogAddBean);

        this.orderService.relationScorelog(orderId,scoreLog);
        return scoreLog;
    }

}
