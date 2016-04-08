package com.mishu.cgwy;

import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.score.controller.ScoreLogQueryRequest;
import com.mishu.cgwy.score.domain.ScoreLog;
import com.mishu.cgwy.score.service.ScoreService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.transaction.Transactional;

/**
 * Created by king-ck on 2015/11/13.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml"})
public class ScoreTest {


    @Autowired
    private ScoreService scoreService;



    @Test
    public void testRegister() {

        ScoreLogQueryRequest queryRequest = new ScoreLogQueryRequest();
        queryRequest.setCityId(1L);
        queryRequest.setRestaurantName("name");
        Page<ScoreLog> slogs = scoreService.getScoreLogs(queryRequest);
        System.out.println(slogs);
    }
}