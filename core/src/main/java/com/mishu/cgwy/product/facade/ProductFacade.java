package com.mishu.cgwy.product.facade;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.category.controller.CategoryRequest;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.MediaFile;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.common.vo.MediaFileVo;
import com.mishu.cgwy.common.wrapper.MediaFileWrapper;
import com.mishu.cgwy.error.CategoryAlreadyExistsException;
import com.mishu.cgwy.error.CategoryCirculationExistsException;
import com.mishu.cgwy.error.ProductAlreadyExistsException;
import com.mishu.cgwy.inventory.domain.BundleDynamicSkuPriceStatus;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.domain.SingleDynamicSkuPriceStatus;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.controller.*;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.dto.*;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuSalesStatisticsService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.vo.*;
import com.mishu.cgwy.product.wrapper.*;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.purchase.controller.PurchaseOrderRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.search.ProductSearchCriteria;
import com.mishu.cgwy.search.ProductSearchResult;
import com.mishu.cgwy.search.SearchService;
import com.mishu.cgwy.search.SkuSearchHits;
import com.mishu.cgwy.utils.LegacyProductUtils;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import com.mishu.cgwy.utils.TreeJsonHasChildCategory;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 10:58 PM
 */
@Service
public class ProductFacade {
    @Autowired
    private ProductService productService;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private SearchService searchService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private ContextualInventoryService contextualInventoryService;

    @Autowired
    private SkuSalesStatisticsService skuSalesStatisticsService;

    @Autowired
    private SkuService skuService;

    @Autowired
    private OrganizationService organizationService;

    /**
     * admin
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public ProductVo getProduct(Long id) {
        Product product = productService.getProduct(id);
        ProductVo productVo = new ProductVo();
        productVo.setId(product.getId());
        productVo.setName(product.getName());
        if (product.getCategory() != null) {
            CategoryVo categoryVo = new CategoryVo();
            categoryVo.setId(product.getCategory().getId());
            productVo.setCategory(categoryVo);
        }
        if (product.getBrand() != null) {
            BrandVo brandVo = new BrandVo();
            brandVo.setId(product.getBrand().getId());
            productVo.setBrand(brandVo);
        }
        List<MediaFile> mediaFiles = product.getMediaFiles();
        for (MediaFile mediaFile : mediaFiles) {
            MediaFileVo mediaFileVo = new MediaFileVo();
            mediaFileVo.setId(mediaFile.getId());
            mediaFileVo.setUrl(mediaFile.getUrl());
            productVo.getMediaFiles().add(mediaFileVo);
        }
        productVo.setDiscrete(product.isDiscrete());
        productVo.setShelfLife(product.getShelfLife());
        productVo.setSpecification(product.getSpecification());
        productVo.setProperties(product.getPropertyMap());
        productVo.setDetails(product.getDetails());
        productVo.setDiscrete(product.isDiscrete());
        List<Sku> skus = product.getSkus();
        for (Sku sku : skus) {
            SkuVo skuVo = new SkuVo();
            skuVo.setId(sku.getId());
            skuVo.setProductId(product.getId());
            skuVo.setCapacityInBundle(sku.getCapacityInBundle());
            skuVo.setSingleUnit(sku.getSingleUnit());
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

            skuVo.setStatus(SkuStatus.fromInt(sku.getStatus()));
            skuVo.setRate(sku.getRate());

            List<SkuTag> skuTags = sku.getSkuTags();
            for (SkuTag skuTag : skuTags) {
                SkuTagVo skuTagVo = new SkuTagVo();
                skuTagVo.setSkuId(skuTag.getSku().getId());
                skuTagVo.setCityId(skuTag.getCity().getId());
                skuTagVo.setInDiscount(skuTag.getInDiscount());
                skuTagVo.setLimitedQuantity(skuTag.getLimitedQuantity());
                skuVo.getSkuTags().add(skuTagVo);
            }
            productVo.getSkus().add(skuVo);
        }

        return productVo;
    }

    @Transactional(readOnly = true)
    public CompleteSkuWrapper getSku(Long skuId, Customer customer) {
        final Sku sku = productService.getSku(skuId);
        final CompleteSkuWrapper SkuVo = new CompleteSkuWrapper(sku);

        Long cityId = Constants.DEFAULT_CITY;
        Long warehouseId = Constants.DEFAULT_WAREHOUSE;

        if (customer != null && customer.getCity() != null) {
            cityId = customer.getCity().getId();
            warehouseId = locationService.getDefaultWarehouse(cityId).getId();
        }

        if (customer != null && customer.getBlock() != null && customer.getBlock().getWarehouse() != null) {
            warehouseId = customer.getBlock().getWarehouse().getId();
        }

        final DynamicSkuPrice dynamicSkuPrice = contextualInventoryService.getDynamicSkuPrice(skuId, warehouseId);
        if (dynamicSkuPrice != null) {
            SkuVo.setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getSinglePriceStatus()));
            SkuVo.setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getBundlePriceStatus()));
        }

//        SkuVo.setSaleAmount(skuSalesStatisticsService.getSaleCount(skuId));

        return SkuVo;
    }

/*
    @Transactional(readOnly = true)
    public ProductWrapper getProduct(Long productId, Long warehouseId) {
        ProductWrapper productWrapper = productService.getProductWrapper(productId);

        for (SkuVo SkuVo : productWrapper.getSkus()) {
            final DynamicSkuPrice salePrice = contextualInventoryService.getDynamicSkuPrice(SkuVo.getId(), warehouseId);
            if (salePrice.getSinglePriceStatus() != null) {
                SkuVo.setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(salePrice.getSinglePriceStatus()));
            }
            if (salePrice.getBundlePriceStatus() != null) {
                SkuVo.setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(salePrice.getBundlePriceStatus()));
            }
        }

        return productWrapper;
    }*/

