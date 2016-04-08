package com.mishu.cgwy.admin.controller;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.bonus.controller.SalesmanStatistics;
import com.mishu.cgwy.bonus.controller.SalesmanStatisticsRequest;
import com.mishu.cgwy.bonus.facade.CustomerServiceStatisticsFacade;
import com.mishu.cgwy.bonus.vo.CustomerServiceStatisticsVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * User: xudong
 * Date: 5/31/15
 * Time: 2:11 PM
 */
@Controller
public class PerformanceController {

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }


    @Autowired
    private CustomerServiceStatisticsFacade customerServiceStatisticsFacade;

    @RequestMapping(value = "/api/performance", method = RequestMethod.GET)
    @ResponseBody
    public List<CustomerServiceStatisticsVo> getPerformance(@RequestParam("month") Date month) {
        return customerServiceStatisticsFacade.getCustomerServiceStatistics(month);
    }

    @RequestMapping(value = "/api/refresh-bonus", method = RequestMethod.GET)
    @ResponseBody
    public String refreshBonus(@RequestParam("date") Date date) {
        customerServiceStatisticsFacade.refreshBonus(date);
        return "success";
    }

    @RequestMapping(value = "/api/refresh-performance", method = RequestMethod.GET)
    @ResponseBody
    public String refreshPerformance(@RequestParam("month") Date month) {
        customerServiceStatisticsFacade.refreshStatistics(month);
        return "success";
    }

    @RequestMapping(value = "/api/salesman-statistics", method = RequestMethod.GET)
    @ResponseBody
    public List<SalesmanStatistics> getSalesmanStatistics(SalesmanStatisticsRequest request ,@CurrentAdminUser AdminUser operator) {

       /* SalesmanStatisticsRequest salesmanStatisticsRequest = new SalesmanStatisticsRequest();
        //salesmanStatisticsRequest.setStart(DateUtils.addDays(new Date(), -20));
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = simpleDateFormat.parse("2015-08-01");
            salesmanStatisticsRequest.setStart(start);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        salesmanStatisticsRequest.setEnd(new Date());*/

        return customerServiceStatisticsFacade.getSalesmanStatistics(request, operator);
    }
}
