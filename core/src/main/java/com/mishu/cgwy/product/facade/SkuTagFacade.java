package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.product.controller.SkuTagQueryRequest;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuTag;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.service.SkuTagService;
import com.mishu.cgwy.product.wrapper.SkuTagWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.search.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by wangwei on 15/12/2.
 */
@Service
public class SkuTagFacade {

    @Autowired
    private SkuTagService skuTagService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SearchService searchService;

    @Transactional(readOnly = true)
    public QueryResponse<SkuTagWrapper> getSkuTag(SkuTagQueryRequest request) {
        QueryResponse<SkuTagWrapper> response = new QueryResponse<>();
        Page<SkuTag> page = skuTagService.getSkuTag(request);
        for (SkuTag skuTag : page) {
            response.getContent().add(new SkuTagWrapper(skuTag));
        }
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional
    public void updateSkuTag(Long skuId, List<Long> cityIds, Integer limitedQuantity, List<Long> limitedCityIds) {
        Sku sku = skuService.findOne(skuId);
        if (null != sku) {
            sku.getSkuTags().clear();
            if (null != cityIds) {
                for (Long cityId : cityIds) {
                    SkuTag skuTag = new SkuTag();
                    skuTag.setSku(sku);
                    skuTag.setCity(locationService.getCity(cityId));
                    skuTag.setInDiscount(Boolean.TRUE);
                    if (limitedCityIds != null &&!limitedCityIds.isEmpty()) {

                        for (Long limitedCityId : limitedCityIds) {
                            if (cityId.equals(limitedCityId)) {
                                skuTag.setLimitedQuantity(limitedQuantity);
                            }
                        }
                    }
                    sku.getSkuTags().add(skuTag);
                }
            }
            productService.saveSku(sku);
            searchService.indexProduct(sku.getProduct());
        }
    }
}
