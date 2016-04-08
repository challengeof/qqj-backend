package com.mishu.cgwy.score.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.ScoreExchangeException;
import com.mishu.cgwy.error.ScoreNotEnoughException;
import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.score.Wrapper.ScoreLogWrapper;
import com.mishu.cgwy.score.Wrapper.ScoreResponse;
import com.mishu.cgwy.score.Wrapper.ScoreWrapper;
import com.mishu.cgwy.score.constants.ScoreTypeEnum;
import com.mishu.cgwy.score.constants.ScoreTypeRemark;
import com.mishu.cgwy.score.controller.*;
import com.mishu.cgwy.score.domain.Score;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.score.repository.ScoreLogRepository;
import com.mishu.cgwy.score.service.ScoreService;
import com.mishu.cgwy.score.vo.ScoreLogAddBean;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by bowen on 15/11/10.
 */
@Service
public class ScoreFacade {

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    public ScoreSumResponse orderScoreSum(ScoreSumRequest request) {
        return scoreService.getOrderScoreSum(request);
    }



    /**
     *
     * @param request
     * @return
     */
    public ScoreLogPageResponse getScoreLogs(ScoreLogQueryRequest request) throws Exception {

        Page<ScoreLog> scoreLogs = scoreService.getScoreLogs(request);

        ScoreLogPageResponse result = new ScoreLogPageResponse();
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(scoreLogs.getTotalElements());
        result.setScoreLogs(ScoreLogWrapper.getWrappers(scoreLogs));
        return result;
    }

    public ScoreLogPageResponse getObtainScoreDetail(Customer customer , int page , int pageSize) throws Exception {

        ScoreLogQueryRequest request = new ScoreLogQueryRequest();
        request.setCustomerId(customer.getId());
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setScoreLogStatus(ScoreTypeEnum.OBTAIN_SCORE.val);

        Page<ScoreLog> scoreLogs = scoreService.getScoreLogs(request);
        ScoreLogPageResponse result = new ScoreLogPageResponse();
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(scoreLogs.getTotalElements());
        result.setScoreLogs(ScoreLogWrapper.getWrappers(scoreLogs));

        return result;
    }

    public ScoreLogPageResponse getExchangeScoreDetail(Customer customer, int page, int pageSize) throws Exception {

        ScoreLogQueryRequest request = new ScoreLogQueryRequest();
        request.setCustomerId(customer.getId());
        request.setPage(page);
        request.setPageSize(pageSize);
        request.setScoreLogStatus(ScoreTypeEnum.EXCHANGE_SCORE.val);

        Page<ScoreLog> scoreLogs = scoreService.getScoreLogs(request);
        ScoreLogPageResponse result = new ScoreLogPageResponse();
        result.setPage(page);
        result.setPageSize(pageSize);
        result.setTotal(scoreLogs.getTotalElements());
        result.setScoreLogs(ScoreLogWrapper.getWrappers(scoreLogs));

        return result;
    }

    public ScoreQueryResponse getScores(ScoreQueryRequest request) {

        Page<Score> scores = scoreService.getScores(request);
        ScoreQueryResponse result = new ScoreQueryResponse();
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(scores.getTotalElements());
        result.setScores(ScoreWrapper.getWrappers(scores));

        return result;
    }


    public ScoreWrapper getScoreWrapper(Customer customer){
        Score score = scoreService.getOne(customer);
        return new ScoreWrapper(score);
    }
    public ScoreResponse getScore(Customer customer) {
        Long lastMonthObtainScore = scoreService.getLastMonthObtainScore(customer);
        ScoreResponse scoreResponse = new ScoreResponse();
        Score score = customer.getScore();
        //Score score = scoreService.getOne(customer);
        if (score != null) {

            scoreResponse.setAvailableScore(score.calculateAvailableScore());
            scoreResponse.setExchangeScore(score.getExchangeScore());
            scoreResponse.setTotalScore(score.getTotalScore());
            scoreResponse.setLastMonthObtainScore(lastMonthObtainScore);
        }
        return scoreResponse;
    }

    @Transactional
    public Score saveScore(Score score) {

        return scoreService.saveScore(score);
    }

    @Transactional
    public ScoreLog saveScoreLog(ScoreLog scoreLog) {

        return scoreService.saveScoreLog(scoreLog);
    }

    public Score getOne(Customer customer) {

        return scoreService.getOne(customer);
    }


