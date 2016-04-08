package com.mishu.cgwy.purchase.service;

import com.mishu.cgwy.purchase.domain.PurchaseOrderItemSign;
import com.mishu.cgwy.purchase.repository.PurchaseOrderItemSignRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wangguodong on 15/9/14.
 */
@Service
public class PurchaseOrderItemSignService {

    @Autowired
    private PurchaseOrderItemSignRepository purchaseOrderItemSignRepository;

    public List<PurchaseOrderItemSign> findByCityIdAndDepotIdAndSkuId(Long cityId, Long depotId, Long skuId) {
        return purchaseOrderItemSignRepository.findByCityIdAndDepotIdAndSkuId(cityId, depotId, skuId);
    }

    public void save(PurchaseOrderItemSign sign) {
        purchaseOrderItemSignRepository.save(sign);
    }

    public void deleteByCityIdAndDepotId(Long cityId, Long depotId) {
        List<PurchaseOrderItemSign> signList = purchaseOrderItemSignRepository.findByCityIdAndDepotId(cityId, depotId);
        if (CollectionUtils.isNotEmpty(signList)) {
            for (PurchaseOrderItemSign sign : signList) {
                purchaseOrderItemSignRepository.delete(sign.getId());
            }
        }
    }
}
