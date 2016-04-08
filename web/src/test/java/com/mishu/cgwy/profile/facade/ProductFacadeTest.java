package com.mishu.cgwy.profile.facade;

import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Region;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Zone;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.facade.ProductFacade;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.search.ElasticSearchService;
import org.elasticsearch.client.Client;
import org.elasticsearch.node.Node;
import org.elasticsearch.node.NodeBuilder;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

/**
 * Created by kaicheng on 3/23/15.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml", "/application-search.xml"})
public class ProductFacadeTest {
    @Autowired
    ProductFacade productFacade;

    @Autowired
    ProductService productService;

    @Autowired
    ElasticSearchService searchService;

    @Autowired
    ContextualInventoryService inventoryService;


    @Autowired
    LocationService locationService;
    private Zone qinghe;
    private Product lijinjiDarkSoySauce;
    private Category darkSoySauce;
    private Category soySauce;
    private Node node;
    private Client client;

    /*@Before
    public void setup() {
        node = NodeBuilder.nodeBuilder().local(true).node();
        client = node.client();
        searchService.setClient(client);

        City beijing = newCity("北京");

        Region haidian = newRegion(beijing, "海淀");

        Region xicheng = newRegion(beijing, "西城");

        Warehouse northMarket = newWarehouse(beijing, "城北市场");

        qinghe = newZone(xicheng, "清河", northMarket);

        Zone shangdi = newZone(haidian, "上地", northMarket);

        Zone jishuitan = newZone(xicheng, "积水潭", northMarket);

        Category flavour = newCategory(null, "调味品");

        soySauce = newCategory(flavour, "酱油");

        Category vinegar = newCategory(flavour, "醋");

        Category lightSoySauce = newCategory(soySauce, "生抽");

        darkSoySauce = newCategory(soySauce, "老抽");

        Brand lijinjiBrand = newBrand("李锦记");
        Brand haitianBrand = newBrand("海天");

        Product lijinjiLightSoySauce = newProduct(lightSoySauce, lijinjiBrand, "李锦记生抽");

        Product haitianLightSoySauce = newProduct(lightSoySauce, haitianBrand, "海天生抽");

        lijinjiDarkSoySauce = newProduct(darkSoySauce, lijinjiBrand, "李锦记老抽");

        Category riceVinegar = newCategory(vinegar, "米醋");

        Category whiteVinegar = newCategory(vinegar, "白醋");

        Sku lijinjiLightSoySauceSku = newSku(lijinjiLightSoySauce);
        newDynamicSkuSalePrice(lijinjiLightSoySauceSku, northMarket, BigDecimal.TEN);

        Sku haitianLightSoySauceSku = newSku(haitianLightSoySauce);
        newDynamicSkuSalePrice(haitianLightSoySauceSku, northMarket, BigDecimal.TEN);

        Sku lijinjiDarkSoySauceSku = newSku(lijinjiDarkSoySauce);
        newDynamicSkuSalePrice(lijinjiDarkSoySauceSku, northMarket, BigDecimal.TEN);

        searchService.indexProduct(lijinjiLightSoySauce);
        searchService.indexProduct(haitianLightSoySauce);
        searchService.indexProduct(lijinjiDarkSoySauce);

        searchService.flush();
    }

    @After
    public void destroy() {
        client.close();
    }

    private Product newProduct(Category category, Brand brand, String name) {
        Product product = new Product();
        product.setName(name);
        product.setCategory(category);
        product.setBrand(brand);
        return productService.saveProduct(product);
    }

    private Sku newSku(Product product) {
        Sku sku = new Sku();
        sku.setProduct(product);
        product.getSkus().add(sku);
        sku.setStatus(SkuStatus.ACTIVE.getValue());
        sku = productService.saveSku(sku);
        return sku;
    }

    private DynamicSkuPrice newDynamicSkuSalePrice(Sku sku, Warehouse northMarket, BigDecimal salePrice) {
        DynamicSkuPrice dynamicSkuPrice = new DynamicSkuPrice();
        dynamicSkuPrice.setSku(sku);
//        dynamicSkuPrice.setAvailable(true);
//        dynamicSkuPrice.setSalePrice(salePrice);
        dynamicSkuPrice.setWarehouse(northMarket);

        dynamicSkuPrice = inventoryService.saveDynamicPrice(dynamicSkuPrice);
        return dynamicSkuPrice;
    }

    private Warehouse newWarehouse(City city, String name) {
        Warehouse warehouse = new Warehouse();
        warehouse.setCity(city);
        warehouse.setName(name);
        return locationService.saveWarehouse(warehouse);
    }

    private Brand newBrand(String brandName) {
        Brand brand = new Brand();

        brand.setBrandName(brandName);
        return productService.saveBrand(brand);
    }

    private Category newCategory(Category parent, String name) {
        Category category = new Category();
        category.setParentCategory(parent);
        category.setName(name);
        category = productService.saveCategory(category);

        category.setStatus(CategoryStatus.ACTIVE.getValue());

        if (parent != null) {
            parent.getChildrenCategories().add(category);
        }
        return category;
    }

    private Zone newZone(Region region, String name, Warehouse warehouse) {
        Zone zone = new Zone();
        zone.setName(name);
        zone.setActive(true);
        zone.setRegion(region);
        zone.setWarehouse(warehouse);

        return locationService.saveZone(zone);
    }

    private Region newRegion(City beijing, String name) {
        Region region = new Region();
        region.setName(name);
        region.setCity(beijing);
        return locationService.saveRegion(region);
    }

    private City newCity(String name) {
        City city = new City();
        city.setName(name);

        return locationService.saveCity(city);
    }

    @Test
    @Transactional
    @Rollback
    public void testIndexProduct() {
//        Assert.assertEquals(1, productFacade.listCategories(null).size());
        Assert.assertEquals(2, productFacade.buildAllCategoriesResponse().getD2Count().intValue());
        Assert.assertEquals(4, productFacade.buildAllCategoriesResponse().getD3Count().intValue());
    }

    @Test
    @Transactional
    @Rollback
    public void testListProduct() throws InterruptedException {
        Customer customer = new Customer();
        customer.setUsername("testUsername");
        customer.setPassword("testPassword");

//        customer.setZone(qinghe);

        Assert.assertEquals(1, productFacade.buildListProductResponse(darkSoySauce.getId(), null, 0,
                Constants
                        .ROWS_DEFAULT
                , Constants.ORDER_DESC, Constants.SORT_SELL_COUNT, customer).getRows().size());

        Assert.assertEquals(3, productFacade.buildListProductResponse(soySauce.getId(), null, 0,
                Constants
                        .ROWS_DEFAULT
                , Constants.ORDER_DESC, Constants.SORT_SELL_COUNT, customer).getRows().size());
    }*/


}
