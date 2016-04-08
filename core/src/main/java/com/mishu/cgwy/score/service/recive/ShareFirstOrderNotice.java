package com.mishu.cgwy.score.service.recive;

import com.mishu.cgwy.coupon.constant.ShareTypeEnum;
import com.mishu.cgwy.coupon.domain.Share;
import com.mishu.cgwy.coupon.service.ShareService;
import com.mishu.cgwy.message.score.ScoreMessage;
import com.mishu.cgwy.score.constants.ScoreTypeRemark;
import com.mishu.cgwy.score.service.ScoreService;
import com.mishu.cgwy.score.vo.ScoreLogAddBean;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.repository.StockOutRepository;
import com.mishu.cgwy.stock.service.StockOutService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 分享注册后首单赠送积分
 * Created by king-ck on 2015/11/17.
 */
@Component
public class ShareFirstOrderNotice extends ScoreMessageNotice<ScoreMessage> {

    private Logger logger = LoggerFactory.getLogger(getClass());

    public final double N_TIMES=1;  //积分倍数
    @Autowired
    private StockOutRepository stockOutRepository;
    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private ScoreService scoreService;
    @Autowired
    private ShareService shareService;

    @Override
    public void addScore(ScoreMessage addScoreParam) {
        try {
            StockOut stockOut = this.stockOutRepository.findOne(addScoreParam.getStockOutId());
            if (stockOut != null && stockOut.getStatus() == StockOutStatus.FINISHED.getValue()
                    && stockOut.getReceiveDate() != null && stockOut.getReceiveAmount() != null) {

                Long customerId = stockOut.getOrder().getCustomer().getId();

                Share share = shareService.findShare(customerId, ShareTypeEnum.scoreShareRegister);
                if(share==null){
                    return ;
                }
                //注册完成的首单赠送
                boolean isFirst = stockOutService.checkIsFirstByThisCustomer(addScoreParam.getStockOutId());

                if (isFirst) {
                    ScoreLogAddBean byReference = this.getScoreLogByReference(customerId,stockOut);
                    ScoreLogAddBean byCurrent = this.getScoreLogByCurrent(stockOut);

                    //送积分给分享者和被分享者
                    scoreService.addScoreLog(byReference,byCurrent);
                }
            }
        }catch( Exception ex ){
            logger.error(addScoreParam.toString(),ex);
        }
    }

    /**
     * 分享者获得积分的设置
      * @param customerId
     * @param stockOut
     * @return
     */
    private ScoreLogAddBean getScoreLogByReference(Long customerId, StockOut stockOut){
        long scoreVal = this.parseScore(stockOut);
        ScoreLogAddBean addBean = new ScoreLogAddBean();
        addBean.setCustomerId(customerId);
        addBean.setScore(scoreVal);
        addBean.setStockOutId(stockOut.getId());
        addBean.setBeSharedCustomerId(stockOut.getOrder().getCustomer().getId());
        addBean.setScoreTypeRemark(ScoreTypeRemark.SHARER);
        return addBean;
    }


    /**
     *  接受分享的人获得积分的设置
     * @param stockOut
     * @return
     */
    private ScoreLogAddBean getScoreLogByCurrent(StockOut stockOut){
        long scoreVal = this.parseScore(stockOut);
        ScoreLogAddBean addBean = new ScoreLogAddBean();
        addBean.setCustomerId(stockOut.getOrder().getCustomer().getId());
        addBean.setScore(scoreVal);
        addBean.setStockOutId(stockOut.getId());
        addBean.setScoreTypeRemark(ScoreTypeRemark.BE_SHARE);
        return addBean;
    }


    private long parseScore(StockOut stockOut){
        //1块钱==1积分  n倍，  小数点省略
        return (long)(stockOut.getReceiveAmount().doubleValue()*N_TIMES);
    }
}
