package com.qqj.product.service;

import com.qqj.product.controller.ProductListRequest;
import com.qqj.product.controller.ProductRequest;
import com.qqj.product.domain.Product_;
import com.qqj.product.wrapper.ProductWrapper;
import com.qqj.product.domain.Product;
import com.qqj.product.repository.ProductRepository;
import com.qqj.response.query.QueryResponse;
import com.qqj.utils.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Transactional(readOnly = true)
    public QueryResponse<ProductWrapper> list(final ProductListRequest request) {
        final PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());

        Page<Product> page = productRepository.findAll(new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getName() != null) {
                    predicates.add(cb.like(root.get(Product_.name), String.format("%%%s%%", request.getName())));
                }

                query.orderBy(cb.desc(root.get(Product_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageRequest);

        QueryResponse<ProductWrapper> response = new QueryResponse<>();
        response.setContent(EntityUtils.toWrappers(page.getContent(), ProductWrapper.class));
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }

    @Transactional
    public void add(ProductRequest request) {
        Product product = new Product();
        product.setName(request.getName());
        product.setStatus(request.getStatus());
        product.setPrice(request.getPrice());
        product.setPrice0(request.getPrice0());
        product.setPrice1(request.getPrice1());
        product.setPrice2(request.getPrice2());

        productRepository.save(product);
    }

    public List<ProductWrapper> all() {
        return EntityUtils.toWrappers(productRepository.findAll(), ProductWrapper.class);
    }

    public Product get(Long id) {
        return productRepository.getOne(id);
    }

    public void save(Product product) {
        productRepository.save(product);
    }

    public void modify(Long id, ProductRequest request) {
        Product product = productRepository.getOne(id);
        product.setName(request.getName());
        product.setStatus(request.getStatus());
        product.setPrice(request.getPrice());
        product.setPrice0(request.getPrice0());
        product.setPrice1(request.getPrice1());
        product.setPrice2(request.getPrice2());
        productRepository.save(product);
    }
}
