package com.mishu.cgwy.order.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Region;
import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.CustomerService;
import org.apache.commons.lang.time.DateUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.util.Arrays;
import java.util.HashSet;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class OrderServiceTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private AdminUserService adminUserService;

    private Customer customer;
    private AdminUser adminUser;


    @Before
    public void setup() throws ParseException {
        City city = new City();
        locationService.saveCity(city);

        Region region = new Region();
        region.setCity(city);
        locationService.saveRegion(region);

        Zone zone = new Zone();
        zone.setRegion(region);
        locationService.saveZone(zone);


        adminUser = new AdminUser();
        adminUser.setUsername("u");
        adminUser.setPassword("p");
        adminUser.setRealname("u");

        adminUserService.register(adminUser);

        customer = new Customer();
        customer.setUsername("u");
        customer.setPassword("u");
//        customer.setZone(zone);
        customer.setAdminUser(adminUser);

        customerService.register(customer);



        Restaurant r = new Restaurant();
        r.setStatus(RestaurantStatus.ACTIVE.getValue());
        r.setCustomer(customer);

        Address address = new Address();
//        address.setZone(zone);
        r.setAddress(address);

        customerService.saveRestaurant(r);



        Order order1 = new Order();

        order1.setStatus(OrderStatus.UNCOMMITTED.getValue());

        order1.setCustomer(customer);
        order1.setSubmitDate(DateUtils.parseDate("20150401 12:00:00", new String[]{"yyyyMMdd HH:mm:ss"}));
        order1.setRestaurant(r);

        orderService.saveOrder(order1);

        Order order2 = new Order();
        order2.setStatus(OrderStatus.COMMITTED.getValue());
        order2.setRestaurant(r);

        order2.setCustomer(customer);
        order2.setSubmitDate(DateUtils.parseDate("20150402 12:00:00", new String[]{"yyyyMMdd HH:mm:ss"}));

        orderService.saveOrder(order2);




    }

    @Test
    @Transactional
    @Rollback
    public void testFindOrdersByCustomerId() throws Exception {
        OrderQueryRequest request = new OrderQueryRequest();
        request.setCustomerId(customer.getId());

        final Page<Order> orders = orderService.findOrders(request, adminUser);
        Assert.assertEquals(2, orders.getContent().size());

    }

    @Test
    @Transactional
    @Rollback
    public void testFindOrders() throws Exception {
        OrderQueryRequest request = new OrderQueryRequest();

        final Page<Order> orders = orderService.findOrders(request, adminUser);
        Assert.assertEquals(2, orders.getContent().size());

    }

    @Test
    @Transactional
    @Rollback
    public void testFindOrdersByStatus() throws Exception {
        OrderQueryRequest request = new OrderQueryRequest();
        request.setStatus(OrderStatus.UNCOMMITTED.getValue());

        final Page<Order> orders = orderService.findOrders(request, adminUser);
        Assert.assertEquals(1, orders.getContent().size());

    }
}