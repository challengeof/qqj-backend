package com.mishu.cgwy.product.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.vo.WarehouseVo;
import com.mishu.cgwy.inventory.domain.*;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.product.controller.*;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuPriceHistoryService;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuVendorService;
import com.mishu.cgwy.product.vo.BundleDynamicSkuPriceStatusVo;
import com.mishu.cgwy.product.vo.DynamicSkuPriceVo;
import com.mishu.cgwy.product.vo.SingleDynamicSkuPriceStatusVo;
import com.mishu.cgwy.product.vo.SkuVo;
import com.mishu.cgwy.product.wrapper.*;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceFullWrapper;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.search.SearchService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Collection;
import java.util.List;

/**
 * User: xudong
 * Date: 4/14/15
 * Time: 11:44 PM
 */
@Service
public class DynamicPriceFacade {
    @Autowired
    private ProductService productService;

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SkuVendorService skuVendorService;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private SkuPriceHistoryService skuPriceHistoryService;

    @Autowired
    private StockTotalService stockTotalService;

    @Transactional(readOnly = true)
    public QueryResponse<DynamicSkuPriceVo> queryDynamicPrice(DynamicPriceQueryRequest request, AdminUser adminUser) {
        Page<DynamicSkuPrice> page = inventoryService.queryDynamicPrice(request, adminUser);

        QueryResponse<DynamicSkuPriceVo> response = new QueryResponse<>();
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        List<DynamicSkuPriceVo> list = new ArrayList<>();
        for (DynamicSkuPrice dynamicSkuPrice : page.getContent()) {
            DynamicSkuPriceVo dynamicSkuPriceVo = new DynamicSkuPriceVo();
            dynamicSkuPriceVo.setId(dynamicSkuPrice.getId());
            SkuVo skuVo = new SkuVo();
            Sku sku = dynamicSkuPrice.getSku();
            skuVo.setId(sku.getId());
            skuVo.setName(sku.getName());
            skuVo.setStatus(SkuStatus.fromInt(sku.getStatus()));
            dynamicSkuPriceVo.setSku(skuVo);

            SingleDynamicSkuPriceStatus singleDynamicSkuPriceStatus = dynamicSkuPrice.getSinglePriceStatus();
            SingleDynamicSkuPriceStatusVo singleDy = new SingleDynamicSkuPriceStatusVo();
            singleDy.setSingleInSale(singleDynamicSkuPriceStatus.isSingleInSale());
            singleDy.setSingleAvailable(singleDynamicSkuPriceStatus.isSingleAvailable());
            singleDy.setSingleSalePrice(singleDynamicSkuPriceStatus.getSingleSalePrice());
            dynamicSkuPriceVo.setSingleDynamicSkuPriceStatus(singleDy);

            BundleDynamicSkuPriceStatus bundleDynamicSkuPriceStatus = dynamicSkuPrice.getBundlePriceStatus();
            BundleDynamicSkuPriceStatusVo bundleDy = new BundleDynamicSkuPriceStatusVo();
            bundleDy.setBundleAvailable(bundleDynamicSkuPriceStatus.isBundleAvailable());
            bundleDy.setBundleInSale(bundleDynamicSkuPriceStatus.isBundleInSale());
            bundleDy.setBundleSalePrice(bundleDynamicSkuPriceStatus.getBundleSalePrice());
            dynamicSkuPriceVo.setBundleDynamicSkuPriceStatus(bundleDy);

            SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(request.getCityId(), dynamicSkuPrice.getSku().getId());
            dynamicSkuPriceVo.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
            dynamicSkuPriceVo.setSingleSalePriceLimit(skuPrice == null || skuPrice.getSingleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getSingleSalePriceLimit());
            dynamicSkuPriceVo.setBundleSalePriceLimit(skuPrice == null || skuPrice.getBundleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getBundleSalePriceLimit());

            Warehouse warehouse = dynamicSkuPrice.getWarehouse();
            WarehouseVo warehouseVo = new WarehouseVo();
            warehouseVo.setId(warehouse.getId());
            warehouseVo.setName(warehouse.getName());
            dynamicSkuPriceVo.setWarehouse(warehouseVo);
            list.add(dynamicSkuPriceVo);
        }
        response.setContent(list);
        return response;
    }


    @Transactional
    public DynamicSkuPriceWrapper updateDynamicPrice(DynamicSkuPriceWrapper dynamicSkuPriceWrapper, AdminUser adminUser) {
        Long warehouseId = dynamicSkuPriceWrapper.getWarehouse().getId();
        Long skuId = dynamicSkuPriceWrapper.getSku().getId();

        DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPrice(skuId, warehouseId);
        if (dynamicSkuPrice == null) {
            dynamicSkuPrice = new DynamicSkuPrice();
        }

        final Sku sku = productService.getSku(skuId);

        dynamicSkuPrice.setSku(sku);
        dynamicSkuPrice.setWarehouse(locationService.getWarehouse(warehouseId));

        dynamicSkuPrice.setSinglePriceStatus(new SingleDynamicSkuPriceStatus(dynamicSkuPriceWrapper.getSingleDynamicSkuPriceStatus()));
        dynamicSkuPrice.setBundlePriceStatus(new BundleDynamicSkuPriceStatus(dynamicSkuPriceWrapper.getBundleDynamicSkuPriceStatus()));



        dynamicSkuPrice = inventoryService.saveDynamicPrice(dynamicSkuPrice);

        updateDynamicPriceInSkuPrice(dynamicSkuPrice, adminUser);

        BigDecimal marketPrice = dynamicSkuPriceWrapper.getSku().getMarketPrice();
        if (null == marketPrice || marketPrice.compareTo(BigDecimal.ZERO) == 0) {
            sku.setMarketPrice(getMaxSalePrice(skuId).multiply(BigDecimal.valueOf(1.06)));
        } else {
            sku.setMarketPrice(marketPrice);
        }
        productService.saveSku(sku);

        searchService.indexProduct(sku.getProduct());

        return new DynamicSkuPriceWrapper(dynamicSkuPrice);
    }

