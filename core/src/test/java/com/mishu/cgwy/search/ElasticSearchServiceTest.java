package com.mishu.cgwy.search;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.service.ProductService;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml"})
public class ElasticSearchServiceTest {

    @Autowired
    private ElasticSearchService searchService;

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private LocationService locationService;
    private Node node;
    private Client client;

    @Before
    public void setup() {
        node = NodeBuilder.nodeBuilder().local(true).node();
        client = node.client();
        searchService.setClient(client);
    }

    @After
    public void destroy() {
        client.close();
    }


    @Test
    public void testIndexProduct() throws Exception {
        Product product = new Product();

        product.setName("美乐香辣酱350g*24瓶/箱");

        product = productService.saveProduct(product);


        Sku sku = new Sku();
        sku.setProduct(product);
        sku.setStatus(SkuStatus.ACTIVE.getValue());
        product.getSkus().add(sku);

        sku = productService.saveSku(sku);


        City city = new City();
        city.setName("testCity");
        locationService.saveCity(city);

        List<Warehouse> warehouses = new ArrayList<Warehouse>();
        Warehouse e1 = new Warehouse();
        e1.setCity(city);
        warehouses.add(e1);

        e1 = locationService.saveWarehouse(e1);

        Warehouse e2 = new Warehouse();
        e2.setCity(city);
        warehouses.add(e2);
        e2 = locationService.saveWarehouse(e2);

        DynamicSkuPrice dynamicSkuPrice = new DynamicSkuPrice();
        dynamicSkuPrice.setSku(sku);
//        dynamicSkuPrice.setWarehouse(e1);
//        dynamicSkuPrice.setSalePrice(BigDecimal.TEN);
//        dynamicSkuPrice.setAvailable(true);
        inventoryService.saveDynamicPrice(dynamicSkuPrice);

        searchService.indexProduct(product);

        searchService.flush();

        final ProductSearchCriteria productSearchCriteria = new ProductSearchCriteria();
        productSearchCriteria.setCategoryId(null);
        productSearchCriteria.setQuery("香辣酱");
        productSearchCriteria.setWarehouseId(e1.getId());

        final SkuSearchHits searchResult = searchService.findSkus(productSearchCriteria);
        Assert.assertEquals(Arrays.asList(sku.getId()), searchResult.getSkuIds());

    }
}