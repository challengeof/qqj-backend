package com.mishu.cgwy.inventory.service;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.inventory.domain.*;
import com.mishu.cgwy.inventory.repository.DynamicSkuPriceRepository;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.organization.domain.Organization_;
import com.mishu.cgwy.product.controller.DynamicPriceCandidatesRequest;
import com.mishu.cgwy.product.controller.DynamicPriceQueryRequest;
import com.mishu.cgwy.product.domain.*;
import com.mishu.cgwy.product.wrapper.DynamicSkuPriceWrapper;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 3/5/15
 * Time: 7:08 PM
 */
@Service
public class ContextualInventoryService {

    @Autowired
    private DynamicSkuPriceRepository dynamicSkuPriceRepository;

    public boolean isAvailable(Sku sku, Warehouse warehouse, boolean bundle) {
        final DynamicSkuPrice dynamicSkuPrice = getDynamicSkuPrice(sku.getId(), warehouse.getId());

        if (bundle) {
            //打包
            if (dynamicSkuPrice != null && dynamicSkuPrice.getBundlePriceStatus() != null) {
                return dynamicSkuPrice.getBundlePriceStatus().isBundleAvailable() && dynamicSkuPrice.getBundlePriceStatus().isBundleInSale() && dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice().compareTo(BigDecimal.ZERO) > 0;
            }
        } else {
            //单品
            if (dynamicSkuPrice != null && dynamicSkuPrice.getSinglePriceStatus() != null) {
                return dynamicSkuPrice.getSinglePriceStatus().isSingleAvailable() && dynamicSkuPrice.getSinglePriceStatus().isSingleInSale() && dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice().compareTo(BigDecimal.ZERO) > 0;
            }
        }
        return false;
    }

    /*public int getStock(Sku sku,  Warehouse warehouse) {
        final DynamicSkuPrice dynamicSkuPrice = getDynamicSkuPrice(sku.getId(), warehouse.getId());
        if (dynamicSkuPrice != null) {
            if (sku.getStatus() == SkuStatus.ACTIVE.getValue() && dynamicSkuPrice.isAvailable()) {
                return dynamicSkuPrice.getStock();
            }
        }

        return 0;
    }*/


