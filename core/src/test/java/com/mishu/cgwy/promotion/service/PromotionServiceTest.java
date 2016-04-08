package com.mishu.cgwy.promotion.service;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.promotion.domain.Promotion;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Date;

public class PromotionServiceTest {

    private PromotionService promotionService;

    @Before
    public void setup() {
        promotionService = new PromotionService();
    }

    @Test
    public void testFindApplicablePromotion() throws Exception {

        Promotion promotion = new Promotion();
        promotion.setDiscount(BigDecimal.TEN);
        promotion.setStart(DateUtils.addDays(new Date(), -1));
        promotion.setEnd(DateUtils.addDays(new Date(), 1));

        promotion.setRule("order.getSubTotal()>50");

        Order order = new Order();
        order.setSubTotal(BigDecimal.valueOf(400));

        //final boolean b = promotionService.couldOfferApplyToOrder(promotion, order, null);
        //Assert.assertTrue(b);


    }
}