    @Transactional
    public ScoreWrapper scoreExchange(AdminUser adminUser , ScoreAdminExchange scoreExchange) throws Exception {

        Customer customer = new Customer();
        customer.setId(scoreExchange.getCustomerId());
        Score score = scoreService.getOne(customer);
        Coupon coupon = couponService.getCouponById(scoreExchange.getCouponId());

        if (coupon.getScore() == null ||scoreExchange.getCustomerId()==null || scoreExchange.getCount()<=0) {
            throw new ScoreExchangeException();
        }

        if(score.calculateAvailableScore() < coupon.getScore()*scoreExchange.getCount()){
            throw new ScoreNotEnoughException();
        }
        ScoreLogAddBean scoreLogAddBean = new ScoreLogAddBean();
        scoreLogAddBean.setScoreTypeRemark(ScoreTypeRemark.EXCHANGE);
        scoreLogAddBean.setScore(coupon.getScore() * scoreExchange.getCount());
        scoreLogAddBean.setCustomerId(scoreExchange.getCustomerId());
        scoreLogAddBean.setSender(adminUser);
        scoreLogAddBean.setCouponId(scoreExchange.getCouponId());
        scoreService.addScoreLog(scoreLogAddBean);

        for (int i = 0; i < scoreExchange.getCount(); i++) {

            CustomerCoupon customerCoupon = new CustomerCoupon();

            customerCoupon.setCustomer(customer);
            customerCoupon.setCoupon(coupon);

            Date start = new Date();

            Date end = DateUtils.truncate(DateUtils.addDays(start, 15), Calendar.DAY_OF_MONTH);

            customerCoupon.setSender(adminUser);
            customerCoupon.setSendDate(start);
            customerCoupon.setStart(start);
            customerCoupon.setEnd(end);

            couponService.saveCustomerCoupon(customerCoupon);
        }

        return new ScoreWrapper(score);
    }

    @Transactional
    public ScoreResponse scoreExchange(Customer customer , ScoreExchange scoreExchange) throws Exception {

        ScoreResponse scoreResponse = new ScoreResponse();

        Score score = customer.getScore();

        Coupon coupon = couponService.getCouponById(scoreExchange.getCouponId());

        if (coupon.getScore() == null ) {
            throw new ScoreExchangeException();
        }
        if(score.calculateAvailableScore() < coupon.getScore()*scoreExchange.getCount()){
            throw new ScoreNotEnoughException();
        }

        ScoreLog scoreLog = new ScoreLog();

        scoreLog.setIntegral(coupon.getScore() * scoreExchange.getCount());
        scoreLog.setScore(score);
        scoreLog.setStatus(ScoreTypeEnum.EXCHANGE_SCORE.val);
        scoreLog.setCoupon(coupon);
        scoreLog.setCreateTime(new Date());
        scoreLog.setCustomer(customer);
        scoreLog.setCount(scoreExchange.getCount());
        scoreLog.setRemark(ScoreTypeRemark.EXCHANGE.getRemark());
        scoreService.saveScoreLog(scoreLog);

        score.setUpdateTime(new Date());

        scoreService.saveScore(score);

        scoreService.increaseSalary(customer.getId(), coupon.getScore() * scoreExchange.getCount());

        for (int i = 0; i < scoreExchange.getCount(); i++) {

            CustomerCoupon customerCoupon = new CustomerCoupon();

            customerCoupon.setCustomer(customer);
            customerCoupon.setCoupon(coupon);

            Date start = new Date();

            Date end = DateUtils.truncate(DateUtils.addDays(start, 15), Calendar.DAY_OF_MONTH);

            customerCoupon.setSendDate(start);
            customerCoupon.setStart(start);
            customerCoupon.setEnd(end);

            couponService.saveCustomerCoupon(customerCoupon);
        }

        scoreResponse.setExchangeScore(score.getExchangeScore());
        scoreResponse.setAvailableScore(score.calculateAvailableScore());
        scoreResponse.setTotalScore(score.getTotalScore());

        return scoreResponse;

    }

    public List<SimpleCouponWrapper> getExchangeCoupon(Customer customer) {

        List<Coupon> coupons = couponService.getAvailableExchangeCouponCandidate(CouponConstant.EXCHANGE_COUPON.getType(), true, new Date(), customer);
        List<SimpleCouponWrapper> couponList = new ArrayList<>();
        for (Coupon coupon : coupons) {
            couponList.add(new SimpleCouponWrapper(coupon));
        }

        Collections.sort(couponList, new Comparator<SimpleCouponWrapper>() {
            @Override
            public int compare(SimpleCouponWrapper o1, SimpleCouponWrapper o2) {
                return o1.getScore().compareTo(o2.getScore());
            }

        });

        return couponList;
    }

    private final static Long scoreVal=10L; //评论一次所返的固定积分
    //评论返积分
    @Transactional(rollbackFor = Exception.class)
    public ScoreLogWrapper evaluateBakScore(Long orderId, AdminUser user) throws Exception {

        ScoreLog scoreLog = scoreService.evaluateBakScore(orderId, user);

        ScoreLog cScoreLog = this.scoreLogRepository.findOne(scoreLog.getId());
        return new ScoreLogWrapper(cScoreLog);
    }

    public ScoreQueryExchangeResponse queryExchangeCoupon(Long customerId) {
        Customer customer = customerService.getCustomerById(customerId);
        Score score = scoreService.getOne(customer);

        List<SimpleCouponWrapper> couponWrappers = this.getExchangeCoupon(customer);
        CustomerWrapper customerWrapper = new CustomerWrapper(customer);
        ScoreWrapper scoreWrapper = new ScoreWrapper(score);
        ScoreQueryExchangeResponse exchangeResponse = new ScoreQueryExchangeResponse(scoreWrapper,couponWrappers,customerWrapper);
        return exchangeResponse;
    }
}
