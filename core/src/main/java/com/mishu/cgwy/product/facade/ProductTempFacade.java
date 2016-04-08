package com.mishu.cgwy.product.facade;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.common.vo.MediaFileVo;
import com.mishu.cgwy.error.ProductAlreadyExistsException;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.organization.vo.OrganizationVo;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.controller.ChangeDetailResponse;
import com.mishu.cgwy.product.controller.ProductOrDynamicPriceQueryRequest;
import com.mishu.cgwy.product.controller.ProductTempQueryResponse;
import com.mishu.cgwy.product.controller.SkuRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.dto.ProductRequest;
import com.mishu.cgwy.product.service.ChangeDetailService;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.vo.BrandVo;
import com.mishu.cgwy.product.vo.CategoryVo;
import com.mishu.cgwy.product.vo.ProductVo;
import com.mishu.cgwy.product.vo.SkuVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by bowen on 15-6-3.
 */
@Service
public class ProductTempFacade {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private ProductService productService;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private ChangeDetailService changeDetailService;

    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AdminUserService adminUserService;

    public ChangeDetail updateProductTemp(Long id, ProductRequest productRequest,AdminUser submitter){
        ChangeDetail changeDetail = null;
        try {
            changeDetail = saveProductTemp(id, productRequest, submitter);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return changeDetail;
    }

    private ChangeDetail saveProductTemp(Long productId, ProductRequest productRequest,AdminUser submitter) throws JsonProcessingException {
        Organization organization = organizationService.getDefaultOrganization();
        // check product brandName
        checkProductNameDuplication(productId, productRequest.getName(), organization.getId());


        ProductVo productVo = new ProductVo();
        if (productId != null) {
            Product product = productService.getProduct(productId);
            productVo = initProductVo(product);
        }

        List<SkuVo> skuVos = productVo.getSkus();
        SkuVo skuVo = null;

        if (skuVos.isEmpty()) {
            skuVo = new SkuVo();
            productVo.getSkus().add(skuVo);
        } else {
            skuVo = productVo.getSkus().get(0);
        }

        productVo.setName(productRequest.getName());
        List<Long> mediaFileIds = productRequest.getMediaFileIds();
        productVo.getMediaFiles().clear();
        for (Long mediaFileId : mediaFileIds) {
            if (mediaFileId != null && mediaFileId != 0) {
                MediaFile mediaFile = mediaFileService.getMediaFile(mediaFileId);
                MediaFileVo mediaFileVo = new MediaFileVo();
                mediaFileVo.setId(mediaFile.getId());
                mediaFileVo.setUrl(mediaFile.getUrl());
                productVo.getMediaFiles().add(mediaFileVo);
            }
        }

        if (productVo.getMediaFiles().isEmpty()) {
            MediaFile mediaFile = mediaFileService.getMediaFile(MediaFile.DEFAULT_IMAGE);
            MediaFileVo mediaFileVo = new MediaFileVo();
            mediaFileVo.setId(mediaFile.getId());
            mediaFileVo.setUrl(mediaFile.getUrl());
            productVo.getMediaFiles().add(mediaFileVo);
        }

        if (productRequest.getBrandId() != null) {
            Brand brand = productService.getBrand(productRequest.getBrandId());
            if (brand != null) {
                BrandVo brandVo = new BrandVo();
                brandVo.setId(brand.getId());
                brandVo.setBrandName(brand.getBrandName());
                productVo.setBrand(brandVo);
            }
        }
        if (productRequest.getCategoryId() != null) {
            Category category = productService.getCategory(productRequest.getCategoryId());
            if (category != null) {
                CategoryVo categoryVo = new CategoryVo();
                categoryVo.setId(category.getId());
                categoryVo.setName(category.getName());
                Category current = category;
                String hierarchyName = category.getName();
                while (current.getParentCategory() != null) {
                    hierarchyName = current.getParentCategory().getName() + "-" + hierarchyName;
                    current = current.getParentCategory();
                }
                categoryVo.setHierarchyName(hierarchyName);
                productVo.setCategory(categoryVo);
            }

        }
        productVo.getProperties().putAll(productRequest.getProperties());

        productVo.setSpecification(productRequest.getSpecification());

        productVo.setShelfLife(productRequest.getShelfLife());

        productVo.setDetails(productRequest.getDetails());
        
        productVo.setDiscrete(productRequest.isDiscrete());

        SkuRequest skuRequest = productRequest.getSkuRequest();

        skuVo.setRate(skuRequest.getRate());
        skuVo.setStatus(SkuStatus.fromInt(skuRequest.getStatus()));
        //TODO 转化率全部通过后台数据库修改
        if (productVo.getId() == null) {
            skuVo.setCapacityInBundle(skuRequest.getCapacityInBundle());
        }

        skuVo.setSingleUnit(skuRequest.getSingleUnit());
        skuVo.setSingleGross_wight(skuRequest.getSingleGross_wight());
        skuVo.setSingleNet_weight(skuRequest.getSingleNet_weight());
        skuVo.setSingleLong(skuRequest.getSingleLong());
        skuVo.setSingleWidth(skuRequest.getSingleWidth());
        skuVo.setSingleHeight(skuRequest.getSingleHeight());

        skuVo.setBundleUnit(skuRequest.getBundleUnit());
        skuVo.setBundleGross_wight(skuRequest.getBundleGross_wight());
        skuVo.setBundleNet_weight(skuRequest.getBundleNet_weight());
        skuVo.setBundleLong(skuRequest.getBundleLong());
        skuVo.setBundleWidth(skuRequest.getBundleWidth());
        skuVo.setBundleHeight(skuRequest.getBundleHeight());

        skuVo.setProductId(productId);


        return saveChangeDetail(productVo, productId, new ChangeDetail(), submitter);
    }

    private void checkProductNameDuplication(Long productId, String productName, Long organizationId) {
        Organization organization = organizationService.findById(organizationId);
        List<Product> products = productService.findByProductName(productName, organization);

        for (Product product : products) {
            if (!product.getId().equals(productId)) {
                throw new ProductAlreadyExistsException();
            }
        }
    }
	@Transactional
    public ChangeDetail saveChangeDetail(ProductVo productVo,Long objectId,ChangeDetail changeDetail,AdminUser submitter) {
        String content = null;
        try {
            content = objectMapper.writeValueAsString(productVo);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        changeDetail.setProductName(productVo.getName());
        Organization organization = organizationService.getDefaultOrganization();
        changeDetail.setOrganizationId(organization.getId());
        changeDetail.setSubmitter(submitter);
        changeDetail.setObjectId(objectId);
        changeDetail.setContent(content);
        changeDetail.setObjectType(Constants.PRODUCT_TYPE);
        changeDetail.setSubmitDate(new Date());
        changeDetail = changeDetailService.saveChangeDetail(changeDetail);
        return changeDetail;
    }
	
	@Transactional
	public void saveChangeDetailPhoto(ProductVo productVo, AdminUser submitter) {
		ProductOrDynamicPriceQueryRequest request = new ProductOrDynamicPriceQueryRequest();
        AdminUser adminUser = adminUserService.getAdminUser(submitter.getId());
		request.setPageSize(Integer.MAX_VALUE);
		request.setStatus(Constants.NOT_AUDIT);
        request.setObjectType(Constants.PRODUCT_TYPE);

		if(null != productVo.getId()) {
			request.setObjectId(productVo.getId());
		} else {
			request.setProductName(productVo.getName());
		}
		Page<ChangeDetail> page = changeDetailService.findProductsOrDynamicPrices(request, adminUser);
		List<ChangeDetail> list = page.getContent();
		if(list.isEmpty()) {
			saveChangeDetail(productVo, productVo.getId(), new ChangeDetail(), adminUser);
		} else {
			for (ChangeDetail changeDetail : list) {
				ProductVo productVo1 = new ChangeDetailResponse(changeDetail).getProductVo();
                productVo1.setMediaFiles(productVo.getMediaFiles());
				saveChangeDetail(productVo1, productVo1.getId(), changeDetail, adminUser);
			}
		}
	}
	
	
	@Transactional
    public ChangeDetail createProductTemp(ProductRequest productRequest,AdminUser submitter) {
        ChangeDetail changeDetail = null;
        try {
            changeDetail = saveProductTemp(null, productRequest, submitter);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return changeDetail;
    }

    @Transactional
    public ChangeDetail getChangeDetail(Long id) {

        return changeDetailService.getChangeDetail(id);
    }

    @Transactional
    public ChangeDetail updateStatus(ChangeDetail changeDetail) {
        changeDetail.setPassDate(new Date());
        return changeDetailService.saveChangeDetail(changeDetail);
    }

    @Transactional(readOnly = true)
    public ProductTempQueryResponse findProductTemps(ProductOrDynamicPriceQueryRequest request, AdminUser adminUser) {
        request.setObjectType(Constants.PRODUCT_TYPE);
        Page<ChangeDetail> page = changeDetailService.findProductsOrDynamicPrices(request, adminUser);
        ProductTempQueryResponse response = new ProductTempQueryResponse();
        List<ChangeDetail> content = page.getContent();
        List<ChangeDetailResponse> productTemps = new ArrayList<>();
        for (ChangeDetail changeDetail : content) {
            ChangeDetailResponse changeDetailResponse = new ChangeDetailResponse(changeDetail);
            Product originProduct = null;
            if(changeDetail.getObjectId() != null){
            	originProduct = productService.findById(changeDetail.getObjectId());
            }
            ProductVo productVo = null;
            if(originProduct != null){
            	productVo = initProductVo(originProduct);
            }
            changeDetailResponse.setOriginProductVo(productVo);
            productTemps.add(changeDetailResponse);
            
        }
        response.setChangeDetailResponses(productTemps);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

        return response;
    }

    public ChangeDetailResponse getProductTemp(Long id) {
        ChangeDetail changeDetail = changeDetailService.getChangeDetail(id);
        ChangeDetailResponse response = new ChangeDetailResponse(changeDetail);

        Organization organization = organizationService.findById(changeDetail.getOrganizationId());
        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(organization.getId());
        organizationVo.setName(organization.getName());
        organizationVo.setCreateDate(organization.getCreateDate());
        organizationVo.setEnabled(organization.isEnabled());
        organizationVo.setTelephone(organization.getTelephone());
        response.setOrganization(organizationVo);
        return response;
    }

    @Transactional
	public void updateProduct(
			Long id,Long status,AdminUser verifier) {
		ChangeDetail changeDetail = getChangeDetail(id);
        Long productId = changeDetail.getObjectId();
        ProductVo productVo = null;
        changeDetail.setStatus(status);
        changeDetail.setVerifier(verifier);
        updateStatus(changeDetail);
        if (status.equals(Constants.THROUGH)) {
            try {
                objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                productVo = objectMapper.readValue(changeDetail.getContent(), ProductVo.class);
                if (productId != null) {
                    productFacade.updateProduct(productId, productVo, verifier);
                } else {
                    productFacade.createProduct(productVo, verifier);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
	}

    public ProductVo initProductVo(Product product) {
        ProductVo productVo = new ProductVo();
        productVo.setId(product.getId());
        productVo.setName(product.getName());

        Brand brand = product.getBrand();
        if (brand != null) {
            BrandVo brandVo = new BrandVo();
            brandVo.setId(brand.getId());
            brandVo.setBrandName(brand.getBrandName());
            productVo.setBrand(brandVo);
        }
        Category category = product.getCategory();
        if (category != null) {
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(category.getId());
            categoryVo.setName(category.getName());
            Category current = category;
            String hierarchyName = category.getName();
            while (current.getParentCategory() != null) {
                hierarchyName = current.getParentCategory().getName() + "-" + hierarchyName;
                current = current.getParentCategory();
            }
            categoryVo.setHierarchyName(hierarchyName);
            productVo.setCategory(categoryVo);
        }
        if (!product.getMediaFiles().isEmpty()) {
            for (MediaFile mediaFile : product.getMediaFiles()) {
                MediaFileVo mediaFileVo = new MediaFileVo();
                mediaFileVo.setId(mediaFile.getId());
                mediaFileVo.setUrl(mediaFile.getUrl());
                productVo.getMediaFiles().add(mediaFileVo);
            }
        }
        productVo.setShelfLife(product.getShelfLife());
        productVo.setSpecification(product.getSpecification());
        productVo.setProperties(product.getPropertyMap());
        productVo.setDetails(product.getDetails());
        productVo.setDiscrete(product.isDiscrete());
        Organization organization = organizationService.getDefaultOrganization();
        OrganizationVo organizationVo = new OrganizationVo();
        organizationVo.setId(organization.getId());
        organizationVo.setName(organization.getName());
        productVo.setOrganization(organizationVo);

        List<Sku> skus = product.getSkus();
        SkuVo skuVo = null;
        if (!skus.isEmpty()) {
            Sku sku = skus.get(0);
            skuVo = new SkuVo();
            skuVo.setId(sku.getId());
            skuVo.setProductId(product.getId());
            skuVo.setRate(sku.getRate());
            skuVo.setCapacityInBundle(sku.getCapacityInBundle());
            skuVo.setStatus(SkuStatus.fromInt(sku.getStatus()));

            skuVo.setSingleGross_wight(sku.getSingleGross_wight());
            skuVo.setSingleHeight(sku.getSingleHeight());
            skuVo.setSingleLong(sku.getSingleLong());
            skuVo.setSingleWidth(sku.getSingleWidth());
            skuVo.setSingleUnit(sku.getSingleUnit());

            skuVo.setBundleGross_wight(sku.getBundleGross_wight());
            skuVo.setBundleHeight(sku.getBundleHeight());
            skuVo.setBundleLong(sku.getBundleLong());
            skuVo.setBundleWidth(sku.getBundleWidth());
            skuVo.setBundleUnit(sku.getBundleUnit());
        } else {
            skuVo = new SkuVo();
        }
        productVo.getSkus().add(skuVo);
        return productVo;
    }
}
