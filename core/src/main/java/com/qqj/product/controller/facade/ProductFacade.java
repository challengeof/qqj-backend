package com.qqj.product.controller.facade;

import com.qqj.product.controller.controller.ProductListRequest;
import com.qqj.product.controller.controller.ProductRequest;
import com.qqj.product.controller.wrapper.ProductWrapper;
import com.qqj.product.controller.service.ProductService;
import com.qqj.response.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductFacade {

    @Autowired
    private ProductService productService;

    public Response<ProductWrapper> list(ProductListRequest request) {
        return productService.list(request);
    }

    @Transactional
    public void add(ProductRequest request) {
        productService.add(request);
    }

    public List<ProductWrapper> all() {
        return productService.all();
    }

    public void modify(Long id, ProductRequest request) {
        productService.modify(id, request);
    }

    public ProductWrapper getProduct(Long id) {
        return new ProductWrapper(productService.get(id));
    }
}