package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by bowen on 15/8/28.
 */
@Data
public class SimpleOrderGroupResponse {

    private Long id;

    private String name;

    private BigDecimal sumOfTotal = BigDecimal.ZERO;

    private BigDecimal sumOfSubTotal = BigDecimal.ZERO;

//    private Date expectedArrivedDate;

    private String tracker;

    private boolean checkResult;

    public SimpleOrderGroupResponse() {

    }

    public SimpleOrderGroupResponse(OrderGroup orderGroup) {

        id = orderGroup.getId();

        name = orderGroup.getName();

        for (Order member : orderGroup.getMembers()) {

            sumOfTotal = sumOfTotal.add(member.getTotal());

            sumOfSubTotal = sumOfSubTotal.add(member.getSubTotal());
        }

//        expectedArrivedDate = orderGroup.getExpectedArrivedDate();

        tracker = orderGroup.getTracker() == null ? "NO Tracker" : orderGroup.getTracker().getRealname();

        checkResult = orderGroup.isCheckResult();
    }
}