    private void updateDynamicPriceInSkuPrice(DynamicSkuPrice price, AdminUser operator) {
        SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(price.getWarehouse().getCity().getId(), price.getSku().getId());
        boolean isNew = false;
        if (skuPrice == null) {
            isNew = true;
            skuPrice = new SkuPrice();
        }
        skuPrice.setCity(price.getWarehouse().getCity());
        skuPrice.setSku(price.getSku());
        skuPrice.setOldSingleSalePrice(isNew ? BigDecimal.ZERO : skuPrice.getSingleSalePrice());
        skuPrice.setOldBundleSalePrice(isNew ? BigDecimal.ZERO : skuPrice.getBundleSalePrice());
        skuPrice.setSingleSalePrice(price.getSinglePriceStatus().getSingleSalePrice());
        skuPrice.setBundleSalePrice(price.getBundlePriceStatus().getBundleSalePrice());
        skuPrice = skuPriceService.save(skuPrice);

        SkuPriceHistory history = new SkuPriceHistory();
        history.setCreateDate(new Date());
        history.setCity(skuPrice.getCity());
        history.setType(SkuPriceType.SALE_PRICE.getValue());
        history.setSku(skuPrice.getSku());
        history.setSingleSalePrice(skuPrice.getSingleSalePrice());
        history.setBundleSalePrice(skuPrice.getBundleSalePrice());
        history.setOperator(operator);
        skuPriceHistoryService.save(history);
    }

    private BigDecimal getMaxSalePrice(Long skuId) {
        List<DynamicSkuPrice> list = inventoryService.getDynamicSkuPricesBySkuId(skuId);
        BigDecimal maxPrice = BigDecimal.ZERO;
        for (DynamicSkuPrice price : list) {
            BigDecimal tmpPrice = price.getSinglePriceStatus().getSingleSalePrice();
            if (tmpPrice != null) {
                maxPrice = maxPrice.max(tmpPrice);
            }
        }
        return maxPrice;
    }

    @Transactional(readOnly = true)
    public DynamicSkuPriceWrapper getDynamicPrice(Long id, AdminUser adminUser) {
        DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPriceById(id);
        return new DynamicSkuPriceWrapper(dynamicSkuPrice);
    }

    @Transactional(readOnly = true)
    public DynamicSkuPriceWrapper queryUniqueDynamicPrice(Long skuId, Long warehouseId, AdminUser adminUser) {

        DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPrice(skuId, warehouseId);
        if (dynamicSkuPrice == null) {
            return null;
        } else {
            DynamicSkuPriceWrapper skuPriceWrapper = new DynamicSkuPriceWrapper(dynamicSkuPrice);
            Warehouse warehouse = locationService.getWarehouse(warehouseId);
            if (warehouse != null) {
                SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(warehouse.getCity().getId(), skuPriceWrapper.getSku().getId());
                skuPriceWrapper.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
            }
            return skuPriceWrapper;
        }
    }

    public List<DynamicSkuPriceWrapper> getDynamicPriceCandidates(DynamicPriceCandidatesRequest request) {
        return inventoryService.getDynamicSkuPriceCandidates(request);
    }

    public List<SimpleSkuWrapper> getSkuPlus(DynamicPriceCandidatesRequest request) {
        List<DynamicSkuPriceWrapper> skuPrices = inventoryService.getDynamicSkuPriceCandidates(request);
        if(com.mishu.cgwy.utils.NumberUtils.isLong(request.getName())){
            Long skuid = Long.parseLong(request.getName());
            boolean hasSku =false;
            for(DynamicSkuPriceWrapper item: skuPrices){
                if(item.getSku().getId() == skuid){
                    hasSku=true;
                    break;
                }
            }
            if(!hasSku) {
//                DynamicSkuPriceWrapper skuPriceWrapper = this.queryUniqueDynamicPrice(skuid, request.getWarehouse(), null);
                List<DynamicSkuPrice> cSkuPrices = inventoryService.getDynamicSkuPricesBySkuId(skuid);
                if (cSkuPrices != null) {
                    for(DynamicSkuPrice dsp : cSkuPrices){
                        skuPrices.add(new DynamicSkuPriceWrapper(dsp));
                    }
                }
            }
        }
        Collection<SimpleSkuWrapper> skuWrappers = Collections2.transform(skuPrices, new Function<DynamicSkuPriceWrapper, SimpleSkuWrapper>() {
            @Override
            public SimpleSkuWrapper apply(DynamicSkuPriceWrapper input) {
                return input.getSku();
            }
        });

        Collection<SimpleSkuWrapper> skus= CollectionUtils.filterRepeat(skuWrappers, new CollectionUtils.GetRepeatKey<SimpleSkuWrapper>() {
            @Override
            public Object getKey(SimpleSkuWrapper src) {
                return src.getId();
            }
        });
        return new ArrayList<>(skus);
    }

    public DynamicSkuPriceFullWrapper findBySkuIdAndWarehouseId(Long id, Long warehouse) {
        //return new DynamicSkuPriceFullWrapper(inventoryService.findBySkuIdAndWarehouseId(id, warehouse));
        if (inventoryService.findBySkuIdAndWarehouseIdAndStatus(id, warehouse) != null) {

            return new DynamicSkuPriceFullWrapper(inventoryService.findBySkuIdAndWarehouseIdAndStatus(id, warehouse));
        }
        return null;
    }
}
