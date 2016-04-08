package com.mishu.cgwy.accounting.controller;

import com.mishu.cgwy.accounting.dto.CollectionPaymentMethodData;
import com.mishu.cgwy.accounting.dto.CollectionPaymentMethodRequest;
import com.mishu.cgwy.accounting.facade.CollectionPaymentMethodFacade;
import com.mishu.cgwy.accounting.wrapper.CollectionPaymentMethodWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by xiao1zhao2 on 15/10/22.
 */
@Controller
public class AccountCollectionPaymentMethodController {

    @Autowired
    private CollectionPaymentMethodFacade collectionPaymentMethodFacade;

    @RequestMapping(value = "/api/account/collectionPaymentMethod/list", method = RequestMethod.GET)
    @ResponseBody
    public List<CollectionPaymentMethodWrapper> accountCollectionPaymentMethodList(CollectionPaymentMethodRequest request) {
        return collectionPaymentMethodFacade.findCollectionPaymentMethodList(request);
    }

    @RequestMapping(value = "/api/account/collectionPaymentMethod/{id}", method = RequestMethod.GET)
    @ResponseBody
    public CollectionPaymentMethodWrapper getAccountCollectionPaymentMethodById(@PathVariable(value = "id") Long id) {
        return collectionPaymentMethodFacade.findDepot(id);
    }

    @RequestMapping(value = "/api/account/collectionPaymentMethod", method = RequestMethod.POST)
    @ResponseBody
    public CollectionPaymentMethodWrapper addAccountCollectionPaymentMethod(@RequestBody CollectionPaymentMethodData data) {
        return collectionPaymentMethodFacade.addMethod(data);
    }

    @RequestMapping(value = "/api/account/collectionPaymentMethod/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public CollectionPaymentMethodWrapper updateAccountCollectionPaymentMethod(@PathVariable(value = "id") Long id, @RequestBody CollectionPaymentMethodData data) {
        return collectionPaymentMethodFacade.updateMethod(id, data);
    }
}
