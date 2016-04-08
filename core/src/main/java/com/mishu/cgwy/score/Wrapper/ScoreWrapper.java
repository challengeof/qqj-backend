package com.mishu.cgwy.score.Wrapper;

import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.score.domain.Score;
import com.mishu.cgwy.score.domain.ScoreLog;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by king-ck on 2015/11/10.
 */
@Data
public class ScoreWrapper {

    private Long id;

    private Long totalScore;

    private Long exchangeScore;

    private Date createTime;

    private Date updateTime;

    private CustomerWrapper customer;

    public ScoreWrapper(){}

    public static List<ScoreWrapper> getWrappers(Page<Score> scores){
        List<ScoreWrapper> scoreWrappers = new ArrayList<>();
        for(Score score : scores){
            scoreWrappers.add(new ScoreWrapper(score));
        }
        return scoreWrappers;
    }

    public ScoreWrapper(Score score){
        this.id=score.getId();
        this.totalScore=score.getTotalScore();
        this.exchangeScore=score.getExchangeScore();
        this.createTime=score.getCreateTime();
        this.updateTime=score.getUpdateTime();

        this.customer=new CustomerWrapper(score.getCustomer());
    }

}
