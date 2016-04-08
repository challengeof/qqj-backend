package com.mishu.cgwy.order.wrapper;

import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import lombok.Data;

import java.util.Date;

/**
 * Created by bowen on 15-6-24.
 */
@Data
public class CustomerCouponWrapper {

    private Long id;

    private CouponStatus status;

    private CustomerWrapper customer;

    private SimpleCouponWrapper coupon;

    private Date start;

    private Date end;

    private String operater;  //状态 操作人

    private Date operateTime;  // 操作时间

    public CustomerCouponWrapper() {

    }

    public CustomerCouponWrapper(CustomerCoupon customerCoupon) {
        this.id = customerCoupon.getId();
        this.customer = new CustomerWrapper(customerCoupon.getCustomer());
        this.coupon = new SimpleCouponWrapper(customerCoupon.getCoupon());
        this.start = customerCoupon.getStart();
        this.end = customerCoupon.getEnd();
        this.status = CouponStatus.fromInt(customerCoupon.getStatus());


        if(null!=customerCoupon.getOperater()){
            this.operater=customerCoupon.getOperater().getRealname();
        }
        this.operateTime=customerCoupon.getOperateTime();


    }
}
