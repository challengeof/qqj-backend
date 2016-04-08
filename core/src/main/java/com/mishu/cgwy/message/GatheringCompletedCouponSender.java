package com.mishu.cgwy.message;

import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.service.StockOutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GatheringCompletedCouponSender extends CouponSender {

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private CompleteOrderCouponSender completeOrderCouponSender;

	@Transactional
    public void send(PromotionMessage promotionMessage) {
        List<Long> stockOutIds = promotionMessage.getStockOutIds();

        for (Long stockOutId : stockOutIds) {
            StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
            completeOrderCouponSender.send(stockOut.getOrder());
        }
    }
}
