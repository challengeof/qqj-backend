package com.mishu.cgwy.product.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.wrapper.SimpleWarehouseWrapper;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.job.domain.UpdateDynamicSkuPriceJob;
import com.mishu.cgwy.job.service.UpdateDynamicSkuPriceJobService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.controller.DynamicPriceRequest;
import com.mishu.cgwy.product.controller.DynamicPriceTempQueryResponse;
import com.mishu.cgwy.product.controller.DynamicPriceTempResponse;
import com.mishu.cgwy.product.controller.ProductOrDynamicPriceQueryRequest;
import com.mishu.cgwy.product.domain.ChangeDetail;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.service.ChangeDetailService;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.wrapper.BundleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.product.wrapper.SingleDynamicSkuPriceStatusWrapper;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 15-6-4.
 */
@Service
public class DynamicPriceTempFacade {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductService productService;

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private VendorService vendorService;

    @Autowired
    private ChangeDetailService changeDetailService;

    @Autowired
    private DynamicPriceFacade dynamicPriceFacade;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private ProductEdbFacade productEdbFacade;

    @Autowired
    private UpdateDynamicSkuPriceJobService updateDynamicSkuPriceJobService;

    @Transactional
    public ChangeDetail updateDynamicPriceTemp(DynamicPriceRequest request, AdminUser adminUser) {
        DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPrice(request.getSkuId(), request.getWarehouseId());


        DynamicSkuPriceWrapper dynamicSkuPriceWrapper = null;

        if (dynamicSkuPrice == null) {
            dynamicSkuPrice = new DynamicSkuPrice();
            dynamicSkuPriceWrapper = new DynamicSkuPriceWrapper();
        }else{
            dynamicSkuPriceWrapper = new DynamicSkuPriceWrapper(dynamicSkuPrice);
        }

        final Sku sku = productService.getSku(request.getSkuId());
        dynamicSkuPriceWrapper.setSku(new SimpleSkuWrapper(dynamicSkuPrice.getSku()));
        dynamicSkuPriceWrapper.setWarehouse(new SimpleWarehouseWrapper(locationService.getWarehouse(request.getWarehouseId
                ())));
        SingleDynamicSkuPriceStatusWrapper singleDynamicSkuPriceStatus = new SingleDynamicSkuPriceStatusWrapper();
        singleDynamicSkuPriceStatus.setSingleAvailable(request.isSingleAvailable());
        singleDynamicSkuPriceStatus.setSingleInSale(request.isSingleInSale());
        singleDynamicSkuPriceStatus.setSingleSalePrice(request.getSingleSalePrice());
        dynamicSkuPriceWrapper.setSingleDynamicSkuPriceStatus(singleDynamicSkuPriceStatus);

        BundleDynamicSkuPriceStatusWrapper bundleDynamicSkuPriceStatus = new BundleDynamicSkuPriceStatusWrapper();
        bundleDynamicSkuPriceStatus.setBundleAvailable(request.isBundleAvailable());
        bundleDynamicSkuPriceStatus.setBundleInSale(request.isBundleInSale());
        bundleDynamicSkuPriceStatus.setBundleSalePrice(request.getBundleSalePrice());
        dynamicSkuPriceWrapper.setBundleDynamicSkuPriceStatus(bundleDynamicSkuPriceStatus);

        dynamicSkuPriceWrapper.setEffectType(request.isEffectType());
        dynamicSkuPriceWrapper.setEffectTime(request.getEffectTime());

        ChangeDetail changeDetail = new ChangeDetail();
        changeDetail.setProductName(sku.getName());

        return saveChangeDetail(dynamicSkuPriceWrapper, dynamicSkuPrice.getId(), changeDetail, adminUser);

    }



    @Transactional
    public ChangeDetail fastUpdateDynamicPriceTemp(DynamicPriceRequest request, AdminUser adminUser) {
        DynamicSkuPriceWrapper dynamicSkuPriceWrapper = null;
        DynamicSkuPrice dynamicSkuPrice = inventoryService.getDynamicSkuPrice(request.getSkuId(), request.getWarehouseId());
        if (dynamicSkuPrice != null) {
            dynamicSkuPriceWrapper = new DynamicSkuPriceWrapper(dynamicSkuPrice);
            BundleDynamicSkuPriceStatusWrapper bundleDynamicSkuPriceStatus = dynamicSkuPriceWrapper.getBundleDynamicSkuPriceStatus();
            bundleDynamicSkuPriceStatus.setBundleSalePrice(request.getBundleSalePrice());

            SingleDynamicSkuPriceStatusWrapper singleDynamicSkuPriceStatus = dynamicSkuPriceWrapper.getSingleDynamicSkuPriceStatus();
            singleDynamicSkuPriceStatus.setSingleSalePrice(request.getSingleSalePrice());
        }

        return saveChangeDetail(dynamicSkuPriceWrapper, dynamicSkuPrice.getId(), new ChangeDetail(),adminUser);
    }

