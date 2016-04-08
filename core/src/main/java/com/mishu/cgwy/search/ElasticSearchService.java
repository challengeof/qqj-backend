package com.mishu.cgwy.search;


import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.repository.DynamicSkuPriceRepository;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuPriceHistoryService;
import com.mishu.cgwy.product.service.SkuSalesStatisticsService;
import com.mishu.cgwy.product.wrapper.BrandWrapper;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.lang.StringUtils;
import org.elasticsearch.action.admin.indices.alias.IndicesAliasesRequestBuilder;
import org.elasticsearch.action.admin.indices.mapping.put.PutMappingRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.Requests;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.collect.UnmodifiableIterator;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.persistence.Column;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

import static org.elasticsearch.common.xcontent.XContentFactory.jsonBuilder;

/**
 * User: xudong
 * Date: 3/10/15
 * Time: 4:08 PM
 */
public class ElasticSearchService implements SearchService {

    private static Logger logger = LoggerFactory.getLogger(ElasticSearchService.class);

    private Client client;

    /*@Autowired
    private DynamicSkuPriceRepository dynamicSkuPriceRepository;*/

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private SkuSalesStatisticsService skuSalesStatisticsService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private SkuPriceHistoryService skuPriceHistoryService;


    private String indexAlias = "product";

    private String clusterName = "elasticsearch";

    private String skuPriceHistoryIndex = "sku_price_history";

    private String host = "127.0.0.1";

    private int port = 9300;

    @PostConstruct
    public void setup() {
        Settings settings = ImmutableSettings.settingsBuilder()
                .put("cluster.name", clusterName)
                .build();


        client = new TransportClient(settings)
                .addTransportAddress(new InetSocketTransportAddress(host, port));

    }

    @PreDestroy
    public void release() {
        client.close();
    }


    @Override
    @Transactional(readOnly = true)
    public void rebuildIndex() throws IOException {
        // create new index
        final String newIndex = "product_v_" + System.currentTimeMillis();
        client.admin().indices().prepareCreate(newIndex).execute().actionGet();

        // create mapping
        XContentBuilder mapping = jsonBuilder().startObject().startObject("sku").startObject("properties")
                .startObject("marketPrice").field("type", "double").endObject()
                .startObject("categoryId").field("type", "long").endObject()
                .startObject("warehouseId").field("type", "long").endObject()
//                .startObject("blockId").field("type", "long").endObject()
                .startObject("cityId").field("type", "long").endObject()
                .startObject("brandName").field("type", "string").field("analyzer", "ik").endObject()
                .startObject("name").field("type", "string").field("analyzer", "ik").endObject()
                .endObject()
                .endObject()
                .endObject();

        PutMappingRequest putMappingRequest = Requests.
                putMappingRequest(newIndex).type("sku").source(mapping);

        client.admin().indices().putMapping(putMappingRequest).actionGet();

        // build new index
        for (Product product : productService.findAllProduct()) {
            Category category = product.getCategory();
            boolean active = true;
            while (category != null) {
                if (category.getStatus() != CategoryStatus.ACTIVE.getValue()) {
                    active = false;
                }
                category = category.getParentCategory();
            }
            if (active) {
                indexProduct(newIndex, product);
            }

        }


        // find all old indexes
        List<String> oldIndexes = new ArrayList<>();

        if (client.admin().indices().prepareAliasesExist(indexAlias).execute().actionGet().exists()) {
            final UnmodifiableIterator<String> iterator = client.admin().indices().prepareGetAliases(indexAlias).execute().actionGet().getAliases().keysIt();

            while (iterator.hasNext()) {
                oldIndexes.add(iterator.next());
            }
        }

        // swap alias
        final IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
        if (!oldIndexes.isEmpty()) {
            indicesAliasesRequestBuilder.removeAlias(oldIndexes.toArray(new String[oldIndexes.size()]), indexAlias);
        }
        indicesAliasesRequestBuilder.addAlias(newIndex, indexAlias);
        indicesAliasesRequestBuilder.execute().actionGet();

        // remove old indexes
        if (!oldIndexes.isEmpty()) {
            client.admin().indices().prepareDelete(oldIndexes.toArray(new String[oldIndexes.size()])).execute().actionGet();
        }

        flush();
    }

