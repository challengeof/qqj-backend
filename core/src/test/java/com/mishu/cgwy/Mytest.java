package com.mishu.cgwy;

import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by king-ck on 2015/9/24.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class Mytest {

    @Autowired
    CustomerService customerService;
    @Test
    public void testJpaQuery(){
        Restaurant restaurant = customerService.findRestaurant("13717959262");

        System.out.println(restaurant);
    }


}
