package com.mishu.cgwy.purchase.service;

import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.order.domain.CutOrder_;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.SkuVendorRepository;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.wrapper.SimpleSkuWrapper;
import com.mishu.cgwy.purchase.controller.PurchaseAccordingResultResponse;
import com.mishu.cgwy.purchase.controller.PurchaseOrderRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.enumeration.PurchaseOrderItemSignStatus;
import com.mishu.cgwy.purchase.repository.PurchaseOrderItemRepository;
import com.mishu.cgwy.purchase.vo.PurchaseOrderItemVo;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.stock.domain.StockTotal;
import com.mishu.cgwy.stock.service.StockTotalService;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by wangguodong on 15/9/14.
 */
@Service
public class PurchaseOrderItemService {

    @Autowired
    private PurchaseOrderItemRepository purchaseOrderItemRepository;

    @Autowired
    private SkuVendorRepository skuVendorRepository;

    @Autowired
    private StockTotalService stockTotalService;

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private PurchaseOrderItemSignService purchaseOrderItemSignService;

    public void save(PurchaseOrderItem purchaseOrderItem) {
        purchaseOrderItemRepository.save(purchaseOrderItem);
    }

    public void delete(PurchaseOrderItem purchaseOrderItem) {
        purchaseOrderItemRepository.save(purchaseOrderItem);
    }

    public PurchaseOrderItem findOne(Long id) {
        return purchaseOrderItemRepository.findOne(id);
    }

    public List<PurchaseOrderItem> findBySkuId(Long skuId) {
        return purchaseOrderItemRepository.findBySkuId(skuId);
    }

    public PurchaseOrderItem getOne(Long id) {
        return purchaseOrderItemRepository.getOne(id);
    }