    public void createSkuPriceHistoryIndex() throws Exception {

        // create new index
        client.admin().indices().prepareCreate(skuPriceHistoryIndex).execute().actionGet();

//        final IndicesAliasesRequestBuilder indicesAliasesRequestBuilder = client.admin().indices().prepareAliases();
//        indicesAliasesRequestBuilder.addAlias(index, index);
//        indicesAliasesRequestBuilder.execute().actionGet();

        buildSkuPriceHistoryIndex();

        flush();
    }

    public void deleteSkuPriceHistoryIndex() throws Exception {
        client.admin().indices().prepareDelete(skuPriceHistoryIndex).execute().actionGet();
    }

    public void buildSkuPriceHistoryIndex() throws Exception {

        // create mapping
        XContentBuilder mapping = jsonBuilder().startObject().startObject(skuPriceHistoryIndex).startObject("properties")
                .startObject("singleSalePriceLimit").field("type", "double").endObject()
                .startObject("bundleSalePriceLimit").field("type", "double").endObject()
                .startObject("fixedPrice").field("type", "double").endObject()
                .startObject("singleSalePrice").field("type", "double").endObject()
                .startObject("bundleSalePrice").field("type", "double").endObject()
                .startObject("purchasePrice").field("type", "double").endObject()
                .endObject()
                .endObject()
                .endObject();

        PutMappingRequest putMappingRequest = Requests.
                putMappingRequest(skuPriceHistoryIndex).type(skuPriceHistoryIndex).source(mapping);

        client.admin().indices().putMapping(putMappingRequest).actionGet();

        // build new index
        for (SkuPriceHistory history : skuPriceHistoryService.finaAll()) {
            indexSkuPriceHistory(skuPriceHistoryIndex, history);
        }
    }

    public void indexSkuPriceHistory(String index, SkuPriceHistory history) {
        client.prepareIndex(index, "skuPriceHistory", history.getId().toString())
                .setSource(buildSkuPriceHistoryDocument(history))
                .execute()
                .actionGet();
    }

    @Override
    public void flush() {
        client.admin().indices().prepareRefresh().execute().actionGet();
    }

    @Override
    public void indexProduct(Product product) {
        try {
            indexProduct(indexAlias, product);
        } catch (Exception e) {
            logger.warn("catch exception", e);
        }
    }

    private void indexProduct(String index, Product product) {

        for (Sku sku : product.getSkus()) {

            if (sku.getStatus() == SkuStatus.ACTIVE.getValue() && product.getOrganization().isEnabled()) {

                client.prepareIndex(index, "sku", sku.getId().toString())
                        .setSource(buildSkuDocument(sku))
                        .execute()
                        .actionGet();

            }
            else {
                client.prepareDelete(index, "sku", sku.getId().toString()).execute().actionGet();
            }
        }
    }

    private Map<String, Object> buildSkuPriceHistoryDocument(SkuPriceHistory history) {
        Map<String, Object> document = new HashMap<>();
        document.put("skuId", history.getSku().getId());
        document.put("productId", history.getSku().getProduct().getId());
        document.put("name", history.getSku().getName());
        document.put("cityId", history.getCity().getId());
        document.put("createDate", history.getCreateDate());
        document.put("创建时间", history.getCreateDate());
        document.put("type", history.getType());
        document.put("singleSalePriceLimit", NumberUtils.doubleVal(history.getSingleSalePriceLimit()));
        document.put("bundleSalePriceLimit", NumberUtils.doubleVal(history.getBundleSalePriceLimit()));
        document.put("fixedPrice", NumberUtils.doubleVal(history.getFixedPrice()));
        document.put("singleSalePrice", NumberUtils.doubleVal(history.getSingleSalePrice()));
        document.put("bundleSalePrice", NumberUtils.doubleVal(history.getBundleSalePrice()));
        document.put("purchasePrice", NumberUtils.doubleVal(history.getPurchasePrice()));

        return document;
    }

