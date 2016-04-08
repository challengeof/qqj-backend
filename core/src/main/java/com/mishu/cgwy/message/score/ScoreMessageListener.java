package com.mishu.cgwy.message.score;

import com.mishu.cgwy.score.service.ScoreMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;
import java.io.Serializable;

/**
 * Created by king-ck on 2015/11/19.
 */
public class ScoreMessageListener  implements MessageListener{
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ScoreMessageService scoreMessageService;

    @Override
    public void onMessage(Message message) {
        try {
            Serializable msg =((ObjectMessage) message).getObject();
            if(msg instanceof ScoreMessage){
                this.singleIdOnMessage((ScoreMessage) msg);
            }else if(msg instanceof  ScoreListMessage){
                this.moreIdOnMessage((ScoreListMessage) msg);
            }
        }catch (Exception ex){
            logger.error(String.format("scoreMessage: %s",message),ex);
        }

    }


    /**
     * 保留代码用于兼容队列里的旧数据
     */
    @Deprecated
    private void singleIdOnMessage(ScoreMessage message) {
        scoreMessageService.addScoreNotice(message);
    }

    private void moreIdOnMessage(ScoreListMessage message) {
            for(Long soId : message.getStockOutIds()){
                try {
                    ScoreMessage smessage = new ScoreMessage(soId);
                    scoreMessageService.addScoreNotice(smessage);
                }catch (Exception ex){
                    logger.error(String.format("scoreMessage  stockoutid: %s",soId),ex);
                }
            }
    }

}
