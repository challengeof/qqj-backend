package com.mishu.cgwy.spike.controller;

import com.mishu.cgwy.operating.skipe.constant.SpikeActivityState;
import com.mishu.cgwy.operating.skipe.facade.SpikeFacade;
import com.mishu.cgwy.operating.skipe.service.SpikeCacheService;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.domain.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * Created by king-ck on 2016/1/12.
 */
@Controller
public class SpikeController {

    @Autowired
    private SpikeFacade spikeFacade;
    @Autowired
    private SpikeCacheService spikeCacheService;

//    @Autowired
//    private WebOrderController webOrderController;

//    @RequestMapping(value = "/api/v2/spike/testFun", method = RequestMethod.GET)
//    @ResponseBody
//    public void testFun(@CurrentCustomer Customer customer){
////        cartRequestList: [{skuId: 11431, quantity: 1, bundle: true},…]
////        0: {skuId: 11431, quantity: 1, bundle: true}
////        1: {skuId: 264, quantity: 3, bundle: false, spikeItemId: 38, cartSkuType: 2}
////        2: {skuId: 56, quantity: 2, bundle: true, spikeItemId: 36, cartSkuType: 2}
////        3: {skuId: 245, quantity: 2, bundle: true, spikeItemId: 37, cartSkuType: 2}
////        couponId: null
//        List<CartRequest> cartRequest = new ArrayList<>();
//        CartRequest cRequest = new CartRequest();
//        cRequest.setSkuId(11431L);
//        cRequest.setQuantity(1);
//        cRequest.setBundle(true);
//        CartRequest cRequest2 = new CartRequest();
//        cRequest2.setSpikeItemId(38L);
//        cRequest2.setCartSkuType(2);
//        cRequest2.setQuantity(3);
//
//        CartRequest cRequest3 = new CartRequest();
//        cRequest3.setSpikeItemId(36L);
//        cRequest3.setCartSkuType(2);
//        cRequest3.setQuantity(2);
//
//        CartRequest cRequest4 = new CartRequest();
//        cRequest4.setSpikeItemId(37L);
//        cRequest4.setCartSkuType(2);
//        cRequest4.setQuantity(3);
//
//        cartRequest.add(cRequest);
//        cartRequest.add(cRequest2);
//        cartRequest.add(cRequest3);
//        cartRequest.add(cRequest4);
//
//
//        CartAndCouponRequest cacRequest = new CartAndCouponRequest();
//        cacRequest.setCartRequestList(cartRequest);
//        webOrderController.createOrder(customer,cacRequest);
//
//    }


    @RequestMapping(value = "/api/v2/spike/clearCache", method = RequestMethod.GET)
    @ResponseBody
    public void getSpikes(@CurrentCustomer Customer customer){
        spikeCacheService.removeAllSpike();
    }

    @RequestMapping(value = "/api/v2/spikeitem/query/{spikItemId}", method = RequestMethod.GET)
    @ResponseBody
    public SpikeItemWrapper getSpikeItem(@PathVariable("spikItemId") Long spikeItemId, @CurrentCustomer Customer customer){


        return spikeFacade.findSpikeItem(spikeItemId);
    }

    @RequestMapping(value = "/api/v2/spike/effective/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public List<SpikeWrapper> getSpikes(@PathVariable("cityId") Long cityId, @CurrentCustomer Customer customer){

        return spikeFacade.findCacheSpikeByState( cityId, SpikeActivityState.unStart, SpikeActivityState.process );
    }

    @RequestMapping(value = "/api/v2/spike/item/{spikeId}", method = RequestMethod.GET)
    @ResponseBody
    public List<SpikeItemWrapper> getSpikeItems(@PathVariable("spikeId") Long spikeId ){

        this.spikeFacade.spikeValidity(spikeId);
        return spikeFacade.findCacheSpikeItems(spikeId);
    }

}
