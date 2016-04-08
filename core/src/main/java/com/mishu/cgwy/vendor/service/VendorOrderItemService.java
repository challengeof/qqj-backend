package com.mishu.cgwy.vendor.service;

import com.google.common.collect.Collections2;
import com.mishu.cgwy.common.domain.Warehouse_;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.response.Response;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.Depot;
import com.mishu.cgwy.stock.domain.Depot_;
import com.mishu.cgwy.vendor.controller.VendorOrderHistoryListRequest;
import com.mishu.cgwy.vendor.domain.VendorOrderItem;
import com.mishu.cgwy.vendor.domain.VendorOrderItem_;
import com.mishu.cgwy.vendor.repository.VendorOrderItemRepository;
import com.mishu.cgwy.vendor.wrapper.VendorOrderHistory;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Tuple;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class VendorOrderItemService {

    @Autowired
    private EntityManager entityManager;

    @Autowired
    private VendorOrderItemRepository vendorOrderItemRepository;

    public Specification<VendorOrderItem> getVendorOrderItemsSpecification(final Long vendorId, final Long depotId) {
        return new Specification<VendorOrderItem>() {
            @Override
            public Predicate toPredicate(Root<VendorOrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(VendorOrderItem_.vendor).get(Vendor_.id), vendorId));

                if (depotId != null) {
                    predicates.add(cb.equal(root.get(VendorOrderItem_.depot).get(Depot_.id), depotId));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public List<VendorOrderItem> getVendorOrderItems(Long vendorId, Long depotId) {
        return vendorOrderItemRepository.findAll(getVendorOrderItemsSpecification(vendorId, depotId));

    }

    @Transactional
    public Response submit(Vendor vendor, Long[] ids) {

        Response res = new Response();

        if (ArrayUtils.isEmpty(ids)) {
            res.setSuccess(Boolean.FALSE);
            res.setMsg("没有选择需要备货的商品");
            return res;
        }

        for (Long id : ids) {
            VendorOrderItem item = vendorOrderItemRepository.getOne(id);
            item.setQuantityReady(item.getQuantityNeed() + item.getQuantityReady());
            item.setQuantityNeed(0);
            vendorOrderItemRepository.save(item);
        }

        res.setSuccess(Boolean.TRUE);
        res.setMsg("备货成功");
        return res;
    }

    private Specification<PurchaseOrderItem> getPurchaseOrderItemsSpecification(final Vendor vendor, final VendorOrderHistoryListRequest request) {
        return new Specification<PurchaseOrderItem>() {

            @Override
            public Predicate toPredicate(Root<PurchaseOrderItem> root, CriteriaQuery<?> query, CriteriaBuilder cb) {

                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.vendor).get(Vendor_.id), vendor.getId()));
                predicates.add(cb.equal(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.status), PurchaseOrderStatus.COMPLETED.getValue()));

                if (request.getStart() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.receiveTime), request.getStart()));
                }

                if (request.getEnd() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.receiveTime), request.getEnd()));
                }
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public QueryResponse<VendorOrderHistory> getVendorOrderHistory(Vendor vendor, VendorOrderHistoryListRequest request) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Tuple> query = cb.createTupleQuery();
        Root<PurchaseOrderItem> root = query.from(PurchaseOrderItem.class);

        Expression skuId = root.get(PurchaseOrderItem_.sku).get(Sku_.id);
        Expression skuName = root.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.name);
        Expression needBundleQuantity = cb.sum(cb.quot(cb.diff(root.get(PurchaseOrderItem_.needQuantity), cb.<Integer>selectCase().when(cb.isNull(root.get(PurchaseOrderItem_.returnQuantity)), 0).otherwise(root.get(PurchaseOrderItem_.returnQuantity))), root.get(PurchaseOrderItem_.sku).get(Sku_.capacityInBundle)));
        Expression needSingleQuantity = cb.sum(cb.mod(cb.diff(root.get(PurchaseOrderItem_.needQuantity), cb.<Integer>selectCase().when(cb.isNull(root.get(PurchaseOrderItem_.returnQuantity)), 0).otherwise(root.get(PurchaseOrderItem_.returnQuantity))), root.get(PurchaseOrderItem_.sku).get(Sku_.capacityInBundle)));
        Expression purchaseBundleQuantity = cb.sum(cb.quot(cb.diff(root.get(PurchaseOrderItem_.purchaseQuantity), cb.<Integer>selectCase().when(cb.isNull(root.get(PurchaseOrderItem_.returnQuantity)), 0).otherwise(root.get(PurchaseOrderItem_.returnQuantity))), root.get(PurchaseOrderItem_.sku).get(Sku_.capacityInBundle)));
        Expression purchaseSingleQuantity = cb.sum(cb.mod(cb.diff(root.get(PurchaseOrderItem_.purchaseQuantity), cb.<Integer>selectCase().when(cb.isNull(root.get(PurchaseOrderItem_.returnQuantity)), 0).otherwise(root.get(PurchaseOrderItem_.returnQuantity))), root.get(PurchaseOrderItem_.sku).get(Sku_.capacityInBundle)));
        Expression bundleUnit = root.get(PurchaseOrderItem_.sku).get(Sku_.bundleUnit);
        Expression singleUnit = root.get(PurchaseOrderItem_.sku).get(Sku_.singleUnit);
        Expression totalPrice = cb.sum(cb.prod(root.get(PurchaseOrderItem_.purchaseQuantity), root.get(PurchaseOrderItem_.price)));

        query.multiselect(skuId, skuName, needBundleQuantity, needSingleQuantity, purchaseBundleQuantity, purchaseSingleQuantity, totalPrice, bundleUnit, singleUnit);
        query.where(getPurchaseOrderItemsSpecification(vendor, request).toPredicate(root, query, cb)).groupBy(skuId);

        PageRequest pageRequest = new PageRequest(request.getPage(), request.getPageSize());
        List<VendorOrderHistory> vendorOrderHistoryList = new ArrayList<>();
        for (Tuple tuple : entityManager.createQuery(query).setFirstResult(pageRequest.getOffset()).setMaxResults(pageRequest.getPageSize()).getResultList()) {
            VendorOrderHistory vendorOrderHistory = new VendorOrderHistory();
            vendorOrderHistory.setId((Long) tuple.get(0));
            vendorOrderHistory.setName((String) tuple.get(1));
            vendorOrderHistory.setNeedBundleQuantity(((Number) tuple.get(2)).intValue());
            vendorOrderHistory.setNeedSingleQuantity(((Number) tuple.get(3)).intValue());
            vendorOrderHistory.setPurchaseBundleQuantity(((Number) tuple.get(4)).intValue());
            vendorOrderHistory.setPurchaseSingleQuantity(((Number) tuple.get(5)).intValue());
            vendorOrderHistory.setTotal((BigDecimal) tuple.get(6));
            vendorOrderHistory.setBundleUnit((String) tuple.get(7));
            vendorOrderHistory.setSingleUnit((String) tuple.get(8));

            vendorOrderHistoryList.add(vendorOrderHistory);
        }

        CriteriaQuery<Long> totalQuery = cb.createQuery(Long.class);
        Root<PurchaseOrderItem> totalRoot = totalQuery.from(PurchaseOrderItem.class);
        totalQuery.select(cb.countDistinct(totalRoot.get(PurchaseOrderItem_.sku).get(Sku_.id))).where(getPurchaseOrderItemsSpecification(vendor, request).toPredicate(root, query, cb));
        Long totalElements = entityManager.createQuery(totalQuery).getSingleResult();

        QueryResponse<VendorOrderHistory> res = new QueryResponse<VendorOrderHistory>();
        res.setContent(vendorOrderHistoryList);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(totalElements);

        return res;
    }

    public List<VendorOrderItem> getVendorOrderItemsNotReady(Long vendorId, Long depotId) {
        return new ArrayList<>(Collections2.filter(getVendorOrderItems(vendorId, depotId),
                new com.google.common.base.Predicate<VendorOrderItem>() {
                    @Override
                    public boolean apply(VendorOrderItem item) {
                        return item.getQuantityNeed().compareTo(0) > 0;
                    }
                }));
    }

    public List getVendorOrderItemsReady(Long vendorId, Long depotId) {
        return new ArrayList<>(Collections2.filter(getVendorOrderItems(vendorId, depotId),
                new com.google.common.base.Predicate<VendorOrderItem>() {
                    @Override
                    public boolean apply(VendorOrderItem item) {
                        return item.getQuantityReady().compareTo(0) > 0;
                    }
                }));
    }

    @Transactional
    public void generateVendorOrderItems(CutOrder cutOrder) {

        if (cutOrder == null) {
            return;
        }

        generateVendorOrder(cutOrder.getPurchaseOrders(), cutOrder.getDepot());
    }

    private void generateVendorOrder(List<PurchaseOrder> purchaseOrders, Depot depot) {

        if (CollectionUtils.isEmpty(purchaseOrders)) {
            return;
        }

        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            generateVendorOrder(purchaseOrder, depot);
        }
    }

    public void generateVendorOrder(PurchaseOrder purchaseOrder, Depot depot) {

        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
        if (CollectionUtils.isEmpty(purchaseOrderItems)) {
            return;
        }

        Vendor vendor = purchaseOrder.getVendor();

        List<VendorOrderItem> vendorOrderItems = getVendorOrderItems(vendor.getId(), depot.getId());

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            boolean existsVendor = false;
            for (VendorOrderItem vendorOrderItem : vendorOrderItems) {
                if (vendorOrderItem.getSku().getId().equals(purchaseOrderItem.getSku().getId())
                        && vendorOrderItem.getVendor().getId().equals(purchaseOrderItem.getPurchaseOrder().getVendor().getId())) {
                    vendorOrderItem.setQuantityNeed(vendorOrderItem.getQuantityNeed() + purchaseOrderItem.getNeedQuantity());
                    save(vendorOrderItem);
                    existsVendor = true;
                    break;
                }
            }

            if (!existsVendor) {
                VendorOrderItem vendorOrderItem = new VendorOrderItem();
                vendorOrderItem.setDepot(depot);
                vendorOrderItem.setSku(purchaseOrderItem.getSku());
                vendorOrderItem.setVendor(vendor);
                vendorOrderItem.setQuantityNeed(purchaseOrderItem.getNeedQuantity());
                vendorOrderItem.setQuantityReady(0);
                save(vendorOrderItem);
            }
        }
    }

    private void save(VendorOrderItem vendorOrderItem) {
        vendorOrderItemRepository.save(vendorOrderItem);
    }

    public void deleteVendorOrderItems(List<CutOrder> cutOrders) {

        if (CollectionUtils.isEmpty(cutOrders)) {
            return;
        }

        for (CutOrder cutOrder : cutOrders) {
            deleteVendorOrderItems(cutOrder);
        }
    }

    private void deleteVendorOrderItems(CutOrder cutOrder) {

        if (cutOrder == null) {
            return;
        }

        deleteVendorOrderItemsByPurchaseOrders(cutOrder.getPurchaseOrders(), cutOrder.getDepot());
    }

    private void deleteVendorOrderItemsByPurchaseOrders(List<PurchaseOrder> purchaseOrders, Depot depot) {

        if (CollectionUtils.isEmpty(purchaseOrders)) {
            return;
        }

        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            deleteVendorOrderItemsByPurchaseOrder(purchaseOrder, depot);
        }
    }

    private void deleteVendorOrderItemsByPurchaseOrder(PurchaseOrder purchaseOrder, Depot depot) {

        List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
        if (CollectionUtils.isEmpty(purchaseOrderItems)) {
            return;
        }

        deleteVendorOrderItems(purchaseOrderItems, purchaseOrder.getVendor(), depot);
    }

    @Transactional
    public void deleteVendorOrderItems(List<PurchaseOrderItem> purchaseOrderItems, Vendor vendor, Depot depot) {
        List<VendorOrderItem> vendorOrderItems = getVendorOrderItems(vendor.getId(), depot.getId());

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            for (VendorOrderItem vendorOrderItem : vendorOrderItems) {
                if (vendorOrderItem.getSku().getId().equals(purchaseOrderItem.getSku().getId())
                        && vendorOrderItem.getVendor().getId().equals(vendor.getId())) {
                    int ready = vendorOrderItem.getQuantityReady();
                    int need = vendorOrderItem.getQuantityNeed();
                    int submit = purchaseOrderItem.getNeedQuantity();

                    vendorOrderItem.setQuantityReady(ready>submit ? ready-submit : 0);
                    vendorOrderItem.setQuantityNeed(ready>submit ? need : (need+ready>submit ? need+ready-submit : 0));
                    save(vendorOrderItem);
                }
            }
        }
    }

    private VendorOrderItem getVendorOrderItem(Long skuId, Long vendorId, Long depotId) {
        List<VendorOrderItem> items = vendorOrderItemRepository.findBySkuIdAndVendorIdAndDepotId(skuId, vendorId, depotId);

        return CollectionUtils.isNotEmpty(items) ? items.get(0) : null;
    }

    @Transactional
    public void updateVendorOrderItems(List<PurchaseOrderItem> purchaseOrderItems, Vendor oldVendor, Vendor newVendor, Depot depot) {

        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            Sku sku = purchaseOrderItem.getSku();
            VendorOrderItem oldVendorOrderItem = getVendorOrderItem(sku.getId(), oldVendor.getId(), depot.getId());
            VendorOrderItem newVendorOrderItem = getVendorOrderItem(sku.getId(), newVendor.getId(), depot.getId());
            if (oldVendorOrderItem != null) {
                int oldReady = oldVendorOrderItem.getQuantityReady();
                int oldNeed = oldVendorOrderItem.getQuantityNeed();
                int changed = purchaseOrderItem.getNeedQuantity();

                int needChanged = oldReady > changed ? 0 : (oldNeed + oldReady > changed ? changed - oldReady : oldNeed);
                int readyChanged = oldReady > changed ? changed : oldReady;

                oldVendorOrderItem.setQuantityNeed(oldNeed - needChanged);
                oldVendorOrderItem.setQuantityReady(oldReady - readyChanged);

                save(oldVendorOrderItem);

                if (newVendorOrderItem != null) {
                    newVendorOrderItem.setQuantityNeed(newVendorOrderItem.getQuantityNeed() + needChanged);
                    newVendorOrderItem.setQuantityReady(newVendorOrderItem.getQuantityReady() + readyChanged);
                } else {
                    newVendorOrderItem = new VendorOrderItem();
                    newVendorOrderItem.setDepot(depot);
                    newVendorOrderItem.setSku(purchaseOrderItem.getSku());
                    newVendorOrderItem.setVendor(newVendor);
                    newVendorOrderItem.setQuantityNeed(needChanged);
                    newVendorOrderItem.setQuantityReady(readyChanged);
                }
                save(newVendorOrderItem);
            }
        }
    }
}
