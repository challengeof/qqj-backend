package com.mishu.cgwy.score.service.recive;

import com.mishu.cgwy.message.score.ScoreMessage;
import com.mishu.cgwy.score.constants.ScoreTypeRemark;
import com.mishu.cgwy.score.vo.ScoreLogAddBean;
import com.mishu.cgwy.score.service.ScoreService;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.repository.StockOutRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 订单收款后-赠送积分（）
 * Created by king-ck on 2015/11/17.
 */
@Component
public class OrderReciveNotice extends ScoreMessageNotice<ScoreMessage> {
    private Logger logger = LoggerFactory.getLogger(getClass());
    @Autowired
    private StockOutRepository stockOutRepository;
    @Autowired
    private ScoreService scoreService;

    @Override
    public void addScore(ScoreMessage addScoreParam) {
        try {
            StockOut stockOut = this.stockOutRepository.findOne(addScoreParam.getStockOutId());
            //判断订单收款后积分赠送
            if (stockOut != null && stockOut.getStatus() == StockOutStatus.FINISHED.getValue()
                    && stockOut.getReceiveDate() != null && stockOut.getReceiveAmount() != null) {

                ScoreLogAddBean addBean = this.getScoreLogAddBean(stockOut);
                scoreService.addScoreLog(addBean);
            }
        }catch( Exception ex ){
            logger.error(addScoreParam.toString(),ex);
        }
    }

    private ScoreLogAddBean getScoreLogAddBean(StockOut stockOut){
        long scoreVal = this.parseScore(stockOut);
        ScoreLogAddBean addBean = new ScoreLogAddBean();
        addBean.setCustomerId(stockOut.getOrder().getCustomer().getId());
        addBean.setScore(scoreVal);
        addBean.setStockOutId(stockOut.getId());
        addBean.setScoreTypeRemark(ScoreTypeRemark.ORDER_COMPLETE);
        return addBean;
    }

    private long parseScore(StockOut stockOut){
        //1块钱==1积分，  小数点省略
        return stockOut.getReceiveAmount().longValue();
    }

}
