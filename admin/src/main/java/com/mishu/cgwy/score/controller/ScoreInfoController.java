package com.mishu.cgwy.score.controller;

import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.score.facade.ScoreFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by king-ck on 2015/11/12.
 */
@Controller
public class ScoreInfoController {
        @Autowired
        private ScoreFacade scoreFacade;

        @Autowired
        private CustomerService customerService;

        @InitBinder
        public void initBinder(WebDataBinder binder) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
        }

        @RequestMapping(value = "/api/scoreLog/query",method = RequestMethod.GET)
        @ResponseBody
        public ScoreLogPageResponse queryScoreLogs(ScoreLogQueryRequest request) throws Exception {

                return scoreFacade.getScoreLogs(request);
        }

        @RequestMapping(value = "/api/score/query",method = RequestMethod.GET)
        @ResponseBody
        public ScoreQueryResponse queryScores(ScoreQueryRequest request) {
                return scoreFacade.getScores(request);
        }


        /**
         * 积分明细- 订单实收金额合计， 订单积分合计
         */
        @RequestMapping(value = "/api/score/order/sum",method = RequestMethod.GET)
        @ResponseBody
        public ScoreSumResponse orderScoreSumQuery(HttpServletRequest requesth,ScoreSumRequest request){

                return scoreFacade.orderScoreSum(request);
        }


        @RequestMapping(value = "/api/score/exchange-coupon/{customerId}",method = RequestMethod.GET)
        @ResponseBody
        public ScoreQueryExchangeResponse queryExchangeCoupon(@PathVariable("customerId") Long customerId){

                return scoreFacade.queryExchangeCoupon(customerId);
        }


        @RequestMapping(value = "/api/score/exchange", method = RequestMethod.PUT)
        @ResponseBody
        public ScoreQueryExchangeResponse scoreExchange(@RequestBody ScoreAdminExchange scoreAdminExchange, @CurrentAdminUser AdminUser adminUser) throws Exception {

                scoreFacade.scoreExchange(adminUser, scoreAdminExchange);
                return scoreFacade.queryExchangeCoupon(scoreAdminExchange.getCustomerId());
        }
}
