package com.mishu.cgwy.product.service;

import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.repository.SkuPriceRepository;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.util.List;

@Service
public class SkuPriceService {
	
    @Autowired
    private SkuPriceRepository skuPriceRepository;

    public SkuPrice save(SkuPrice skuPrice) {
        return skuPriceRepository.save(skuPrice);
    }

    public SkuPrice findByCityIdAndSkuId(Long cityId, Long skuId) {
        List<SkuPrice> skuPriceList = skuPriceRepository.findByCityIdAndSkuId(cityId, skuId);
        return CollectionUtils.isNotEmpty(skuPriceList) ? skuPriceList.get(0) : null;
    }

    public Page<SkuPrice> findAll(Specification<SkuPrice> skuPriceListSpecification, PageRequest pageable) {
        return skuPriceRepository.findAll(skuPriceListSpecification, pageable);
    }

    public List<SkuPrice> findAll(Specification<SkuPrice> skuPriceListSpecification) {
        return skuPriceRepository.findAll(skuPriceListSpecification);
    }
}
