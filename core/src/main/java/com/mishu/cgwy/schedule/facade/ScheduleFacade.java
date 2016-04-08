package com.mishu.cgwy.schedule.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.SystemEmail;
import com.mishu.cgwy.common.domain.SystemEmailType;
import com.mishu.cgwy.common.service.SystemEmailService;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.ScoreExchangeException;
import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.product.service.SkuSalesStatisticsService;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.push.service.DailyPushService;
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
import com.mishu.cgwy.search.SearchService;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockTotalDaily;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.stock.service.StockTotalDailyService;
import com.mishu.cgwy.task.domain.Task;
import com.mishu.cgwy.task.service.TaskService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by bowen on 15/11/10.
 */
@Service
public class ScheduleFacade {
    private static Logger logger = LoggerFactory.getLogger(ScheduleFacade.class);
    @Autowired
    private ScoreService scoreService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private ScoreLogRepository scoreLogRepository;

    @Autowired
    private TaskService taskService;

    @Autowired(required = false)
    private SearchService searchService;

    @Autowired
    private SkuSalesStatisticsService skuSalesStatisticsService;

    @Autowired
    private DailyPushService dailyPushService;

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private SystemEmailService systemEmailService;

    @Autowired
    private StockTotalDailyService stockTotalDailyService;

    private ExecutorService taskExecutor = Executors.newCachedThreadPool();
    @Autowired(required=false)
    private JavaMailSender mailSender;

    public ScoreSumResponse orderScoreSum(ScoreSumRequest request) {
        return scoreService.getOrderScoreSum(request);
    }

    /**
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

    public ScoreLogPageResponse getObtainScoreDetail(Customer customer, int page, int pageSize) throws Exception {

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
    public ScoreResponse scoreExchange(Customer customer, ScoreExchange scoreExchange) throws Exception {

        ScoreResponse scoreResponse = new ScoreResponse();

        Score score = customer.getScore();

        Coupon coupon = couponService.getCouponById(scoreExchange.getCouponId());

        if (coupon.getScore() == null || score.calculateAvailableScore() < coupon.getScore()) {

            throw new ScoreExchangeException();

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

    private final static Long scoreVal = 10L; //评论一次所返的固定积分

    //评论返积分
    @Transactional(rollbackFor = Exception.class)
    public ScoreLogWrapper evaluateBakScore(Long orderId, AdminUser user) throws Exception {

        ScoreLog scoreLog = scoreService.evaluateBakScore(orderId, user);

        ScoreLog cScoreLog = this.scoreLogRepository.findOne(scoreLog.getId());
        return new ScoreLogWrapper(cScoreLog);
    }

    public void deleteExcels() {

        Date dateToDelete = DateUtils.truncate(DateUtils.addDays(new Date(), -2), Calendar.DATE);
        List<Task> taskList = taskService.findBySubmitDateLessThan(dateToDelete);

        for (Task task : taskList) {
            taskService.delete(task.getId());
        }

        File excelFolder = new File(ExportExcelUtils.excelFolderName);
        if (excelFolder.exists() && excelFolder.isDirectory()) {
            File[] files = excelFolder.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.isFile() && file.lastModified() < dateToDelete.getTime()) {
                        file.delete();
                    }
                }
            }
        }
    }

    public void rebuildIndex() throws Exception {
        searchService.rebuildIndex();
        skuSalesStatisticsService.refreshSkuSalesStatistics();
    }

    public void dailyPush() {
        dailyPushService.dailyPush();
    }

    public List<StockTotalDaily> saveStockTotalDaily() {
        return stockTotalDailyService.saveStockTotalDaily();
    }

    public void sendNotReceiveOrderMail () {
        List<StockOut> stockOuts = stockOutService.getNotReceiveStockOuts(-3);
        if (stockOuts == null || stockOuts.isEmpty()) {
            return;
        }
        Map<Long, List<StockOut>> map = new HashMap<>();
        for (StockOut stockOut : stockOuts) {
            if (map.containsKey(stockOut.getDepot().getCity().getId())) {
                List<StockOut> tmpStockOuts = map.get(stockOut.getDepot().getCity().getId());
                tmpStockOuts.add(stockOut);
            } else {
                List<StockOut> tmpStockOuts = new ArrayList<>();
                tmpStockOuts.add(stockOut);
                map.put(stockOut.getDepot().getCity().getId(), tmpStockOuts);
            }
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (Map.Entry<Long, List<StockOut>> entry : map.entrySet()) {
            List<StockOut> mapOuts = entry.getValue();
            Long cityId = entry.getKey();
            String cityName = null;

            SystemEmail systemEmail = getSystemEmail(cityId, SystemEmailType.NOTRECEIVE.getValue());
            if (systemEmail == null || StringUtils.isBlank(systemEmail.getSendTo())) {
                continue;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("<table><tr><td>订单号</td><td>出库单号</td><td>餐馆</td><td>销售员</td><td>金额</td><td>出库时间</td><td>司机</td></tr>");
            for (StockOut stockOut : mapOuts) {
                if (cityName == null) {
                    cityName = stockOut.getDepot().getCity().getName();
                }
                sb.append("<tr>");
                sb.append("<td>");
                sb.append(stockOut.getOrder().getId());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(stockOut.getId());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(stockOut.getOrder().getRestaurant().getName());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(stockOut.getOrder().getAdminUser() != null ? stockOut.getOrder().getAdminUser().getRealname() : "");
                sb.append("</td>");
                sb.append("<td>");
                sb.append(stockOut.getReceiveAmount());
                sb.append("</td>");
                sb.append("<td>");
                sb.append(sdf.format(stockOut.getFinishDate()));
                sb.append("</td>");
                sb.append("<td>");
                if (stockOut.getOrderGroup() != null && stockOut.getOrderGroup().getTracker() != null) {
                    sb.append(stockOut.getOrderGroup().getTracker().getRealname());
                }
                sb.append("</td>");
                sb.append("</tr>");
            }
            sb.append("</table>");

            final String subject = (cityName != null ? cityName : "") + "-出库超过3天没收货订单";
            final String to = systemEmail.getSendTo();
            final String cc = systemEmail.getSendCc();
            final String content = sb.toString();
            String[] arrCC = StringUtils.isNotBlank(cc) ? cc.split(",") : new String[0];
            sendMail(null, subject, to.split(","), arrCC, content);
        }
    }

    private SystemEmail getSystemEmail (Long cityId, int type) {
        return systemEmailService.findSystemEmailByCityAndType(cityId, type);
    }

    private void sendMail(final List<File> attachments, final String subject, final String[] to, final String[] cc, final String text) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                try {

                    System.setProperty("mail.mime.encodefilename", "true");
                    System.setProperty("mail.mime.decodefilename", "true");

                    MimeMessage mimeMessage = mailSender.createMimeMessage();
                    final MimeMessageHelper message = new MimeMessageHelper(mimeMessage, true, "utf-8");
                    message.setFrom("jiqiren@canguanwuyou.cn");
                    message.setTo(to);
                    message.setCc(cc);
                    message.setSubject(subject);
                    message.setText(text, true);
                    if (attachments != null) {
                        for (File file : attachments) {
                            message.addAttachment(file.getName(), file);
                        }
                    }

                    mailSender.send(mimeMessage);

                } catch (MessagingException e) {
                    logger.error("send mail failed", e);
                }

            }
        });

    }
}