    private Map<String, Object> buildSkuDocument(Sku sku) {
        Organization organization = sku.getProduct().getOrganization();

        final Map<String, Object> document = buildProductDocument(sku.getProduct(), organization);

        Set<Long> allWarehouseIds = organizationService.getOrganizationAllWarehouseIds(organization);
//        Set<Long> allBlockIds = organizationService.getOrganizationAllBlockIds(organization);
        Set<Long> allCityIds = organizationService.getOrganizationAllCityIds(organization);

        Set<Long> realWarehouseIds = new HashSet<>();
        Set<Long> realCityIds = new HashSet<>();


        for (DynamicSkuPrice price : inventoryService.getDynamicSkuPricesBySkuId(sku.getId())) {

//            if ((price.getBundlePriceStatus().isBundleAvailable() && price.getBundlePriceStatus().isBundleInSale()) || (price.getSinglePriceStatus().isSingleAvailable() && price.getSinglePriceStatus().isSingleInSale())) {
            if (price.getBundlePriceStatus().isBundleAvailable() || price.getSinglePriceStatus().isSingleAvailable()) {
                if (allWarehouseIds.contains(price.getWarehouse().getId())) {
                    realWarehouseIds.add(price.getWarehouse().getId());
                }
                if (allCityIds.contains(price.getWarehouse().getCity().getId())) {
                    realCityIds.add(price.getWarehouse().getCity().getId());
                }
            }
        }

        Set<Long> discountCityIds = new HashSet<>();
        if (!sku.getSkuTags().isEmpty()) {
            for (SkuTag skuTag : sku.getSkuTags()) {
                discountCityIds.add(skuTag.getCity().getId());
            }
        }
        document.put("discountCityIds", discountCityIds);

        document.put("cityId", realCityIds);

        document.put("warehouseId", realWarehouseIds);

//        document.put("blockId", allBlockIds);

        document.put("marketPrice", sku.getMarketPrice());

        document.put("capacityInBundle", sku.getCapacityInBundle());

        document.put("rate", sku.getRate());

        document.put("salesCount", skuSalesStatisticsService.getSaleCount(sku.getId()));

        document.put("id", sku.getId());

        document.put("status", sku.getStatus());

        return document;

    }


    private Map<String, Object> buildProductDocument(Product product, Organization organization) {
        Map<String, Object> document = new HashMap<>();
        if (product.getBrand() != null) {
            document.put("brandId", product.getBrand().getId());
            document.put("brandName", product.getBrand().getBrandName());
        }
        document.put("name", product.getName());

        Category category = product.getCategory();
        List<Category> parentCategories = new ArrayList<Category>();

        while (category != null) {
            parentCategories.add(category);
            category = category.getParentCategory();
        }

        document.put("categoryId", Collections2.transform(parentCategories, new Function<Category, Long>() {
            @Override
            public Long apply(Category input) {
                return input.getId();
            }
        }));

        document.put("warehouseId", organizationService.getOrganizationAllWarehouseIds(organization));

//        document.put("blockId", organizationService.getOrganizationAllBlockIds(organization));

        document.put("cityId", organizationService.getOrganizationAllCityIds(organization));

        return document;
    }


    @Override
    public SkuSearchHits findSkus(ProductSearchCriteria productSearchCriteria) {
        final SearchRequestBuilder builder = getSearchRequestBuilder(productSearchCriteria);

        if (productSearchCriteria.getPage() < 0) {
            productSearchCriteria.setPage(0);
        }

        builder.setFrom(productSearchCriteria.getPage() * productSearchCriteria.getPageSize()).setSize
                (productSearchCriteria.getPageSize());

        if ("marketPrice".equalsIgnoreCase(productSearchCriteria.getSortField())) {
            builder.addSort("marketPrice", productSearchCriteria.isAsc() ? SortOrder.ASC : SortOrder.DESC);
        } else if ("salesCount".equalsIgnoreCase(productSearchCriteria.getSortField())) {
            builder.addSort("salesCount", SortOrder.DESC);
        }

        if (StringUtils.isNotBlank(productSearchCriteria.getQuery())) {
            builder.addSort("_score", SortOrder.DESC);
        }

        final SearchResponse searchResponse = builder.execute().actionGet();

        List<Long> productIds = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().hits()) {
            Long id = Long.valueOf(hit.field("id").getValue().toString());
            productIds.add(id);
        }

