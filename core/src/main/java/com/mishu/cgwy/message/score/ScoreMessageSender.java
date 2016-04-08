package com.mishu.cgwy.message.score;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

/**
 * Created by king-ck on 2015/11/19.
 */
@Component
public class ScoreMessageSender {

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private JmsTemplate scoreMessagejmsTemplate;


//    @Deprecated
//    public void send(final ScoreMessage scoreMessage){
//        logger.info("sendScoreMessage stockoutid:"+scoreMessage.getStockOutId());
//        scoreMessagejmsTemplate.send(new MessageCreator() {
//            @Override
//            public Message createMessage(Session session) throws JMSException {
//                return session.createObjectMessage(scoreMessage);
//            }
//        });
//        logger.debug(String.format("send scoremessage end : %s",scoreMessage));
//    }

    public void send(final ScoreListMessage scoreListMessage){

        scoreMessagejmsTemplate.send(new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(scoreListMessage);
            }
        });

    }

}
