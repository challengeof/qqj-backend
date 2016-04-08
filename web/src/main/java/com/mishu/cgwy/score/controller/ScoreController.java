package com.mishu.cgwy.score.controller;

import com.mishu.cgwy.order.wrapper.SimpleCouponWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.score.Wrapper.ScoreResponse;
import com.mishu.cgwy.score.facade.ScoreFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by bowen on 15/11/10.
 */
@Controller
public class ScoreController {

    @Autowired
    private ScoreFacade scoreFacade;

    @RequestMapping(value = "/api/v2/score", method = RequestMethod.GET)
    @ResponseBody
    public ScoreResponse getScore(@CurrentCustomer Customer customer) {

        return scoreFacade.getScore(customer);
    }

    @RequestMapping(value = "/api/v2/score/exchange", method = RequestMethod.PUT)
    @ResponseBody
    public ScoreResponse scoreExchange(@CurrentCustomer Customer customer ,@RequestBody ScoreExchange scoreExchange) throws Exception {

        return scoreFacade.scoreExchange(customer, scoreExchange);
    }

    @RequestMapping(value = "/api/v2/score/exchange-coupon" , method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleCouponWrapper> getExchangeCoupon(@CurrentCustomer Customer customer) {

        return scoreFacade.getExchangeCoupon(customer);
    }

    @RequestMapping(value = "/api/v2/score/obtain/score-detail", method = RequestMethod.GET)
    @ResponseBody
    public ScoreLogPageResponse getObtainScoreDetail(@CurrentCustomer Customer customer , @RequestParam(value = "page") int page, @RequestParam(value = "pageSize") int pageSize) throws Exception {

        return scoreFacade.getObtainScoreDetail(customer, page, pageSize);
    }

    @RequestMapping(value = "/api/v2/score/exchange/score-detail", method = RequestMethod.GET)
    @ResponseBody
    public ScoreLogPageResponse getExchangeScoreDetail(@CurrentCustomer Customer customer, @RequestParam(value = "page") int page, @RequestParam(value = "pageSize") int pageSize) throws Exception {

        return scoreFacade.getExchangeScoreDetail(customer, page, pageSize);
    }
}