    public DynamicSkuPrice getDynamicSkuPrice(final Long skuId, final Long warehouseId) {
        return dynamicSkuPriceRepository.findOne(new Specification<DynamicSkuPrice>() {
            @Override
            public Predicate toPredicate(Root<DynamicSkuPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return cb.and(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.id), skuId),
                        cb.equal(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.id), warehouseId));
            }
        });
    }

    public DynamicSkuPrice getDynamicSkuPriceById(Long id) {
        return dynamicSkuPriceRepository.getOne(id);
    }

    public Map<Long, DynamicSkuPrice> getDynamicSkuPrices(List<Long> skuIds, Long warehouseId) {
        final List<DynamicSkuPrice> list = dynamicSkuPriceRepository.findBySkuIdInAndWarehouseId
                (skuIds, warehouseId);

        Map<Long, DynamicSkuPrice> result = new HashMap<Long, DynamicSkuPrice>();
        for (DynamicSkuPrice dynamicSkuPrice : list) {
            result.put(dynamicSkuPrice.getSku().getId(), dynamicSkuPrice);
        }

        return result;
    }

    public DynamicSkuPrice findBySkuIdInAndWarehouseId(Long skuId, Long warehouseId) {
    	return dynamicSkuPriceRepository.findBySkuIdAndWarehouseId(skuId, warehouseId);
    }

    public List<DynamicSkuPrice> getDynamicSkuPricesByWarehouseId(Long warehouseId) {
        return dynamicSkuPriceRepository.findByWarehouseId(warehouseId);
    }

    public List<DynamicSkuPrice> getDynamicSkuPricesBySkuId(Long skuId) {
        return dynamicSkuPriceRepository.findBySkuId(skuId);
    }

    public DynamicSkuPrice saveDynamicPrice(DynamicSkuPrice dynamicSkuPrice) {
        return dynamicSkuPriceRepository.save(dynamicSkuPrice);
    }

    public Page<DynamicSkuPrice> queryDynamicPrice(final DynamicPriceQueryRequest request,
                                                   final AdminUser adminUser) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        return dynamicSkuPriceRepository.findAll(new Specification<DynamicSkuPrice>() {
            @Override
            public Predicate toPredicate(Root<DynamicSkuPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                if (!query.getResultType().equals(Long.class)) {
                    Fetch<DynamicSkuPrice, Sku> skuFetch = root.fetch(DynamicSkuPrice_.sku, JoinType.INNER);
                    Fetch<Sku, Product> productFetch = skuFetch.fetch(Sku_.product, JoinType.LEFT);
//                    productFetch.fetch(Product_.mediaFile, JoinType.LEFT);
                    productFetch.fetch(Product_.brand, JoinType.LEFT);
                    productFetch.fetch(Product_.organization, JoinType.LEFT);

                }

                List<Predicate> predicates = new ArrayList<Predicate>();

                if (adminUser != null) {
                    if (!adminUser.isGlobalAdmin()) {
                        request.setOrganizationId(adminUser.getOrganizations().iterator().next().getId());
                    }

                    Set<Long> warehouseIds = new HashSet<>();
                    Set<Long> cityIds = new HashSet<>();

                    for (City city : adminUser.getCities()) {
                        cityIds.add(city.getId());
                    }

                    for (Warehouse warehouse : adminUser.getWarehouses()) {
                        warehouseIds.add(warehouse.getId());
                    }

                    if (PermissionCheckUtils.canViewAllInBlock(adminUser)) {

                        List<Predicate> blockCondition = new ArrayList<>();
                        if (!warehouseIds.isEmpty()) {
                            blockCondition.add(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.id).in(warehouseIds));
                        }
                        if (!cityIds.isEmpty()) {
                            blockCondition.add(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.city).get(City_.id).in(cityIds));
                        }

                        if (!blockCondition.isEmpty()) {
                            predicates.add(cb.or(blockCondition.toArray(new Predicate[blockCondition.size()])));
                        } else {
                            predicates.add(cb.or());
                        }

                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.city).get(City_.id), request.getCityId()));
                }

                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.id), request.getWarehouseId()));
                }

                if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));
                }

                if (request.getSkuId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.id), request.getSkuId()));
                }

                if (request.getProductId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.id),
                            request.getProductId()));
                }

                if (request.getCategoryId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.category).get(Category_.id), request.getCategoryId()));
                }

                if (StringUtils.isNotBlank(request.getProductName())) {
                    predicates.add(cb.like(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.name),
                            "%" + request.getProductName() + "%"));
                }

                if (request.getSkuCreateDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(DynamicSkuPrice_.sku).get(Sku_.createDate), DateUtils.truncate(request.getSkuCreateDate(), Calendar.DATE)));
                }

                if (request.getWarehouseId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.warehouse).get(Warehouse_.id),
                            request.getWarehouseId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.status), request.getStatus()));
                }

                if (request.getSingleAvailable() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleAvailable),
                            request.getSingleAvailable()));
                }

                if (request.getSingleInSale() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleInSale),
                            request.getSingleInSale()));
                }

                if (request.getBundleAvailable() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleAvailable),
                            request.getBundleAvailable()));
                }

                if (request.getBundleInSale() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleInSale),
                            request.getBundleInSale()));
                }

                if (request.getBrandId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.brand).get
                            (Brand_.id), request.getBrandId()));
                }

                predicates.add(cb.isTrue(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.organization).get(Organization_.enabled)));

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);


    }

    public void updateSalePrice(com.mishu.cgwy.order.domain.Order order, Warehouse warehouse) {
        final Iterator<OrderItem> iterator = order.getOrderItems().iterator();
        while (iterator.hasNext()){
            OrderItem orderItem = iterator.next();
            if(orderItem.getSpikeItem()!=null){
                orderItem.setPrice(orderItem.getSpikeItem().getPrice());
            }else {

                DynamicSkuPrice dynamicSkuPrice = getDynamicSkuPrice(orderItem.getSku().getId(), warehouse.getId());
                if (dynamicSkuPrice != null) {
                    if (orderItem.isBundle() && dynamicSkuPrice.getBundlePriceStatus() != null && dynamicSkuPrice.getBundlePriceStatus().isBundleAvailable() && dynamicSkuPrice.getBundlePriceStatus().isBundleInSale() && !dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice().equals(BigDecimal.ZERO)) {
                        orderItem.setPrice(dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice());
                    } else if (!orderItem.isBundle() && dynamicSkuPrice.getSinglePriceStatus() != null && dynamicSkuPrice.getSinglePriceStatus().isSingleAvailable() && dynamicSkuPrice.getSinglePriceStatus().isSingleInSale() && !dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice().equals(BigDecimal.ZERO)) {
                        orderItem.setPrice(dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice());
                    } else {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }

        order.calculateSubTotal();
        order.calculateTotal();
    }

    public void updateOrderItemPrice(com.mishu.cgwy.order.domain.Order order, Warehouse warehouse) {
        final Iterator<OrderItem> iterator = order.getOrderItems().iterator();
        while (iterator.hasNext()){
            OrderItem orderItem = iterator.next();
            DynamicSkuPrice dynamicSkuPrice = getDynamicSkuPrice(orderItem.getSku().getId
                    (), warehouse.getId());

            if (dynamicSkuPrice != null) {
                if (dynamicSkuPrice.getSinglePriceStatus() != null && dynamicSkuPrice.getSinglePriceStatus().isSingleAvailable() && dynamicSkuPrice.getSinglePriceStatus().isSingleInSale() && !dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice().equals(BigDecimal.ZERO)) {
                    orderItem.setPrice(dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice());
                } else if (dynamicSkuPrice.getBundlePriceStatus() != null && dynamicSkuPrice.getBundlePriceStatus().isBundleAvailable() && dynamicSkuPrice.getBundlePriceStatus().isBundleInSale() && !dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice().equals(BigDecimal.ZERO)) {
                    orderItem.setPrice(dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice().divide(new BigDecimal(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_DOWN));
                } else {
                    iterator.remove();
                }
            } else {
                iterator.remove();
            }
        }

        order.calculateSubTotal();
        order.calculateTotal();
    }

    public List<DynamicSkuPriceWrapper> getDynamicSkuPriceCandidates(final DynamicPriceCandidatesRequest request) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());

        Page<DynamicSkuPrice> page = dynamicSkuPriceRepository.findAll(new Specification<DynamicSkuPrice>() {
            @Override
            public Predicate toPredicate(Root<DynamicSkuPrice> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<Predicate>();

                predicates.add(cb.like(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getName())));

                /*if (request.getOrganizationId() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.product).get(Product_.organization).get(Organization_.id), request.getOrganizationId()));
                }*/
                if (request.getWarehouse() != null) {
                    predicates.add(cb.equal(root.get(DynamicSkuPrice_.warehouse), request.getWarehouse()));
                }
                predicates.add(cb.equal(root.get(DynamicSkuPrice_.sku).get(Sku_.status) , SkuStatus.ACTIVE.getValue()));
                //predicates.add(cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleAvailable), true));
                //predicates.add(cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleInSale), true));
;
                //predicates.add(cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleAvailable), true));
                //predicates.add(cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleInSale), true));
                predicates.add(cb.or(cb.and(cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleAvailable), true), cb.equal(root.get(DynamicSkuPrice_.singlePriceStatus).get(SingleDynamicSkuPriceStatus_.singleInSale), true)),
                        cb.and(cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleAvailable), true), cb.equal(root.get(DynamicSkuPrice_.bundlePriceStatus).get(BundleDynamicSkuPriceStatus_.bundleInSale), true))));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        }, pageable);

        List<DynamicSkuPriceWrapper> wrappers = new ArrayList<>();

        for (DynamicSkuPrice dynamicSkuPrice : page.getContent()) {
            wrappers.add(new DynamicSkuPriceWrapper(dynamicSkuPrice));
        }
        return wrappers;
    }

    public DynamicSkuPrice findBySkuIdAndWarehouseIdAndStatus(Long id, Long warehosue) {
        return dynamicSkuPriceRepository.findBySkuIdAndWarehouseIdAndSkuStatus(id,warehosue,SkuStatus.ACTIVE.getValue());
    }

    public Page<DynamicSkuPrice> findAll(Specification<DynamicSkuPrice> skuPriceListSpecification, PageRequest pageable) {
        return dynamicSkuPriceRepository.findAll(skuPriceListSpecification, pageable);
    }

    public List<DynamicSkuPrice> findAll(Specification<DynamicSkuPrice> skuPriceListSpecification) {
        return dynamicSkuPriceRepository.findAll(skuPriceListSpecification);
    }
}
