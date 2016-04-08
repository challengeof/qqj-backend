package com.mishu.cgwy.score.Wrapper;

import com.mishu.cgwy.order.wrapper.CouponWrapper;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.stock.wrapper.StockOutWrapper;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2015/11/11.
 */
@Data
public class ScoreLogWrapper {


    private Long id;

    private Long integral;

    private Date createTime;

    private String remark;

    private int status;

    private int count;

    private StockOutWrapper stockOut;

    private OrderWrapper order;

    private CustomerWrapper customer;

    private String sender;

    private CouponWrapper coupon;

    private ScoreWrapper score;


    public static List<ScoreLogWrapper> getWrappers(Page<ScoreLog> scoreLogs) throws Exception {
        List<ScoreLogWrapper> logWrappers = new ArrayList<>();
        for(ScoreLog scorelog : scoreLogs){
            logWrappers.add(new ScoreLogWrapper(scorelog));
        }
        return logWrappers;
    }

    public ScoreLogWrapper(ScoreLog scoreLog) throws Exception {
        this.id=scoreLog.getId();
        this.integral=scoreLog.getIntegral();
        this.createTime=scoreLog.getCreateTime();
        this.remark=scoreLog.getRemark();

        this.status=scoreLog.getStatus();

        this.count=scoreLog.getCount();

        if(scoreLog.getSender()!=null){
            this.sender = scoreLog.getSender().getRealname();
        }
        if(scoreLog.getStockOut()!=null){
            this.stockOut=new StockOutWrapper(scoreLog.getStockOut());
        }else if(scoreLog.getOrder()!=null && scoreLog.getOrder().getStockOuts()!=null && scoreLog.getOrder().getStockOuts().size()!=0){
            this.stockOut=new StockOutWrapper(scoreLog.getOrder().getStockOuts().get(0));
        }
        if(scoreLog.getOrder()!=null){
            this.order= new OrderWrapper();
            this.order.setId(scoreLog.getOrder().getId());
            this.order.setSubmitDate(scoreLog.getOrder().getSubmitDate());

        }

        if(scoreLog.getCustomer()!=null){
            this.customer=new CustomerWrapper(scoreLog.getCustomer());
        }
        if(scoreLog.getCoupon()!=null){
            this.coupon=new CouponWrapper(scoreLog.getCoupon());
        }
        if(scoreLog.getScore()!=null){
            this.score=new ScoreWrapper(scoreLog.getScore());
        }

    }




}
