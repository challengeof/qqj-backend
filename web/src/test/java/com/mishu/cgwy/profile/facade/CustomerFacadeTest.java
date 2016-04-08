package com.mishu.cgwy.profile.facade;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Region;
import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.profile.constants.Constants;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.dto.CustomerCenterResponse;
import com.mishu.cgwy.profile.service.CustomerHintService;
import com.mishu.cgwy.profile.service.CustomerService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by kaicheng on 3/18/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class CustomerFacadeTest {
    @Autowired
    CustomerFacade customerFacade;

    @Autowired
    CustomerService customerService;

    @Autowired
    LocationService locationService;

    private RestaurantType restaurantType1;
    private RestaurantType restaurantType2;
    private RestaurantType restaurantType3;
    @Autowired
    private CustomerHintService customerHintService;
    private Zone zone;

    private Region region;



//    @Before
//    public void setup() {
//        restaurantType1 = new RestaurantType();
//        restaurantType1.setId(1);
//        restaurantType1.setShowValue("restaurant 1");
//        customerService.saveRestaurantType(restaurantType1);
//        restaurantType2 = new RestaurantType();
//        restaurantType2.setId(2);
//        restaurantType2.setShowValue("restaurant 2");
//        customerService.saveRestaurantType(restaurantType2);
//        restaurantType3 = new RestaurantType();
//        restaurantType3.setId(3);
//        restaurantType3.setShowValue("restaurant 3");
//        customerService.saveRestaurantType(restaurantType3);
//
//        City city = new City();
//        city = locationService.saveCity(city);
//
//        region = new Region();
//        region.setCity(city);
//        region = locationService.saveRegion(region);
//
//        zone = new Zone();
//        zone.setRegion(region);
//
//        zone = locationService.saveZone(zone);
//
//
//    }

/*
    @Before
    public void setup() {
        restaurantType1 = new RestaurantType();
        restaurantType1.setId(1);
        restaurantType1.setShowValue("restaurant 1");
        customerService.saveRestaurantType(restaurantType1);
        restaurantType2 = new RestaurantType();
        restaurantType2.setId(2);
        restaurantType2.setShowValue("restaurant 2");
        customerService.saveRestaurantType(restaurantType2);
        restaurantType3 = new RestaurantType();
        restaurantType3.setId(3);
        restaurantType3.setShowValue("restaurant 3");
        customerService.saveRestaurantType(restaurantType3);

        City city = new City();
        city = locationService.saveCity(city);

        region = new Region();
        region.setCity(city);
        region = locationService.saveRegion(region);

        zone = new Zone();
        zone.setRegion(region);

        zone = locationService.saveZone(zone);


    }
>>>>>>> origin/master


  /*  @Test
    @Transactional
    @Rollback
    public void testUsableRestaurants() {
        Customer customer = new Customer();
        customer.setUsername("testUsername");
        customer.setPassword("testPassword");
        customer.setZone(zone);


        Restaurant restaurant1 = new Restaurant();
        Address address = new Address();
        address.setZone(zone);
        address.setAddress("test address");
        restaurant1.setStatus(Constants.VALID_RESTAURANT); //鍙敤
        restaurant1.setCustomer(customer);
        restaurant1.setAddress(address);
        restaurant1.setType(1);
        Restaurant restaurant2 = new Restaurant();
        restaurant2.setStatus(Constants.NOT_CHECKED_RESTAURANT); //涓嶅彲鐢�
        restaurant2.setCustomer(customer);
        restaurant2.setAddress(address);
        restaurant2.setType(1);
        Restaurant restaurant3 = new Restaurant();
        restaurant3.setStatus(Constants.VALID_RESTAURANT); //鍙敤
        restaurant3.setCustomer(customer);
        restaurant3.setAddress(address);
        restaurant3.setType(1);
        customerService.register(customer);
        customerService.saveRestaurant(restaurant1);
        customerService.saveRestaurant(restaurant2);
        customerService.saveRestaurant(restaurant3);

        List usableRestaurants = customerFacade.findUsableRestaurantByCustomerId(customer.getId());
        Assert.assertEquals(2, usableRestaurants.size());
    }
//
//    @Test
//    @Transactional
//    @Rollback
//    public void testRestaurantTypes() {
//        Customer customer = new Customer();
//        customer.setUsername("testUsername");
//        customer.setPassword("testPassword");
//
//        customer.setZone(zone);
//
//        Restaurant restaurant1 = new Restaurant();
//        Address address = new Address();
//        address.setAddress("test address");
//        address.setZone(zone);
//        restaurant1.setType(restaurantType1.getId());
//        restaurant1.setCustomer(customer);
//        restaurant1.setAddress(address);
//        Restaurant restaurant2 = new Restaurant();
//        restaurant2.setType(restaurantType2.getId()); //
//        restaurant2.setCustomer(customer);
//        restaurant2.setAddress(address);
//        Restaurant restaurant3 = new Restaurant();
//        restaurant3.setType(restaurantType3.getId()); //
//        restaurant3.setCustomer(customer);
//        restaurant3.setAddress(address);
//        customerService.register(customer);
//        customerService.saveRestaurant(restaurant1);
//        customerService.saveRestaurant(restaurant2);
//        customerService.saveRestaurant(restaurant3);
//
//       // List<RestaurantType> types = customerFacade.findRestaurantTypes();
//     //   Assert.assertEquals(3, types.size());
//
//    }


    @Test
    @Transactional
    @Rollback
    public void testGetCenterInfo() {
        Customer customer = new Customer();
        customer.setUsername("chengzheng");
        customer.setPassword("balabala");
        customerService.register(customer);

        Assert.assertNotNull(customer.getId());

        CustomerHint customerHint = new CustomerHint();
        customerHint.setName("user hint");
        customerHint.setValue(13);
        customerHint.setCustomerId(customer.getId());
        customerHintService.save(customerHint);


        CustomerCenterResponse ccr = customerFacade.findCenterInfo(customer.getUsername());
        System.out.println(ccr.getUsername());

    }
    */
}
