package com.mishu.cgwy.profile.service;

import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.RestaurantType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class CustomerServiceTest {

    @Autowired
    private CustomerService customerService;

    @Test
    @Transactional
    @Rollback
    public void testRegister() {
        Customer customer = new Customer();
        customer.setUsername("testUser");
        customer.setPassword("testPassword");

        customer = customerService.register(customer);
        Assert.assertNotNull(customer.getId());
    }
}