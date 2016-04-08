package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.stock.domain.SellReturnReason;
import lombok.Data;

/**
 * Created by wangwei on 15/10/12.
 */
@Data
public class SellReturnReasonWrapper {

    private Long id;
    private String reason;

    public SellReturnReasonWrapper() {

    }

    public SellReturnReasonWrapper(SellReturnReason sellReturnReason) {
        this.id = sellReturnReason.getId();
        this.reason = sellReturnReason.getReason();
    }
}
