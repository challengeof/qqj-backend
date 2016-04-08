package com.mishu.cgwy.stock.wrapper;

import com.mishu.cgwy.order.domain.OrderGroup;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 15/10/26.
 */
@Data
public class SimpleStockOutGroupWrapper {

    private Long id;

    private String tracker;

    private String name;

//    private Date expectedArrivedDate;

    private BigDecimal sumOfTotal = BigDecimal.ZERO;

    private boolean checkResult;

    private List<GroupStockOutWrapper> members = new ArrayList<>();

    public SimpleStockOutGroupWrapper() {

    }

    public SimpleStockOutGroupWrapper(OrderGroup orderGroup) {
        this.id = orderGroup.getId();
        this.tracker = orderGroup.getTracker() == null ? "NO Tracker" : orderGroup.getTracker().getRealname();
//        this.expectedArrivedDate = orderGroup.getExpectedArrivedDate();
        this.name = orderGroup.getName();
        this.checkResult = orderGroup.isCheckResult();
    }
}