    public PurchaseAccordingResultResponse<PurchaseOrderItemVo> getPurchaseOrderItems(final PurchaseOrderRequest request) throws Exception {
        int page = request.getPage();
        int pageSize = request.getPageSize();

        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemRepository.findAll(new Specification<PurchaseOrderItem>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.depot).get(Depot_.id), request.getDepotId()));
                }

                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.id), request.getId()));
                }

                if (request.getStartDate() != null && request.getEndDate() != null) {
                    predicates.add(cb.between(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.expectedArrivedDate), request.getStartDate(), request.getEndDate()));
                }

                if (request.getType() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.type), request.getType()));
                }

                if (request.getProductName() != null) {
                    predicates.add(cb.like(root.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                }

                PurchaseOrderItemSignStatus sign = PurchaseOrderItemSignStatus.get(request.getSign());
                if (sign == PurchaseOrderItemSignStatus.READY) {
                    Subquery<PurchaseOrderItemSign> purchaseOrderItemSignSubquery = query.subquery(PurchaseOrderItemSign.class);
                    Root<PurchaseOrderItemSign> purchaseOrderItemSignRoot = purchaseOrderItemSignSubquery.from(PurchaseOrderItemSign.class);
                    purchaseOrderItemSignSubquery.where(
                            cb.equal(purchaseOrderItemSignRoot.get(PurchaseOrderItemSign_.city).get(City_.id), request.getCityId()),
                            cb.equal(purchaseOrderItemSignRoot.get(PurchaseOrderItemSign_.depot).get(Depot_.id), request.getDepotId()),
                            cb.equal(purchaseOrderItemSignRoot.get(PurchaseOrderItemSign_.sku).get(Sku_.id), root.get(PurchaseOrderItem_.sku).get(Sku_.id)),
                            cb.equal(purchaseOrderItemSignRoot.get(PurchaseOrderItemSign_.status), PurchaseOrderItemSignStatus.READY.getValue())
                    );
                    predicates.add(
                            cb.exists(purchaseOrderItemSignSubquery.select(purchaseOrderItemSignRoot))
                    );

                } else if (sign == PurchaseOrderItemSignStatus.NOTREADY) {
                    Subquery<PurchaseOrderItemSign> purchaseOrderItemSignSubquery1 = query.subquery(PurchaseOrderItemSign.class);
                    Root<PurchaseOrderItemSign> purchaseOrderItemSignRoot1 = purchaseOrderItemSignSubquery1.from(PurchaseOrderItemSign.class);
                    purchaseOrderItemSignSubquery1.where(
                            cb.equal(purchaseOrderItemSignRoot1.get(PurchaseOrderItemSign_.city).get(City_.id), request.getCityId()),
                            cb.equal(purchaseOrderItemSignRoot1.get(PurchaseOrderItemSign_.depot).get(Depot_.id), request.getDepotId()),
                            cb.equal(purchaseOrderItemSignRoot1.get(PurchaseOrderItemSign_.sku).get(Sku_.id), root.get(PurchaseOrderItem_.sku).get(Sku_.id)),
                            cb.equal(purchaseOrderItemSignRoot1.get(PurchaseOrderItemSign_.status), PurchaseOrderItemSignStatus.NOTREADY.getValue())
                    );

                    Subquery<PurchaseOrderItemSign> purchaseOrderItemSignSubquery2 = query.subquery(PurchaseOrderItemSign.class);
                    Root<PurchaseOrderItemSign> purchaseOrderItemSignRoot2 = purchaseOrderItemSignSubquery2.from(PurchaseOrderItemSign.class);
                    purchaseOrderItemSignSubquery2.where(
                            cb.equal(purchaseOrderItemSignRoot2.get(PurchaseOrderItemSign_.city).get(City_.id), request.getCityId()),
                            cb.equal(purchaseOrderItemSignRoot2.get(PurchaseOrderItemSign_.depot).get(Depot_.id), request.getDepotId()),
                            cb.equal(purchaseOrderItemSignRoot2.get(PurchaseOrderItemSign_.sku).get(Sku_.id), root.get(PurchaseOrderItem_.sku).get(Sku_.id))
                    );
                    predicates.add(
                            cb.or(
                                cb.exists(purchaseOrderItemSignSubquery1.select(purchaseOrderItemSignRoot1)),
                                cb.not(cb.exists(purchaseOrderItemSignSubquery2.select(purchaseOrderItemSignRoot2)))
                            )
                    );
                }

                predicates.add(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.cutOrder).get(CutOrder_.id).in(request.getCutOrders()));
                predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.cutOrder).get(CutOrder_.status), CutOrderStatus.NOTCOMMITED.getValue()));

                query.orderBy(
                        cb.asc(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id)),
                        cb.asc(root.get(PurchaseOrderItem_.sku).get(Sku_.id))
                );
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        Map<String, PurchaseOrderItemVo> map = new LinkedHashMap<String, PurchaseOrderItemVo>();
        for (PurchaseOrderItem item : purchaseOrderItems) {
            String key = String.format("%s_%s", item.getPurchaseOrder().getVendor().getId(), item.getSku().getId());
            if (map.containsKey(key)) {
                PurchaseOrderItemVo wrapper = map.get(key);
                wrapper.setNeedQuantity(wrapper.getNeedQuantity() + item.getNeedQuantity());
                wrapper.setPurchaseQuantity((wrapper.getPurchaseQuantity() == null ? 0 : wrapper.getPurchaseQuantity()) + (item.getPurchaseQuantity() == null ? 0 : item.getPurchaseQuantity()));
                wrapper.setPurchaseBundleQuantity(new BigDecimal(wrapper.getPurchaseQuantity()).divide(new BigDecimal(item.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP));
                wrapper.setPurchaseTotalPrice(wrapper.getPurchasePrice().multiply(new BigDecimal(wrapper.getPurchaseQuantity())).setScale(6, RoundingMode.HALF_UP));
                wrapper.addPurchaseOrderItemId(item.getId());
            } else {
                PurchaseOrderItem newItem = new PurchaseOrderItem();
                BeanUtils.copyProperties(newItem, item);
                SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(item.getPurchaseOrder().getVendor().getCity().getId(), item.getSku().getId());
                PurchaseOrderItemVo wrapper = purchaseOrderItemToPurchaseOrderItemVo(skuPrice, newItem, stockTotalService.findStockTotal(newItem.getPurchaseOrder().getDepot().getCity().getId(), newItem.getSku().getId()));
                wrapper.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
                wrapper.setLastPurchasePrice(skuPrice == null || skuPrice.getPurchasePrice() == null ? BigDecimal.ZERO : skuPrice.getPurchasePrice());
                wrapper.addPurchaseOrderItemId(item.getId());
                List<PurchaseOrderItemSign> signList = purchaseOrderItemSignService.findByCityIdAndDepotIdAndSkuId(request.getCityId(), request.getDepotId(), item.getSku().getId());
                wrapper.setSign(CollectionUtils.isNotEmpty(signList) ? signList.get(0).getStatus() : PurchaseOrderItemSignStatus.NOTREADY.getValue());
                map.put(key, wrapper);
            }
        }


        List<PurchaseOrderItemVo> newPurchaseOrderItems = new ArrayList<>(map.values());

        BigDecimal totalAmount = new BigDecimal(0);
        for (PurchaseOrderItemVo purchaseOrderItemVo : newPurchaseOrderItems) {
            int purchaseQuantity = purchaseOrderItemVo.getPurchaseQuantity();
            totalAmount = totalAmount.add(purchaseOrderItemVo.getPurchasePrice().multiply(new BigDecimal(purchaseQuantity)));
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);


        int count = newPurchaseOrderItems.size();

        PurchaseAccordingResultResponse<PurchaseOrderItemVo> res = new PurchaseAccordingResultResponse<PurchaseOrderItemVo>();
        res.setPage(page);
        res.setPageSize(pageSize);
        res.setTotal(count);
        res.setContent(newPurchaseOrderItems.subList(page * pageSize, Math.min((page + 1) * pageSize, count)));
        res.setTotalAmount(totalAmount);
        return res;
    }

    public PurchaseOrderItemVo purchaseOrderItemToPurchaseOrderItemVo(SkuPrice skuPrice, PurchaseOrderItem purchaseOrderItem, StockTotal stockTotal) {
        PurchaseOrderItemVo vo = new PurchaseOrderItemVo();
        vo.setPurchaseOrder(purchaseOrderService.purchaseOrderToPurchaseOrderVo(purchaseOrderItem.getPurchaseOrder()));
        vo.setId(purchaseOrderItem.getId());
        vo.setSku(new SimpleSkuWrapper(purchaseOrderItem.getSku()));
        vo.setSkuId(purchaseOrderItem.getSku().getId());
        vo.setName(purchaseOrderItem.getSku().getName());
        vo.setRate(purchaseOrderItem.getRate());

        if (stockTotal == null) {
            vo.setQuantity(0);
            vo.setAvgCost(new BigDecimal(0));
        } else {
            vo.setQuantity(stockTotal.getQuantity());
            vo.setAvgCost(stockTotal.getAvgCost());
        }

        Integer purchaseQuantity = purchaseOrderItem.getPurchaseQuantity() != null ? purchaseOrderItem.getPurchaseQuantity() : 0;
        Integer needQuantity = purchaseOrderItem.getNeedQuantity();
        BigDecimal purchasePrice = purchaseOrderItem.getPrice();
        vo.setPurchaseQuantity(purchaseQuantity);
        vo.setNeedQuantity(needQuantity);
        vo.setNeedBundleQuantity(new BigDecimal(needQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP));
        vo.setReturnQuantity(purchaseOrderItem.getReturnQuantity());
        vo.setSingleUnit(purchaseOrderItem.getSku().getSingleUnit());
        vo.setPurchaseBundleQuantity(new BigDecimal(purchaseQuantity).divide(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP));
        vo.setBundleUnit(purchaseOrderItem.getSku().getBundleUnit());

        if (skuPrice != null && skuPrice.getPurchasePrice() != null && purchasePrice.compareTo(BigDecimal.ZERO) <= 0) {
            purchasePrice = skuPrice.getPurchasePrice();
        }
        vo.setPurchasePrice(purchasePrice);
        vo.setPurchaseBundlePrice(new BigDecimal(purchaseOrderItem.getSku().getCapacityInBundle()).multiply(purchasePrice).setScale(2, RoundingMode.HALF_UP));
        vo.setPurchaseTotalPrice(purchasePrice.multiply(new BigDecimal(purchaseQuantity)).setScale(6, RoundingMode.HALF_UP));
        vo.setCapacityInBundle(purchaseOrderItem.getSku().getCapacityInBundle());
        vo.setStatus(PurchaseOrderItemStatus.get(purchaseOrderItem.getStatus()));

        return vo;
    }

    @Transactional
    public PurchaseOrderItem split (PurchaseOrderItem sourcePoi, Integer quantity) {
        if (sourcePoi.getPurchaseQuantity().equals(quantity)) {
            return sourcePoi;
        }
        PurchaseOrderItem newPoi = sourcePoi.clone();
        newPoi.setPurchaseQuantity(quantity);
        newPoi.setNeedQuantity(quantity);
        purchaseOrderItemRepository.save(newPoi);

        sourcePoi.setPurchaseQuantity(sourcePoi.getPurchaseQuantity() - quantity);
        sourcePoi.setNeedQuantity(sourcePoi.getNeedQuantity() - quantity);
        purchaseOrderItemRepository.save(sourcePoi);

        return newPoi;
    }

    public List<PurchaseOrderItem> getItems(final List<Long> cutOrders, final Vendor vendor, final Sku sku) {
        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrderItemRepository.findAll(new Specification<PurchaseOrderItem>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                predicates.add(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.cutOrder).get(CutOrder_.id).in(cutOrders));

                if (vendor != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), vendor.getId()));
                }

                if (sku != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrderItem_.sku).get(Sku_.id), sku.getId()));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        return purchaseOrderItems;
    }
}
