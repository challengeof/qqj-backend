package com.mishu.cgwy.order.facade;

import com.mishu.cgwy.order.controller.CartRequest;
import com.mishu.cgwy.profile.service.CustomerService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security" +
        ".xml", "/application-search.xml", "/application-message.xml"})
public class OrderFacadeTest {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private OrderFacade orderFacade;

    @Test
    public void testConcurrentAddSkuToCart() throws InterruptedException {
        /*final List<CartRequest> l1 = new ArrayList<>();
        final CartRequest e1 = new CartRequest();
        e1.setSkuId(390l);
        e1.setQuantity(1);
        l1.add(e1);

        final List<CartRequest> l2 = new ArrayList<>();
        final CartRequest e2 = new CartRequest();
        e2.setSkuId(391l);
        e2.setQuantity(1);
        l2.add(e2);*/


//        final Thread thread1 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//        orderFacade.addSkuToCart(118l, l1, true);
//            }
//        });
//        thread1.start();
//
//        final Thread thread2 = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                orderFacade.addSkuToCart(118l, l2, true);
//            }
//        });
//        thread2.start();
//
//        thread1.join();
//        thread2.join();

    }
}
