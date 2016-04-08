package com.mishu.cgwy.score.service;

import com.mishu.cgwy.message.score.ScoreMessage;
import com.mishu.cgwy.score.constants.ScoreTypeEnum;
import com.mishu.cgwy.score.service.recive.ScoreMessageNotice;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Component;

/**
 * Created by king-ck on 2015/11/19.
 */
@Component
public class ScoreMessageService implements BeanFactoryAware {

    private BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory=beanFactory;
    }

    /**
     * 通知增加积分
     * @param addsp
     */
    public void addScoreNotice(ScoreMessage addsp){

        for(ScoreTypeEnum scoreType :  ScoreTypeEnum.values()){
            if(scoreType.scoreMessageReciveClass!=null){
                ScoreMessageNotice scoreMessageNotice = beanFactory.getBean(scoreType.scoreMessageReciveClass);
                scoreMessageNotice.addScore(addsp);
            }
        }

    }


}
