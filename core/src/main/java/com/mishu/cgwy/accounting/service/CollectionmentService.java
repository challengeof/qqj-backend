package com.mishu.cgwy.accounting.service;

import com.mishu.cgwy.accounting.domain.CollectionPaymentMethod;
import com.mishu.cgwy.accounting.domain.Collectionment;
import com.mishu.cgwy.accounting.dto.CollectionmentData;
import com.mishu.cgwy.accounting.repository.CollectionPaymentMethodRepository;
import com.mishu.cgwy.accounting.repository.CollectionmentRepository;
import com.mishu.cgwy.stock.domain.StockIn;
import com.mishu.cgwy.stock.domain.StockOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 10/12/15.
 */
@Service
public class CollectionmentService {

    @Autowired
    private CollectionmentRepository collectionmentRepository;
    @Autowired
    private CollectionPaymentMethodRepository collectionPaymentMethodRepository;

    @Transactional
    public List<Collectionment> generateCollectionment(StockOut stockOut, List<CollectionmentData> collectionmentDatas) {

        List<Collectionment> collectionments = new ArrayList<>();
        if (stockOut != null && collectionmentDatas != null && collectionmentDatas.size() > 0) {

            for (CollectionmentData collectionmentData : collectionmentDatas) {

                if (collectionmentData.getAmount().compareTo(BigDecimal.ZERO) != 0) {

                    Collectionment collectionment = new Collectionment();
                    collectionment.setValid(true);
                    collectionment.setAmount(collectionmentData.getAmount());
                    collectionment.setCollectionPaymentMethod(collectionPaymentMethodRepository.getOne(collectionmentData.getCollectionPaymentMethodId()));
                    collectionment.setCreateDate(stockOut.getReceiveDate());
                    collectionment.setCreator(stockOut.getReceiver());
                    collectionment.setRestaurant(stockOut.getOrder().getRestaurant());
                    collectionment.setRealCreateDate(new Date());
                    collectionment = collectionmentRepository.save(collectionment);
                    collectionments.add(collectionment);
                }
            }
        }

        return collectionments;
    }

    @Transactional
    public Collectionment generateCollectionment(StockIn stockIn) {

        if (stockIn != null && stockIn.getAmount() != null && stockIn.getAmount().compareTo(BigDecimal.ZERO) != 0) {

            CollectionPaymentMethod method = this.findCashCollectionPaymentMethod(stockIn.getDepot().getCity().getId());

            Collectionment collectionment = new Collectionment();
            collectionment.setValid(true);
            collectionment.setAmount(stockIn.getAmount().multiply(new BigDecimal(-1)));
            collectionment.setCollectionPaymentMethod(method);
            collectionment.setCreateDate(stockIn.getReceiveDate());
            collectionment.setCreator(stockIn.getReceiver());
            collectionment.setRestaurant(stockIn.getSellReturn().getOrder().getRestaurant());
            collectionment.setRealCreateDate(new Date());

            return collectionmentRepository.save(collectionment);
        }

        return null;
    }

    @Transactional(readOnly = true)
    public CollectionPaymentMethod findCashCollectionPaymentMethod (Long cityId) {
        CollectionPaymentMethod method = null;
        List<CollectionPaymentMethod> methods = collectionPaymentMethodRepository.findByCityIdAndCashTrueAndValidTrue(cityId);
        if (methods != null && methods.size() > 0) {
            method = methods.get(0);
        } else {
            methods = collectionPaymentMethodRepository.findByCityIdAndValid(cityId, true);
            if (methods != null && methods.size() > 0) {
                method = methods.get(0);
            }
        }

        return method;
    }
}


