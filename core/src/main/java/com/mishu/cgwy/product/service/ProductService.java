package com.mishu.cgwy.product.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.controller.ProductQueryRequest;
import com.mishu.cgwy.product.controller.SkuQueryRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.repository.BrandRepository;
import com.mishu.cgwy.product.repository.CategoryRepository;
import com.mishu.cgwy.product.repository.ProductRepository;
import com.mishu.cgwy.product.repository.SkuRepository;
import com.mishu.cgwy.product.wrapper.ProductWrapper;
import com.mishu.cgwy.profile.domain.Restaurant_;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 7:50 PM
 */
@Service
public class ProductService {
    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private SkuRepository skuRepository;

    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }

    public Product getProduct(Long productId) {
        return productRepository.findOne(productId);
    }

    public Category getCategory(Long categoryId) {
        return categoryRepository.getOne(categoryId);
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAll();
    }

    public List<Category> getTopCategories() {
        return categoryRepository.findByParentCategoryIsNull();
    }

    public Category saveCategory(Category category) {
        return categoryRepository.save(category);
    }

    public List<Brand> findAllBrands() {
        return brandRepository.findAll();
    }

    public Brand getBrand(Long brandId) {
        return brandRepository.getOne(brandId);
    }

    @Transactional
    public Brand saveBrand(Brand brand) {
        return brandRepository.save(brand);
    }


    public List<Product> findByProductName(final String productName, final Organization organization) {

        return productRepository.findAll(new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (organization != null) {
                    predicates.add(cb.equal(root.get(Product_.organization).get(Organization_.id), organization.getId()));
                }

                predicates.add(cb.equal(root.get(Product_.name), productName));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }


    @Transactional
    public Sku saveSku(Sku sku) {
        return skuRepository.save(sku);
    }


    public Sku getSku(Long skuId) {
        return skuRepository.findOne(skuId);
    }

    public Sku findSku(Long skuId) {
        return skuRepository.findOne(skuId);
    }

    public List<Sku> getSkus(final List<Long> skuId) {
        final List<Sku> skus = skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                Fetch<Sku, Product> productFetch = root.fetch(Sku_.product, JoinType.INNER);
//                productFetch.fetch(Product_.mediaFile, JoinType.LEFT);
                productFetch.fetch(Product_.brand, JoinType.LEFT);
                return root.get(Sku_.id).in(skuId);
            }
        });

        // keep origin sequence
        final Map<Long, Integer> seq = new HashMap<>();
        int index = 0;
        for (Long id : skuId) {
            seq.put(id, index++);
        }

        Collections.sort(skus, new Comparator<Sku>() {
            @Override
            public int compare(Sku o1, Sku o2) {
                return seq.get(o1.getId()) - seq.get(o2.getId());
            }
        });


        return skus;
    }

    public List<Sku> getAllSkus(){
        return skuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findAllProduct() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Sku> findAllSku() {
        return skuRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Sku> findAllSku(final Long cityId) {
        return skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<Predicate>();

                /*if (cityId != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.organization).get(Organization_.city).get(City_.id), cityId));
                }*/
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    @Transactional(readOnly = true)
    public Page<Product> findProducts(final ProductQueryRequest request, final AdminUser adminUser) {

        final Set<Long> categoryIds = new HashSet<>();

        if (request.getCategoryId() != null) {
            List<Category> subCategories = getRecursiveChildrenCategories(getCategory(request.getCategoryId()));
            for (Category c : subCategories) {
                categoryIds.add(c.getId());
            }
        }

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, "id"));


        return productRepository.findAll(new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!query.getResultType().equals(Long.class)) {
//                    root.fetch(Product_.mediaFile, JoinType.LEFT);
                    root.fetch(Product_.brand, JoinType.LEFT);
                    root.fetch(Product_.skus, JoinType.LEFT);
                    root.fetch(Product_.category, JoinType.LEFT);
                    root.fetch(Product_.organization, JoinType.LEFT);
                }

                List<Predicate> predicates = new ArrayList<Predicate>();

                if(adminUser != null){
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Product_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                predicates.add(cb.isTrue(root.get(Product_.organization).get(Organization_.enabled)));

                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(Product_.id), request.getId()));
                }

                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(Product_.brand).get(Brand_.id), request.getBrandId()));
                }

                if (!categoryIds.isEmpty()) {
                    predicates.add(root.get(Product_.category).get(Category_.id).in(categoryIds));
                }

                if (StringUtils.isNotBlank(request.getName())) {
                    predicates.add(cb.like(root.get(Product_.name), "%" + request.getName() + "%"));
                }

                if (request.getStatus() != null) {
                    query.distinct(true);
                    ListJoin<Product, Sku> skuProductListJoin = root.join(Product_.skus, JoinType.LEFT);
                    predicates.add(cb.equal(skuProductListJoin.get(Sku_.status), SkuStatus.fromInt(request.getStatus()).getValue()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

    //根据商品名称一部分模糊查询商品
    public List<Product> findProductsByPartName(final Long organizationId, final String partName) {
        return productRepository.findAll(new Specification<Product>() {
            @Override
            public Predicate toPredicate(Root<Product> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
//            	root.fetch(Product_.mediaFile, JoinType.LEFT);
                root.fetch(Product_.brand, JoinType.LEFT);
                root.fetch(Product_.category, JoinType.LEFT);

                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.and(cb.like(root.get(Product_.name), partName)));

                predicates.add(cb.equal(root.get(Product_.organization).get(Organization_.id), organizationId));

            	return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
    }

    private List<Category> getRecursiveChildrenCategories(Category category) {
        List<Category> result = new ArrayList<>();

        result.add(category);
        for (Category child : category.getChildrenCategories()) {
            result.addAll(getRecursiveChildrenCategories(child));
        }

        return result;


    }

    public Page<Sku> findSkus(final SkuQueryRequest request) {

        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize(), new Sort(Sort
                .Direction.DESC, "id"));


        return skuRepository.findAll(new Specification<Sku>() {
            @Override
            public Predicate toPredicate(Root<Sku> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!query.getResultType().equals(Long.class)) {
                    Fetch<Sku, Product> fetch = root.fetch(Sku_.product, JoinType.INNER);
//                    fetch.fetch(Product_.mediaFile, JoinType.LEFT);
                    fetch.fetch(Product_.brand, JoinType.LEFT);
                }

                List<Predicate> predicates = new ArrayList<Predicate>();
                if (request.getProductId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.id), request.getProductId()));
                }

                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.id), request.getSkuId()));
                }

                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.brand).get(Brand_.id), request
                            .getBrandId()));
                }

                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(Sku_.product).get(Product_.name), "%" + request.getProductName()
                            + "%"));
                }

                if (request.getStatus() != null && request.getStatus() != 0) {
                    predicates.add(cb.equal(root.get(Sku_.status), SkuStatus.fromInt(request
                            .getStatus()).getValue()));
                }

                if (request.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.category).get(Category_.id), request.getCategoryId()));
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                if (request.getSkuTagCityId() != null) {
                    ListJoin<Sku, SkuTag> skuTagSetJoin = root.join(Sku_.skuTags);
                    predicates.add(cb.equal(skuTagSetJoin.get(SkuTag_.city).get(City_.id), request.getSkuTagCityId()));
                }

                predicates.add(cb.isTrue(root.get(Sku_.product).get(Product_.organization).get(Organization_.enabled)));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);
    }

	public Product findById(Long productId) {
		return productRepository.getOne(productId);
	}

 

}
