package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.StockOut;
import lombok.Data;
import org.apache.commons.lang.time.DateFormatUtils;

import java.math.BigDecimal;

/**
 * Created by xiao1zhao2 on 15/9/16.
 */
@Data
public class IncomeDailyReportWrapper {

    private BigDecimal orderAmount;
    private BigDecimal returnAmount;
    private BigDecimal receivableAmount;
    private BigDecimal actualAmount;
    private BigDecimal liability;
    private BigDecimal cleanedLiability;

    public IncomeDailyReportWrapper() {
        this.orderAmount = BigDecimal.ZERO;
        this.returnAmount = BigDecimal.ZERO;
        this.receivableAmount = BigDecimal.ZERO;
        this.actualAmount = BigDecimal.ZERO;
        this.liability = BigDecimal.ZERO;
        this.cleanedLiability = BigDecimal.ZERO;
    }

    public IncomeDailyReportWrapper(StockOut stockOut) {
        this.orderAmount = stockOut.getOrder().getTotal();
        this.returnAmount = stockOut.getAmount().subtract(stockOut.getReceiveAmount());
        this.receivableAmount = stockOut.getReceiveAmount();
        if (stockOut.isSettle()) {
            this.actualAmount = stockOut.getReceiveAmount();
            this.liability = BigDecimal.ZERO;
        } else {
            this.actualAmount = BigDecimal.ZERO;
            this.liability = stockOut.getReceiveAmount();
        }
        this.cleanedLiability = BigDecimal.ZERO;
    }

    public void merge(IncomeDailyReportWrapper wrapper) {
        this.orderAmount = this.orderAmount.add(wrapper.orderAmount);
        this.returnAmount = this.returnAmount.add(wrapper.returnAmount);
        this.receivableAmount = this.receivableAmount.add(wrapper.receivableAmount);
        this.actualAmount = this.actualAmount.add(wrapper.actualAmount);
        this.liability = this.liability.add(wrapper.liability);
        this.cleanedLiability = this.cleanedLiability.add(wrapper.cleanedLiability);
    }

}
