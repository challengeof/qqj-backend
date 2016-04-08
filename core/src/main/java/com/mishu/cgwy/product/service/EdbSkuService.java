package com.mishu.cgwy.product.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mishu.cgwy.product.domain.EdbSku;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.repository.EdbSkuRepository;


@Service
public class EdbSkuService {
    @Autowired
    private EdbSkuRepository edbSkuRepository;

    @Transactional
    public void saveEdbSku(EdbSku edbSku) {
        edbSkuRepository.save(edbSku);
    }

    public EdbSku findBySkuIdAndStockDate(Sku sku, Date stockDate) {
    	EdbSku edbSku = edbSkuRepository.findBySkuIdAndStockDate(sku.getId(), stockDate);
    	return edbSku == null ? new EdbSku() : edbSku;
    }

    public List<EdbSku> findByStockDate(Date date) {
        return edbSkuRepository.findByStockDate(date);
    }
}
