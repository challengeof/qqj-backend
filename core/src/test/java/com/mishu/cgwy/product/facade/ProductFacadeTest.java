package com.mishu.cgwy.product.facade;

import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.order.facade.OrderFacade;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.domain.Product;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.service.ProductService;

import org.json.JSONException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/application-persist.xml", "/application-context.xml", "/application-security.xml", "/application-search.xml"})
public class ProductFacadeTest {
    @Autowired
    private ProductFacade productFacade;

    @Autowired
    private ProductService productService;
    
    @Autowired
    private ContextualInventoryService contextualInventoryService;
    
    @Autowired
    private ProductEdbFacade productEdbFacade;
    
    @Autowired
    private OrderFacade orderFacade;
    
    @Autowired
    private OrderService orderService;
    
    @Test
    @Transactional
    @Rollback
    public void testGetProduct() throws Exception {
        Product product = new Product();
        product.setName("product 1");

        Sku sku = new Sku();

        product.getSkus().add(sku);

        productService.saveProduct(product);
    }
    
    @Test
    @Transactional
    public void getProduct() throws JSONException {
    	DynamicSkuPrice dynamicSkuPrice = contextualInventoryService.getDynamicSkuPriceById(27458L);
    	productEdbFacade.getProduct(dynamicSkuPrice.getSku());
    }
    
    @Test
    @Transactional
    public void saveProduct() throws JSONException {
    	DynamicSkuPrice dynamicSkuPrice = contextualInventoryService.getDynamicSkuPriceById(27458L);
    	productEdbFacade.saveProduct(dynamicSkuPrice);
    }
}