        SkuSearchHits result = new SkuSearchHits();
        result.setSkuIds(productIds);
        result.setTotalResults(searchResponse.getHits().totalHits());
        return result;
    }

    @Override
    public List<BrandWrapper> groupBrands(ProductSearchCriteria productSearchCriteria) {
        final SearchRequestBuilder builder = getSearchRequestBuilder(productSearchCriteria);

        //这里加载前300个品的品牌
        productSearchCriteria.setPage(0);
        productSearchCriteria.setPageSize(300);

        builder.setFrom(productSearchCriteria.getPage() * productSearchCriteria.getPageSize()).setSize
                (productSearchCriteria.getPageSize());

        final SearchResponse searchResponse = builder.execute().actionGet();

        Map<Long, BrandWrapper> map = new HashMap<>();
        SearchHit[] hits = searchResponse.getHits().hits();
        for (SearchHit hit : hits) {

            if (hit.field("brandId") != null && hit.field("brandName") != null) {
                Long brandId = Long.valueOf(hit.field("brandId").getValue().toString());
                String brandName = hit.field("brandName").getValue().toString();


                if(!map.containsKey(brandId)) {
                    BrandWrapper brand = new BrandWrapper();
                    brand.setId(brandId);
                    brand.setBrandName(brandName);

                    map.put(brandId, brand);
                }
            }
        }

        return new ArrayList<>(map.values());
    }



    private SearchRequestBuilder getSearchRequestBuilder(ProductSearchCriteria productSearchCriteria) {
        final SearchRequestBuilder builder = client.prepareSearch(indexAlias).setTypes("sku");

        if (StringUtils.isNotBlank(productSearchCriteria.getQuery())) {
            final MultiMatchQueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(productSearchCriteria.getQuery(), "name", "brandName");
            queryBuilder.operator(MatchQueryBuilder.Operator.AND);
            builder.setQuery(queryBuilder);
        }

        List<FilterBuilder> filters = new ArrayList<FilterBuilder>();
        for (Map.Entry<String, String[]> entries : productSearchCriteria.getFilterCriteria().entrySet()) {
            filters.add(FilterBuilders.inFilter(entries.getKey(), entries.getValue()));
        }

        filters.add(FilterBuilders.inFilter("status", new int[]{SkuStatus.ACTIVE.getValue()}));

        final Long categoryId = productSearchCriteria.getCategoryId();
        if (categoryId != null && categoryId != 0) {
            filters.add(FilterBuilders.inFilter("categoryId", new long[]{categoryId.longValue()}));
        }

        final Long discountCityId = productSearchCriteria.getDiscountCityId();
        if (discountCityId != null) {
            filters.add(FilterBuilders.inFilter("discountCityIds", new long[]{discountCityId.longValue()}));
        }

        final Long warehouseId = productSearchCriteria.getWarehouseId();
        if (warehouseId != null && warehouseId != 0) {
            filters.add(FilterBuilders.inFilter("warehouseId", new long[]{warehouseId.longValue()}));
        }

        final Long cityId = productSearchCriteria.getCityId();
        if (cityId != null && cityId != 0) {
            filters.add(FilterBuilders.inFilter("cityId", new long[]{cityId.longValue()}));
        }

        /*final Long blockId = productSearchCriteria.getBlockId();
        if (blockId != null && blockId != 0) {
            filters.add(FilterBuilders.inFilter("blockId", new long[]{blockId.longValue()}));
        }*/

        builder.addFields("id", "name", "categoryId", "discountCityIds", "warehouseId", "cityId", "brandId", "brandName", "marketPrice");
//        builder.addFields("id", "name", "categoryId", "discountCityIds", "warehouseId", "cityId", "blockId", "brandId", "brandName", "marketPrice");

        builder.setPostFilter(FilterBuilders.andFilter(filters.toArray(new FilterBuilder[filters.size()])));
        return builder;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