    @Transactional
    public ChangeDetail saveChangeDetail(DynamicSkuPriceWrapper dynamicSkuPriceWrapper,Long objectId,ChangeDetail changeDetail,AdminUser submitter) {
        Organization organization = productService.getSku(dynamicSkuPriceWrapper.getSku().getId()).getProduct().getOrganization();
        changeDetail.setCityId(dynamicSkuPriceWrapper.getWarehouse().getCity().getId());
        changeDetail.setProductName(dynamicSkuPriceWrapper.getSku().getName());
        changeDetail.setOrganizationId(organization.getId());
        changeDetail.setWarehouseId(dynamicSkuPriceWrapper.getWarehouse().getId());
        changeDetail.setSubmitter(submitter);
        changeDetail.setObjectType(Constants.DYNAMIC_PRICE_TYPE);
        changeDetail.setObjectId(objectId);
        String content = null;
        try {
            content = objectMapper.writeValueAsString(dynamicSkuPriceWrapper);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        changeDetail.setContent(content);
        changeDetail.setSubmitDate(new Date());
        changeDetail = changeDetailService.saveChangeDetail(changeDetail);
        return changeDetail;
    }

    @Transactional
    public ChangeDetail updateStatus(ChangeDetail changeDetail) {
        changeDetail.setPassDate(new Date());
        return changeDetailService.saveChangeDetail(changeDetail);
    }

    @Transactional
    public ChangeDetail getChangeDetail(Long id) {

        return changeDetailService.getChangeDetail(id);
    }

    @Transactional(readOnly = true)
    public DynamicPriceTempQueryResponse queryDynamicPriceTemp(ProductOrDynamicPriceQueryRequest request, AdminUser adminUser) {
        request.setObjectType(Constants.DYNAMIC_PRICE_TYPE);
        Page<ChangeDetail> page = changeDetailService.findProductsOrDynamicPrices(request, adminUser);
        DynamicPriceTempQueryResponse response = new DynamicPriceTempQueryResponse();
        List<DynamicPriceTempResponse> list = new ArrayList<>();
        DynamicSkuPriceWrapper originDynamicSkuPriceWrapper = null;
        for (ChangeDetail changeDetail : page.getContent()) {
            DynamicPriceTempResponse dynamicPriceTempReponse = new DynamicPriceTempResponse(changeDetail);
            DynamicSkuPriceWrapper temp = dynamicPriceTempReponse.getDynamicSkuPriceWrapper();
            if (temp != null && temp.getSku() != null && temp.getWarehouse() != null) {
                DynamicSkuPrice originDynamicSkuPrice = inventoryService.getDynamicSkuPrice(temp.getSku().getId(),temp.getWarehouse().getId());
                if (null != originDynamicSkuPrice) {
                    dynamicPriceTempReponse.setOriginDynamicSkuPriceWrapper(new DynamicSkuPriceWrapper(originDynamicSkuPrice));
                }
            }
            SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(temp.getWarehouse().getCity().getId(), temp.getSku().getId());
            dynamicPriceTempReponse.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
            dynamicPriceTempReponse.setSingleSalePriceLimit(skuPrice == null || skuPrice.getSingleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getSingleSalePriceLimit());
            dynamicPriceTempReponse.setBundleSalePriceLimit(skuPrice == null || skuPrice.getBundleSalePriceLimit() == null ? BigDecimal.ZERO : skuPrice.getBundleSalePriceLimit());

            list.add(dynamicPriceTempReponse);
        }
        response.setDynamicPriceTempResponses(list);
        response.setPageSize(request.getPageSize());
        response.setPage(request.getPage());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional
    public void updateDynamicSkuPrice(Long id, Long status, AdminUser adminUser) throws Exception {

        ChangeDetail changeDetail = getChangeDetail(id);

        //拒绝，直接返回。
        if (!status.equals(Constants.THROUGH)) {
            updateChangeDetailStatus(changeDetail, status, adminUser);
            return;
        }

        //同意
        DynamicSkuPriceWrapper dynamicSkuPriceWrapper = null;

        try {
            dynamicSkuPriceWrapper = objectMapper.readValue(changeDetail.getContent(), DynamicSkuPriceWrapper.class);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!dynamicSkuPriceWrapper.isEffectType()) {
            dynamicPriceFacade.updateDynamicPrice(dynamicSkuPriceWrapper, adminUser);
            updateChangeDetailStatus(changeDetail, status, adminUser);
        } else {
            Long skuId = dynamicSkuPriceWrapper.getSku().getId();
            Long warehouseId = dynamicSkuPriceWrapper.getWarehouse().getId();
            List<UpdateDynamicSkuPriceJob> jobs = updateDynamicSkuPriceJobService.findBySkuIdAndWarehouseId(skuId, warehouseId);
            if (CollectionUtils.isNotEmpty(jobs)) {
                for (UpdateDynamicSkuPriceJob updateDynamicSkuPriceJob : jobs) {
                    updateDynamicSkuPriceJobService.delete(updateDynamicSkuPriceJob.getId());
                }
            }
            UpdateDynamicSkuPriceJob job = new UpdateDynamicSkuPriceJob();
            job.setSku(productService.getSku(skuId));
            job.setWarehouse(locationService.getWarehouse(warehouseId));
            job.setAdminUser(adminUser);
            job.setChangeDetail(changeDetail);
            job.setEffectTime(dynamicSkuPriceWrapper.getEffectTime());
            updateDynamicSkuPriceJobService.save(job);
        }
    }

    private void updateChangeDetailStatus(ChangeDetail changeDetail, Long status, AdminUser adminUser) {
        changeDetail.setStatus(status);
        changeDetail.setVerifier(adminUser);
        updateStatus(changeDetail);
    }
}
