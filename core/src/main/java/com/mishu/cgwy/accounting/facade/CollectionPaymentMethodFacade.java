package com.mishu.cgwy.accounting.facade;

import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod;
import com.mishu.cgwy.accounting.dto.CollectionPaymentMethodData;
import com.mishu.cgwy.accounting.dto.CollectionPaymentMethodRequest;
import com.mishu.cgwy.accounting.service.CollectionPaymentMethodService;
import com.mishu.cgwy.accounting.wrapper.CollectionPaymentMethodWrapper;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.error.UserDefinedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 10/12/15.
 */
@Service
public class CollectionPaymentMethodFacade {

    @Autowired
    private CollectionPaymentMethodService collectionPaymentMethodService;
    @Autowired
    private LocationService locationService;

    @Transactional(readOnly = true)
    public List<CollectionPaymentMethodWrapper> findCollectionPaymentMethodList(final CollectionPaymentMethodRequest request) {
        List<CollectionPaymentMethodWrapper> collectionPaymentMethodWrappers = new ArrayList<>();
        for (CollectionPaymentMethod collectionPaymentMethod : collectionPaymentMethodService.findCollectionPaymentMethodList(request)) {
            collectionPaymentMethodWrappers.add(new CollectionPaymentMethodWrapper(collectionPaymentMethod));
        }
        return collectionPaymentMethodWrappers;
    }

    @Transactional
    public CollectionPaymentMethodWrapper addMethod(CollectionPaymentMethodData collectionPaymentMethodData){

        CollectionPaymentMethod method = new CollectionPaymentMethod();
        method.setCode(collectionPaymentMethodData.getCode());
        method.setName(collectionPaymentMethodData.getName());
        method.setCash(collectionPaymentMethodData.isCash());
        method.setValid(collectionPaymentMethodData.isValid());
        method.setCity(locationService.getCity(collectionPaymentMethodData.getCityId()));

        return new CollectionPaymentMethodWrapper(collectionPaymentMethodService.addMethod(method));
    }

    @Transactional
    public CollectionPaymentMethodWrapper updateMethod(Long id, CollectionPaymentMethodData collectionPaymentMethodData){
        CollectionPaymentMethod method = collectionPaymentMethodService.getOne(id);
        CollectionPaymentMethod findMethod = collectionPaymentMethodService.findCollectionPaymentMethodByCode(collectionPaymentMethodData.getCityId(), collectionPaymentMethodData.getCode());
        if (findMethod != null && !findMethod.getId().equals(method.getId())){
            throw new UserDefinedException("编码已存在");
        }
        findMethod = collectionPaymentMethodService.findCollectionPaymentMethodByName(collectionPaymentMethodData.getCityId(), collectionPaymentMethodData.getName());
        if (findMethod != null && !findMethod.getId().equals(method.getId())){
            throw new UserDefinedException("名称已存在");
        }

        method.setCode(collectionPaymentMethodData.getCode());
        method.setName(collectionPaymentMethodData.getName());
        method.setCash(collectionPaymentMethodData.isCash());
        method.setValid(collectionPaymentMethodData.isValid());
        method.setCity(locationService.getCity(collectionPaymentMethodData.getCityId()));

        return new CollectionPaymentMethodWrapper(collectionPaymentMethodService.updateMethod(method));
    }

    @Transactional(readOnly = true)
    public CollectionPaymentMethodWrapper findDepot(Long id) {
        return new CollectionPaymentMethodWrapper(collectionPaymentMethodService.getOne(id));
    }
}
