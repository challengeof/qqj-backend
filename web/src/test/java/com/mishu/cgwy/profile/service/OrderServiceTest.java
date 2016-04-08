package com.mishu.cgwy.profile.service;

import javax.transaction.Transactional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.profile.domain.Restaurant;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml", "/application-search.xml"})
public class OrderServiceTest {
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private OrderService orderService; 
	
	/*@Test
	@Transactional()
	public void testgetOrderCountByRestaurant(){
		Restaurant restaurant = customerService.getRestaurantById(4057L);
		Long integer = orderService.getOrderCountByRestaurant(restaurant);
		System.out.println(integer);
	}*/

}