    @Transactional
    public CategoryWrapper createCategory(CategoryRequest request) {
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = productService.getCategory(request.getParentCategoryId());
        }

        checkCategoryNameDuplication(null, parentCategory, request.getName());

        Category category = new Category();
        category.setName(request.getName());
        category.setParentCategory(parentCategory);

        if (parentCategory != null) {
            parentCategory.getChildrenCategories().add(category);
        }

        category.setStatus(CategoryStatus.fromInt(request.getStatus()).getValue());

        if (request.getMediaFileId() != null) {
            category.setMediaFile(mediaFileService.getMediaFile(request.getMediaFileId()));
        }

        if (null != request.getShowSecond()) {
            category.setShowSecond(request.getShowSecond());
        }

        return new CategoryWrapper(productService.saveCategory(category));
    }

    @Transactional
    public CategoryWrapper updateCategory(Long id, CategoryRequest request) {
        Category parentCategory = null;
        if (request.getParentCategoryId() != null) {
            parentCategory = productService.getCategory(request.getParentCategoryId());
        }

        Category category = productService.getCategory(id);
        if (null != request.getName()) {
            checkCategoryNameDuplication(id, parentCategory, request.getName());

            if (parentCategory != null) {
                checkCategoryCirculation(id, parentCategory);
            }

            category.setName(request.getName());
        }

        if (category.getParentCategory() != null) {
            if (parentCategory == null) {
                category.getParentCategory().getChildrenCategories().remove(category);
            } else if (!parentCategory.getId().equals(category.getParentCategory().getId())) {
                category.getParentCategory().getChildrenCategories().remove(category);
                category.setParentCategory(parentCategory);
                parentCategory.getChildrenCategories().add(category);
            }
        } else {
            if (parentCategory != null) {
                category.setParentCategory(parentCategory);
                parentCategory.getChildrenCategories().add(category);
            }
        }

        if (parentCategory != null) {
            checkCategoryCirculation(id, parentCategory);
        }


        if (request.getMediaFileId() != null) {
            category.setMediaFile(mediaFileService.getMediaFile(request.getMediaFileId()));
        }

        category.setStatus(CategoryStatus.fromInt(request.getStatus()).getValue());

        if (null != request.getShowSecond()) {
            category.setShowSecond(request.getShowSecond());
        }

        return new CategoryWrapper(productService.saveCategory(category));
    }


    // category->parent must not have circle
    private void checkCategoryCirculation(Long id, Category parentCategory) {
        do {
            if (parentCategory.getId().equals(id)) {
                throw new CategoryCirculationExistsException();
            }

            parentCategory = parentCategory.getParentCategory();
        } while (parentCategory != null);

    }


    @Transactional(readOnly = true)
    public List<CategoryWrapper> listCategories(Long parentCategoryId, Integer status) {
        List<CategoryWrapper> result = new ArrayList<CategoryWrapper>();

        for (Category category : getSubCategories(parentCategoryId, status)) {
            if (category != null) {
                result.add(new CategoryWrapper(category));
            }
        }

        return result;
    }


    public List<TreeJsonHasChild> getCategoriesTree(Long id, Integer status) {
        List<TreeJsonHasChild> trees = new ArrayList<>();
        final List<Category> subCategories = getSubCategories(id, status);
        System.out.println(subCategories);
        for (Category category : subCategories) {
            if (null != category) {
                TreeJsonHasChildCategory tree = new TreeJsonHasChildCategory();
                tree.setId(String.valueOf(category.getId()));
                tree.setText(category.getName());
                tree.setStatus(CategoryStatus.fromInt(category.getStatus()).getValue());
                tree.setChildren(getCategoriesTree(category.getId(), status));
                for (City city : category.getCities()) {
                    tree.getCityIds().add(city.getId());
                }
                trees.add(tree);
            }
        }
        return trees;
    }


    @Transactional
    public void setCategoryCity(Long categoryId, Long cityId, Boolean active) {
        Category category = productService.getCategory(categoryId);
        City city = locationService.getCity(cityId);
        if (null != category && null != active && null != city) {
            if (active.equals(Boolean.TRUE)) {
                if (!category.getCities().contains(city)) {
                    category.getCities().add(city);
                }
            } else {
                if (active.equals(Boolean.FALSE)) {
                    if (category.getCities().contains(city)) {
                        category.getCities().remove(city);
                    }
                }
            }
            productService.saveCategory(category);
        }
    }


    @Transactional(readOnly = true)
    public List<CategoryWrapper> listAllCategories() {
        List<CategoryWrapper> result = new ArrayList<CategoryWrapper>();

        final List<Category> allCategories = productService.findAllCategories();

        Collections.sort(allCategories, new Comparator<Category>() {
            @Override
            public int compare(Category o1, Category o2) {
                Integer level1 = getLevel(o1);
                Integer level2 = getLevel(o2);

                int levelCompare = level1.compareTo(level2);

                return levelCompare == 0 ? o1.getId().compareTo(o2.getId()) : levelCompare;
            }

            private Integer getLevel(Category category) {
                int level = 0;
                Category parent = null;

                while ((parent = category.getParentCategory()) != null) {
                    level++;
                    category = parent;
                }
                return level;
            }

        });

        for (Category category : allCategories) {
            result.add(new CategoryWrapper(category));
        }

        return result;
    }


    public List<Category> getSubCategories(Long parentCategoryId, final Integer status) {


        List<Category> categories = new ArrayList<Category>();

        if (parentCategoryId != null && !parentCategoryId.equals(0L)) {

            categories = productService.getCategory(parentCategoryId).getChildrenCategories();

        } else {
            categories = productService.getTopCategories();
        }

        if (status != null) {
            categories = new ArrayList<>(Collections2.filter(categories, new Predicate<Category>() {
                @Override
                public boolean apply(Category input) {
                    return input.getStatus() == status;
                }
            }));
        }

        return categories;
    }

    private void checkCategoryNameDuplication(Long categoryId, Category parentCategory, String name) {
        List<Category> childrenCategories = null;
        if (parentCategory != null) {
            childrenCategories = parentCategory.getChildrenCategories();
        } else {
            childrenCategories = productService.getTopCategories();
        }

        for (Category childCategory : childrenCategories) {
            if (childCategory.getName().equals(name) && !childCategory.getId().equals(categoryId)) {
                throw new CategoryAlreadyExistsException();
            }
        }
    }

    public CategoryWrapper getCategory(Long id) {
        Category category = productService.getCategory(id);
        if (category != null) {
            return new CategoryWrapper(category);
        }
        return null;
    }

    @Transactional(readOnly = true)
    public CategoryAllOrD1Response buildAllCategoriesResponse() {
        List<Category> topCategories = getSubCategories(null, null);
        CategoryAllOrD1Response response = new CategoryAllOrD1Response((topCategories));
        return response;
    }

    @Transactional(readOnly = true)
    public CategoryAllOrD1Response buildD1CategoryResponse(Long D1Id) {
        Category d1 = productService.getCategory(D1Id);
        CategoryAllOrD1Response response = new CategoryAllOrD1Response(d1);
        return response;
    }

    public CategoryD2Response buildD2CategoryResponse(Long id) {
        Category category = productService.getCategory(id);
        CategoryD2Response response = new CategoryD2Response(category);
        return response;
    }

    @Deprecated
    @Transactional
    public GetProductResponse getProductResponse(String productNumber, Long warehouseId) {
        Sku sku = productService.getSku(Long.valueOf(productNumber));
        GetProductResponse response = new GetProductResponse();
        if (sku != null) {
            response.setProductId(sku.getId());
            response.setProductNumber(String.valueOf(sku.getId()));
            response.setName(sku.getProduct().getName());
            response.setCategoryId(sku.getProduct().getCategory().getId());
            //如果未登录

            DynamicSkuPrice dynamicSkuPrice = null;
            if (warehouseId == 0) {
                dynamicSkuPrice = contextualInventoryService.getDynamicSkuPrice(sku.getId(),
                        Constants.DEFAULT_WAREHOUSE);
//                response.setPrice(dynamicSkuPrice.getSalePrice());

            }
            //如果登录
            else {
                dynamicSkuPrice = contextualInventoryService.getDynamicSkuPrice(sku.getId(),
                        warehouseId);
//                response.setPrice(dynamicSkuPrice.getSalePrice());
            }

            List<String> imageList = new ArrayList<String>();
            /*if (sku.getProduct().getMediaFile() != null) {
                imageList.add(sku.getProduct().getMediaFile().getUrl());

            }*/
            response.setImageList(imageList);
            response.setMarketPrice(sku.getMarketPrice());

            Map<String, String> propertyMap = sku.getProduct().getPropertyMap();
            //品牌
            propertyMap.put("brand", sku.getProduct().getBrand() != null ? sku.getProduct().getBrand().getBrandName() : "");
            //大箱包装
            propertyMap.put("capacityInBundle", String.valueOf(sku.getCapacityInBundle()));
            response.setMetaDataList(LegacyProductUtils.transformMetadataItems(propertyMap));

            if (dynamicSkuPrice == null) {
                response.setMaxBuy(Constants.MAX_BUY);
            } else {
//                response.setMaxBuy(Math.max(dynamicSkuPrice.getStock(), Constants.MAX_BUY));
            }


        }

        return response;
    }


    @Transactional
    public void createProduct(ProductVo productWrapper, AdminUser adminUser) {
        saveProduct(null, productWrapper, adminUser);
    }

    @Transactional
    public void updateProduct(Long productId, ProductVo productVo, AdminUser adminUser) {
        saveProduct(productId, productVo, adminUser);
    }


    private Product saveProduct(Long productId, ProductVo productVo, AdminUser adminUser) {

        Organization organization = organizationService.getDefaultOrganization();
        // check product brandName
        checkProductNameDuplication(productId, productVo.getName(), organization);

        Product product = null;


        if (productId != null) {
            product = productService.getProduct(productId);

        } else {
            product = new Product();
        }

        product.setOrganization(organization);

        product.setName(productVo.getName());
//        product.setDescription(productWrapper.getDescription());
        product.setDiscrete(productVo.isDiscrete());

        List<MediaFileVo> mediaFileWrappers = productVo.getMediaFiles();
        if (!mediaFileWrappers.isEmpty()) {
            product.getMediaFiles().clear();
            for (MediaFileVo mediaFileWrapper : mediaFileWrappers) {
                MediaFile mediaFile = mediaFileWrapper == null ? null : mediaFileService.getMediaFile(mediaFileWrapper.getId());
                product.getMediaFiles().add(mediaFile);
            }
        }

        Brand brand = productVo.getBrand() == null ? null : productService.getBrand(productVo.getBrand()
                .getId());
        product.setBrand(brand);

        Category category = productVo.getCategory() == null ? null : productService.getCategory(productVo
                .getCategory().getId());
        product.setCategory(category);

        product.setPropertyMap(productVo.getProperties());

        product.setDetails(productVo.getDetails());

        product.setSpecification(productVo.getSpecification());

        product.setShelfLife(productVo.getShelfLife());

        product = productService.saveProduct(product);

        List<SkuVo> SkuVos = productVo.getSkus();
        List<Sku> skus = product.getSkus();

        if (!SkuVos.isEmpty()) {
            SkuVo SkuVo = SkuVos.get(0);
            Sku sku = null;
            if (!skus.isEmpty()) {
                sku = skus.get(0);
            } else {
                sku = new Sku();
                sku.setProduct(product);
                product.getSkus().add(sku);
            }
            sku.setCapacityInBundle(SkuVo.getCapacityInBundle());
            sku.setRate(SkuVo.getRate());
            sku.setSingleGross_wight(SkuVo.getSingleGross_wight());
            sku.setSingleNet_weight(SkuVo.getSingleNet_weight());
            sku.setSingleLong(SkuVo.getSingleLong());
            sku.setSingleWidth(SkuVo.getSingleWidth());
            sku.setSingleHeight(SkuVo.getSingleHeight());
            sku.setSingleUnit(SkuVo.getSingleUnit());

            sku.setBundleGross_wight(SkuVo.getBundleGross_wight());
            sku.setBundleNet_weight(SkuVo.getBundleNet_weight());
            sku.setBundleHeight(SkuVo.getBundleHeight());
            sku.setBundleLong(SkuVo.getBundleLong());
            sku.setBundleWidth(SkuVo.getBundleWidth());
            sku.setBundleUnit(SkuVo.getBundleUnit());

            sku.setStatus(SkuVo.getStatus().getValue());

            saveSku(sku);
        }

        searchService.indexProduct(product);

        return product;
    }

    public Sku saveSku(Sku sku) {
        final Sku result = productService.saveSku(sku);

        syncSingleSkuToAllWarehouse(result.getId());

        searchService.indexProduct(result.getProduct());
        return result;
    }

    private void checkProductNameDuplication(Long productId, String name, Organization organization) {
        List<Product> products = productService.findByProductName(name, organization);

        for (Product product : products) {
            if (!product.getId().equals(productId)) {
                throw new ProductAlreadyExistsException();
            }
        }
    }

    /**
     * web
     * @param customer
     * @param brandId
     * @param sort
     * @param order
     * @param page
     * @param rows
     * @param name
     * @return
     */
    @Transactional(readOnly = true)
    public SearchProductResponse searchByName(Customer customer, Long brandId, String sort, String order, Integer
            page, Integer rows, String name) {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        if (customer.getBlock() != null) {
            criteria.setCityId(customer.getBlock().getCity().getId());
//            criteria.setBlockId(customer.getBlock().getId());
            criteria.setWarehouseId(customer.getBlock().getWarehouse().getId());
        } else {
            criteria.setCityId(Constants.DEFAULT_CITY);
            criteria.setWarehouseId(locationService.getDefaultWarehouse(Constants.DEFAULT_CITY).getId());
        }

        if (rows != 0) {
            criteria.setPage(page);
            criteria.setPageSize(rows);
        }

        if ("sellCount".equals(sort)) {
            sort = "salesCount";
        }
        if ("price".equals(sort)) {
            sort = "marketPrice";
        }
        criteria.setSortField(sort);
        criteria.setAsc("asc".equalsIgnoreCase(order));


        if (brandId != null) {
            criteria.getFilterCriteria().put("brandId", new String[]{brandId.toString()});
//            Brand brand = productService.getBrand(brandId);
//            if (brand != null){
//                String brandName = brand.getBrandName();
//                criteria.getFilterCriteria().put("brandName", new String[]{brandName});
//
//            }


        }


        //criteria.getFilterCriteria().put("name", new String[]{name});

        criteria.setQuery(name);

        ProductSearchResult result = findProducts(criteria);
        SearchProductResponse response = new SearchProductResponse();
        response.setTotal(result.getTotalResults());
        ArrayList<ProductItem> productItems = new ArrayList<ProductItem>();
        Set<BrandWrapper> brandList = new HashSet<BrandWrapper>();
        //构建rows
        for (SkuWrapper SkuVo : result.getSkus()) {
            productItems.add(new ProductItem(SkuVo));

            if (SkuVo.getBrand() != null) {
                brandList.add(SkuVo.getBrand());
            }
        }
        response.setRows(productItems);
        response.setBrandList(brandList);

        return response;

    }


    /**
     * web
     * @param searchCriteria
     * @return
     */
    @Transactional
    public ProductSearchResult findProducts(ProductSearchCriteria searchCriteria) {

        final SkuSearchHits hits = searchService.findSkus(searchCriteria);
        ProductSearchResult productSearchResult = new ProductSearchResult();
        productSearchResult.setPage(searchCriteria.getPage());
        productSearchResult.setPageSize(searchCriteria.getPageSize());
        productSearchResult.setTotalResults(hits.getTotalResults());

        if (!hits.getSkuIds().isEmpty()) {
            List<Sku> skus = new ArrayList<>();

            skus.addAll(productService.getSkus(hits.getSkuIds()));


            final List<SkuWrapper> SkuVos = new ArrayList<>();

            // update dynamic sku price
            if (searchCriteria.getWarehouseId() != null) {
                final Map<Long, DynamicSkuPrice> dynamicSkuPricesMap = contextualInventoryService.getDynamicSkuPrices(hits.getSkuIds(),
                        searchCriteria.getWarehouseId());
                for (Sku sku : skus) {
                    final DynamicSkuPrice dynamicSkuPrice = dynamicSkuPricesMap.get(sku.getId());
                    if (dynamicSkuPrice != null) {
                        SkuWrapper SkuVo = new SkuWrapper(sku);
                        SkuVo.setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getSinglePriceStatus()));
                        SkuVo.setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getBundlePriceStatus()));
                        SkuVos.add(SkuVo);
                    }
                }
            }

            productSearchResult.setSkus(SkuVos);

        }

        return productSearchResult;
    }

    //返回关键词列表
    public KeywordResponse getKeywordByQuery(String name, Integer page, Integer rows) {
        KeywordResponse response = new KeywordResponse();
        //TODO:搜索相关
        return response;
    }

    /**
     * web
     * @param customer
     * @param productNumber
     * @param page
     * @param rows
     * @return
     */
    //返回热销列表
    @Transactional(readOnly = true)
    public HotSellResponse getHotsellByProductnumber(Customer customer, String productNumber, Integer page, Integer rows) {
        HotSellResponse response = new HotSellResponse();
        Sku sku = productService.getSku(Long.valueOf(productNumber));
        Category category = sku.getProduct().getCategory();
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        /*if (customer != null && customer.getZone() != null) {
            criteria.setWarehouseId(customer.getZone().getWarehouse().getId());
        } else {
            criteria.setWarehouseId(Constants.DEFAULT_WAREHOUSE);
        }*/

        criteria.setCategoryId(category.getId());
        criteria.setPage(page);
        criteria.setPageSize(rows);
        criteria.setSortField(Constants.SORT_SELL_COUNT);
        ProductSearchResult result = findProducts(criteria);
        List<ProductItem> list = new ArrayList<ProductItem>();
        for (SkuWrapper o : result.getSkus()) {
            list.add(new ProductItem(o));
        }
        response.setHotSellList(list);
        return response;
    }

    @Transactional(readOnly = true)
    public List<BrandWrapper> findAllBrands() {
        List<BrandWrapper> result = new ArrayList<>();
        for (Brand brand : productService.findAllBrands()) {
            result.add(new BrandWrapper(brand));
        }

        return result;
    }

    /**
     * admin
     * @param request
     * @return
     */
    @Transactional(readOnly = true)
    public SkuQueryResponse findSkus(SkuQueryRequest request) {
        Page<Sku> page = productService.findSkus(request);
        SkuQueryResponse response = new SkuQueryResponse();
        List<Sku> content = page.getContent();

        List<SkuVo> SkuVos = new ArrayList<>();
        for (Sku sku : content) {
            SkuVo vo = new SkuVo();
            vo.setId(sku.getId());

            final Product product = sku.getProduct();
            vo.setName(product.getName());
            vo.setProductId(product.getId());

            if (product.getBrand() != null) {
                Brand brand = product.getBrand();
                BrandVo brandVo = new BrandVo();

                brandVo.setId(brand.getId());
                brandVo.setBrandName(brand.getBrandName());
                vo.setBrand(brandVo);
            }

            vo.setStatus(SkuStatus.fromInt(sku.getStatus()));

            /*List<MediaFile> mediaFiles = sku.getProduct().getMediaFiles();
            if (!mediaFiles.isEmpty()) {
                String postfix = "?imageView2/0/h/250/format/jpg";
                for(MediaFile mediaFile : mediaFiles) {
                    vo.getMediaFileUrls().add(mediaFile.getUrl() + postfix);
                }
            }*/

            if (product.getCategory() != null) {
                Category category = product.getCategory();
                CategoryVo categoryVo = new CategoryVo();
                categoryVo.setId(category.getId());

                String hierarchyName = category.getName();
                Category current = category;
                while (current.getParentCategory() != null) {
                    hierarchyName = current.getParentCategory().getName() + "-" + hierarchyName;
                    current = current.getParentCategory();
                }
                categoryVo.setHierarchyName(hierarchyName);
                vo.setCategory(categoryVo);
            }

            vo.setCapacityInBundle(sku.getCapacityInBundle());

            vo.setSingleUnit(sku.getSingleUnit());
            vo.setBundleUnit(sku.getBundleUnit());
            vo.setRate(sku.getRate());

            for (SkuTag skuTag : sku.getSkuTags()) {
                SkuTagVo skuTagVo = new SkuTagVo();
                skuTagVo.setSkuId(skuTag.getSku().getId());
                skuTagVo.setCityId(skuTag.getCity().getId());
                skuTagVo.setCityName(skuTag.getCity().getName());
                skuTagVo.setInDiscount(skuTag.getInDiscount());
                skuTagVo.setLimitedQuantity(skuTag.getLimitedQuantity());
                vo.getSkuTags().add(skuTagVo);
            }

            SkuVos.add(vo);
        }

        response.setSkus(SkuVos);
        response.setTotal(page.getTotalElements());
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());

        return response;
    }


    @Transactional
    public void syncAllSkuToWarehouse(Long warehouseId) {
        Warehouse warehouse = locationService.getWarehouse(warehouseId);

        List<Sku> skus = productService.findAllSku(warehouse.getCity().getId());

        final List<DynamicSkuPrice> dynamicSkuPrices = contextualInventoryService.getDynamicSkuPricesByWarehouseId(warehouseId);

        final HashSet<Long> alreadyExisted = new HashSet<>(Collections2.transform(dynamicSkuPrices, new Function<DynamicSkuPrice, Long>() {
            @Override
            public Long apply(DynamicSkuPrice input) {
                return input.getSku().getId();
            }
        }));

        for (Sku sku : skus) {
            if (!alreadyExisted.contains(sku.getId())) {
                DynamicSkuPrice dynamicSkuPrice = new DynamicSkuPrice();
//                dynamicSkuPrice.setAvailable(false);
                dynamicSkuPrice.setWarehouse(warehouse);
                dynamicSkuPrice.setSku(sku);
                dynamicSkuPrice.setBundlePriceStatus(new BundleDynamicSkuPriceStatus());
                dynamicSkuPrice.setSinglePriceStatus(new SingleDynamicSkuPriceStatus());

                contextualInventoryService.saveDynamicPrice(dynamicSkuPrice);
            }
        }
    }

    @Transactional
    public void syncSingleSkuToAllWarehouse(Long skuId) {
        Sku sku = productService.getSku(skuId);

        final List<Warehouse> allWarehouses = locationService.getAllWarehouses();

        final List<DynamicSkuPrice> dynamicSkuPrices = contextualInventoryService.getDynamicSkuPricesBySkuId(skuId);

        for (Warehouse warehouse : allWarehouses) {
            boolean found = false;

            for (DynamicSkuPrice dynamicSkuPrice : dynamicSkuPrices) {
                if (dynamicSkuPrice.getWarehouse().getId().equals(warehouse.getId())) {
                    found = true;
                    break;
                }
            }

            if (!found) {
                DynamicSkuPrice dynamicSkuPrice = new DynamicSkuPrice();
                dynamicSkuPrice.setWarehouse(warehouse);
                dynamicSkuPrice.setSku(sku);
                dynamicSkuPrice.setSinglePriceStatus(new SingleDynamicSkuPriceStatus());
                dynamicSkuPrice.setBundlePriceStatus(new BundleDynamicSkuPriceStatus());

                contextualInventoryService.saveDynamicPrice(dynamicSkuPrice);
            }
        }
    }

    /**
     * web
     * @param request
     * @param customer
     * @return
     */
    @Transactional(readOnly = true)
    public CustomerProductQueryResponse getSkus(CustomerProductQueryRequest request, Customer customer) {
        CustomerProductQueryResponse response = new CustomerProductQueryResponse();

        ProductSearchCriteria criteria = buildCriteria(request, customer);

        ProductSearchResult result = findProducts(criteria);

        List<Long> skuIds = new ArrayList<>(Collections2.transform(result.getSkus(), new Function<SkuWrapper, Long>() {
            @Override
            public Long apply(SkuWrapper input) {
                return input.getId();
            }
        }));

        Map<Long, DynamicSkuPrice> map = contextualInventoryService.getDynamicSkuPrices(skuIds, criteria.getWarehouseId());

        response.setTotal(result.getTotalResults());
        for (SkuWrapper SkuVo : result.getSkus()) {
            if (map.get(SkuVo.getId()) != null) {
                SkuVo.setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(map.get(SkuVo.getId()).getSinglePriceStatus()));
                SkuVo.setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(map.get(SkuVo.getId()).getBundlePriceStatus()));
            }
        }

        final List<SkuSalesStatistics> saleCount = skuSalesStatisticsService.getSaleCount(skuIds);
        Map<Long, Long> saleCountMap = new HashMap<>();

        for (SkuSalesStatistics s : saleCount) {
            saleCountMap.put(s.getSku().getId(), s.getSalesCount());
        }

        for (SkuWrapper SkuVo : result.getSkus()) {
            if (saleCountMap.get(SkuVo.getId()) != null) {
                SkuVo.setSaleAmount(saleCountMap.get(SkuVo.getId()));
            }
        }

        response.setSkus(result.getSkus());

        return response;
    }

    private ProductSearchCriteria buildCriteria(CustomerProductQueryRequest request, Customer customer) {
        ProductSearchCriteria criteria = new ProductSearchCriteria();
        if (request.getBrandId() != null) {
            criteria.getFilterCriteria().put("brandId", new String[]{String.valueOf(request.getBrandId())});
        }

        criteria.setPage(request.getPage());
        criteria.setPageSize(request.getPageSize());

        if (Constants.SORT_SELL_COUNT.equals(request.getSortProperty())) {
            criteria.setSortField(Constants.SORT_SELL_COUNT);
        } else if (Constants.SORT_PRICE.equals(request.getSortProperty())) {
            criteria.setSortField(Constants.SORT_PRICE);
        }

        criteria.setAsc("asc".equalsIgnoreCase(request.getSortDirection()));


        if (customer == null || (customer.getBlock() == null)) {
            if (request.getCityId() != null) {
                criteria.setCityId(request.getCityId());
                criteria.setWarehouseId(locationService.getDefaultWarehouse(request.getCityId()).getId());
            } else {
                criteria.setCityId(Constants.DEFAULT_CITY);
                criteria.setWarehouseId(locationService.getDefaultWarehouse(Constants.DEFAULT_CITY).getId());
            }

            if (request.getCategoryId() != null) {
                Long categoryId = request.getCategoryId();
                if (categoryId == -1) {
                   criteria.setDiscountCityId(locationService.getCity(Constants.DEFAULT_CITY).getId());
                } else {
                    criteria.setCategoryId(request.getCategoryId());
                }
            }
        } else {
            Long cityId = customer.getBlock().getCity().getId();
            criteria.setCityId(cityId);
//            criteria.setBlockId(customer.getBlock().getId());
            criteria.setWarehouseId(customer.getBlock().getWarehouse().getId());

            if (request.getCategoryId() != null) {
                Long categoryId = request.getCategoryId();
                if (categoryId == -1) {
                    criteria.setDiscountCityId(cityId);
                } else {
                    criteria.setCategoryId(request.getCategoryId());
                }
            }

            if (null != request.getCityId() && !cityId.equals(request.getCityId())) {
                criteria.setCityId(request.getCityId());
                criteria.setWarehouseId(locationService.getDefaultWarehouse(request.getCityId()).getId());
//                criteria.setBlockId(null);
                if (request.getCategoryId() != null) {
                    Long categoryId = request.getCategoryId();
                    if (categoryId == -1) {
                        criteria.setDiscountCityId(request.getCityId());
                    } else {
                        criteria.setCategoryId(request.getCategoryId());
                    }
                }
            }
        }

        if (StringUtils.isNotBlank(request.getName())) {
            criteria.setQuery(request.getName());
        }
        return criteria;
    }

    @Transactional(readOnly = true)
    public CustomerProductBrandResponse groupBrands(CustomerProductQueryRequest request, Customer customer) {
        CustomerProductBrandResponse response = new CustomerProductBrandResponse();

        ProductSearchCriteria criteria = buildCriteria(request, customer);

        final List<BrandWrapper> brands = searchService.groupBrands(criteria);

        response.setBrands(brands);

        response.setTotal(brands.size());

        return response;
    }

    @Transactional
    public CategoryWrapper updateCategoryChildren(Long id, Long[] children) {

        Category category = productService.getCategory(id);

        category.getChildrenCategories().clear();
        if (children != null || children.length > 0) {
            for (Long childId : children) {
                Category child = productService.getCategory(childId);
                category.getChildrenCategories().add(child);
                child.setParentCategory(category);
            }
        }

        return new CategoryWrapper(productService.saveCategory(category));

    }
}
