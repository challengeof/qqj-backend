package com.mishu.cgwy.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.mishu.cgwy.coupon.domain.Coupon;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.repository.CouponRepository;
import com.mishu.cgwy.coupon.repository.CustomerCouponRepository;

public class CouponSender {
	
    private Logger logger = LoggerFactory.getLogger(CouponSender.class);   

	@Autowired
	protected CustomerCouponRepository customerCouponRepository;
	
	@Autowired
    protected CustomerService customerService;

	@Autowired
	protected RestaurantService restaurantService;

	@Autowired
	protected CouponRepository couponRepository;

	@Autowired
	protected OrderService orderService;

    @Autowired
    protected CouponService couponService;
	
	private static int batchSize = 100;

	/**
	 * @param entities 待发优惠券list
	 * @param unique 优惠券是否可以多次发送，unique为true表示只发一次。
	 */
	@Transactional
	public void bulkSave(List<CustomerCoupon> entities, boolean unique) {
		int i = 0;
		for (CustomerCoupon customerCoupon : entities) {

			if (needSend(unique, customerCoupon)) {
				customerCouponRepository.save(customerCoupon);
				i++;
			}
			
			if (i % batchSize == 0) {
				customerCouponRepository.flush();
			}
		}
	}
	
	private boolean needSend(boolean unique, CustomerCoupon customerCoupon) {
		if (unique) {
			List<CustomerCoupon> customerCoupons = customerCouponRepository.findByCustomerAndCoupon(customerCoupon.getCustomer(), customerCoupon.getCoupon());
			if (CollectionUtils.isNotEmpty(customerCoupons)) {
				return false;
			}
		}
		return true;
	}
	
	public void send(PromotionMessage promotionMessage){
		logger.info(String.format("message abandoned: %s", promotionMessage));
	};
	
    protected void sendCouponToCustomer(Customer customer, Coupon coupon, Date start, Date end, boolean unique) {

        List<CustomerCoupon> customerCoupons = new ArrayList<>();


        CustomerCoupon customerCoupon = new CustomerCoupon();
        customerCoupon.setSendDate(new Date());
        customerCoupon.setStatus(CouponStatus.UNUSED.getValue());
        customerCoupon.setCoupon(coupon);
        customerCoupon.setCustomer(customer);
        customerCoupon.setStart(start);
        customerCoupon.setEnd(end);
        customerCoupons.add(customerCoupon);

        bulkSave(customerCoupons, unique);
    }
}
