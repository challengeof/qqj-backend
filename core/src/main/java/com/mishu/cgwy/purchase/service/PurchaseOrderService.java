package com.mishu.cgwy.purchase.service;

import com.mishu.cgwy.accounting.domain.AccountPayable;
import com.mishu.cgwy.accounting.domain.AccountPayable_;
import com.mishu.cgwy.accounting.service.AccountPayableService;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.service.VendorService;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.City_;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.inventory.domain.Vendor;
import com.mishu.cgwy.inventory.domain.Vendor_;
import com.mishu.cgwy.inventory.vo.VendorVo;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.order.domain.CutOrder_;
import com.mishu.cgwy.order.service.CutOrderService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.domain.Product_;
import com.mishu.cgwy.product.domain.SkuPrice;
import com.mishu.cgwy.product.domain.SkuVendor;
import com.mishu.cgwy.product.domain.Sku_;
import com.mishu.cgwy.product.repository.SkuVendorRepository;
import com.mishu.cgwy.product.service.SkuPriceService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.purchase.controller.PurchaseOrderData;
import com.mishu.cgwy.purchase.controller.PurchaseOrderItemData;
import com.mishu.cgwy.purchase.controller.PurchaseOrderRequest;
import com.mishu.cgwy.purchase.domain.*;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.repository.PurchaseOrderRepository;
import com.mishu.cgwy.purchase.vo.PurchaseOrderItemVo;
import com.mishu.cgwy.purchase.vo.PurchaseOrderVo;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QueryWithTotalResponse;
import com.mishu.cgwy.response.query.SubmitResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.StockInService;
import com.mishu.cgwy.stock.service.StockTotalService;
import com.mishu.cgwy.stock.wrapper.DepotWrapper;
import com.mishu.cgwy.vendor.service.VendorOrderItemService;
import org.apache.commons.collections.CollectionUtils;
import org.elasticsearch.common.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.criteria.*;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wangguodong on 15/9/14.
 */
@Service
public class PurchaseOrderService {

    @Autowired
    PurchaseOrderRepository purchaseOrderRepository;

    @Autowired
    DepotService depotService;

    @Autowired
    VendorService vendorService;

    @Autowired
    AdminUserService adminUserService;

    @Autowired
    SkuService skuService;

    @Autowired
    private StockTotalService stockTotalService;

    @Autowired
    private StockInService stockInService;

    @Autowired
    private PurchaseOrderItemService purchaseOrderItemService;

    @Autowired
    private SkuVendorRepository skuVendorRepository;

    @Autowired
    private CutOrderService cutOrderService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private AccountPayableService accountPayableService;

    @Autowired
    private VendorOrderItemService vendorOrderItemService;

    @Autowired
    private SkuPriceService skuPriceService;

    @Autowired
    private EntityManager entityManager;

    private Specification<PurchaseOrder> getPurchaseOrderListSpecification(final PurchaseOrderRequest request, final AdminUser adminUser) {
        return new Specification<PurchaseOrder>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (adminUser != null) {
                    Set<Long> cityIds = new HashSet<>();
                    Set<Long> depotIds = new HashSet<>();

                    for (City city : adminUser.getDepotCities()) {
                        cityIds.add(city.getId());
                    }
                    for (Depot depot : adminUser.getDepots()) {
                        depotIds.add(depot.getId());
                    }

                    List<Predicate> depotCondition = new ArrayList<>();
                    if (!cityIds.isEmpty()) {
                        depotCondition.add(root.get(PurchaseOrder_.depot).get(Depot_.city).get(City_.id).in(cityIds));
                    }
                    if (!depotIds.isEmpty()) {
                        depotCondition.add(root.get(PurchaseOrder_.depot).get(Depot_.id).in(depotIds));
                    }

                    if (!depotCondition.isEmpty()) {
                        predicates.add(cb.or(depotCondition.toArray(new Predicate[depotCondition.size()])));
                    } else {
                        predicates.add(cb.or());
                    }
                }

                if (request.getVendorId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.vendor).get(Vendor_.id), request.getVendorId()));
                }

                if (request.getPaymentVendorId() != null) {
                    Subquery<AccountPayable> accountPayableQuery = query.subquery(AccountPayable.class);
                    Root<AccountPayable> accountPayableRoot = accountPayableQuery.from(AccountPayable.class);
                    accountPayableQuery.where(
                            cb.equal(accountPayableRoot.get(AccountPayable_.vendor).get(Vendor_.id), request.getPaymentVendorId()),
                            cb.equal(accountPayableRoot.get(AccountPayable_.stockIn).get(StockIn_.purchaseOrder).get(PurchaseOrder_.id), root.get(PurchaseOrder_.id))
                    );
                    predicates.add(
                            cb.or(
                                    cb.exists(accountPayableQuery.select(accountPayableRoot)),
                                    cb.equal(root.get(PurchaseOrder_.vendor).get(Vendor_.paymentVendor).get(Vendor_.id), request.getPaymentVendorId())
                            )
                    );
                }

                if (request.getCityId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.depot).get(Depot_.city).get(City_.id), request.getCityId()));
                }

                if (request.getDepotId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.depot).get(Depot_.id), request.getDepotId()));
                }

                if (request.getId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.id), request.getId()));
                }

                if (request.getCutOrderId() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.cutOrder).get(CutOrder_.id), request.getCutOrderId()));
                }

                if (request.getStatus() != null) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.status), request.getStatus()));
                }

                if (request.getPrint() != null) {
                    if (request.getPrint()) {
                        predicates.add(cb.isTrue(root.get(PurchaseOrder_.print)));
                    } else {
                        predicates.add(cb.isFalse(root.get(PurchaseOrder_.print)));
                    }
                }

                if (request.getStartDate() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(PurchaseOrder_.createTime), request.getStartDate()));
                }

                if (request.getEndDate() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(PurchaseOrder_.createTime), request.getEndDate()));
                }

                if (request.getMinAmount() != null) {
                    predicates.add(cb.greaterThanOrEqualTo(root.get(PurchaseOrder_.total), request.getMinAmount()));
                }

                if (request.getMaxAmount() != null) {
                    predicates.add(cb.lessThanOrEqualTo(root.get(PurchaseOrder_.total), request.getMaxAmount()));
                }

                if (request.getType() != null && request.getType() != 0) {
                    predicates.add(cb.equal(root.get(PurchaseOrder_.type), request.getType()));
                }

                if (request.getProductName() != null || request.getProductId() != null || request.getSkuId() != null) {
                    Subquery<PurchaseOrderItem> subQuery = query.subquery(PurchaseOrderItem.class);
                    Root<PurchaseOrderItem> subRoot = subQuery.from(PurchaseOrderItem.class);

                    List<Predicate> predicateList = new ArrayList<>();
                    predicateList.add(cb.equal(root.get(PurchaseOrder_.id), subRoot.get(PurchaseOrderItem_.purchaseOrder).get(PurchaseOrder_.id)));
                    if (request.getProductName() != null) {
                        predicateList.add(cb.like(subRoot.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.name), String.format("%%%s%%", request.getProductName())));
                    }

                    if (request.getProductId() != null) {
                        predicateList.add(cb.equal(subRoot.get(PurchaseOrderItem_.sku).get(Sku_.product).get(Product_.id), request.getProductId()));
                    }

                    if (request.getSkuId() != null) {
                        predicateList.add(cb.equal(subRoot.get(PurchaseOrderItem_.sku).get(Sku_.id), request.getSkuId()));
                    }
                    subQuery.where(
                            predicateList.toArray(new Predicate[predicateList.size()])
                    );
                    predicates.add(cb.exists(subQuery.select(subRoot)));
                }

                query.orderBy(cb.desc(root.get(PurchaseOrder_.id)));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
    }

    public PurchaseOrderVo purchaseOrderToPurchaseOrderVo(PurchaseOrder purchaseOrder) {
        PurchaseOrderVo purchaseOrderVo = new PurchaseOrderVo();
        purchaseOrderVo.setId(purchaseOrder.getId());
        purchaseOrderVo.setCityId(purchaseOrder.getDepot().getCity().getId());
        purchaseOrderVo.setOrganizationId(purchaseOrder.getVendor().getOrganization().getId());
        purchaseOrderVo.setStatus(PurchaseOrderStatus.get(purchaseOrder.getStatus()));
        purchaseOrderVo.setPrint(PurchaseOrderPrint.get(purchaseOrder.getPrint()));
        purchaseOrderVo.setTotal(purchaseOrder.getTotal());
        purchaseOrderVo.setDepot(new DepotWrapper(purchaseOrder.getDepot()));
        purchaseOrderVo.setRemark(purchaseOrder.getRemark());
        purchaseOrderVo.setCreater(purchaseOrder.getCreater() == null ? null : purchaseOrder.getCreater().getRealname());
        purchaseOrderVo.setCreateTime(purchaseOrder.getCreateTime());
        purchaseOrderVo.setAuditor(purchaseOrder.getAuditor() == null ? null : purchaseOrder.getAuditor().getRealname());
        purchaseOrderVo.setAuditTime(purchaseOrder.getAuditTime());
        purchaseOrderVo.setReceiver(purchaseOrder.getReceiver() == null ? null : purchaseOrder.getReceiver().getRealname());
        purchaseOrderVo.setReceiveTime(purchaseOrder.getReceiveTime());
        purchaseOrderVo.setExpectedArrivedDate(purchaseOrder.getExpectedArrivedDate());
        purchaseOrderVo.setOpinion(purchaseOrder.getOpinion());
        purchaseOrderVo.setPurchaseOrderType(PurchaseOrderType.fromInt(purchaseOrder.getType()));

        Vendor purchaseVendor = purchaseOrder.getVendor();
        VendorVo vendor = new VendorVo();
        vendor.setId(purchaseVendor.getId());
        vendor.setName(purchaseVendor.getName());
        purchaseOrderVo.setVendor(vendor);

        return purchaseOrderVo;
    }

    public List<PurchaseOrderVo> getPurchaseOrders(PurchaseOrderRequest request, AdminUser adminUser) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll(getPurchaseOrderListSpecification(request, adminUser));

        Set<Long> vendorToExclude = new HashSet<>();
        BigDecimal minPaymentAmount = request.getMinPaymentAmount();
        BigDecimal maxPaymentAmount = request.getMaxPaymentAmount();
        if (minPaymentAmount != null || maxPaymentAmount != null) {
            Map<Long, BigDecimal> totalMap = new HashMap<>();
            for (PurchaseOrder purchaseOrder : purchaseOrders) {
                Vendor vendor = purchaseOrder.getVendor();
                Long vendorId = vendor.getId();
                BigDecimal total = purchaseOrder.getTotal();
                if (totalMap.get(vendorId) == null) {
                    totalMap.put(vendorId, total);
                } else {
                    totalMap.put(vendorId, totalMap.get(vendorId).add(total));
                }
            }

            for (Long vendorId : totalMap.keySet()) {
                BigDecimal total = totalMap.get(vendorId);
                if (minPaymentAmount != null && total.compareTo(minPaymentAmount) < 0) {
                    vendorToExclude.add(vendorId);
                }

                if (maxPaymentAmount != null && total.compareTo(maxPaymentAmount) >= 0) {
                    vendorToExclude.add(vendorId);
                }
            }
        }

        List<PurchaseOrderVo> list = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            if (vendorToExclude.contains(purchaseOrder.getVendor().getId())) {
                continue;
            }
            PurchaseOrderVo purchaseOrderWrapper = purchaseOrderToPurchaseOrderVo(purchaseOrder);

            if (purchaseOrderWrapper.getStatus() == PurchaseOrderStatus.COMPLETED) {
                List<AccountPayable> accountPayables = accountPayableService.findByPurchaseOrderId(purchaseOrder.getId());
                if (CollectionUtils.isNotEmpty(accountPayables)) {
                    AccountPayable accountPayable = accountPayables.get(0);
                    Vendor vendor = accountPayable.getVendor();
                    VendorVo vendorVo = new VendorVo();
                    vendorVo.setId(vendor.getId());
                    vendorVo.setName(vendor.getName());
                    purchaseOrderWrapper.setPaymentVendor(vendorVo);
                }
            } else {
                Vendor vendor = vendorService.findOne(purchaseOrderWrapper.getVendor().getId()).getPaymentVendor();
                VendorVo vendorVo = new VendorVo();
                vendorVo.setId(vendor.getId());
                vendorVo.setName(vendor.getName());
                purchaseOrderWrapper.setPaymentVendor(vendorVo);
            }
            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                purchaseOrderWrapper.getPurchaseOrderItems().add(purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, item, null));
            }
            list.add(purchaseOrderWrapper);
        }

        return list;
    }

    public QueryResponse<PurchaseOrderVo> getPurchaseOrderList(PurchaseOrderRequest request, AdminUser operator) {
        final PageRequest pageable = new PageRequest(request.getPage(), request.getPageSize());
        Specification<PurchaseOrder> spec = getPurchaseOrderListSpecification(request, operator);

        final CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        final CriteriaQuery<BigDecimal> query = cb.createQuery(BigDecimal.class);
        final Root<PurchaseOrder> root = query.from(PurchaseOrder.class);


        query.select(cb.sum(root.get(PurchaseOrder_.total)));
        query.where(spec.toPredicate(root, query, cb));

        BigDecimal sum = entityManager.createQuery(query).getSingleResult();

        Page<PurchaseOrder> page = purchaseOrderRepository.findAll(spec, pageable);

        List<PurchaseOrderVo> list = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : page.getContent()) {
            PurchaseOrderVo purchaseOrderWrapper = purchaseOrderToPurchaseOrderVo(purchaseOrder);

            if (purchaseOrderWrapper.getStatus() == PurchaseOrderStatus.COMPLETED) {
                List<AccountPayable> accountPayables = accountPayableService.findByPurchaseOrderId(purchaseOrder.getId());
                if (CollectionUtils.isNotEmpty(accountPayables)) {
                    AccountPayable accountPayable = accountPayables.get(0);
                    Vendor vendor = accountPayable.getVendor();
                    VendorVo vendorVo = new VendorVo();
                    vendorVo.setId(vendor.getId());
                    vendorVo.setName(vendor.getName());
                    purchaseOrderWrapper.setPaymentVendor(vendorVo);
                }
            } else {
                Vendor vendor = vendorService.findOne(purchaseOrderWrapper.getVendor().getId()).getPaymentVendor();
                VendorVo vendorVo = new VendorVo();
                vendorVo.setId(vendor.getId());
                vendorVo.setName(vendor.getName());
                purchaseOrderWrapper.setPaymentVendor(vendorVo);
            }
            list.add(purchaseOrderWrapper);
        }

        QueryWithTotalResponse<PurchaseOrderVo> res = new QueryWithTotalResponse<PurchaseOrderVo>();
        res.setSum(sum);
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());

        return res;
    }

    @Transactional
    public void savePurchaseOrder(AdminUser adminUser, PurchaseOrderData purchaseOrderData) {
        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setId(purchaseOrderData.getId());
        purchaseOrder.setType(purchaseOrderData.getType());
        purchaseOrder.setDepot(depotService.findOne(purchaseOrderData.getDepotId()));
        purchaseOrder.setExpectedArrivedDate(purchaseOrderData.getExpectedArrivedDate());
        purchaseOrder.setRemark(purchaseOrderData.getRemark());
        purchaseOrder.setStatus(PurchaseOrderStatus.NOTCOMMITTED.getValue());
        purchaseOrder.setVendor(vendorService.findOne(purchaseOrderData.getVendorId()));
        purchaseOrder.setCreater(adminUserService.findOne(adminUser.getId()));
        purchaseOrder.setCreateTime(new Date());

        List<PurchaseOrderItem> items = new ArrayList<>();
        BigDecimal total = new BigDecimal(0);
        for (PurchaseOrderItemData itemData : purchaseOrderData.getPurchaseOrderItems()) {
            PurchaseOrderItem item = new PurchaseOrderItem();
            item.setId(itemData.getId());
            item.setSku(skuService.findOne(itemData.getSkuId()));
            item.setStatus(PurchaseOrderItemStatus.TOBEEXECUTED.getValue());
            item.setPrice(itemData.getPurchasePrice());
            item.setNeedQuantity(itemData.getPurchaseQuantity());
            item.setPurchaseQuantity(itemData.getPurchaseQuantity());
            item.setRate(itemData.getRate());
            item.setPurchaseOrder(purchaseOrder);
            items.add(item);
            total = total.add(itemData.getPurchasePrice().multiply(new BigDecimal(itemData.getPurchaseQuantity())));
        }

        purchaseOrder.setPurchaseOrderItems(items);
        purchaseOrder.setTotal(total.setScale(2, BigDecimal.ROUND_HALF_UP));
        purchaseOrderRepository.save(purchaseOrder);
    }

    public void save(PurchaseOrder purchaseOrder) {
        purchaseOrderRepository.save(purchaseOrder);
    }

    public PurchaseOrderVo getPurchaseOrder(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(id);

        List<PurchaseOrderItemVo> purchaseOrderItemWrappers = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            if (PurchaseOrderItemStatus.INVALID != PurchaseOrderItemStatus.get(item.getStatus())) {
                StockTotal stockTotal = stockTotalService.findStockTotal(purchaseOrder.getDepot().getCity().getId(), item.getSku().getId());
                PurchaseOrderItemVo wrapper = purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, item, stockTotal);
                SkuPrice skuPrice = skuPriceService.findByCityIdAndSkuId(purchaseOrder.getDepot().getCity().getId(), item.getSku().getId());
                wrapper.setFixedPrice(skuPrice == null || skuPrice.getFixedPrice() == null ? BigDecimal.ZERO : skuPrice.getFixedPrice());
                wrapper.setLastPurchasePrice(skuPrice == null || skuPrice.getPurchasePrice() == null ? BigDecimal.ZERO : skuPrice.getPurchasePrice());
                purchaseOrderItemWrappers.add(wrapper);
            }
        }

        PurchaseOrderVo purchaseOrderWrapper = purchaseOrderToPurchaseOrderVo(purchaseOrder);

        purchaseOrderWrapper.setPurchaseOrderItems(purchaseOrderItemWrappers);

        return purchaseOrderWrapper;
    }

    @Transactional
    public PurchaseOrder submit(Long id) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(id);
        purchaseOrder.setStatus(PurchaseOrderStatus.PENDINGAUDIT.getValue());
        return purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void audit(AdminUser adminUser, PurchaseOrderData purchaseOrderData) {

        PurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(purchaseOrderData.getId());
        purchaseOrder.setAuditor(adminUserService.findOne(adminUser.getId()));
        purchaseOrder.setAuditTime(new Date());
        purchaseOrder.setOpinion(purchaseOrderData.getOpinion());

        if (purchaseOrderData.getApprovalResult()) {
            List<StockIn> stockInList = stockInService.findByPurchaseOrderId(purchaseOrder.getId());
            if (CollectionUtils.isNotEmpty(stockInList)) {
                return;
            }
            purchaseOrder.setStatus(PurchaseOrderStatus.EXECUTION.getValue());
            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                item.setStatus(PurchaseOrderItemStatus.EXECUTION.getValue());
            }
            stockInService.createStockIn(purchaseOrderRepository.save(purchaseOrder));
        } else {
            purchaseOrder.setStatus(PurchaseOrderStatus.NOTCOMMITTED.getValue());
            purchaseOrderRepository.save(purchaseOrder);
        }
    }

    @Transactional
    public void complete(StockIn stockIn, boolean part) {

        if (stockIn == null || stockIn.getPurchaseOrder() == null)
            return;

        List<StockInItem> stockInItems = new ArrayList<>();
        for (StockInItem sii : stockIn.getStockInItems()) {
            stockInItems.add(sii.clone());
        }

        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(stockIn.getPurchaseOrder().getId());
        List<PurchaseOrderItem> newPois = new ArrayList<>();
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            if (PurchaseOrderItemStatus.EXECUTION.getValue().equals(item.getStatus())) {

                int itemQuantity = item.getPurchaseQuantity();
                Iterator<StockInItem> iterator = stockInItems.iterator();
                while (iterator.hasNext()) {
                    if (itemQuantity <= 0) {
                        break;
                    }
                    StockInItem stockInItem = iterator.next();
                    if (stockInItem.getSku().getId().equals(item.getSku().getId())) {

                        int realQuantity = stockInItem.getRealQuantity();
                        if (realQuantity > 0) {
                            if (realQuantity >= itemQuantity) {
                                item.setStatus(PurchaseOrderItemStatus.COMPLETED.getValue());
                                realQuantity -= itemQuantity;
                                stockInItem.setRealQuantity(realQuantity);
                                itemQuantity = 0;
                            } else {
                                PurchaseOrderItem newPoi = purchaseOrderItemService.split(item, realQuantity);
                                newPoi.setStatus(PurchaseOrderItemStatus.COMPLETED.getValue());
                                newPois.add(newPoi);
                                itemQuantity -= realQuantity;
                                iterator.remove();
                            }
                        } else {
                            iterator.remove();
                        }
                    }
                }
            }
        }
        purchaseOrder.getPurchaseOrderItems().addAll(newPois);
        BigDecimal total = BigDecimal.ZERO;
        boolean allCancel = true;
        boolean allComplete = true;
        for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
            if (!part && PurchaseOrderItemStatus.EXECUTION.getValue().equals(item.getStatus())) {
                item.setStatus(PurchaseOrderItemStatus.INVALID.getValue());
            }

            if (!PurchaseOrderItemStatus.INVALID.getValue().equals(item.getStatus())) {
                allCancel = false;
            }
            if (PurchaseOrderItemStatus.EXECUTION.getValue().equals(item.getStatus())) {
                allComplete = false;
            }

            if ((PurchaseOrderItemStatus.EXECUTION.getValue().equals(item.getStatus())
                    || PurchaseOrderItemStatus.COMPLETED.getValue().equals(item.getStatus())) && item.getPrice() != null) {
                total = total.add(item.getPrice().multiply(new BigDecimal(item.getPurchaseQuantity())));
            }
        }

        if (allCancel) {
            purchaseOrder.setStatus(PurchaseOrderStatus.CANSELED.getValue());
        } else if (allComplete) {
            purchaseOrder.setStatus(PurchaseOrderStatus.COMPLETED.getValue());
        }
        purchaseOrder.setTotal(total.setScale(2, BigDecimal.ROUND_HALF_UP));
        purchaseOrder.setReceiver(stockIn.getReceiver());
        purchaseOrder.setReceiveTime(stockIn.getReceiveDate());
        purchaseOrderRepository.save(purchaseOrder);
    }

    @Transactional
    public void savePurchaseOrderAccordingResult(final PurchaseOrderData purchaseOrderData) {

        for (PurchaseOrderItemData itemData : purchaseOrderData.getPurchaseOrderItems()) {
            PurchaseOrderItem purchaseOrderItem = purchaseOrderItemService.getOne(itemData.getId());
            PurchaseOrder purchaseOrder = purchaseOrderItem.getPurchaseOrder();
            CutOrder cutOrder = purchaseOrder.getCutOrder();

            Vendor oldVendor = purchaseOrder.getVendor();
            Vendor newVendor = vendorService.findOne(itemData.getVendorId());
            //如果修改了供应商，则创建新的外采单。
            if (!oldVendor.getId().equals(newVendor.getId())) {
                PurchaseOrder newPurchaseOrder = new PurchaseOrder();
                newPurchaseOrder.setType(PurchaseOrderType.ACCORDING.getValue());
                newPurchaseOrder.setDepot(purchaseOrder.getDepot());
                newPurchaseOrder.setExpectedArrivedDate(purchaseOrder.getExpectedArrivedDate());
                newPurchaseOrder.setRemark(purchaseOrder.getRemark());
                newPurchaseOrder.setStatus(purchaseOrder.getStatus());
                newPurchaseOrder.setVendor(newVendor);
                newPurchaseOrder.setCreater(purchaseOrder.getCreater());
                newPurchaseOrder.setCreateTime(new Date());
                newPurchaseOrder.setPrint(purchaseOrder.getPrint());
                newPurchaseOrder.setCutOrder(cutOrder);

                List<PurchaseOrderItem> items = new ArrayList<>();
                for (Long purchaseOrderItemId : itemData.getPurchaseOrderItemIds()) {
                    PurchaseOrderItem itemToMove = purchaseOrderItemService.getOne(purchaseOrderItemId);
                    itemToMove.setPurchaseOrder(newPurchaseOrder);
                    items.add(itemToMove);
                }

                newPurchaseOrder.setPurchaseOrderItems(items);
                purchaseOrderRepository.save(newPurchaseOrder);
//                vendorOrderItemService.generateVendorOrder(newPurchaseOrder, cutOrder.getDepot());
                vendorOrderItemService.updateVendorOrderItems(items, oldVendor, newVendor, cutOrder.getDepot());
            }
        }

        // 对于多个外采单中的相同供应商的相同sku，按照创建时间的先后顺序分配purchaseQuantity。
        for (PurchaseOrderItemData itemData : purchaseOrderData.getPurchaseOrderItems()) {
            List<PurchaseOrderItem> items = new ArrayList<>();
            for (Long purchaseOrderItemId : itemData.getPurchaseOrderItemIds()) {
                PurchaseOrderItem item = purchaseOrderItemService.getOne(purchaseOrderItemId);
                items.add(item);
            }

            Collections.sort(items, new Comparator<PurchaseOrderItem>() {
                @Override
                public int compare(PurchaseOrderItem o1, PurchaseOrderItem o2) {
                    return o1.getPurchaseOrder().getCreateTime().compareTo(o2.getPurchaseOrder().getCreateTime());
                }
            });

            Integer purchaseQuantity = itemData.getPurchaseQuantity();
            for (int i = 0; i < items.size(); i++) {
                PurchaseOrderItem item = items.get(i);
                item.setPrice(itemData.getPurchasePrice());
                Integer itemNeedQuantity = item.getNeedQuantity();
                if (itemNeedQuantity.compareTo(purchaseQuantity) < 0) {//采购数量大于需求数量
                    if (i == items.size() - 1) {//如果在进行最后一次采购单数量分配，则把超过的数量录入到最后一次分配上。
                        item.setPurchaseQuantity(purchaseQuantity);
                        purchaseQuantity = 0;
                    } else {//如果不是最后一次分配，采购数量=需求数量
                        item.setPurchaseQuantity(itemNeedQuantity);
                        purchaseQuantity -= itemNeedQuantity;
                    }
                } else {//采购数量不大于需求数量
                    item.setPurchaseQuantity(purchaseQuantity);
                    purchaseQuantity = 0;
                }
                purchaseOrderItemService.save(item);
            }
        }

        // 取消不包含明细的采购单（由于修改供应商导致）并计算各个采购单的总金额
        List<PurchaseOrder> purchaseOrderList = purchaseOrderRepository.findAll(new Specification<PurchaseOrder>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                return root.get(PurchaseOrder_.cutOrder).get(CutOrder_.id).in(purchaseOrderData.getCutOrders());
            }
        });

        for (PurchaseOrder purchaseOrder : purchaseOrderList) {
            List<PurchaseOrderItem> purchaseOrderItems = purchaseOrder.getPurchaseOrderItems();
            if (CollectionUtils.isEmpty(purchaseOrderItems)) {
                purchaseOrder.setTotal(BigDecimal.ONE);
                purchaseOrder.setStatus(PurchaseOrderStatus.CANSELED.getValue());
                purchaseOrderRepository.save(purchaseOrder);
                continue;
            } else {
                BigDecimal total = new BigDecimal(0);
                for (PurchaseOrderItem item : purchaseOrderItems) {
                    total = total.add(item.getPrice().multiply(new BigDecimal(item.getPurchaseQuantity())));
                }
                purchaseOrder.setTotal(total.setScale(2, BigDecimal.ROUND_HALF_UP));
                purchaseOrderRepository.save(purchaseOrder);
            }
        }
    }

    private void addItem(HashMap<Long, List<PurchaseOrderItem>> purchaseOrderItemsHashMap, Long purchaseOrderId, PurchaseOrderItem purchaseOrderItem) {
        if (purchaseOrderItemsHashMap.get(purchaseOrderId) == null) {
            List<PurchaseOrderItem> purchaseOrderItems = new ArrayList<>();
            purchaseOrderItemsHashMap.put(purchaseOrderId, purchaseOrderItems);
        }
        purchaseOrderItemsHashMap.get(purchaseOrderId).add(purchaseOrderItem);
    }

    @Transactional
    public void generatePurchaseOrder(List<StockOutItem> stockOutItemList, Depot depot, CutOrder cutOrder) {
        Organization organization = organizationService.getDefaultOrganization();

        if (organization == null) {
            throw new UserDefinedException("没有默认自营店中店");
        }

        Vendor defaultVendor = vendorService.getDefaultVendor(organization.getId(), depot.getCity().getId());

        if (defaultVendor == null) {
            throw new UserDefinedException("没有默认供货商");
        }

        Map<Long, List<StockOutItem>> map = new HashMap<>();

        for (StockOutItem stockOutItem : stockOutItemList) {
            List<SkuVendor> skuVendors = skuVendorRepository.findByCityIdAndSkuId(depot.getCity().getId(), stockOutItem.getSku().getId());
            Vendor vendor = skuVendors.isEmpty() ? defaultVendor : skuVendors.get(0).getVendor();
            Long vendorId = vendor.getId();
            if (!map.containsKey(vendorId)) {
                List<StockOutItem> stockOutItems = new ArrayList<>();
                stockOutItems.add(stockOutItem);
                map.put(vendorId, stockOutItems);
            } else {
                map.get(vendorId).add(stockOutItem);
            }
        }

        for (Map.Entry<Long, List<StockOutItem>> entry : map.entrySet()) {
            PurchaseOrder purchaseOrder = new PurchaseOrder();
            purchaseOrder.setVendor(vendorService.findOne(entry.getKey()));
            purchaseOrder.setDepot(depot);
            purchaseOrder.setType(PurchaseOrderType.ACCORDING.getValue());
            purchaseOrder.setCreateTime(new Date());
            purchaseOrder.setStatus(PurchaseOrderStatus.EXECUTION.getValue());
            for (StockOutItem stockOutItem : entry.getValue()) {
                PurchaseOrderItem item = new PurchaseOrderItem();
                item.setPurchaseOrder(purchaseOrder);
                item.setSku(stockOutItem.getSku());
                item.setNeedQuantity(stockOutItem.getExpectedQuantity());
                item.setPurchaseQuantity(stockOutItem.getExpectedQuantity());
                item.setPrice(BigDecimal.ZERO);
                item.setRate(stockOutItem.getSku().getRate());
                item.setStatus(PurchaseOrderItemStatus.EXECUTION.getValue());
                purchaseOrder.getPurchaseOrderItems().add(item);
            }
            purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
            cutOrder.getPurchaseOrders().add(purchaseOrder);
        }
        if (cutOrder.getPurchaseOrders().isEmpty()) {
            cutOrder.setStatus(CutOrderStatus.COMMITED.getValue());
        }
        cutOrderService.saveCutOrder(cutOrder);
    }

    public PurchaseOrder getOne(Long id) {
        return purchaseOrderRepository.getOne(id);
    }

    public List<PurchaseOrderVo> getPurchaseOrdersByIds(final Long[] ids) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll(new Specification<PurchaseOrder>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();

                if (ArrayUtils.isNotEmpty(ids)) {
                    predicates.add(root.get(PurchaseOrder_.id).in(ids));
                }

                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });

        List<PurchaseOrderVo> list = new ArrayList<>();
        for (PurchaseOrder purchaseOrder : purchaseOrders) {
            PurchaseOrderVo purchaseOrderWrapper = purchaseOrderToPurchaseOrderVo(purchaseOrder);
            for (PurchaseOrderItem item : purchaseOrder.getPurchaseOrderItems()) {
                purchaseOrderWrapper.getPurchaseOrderItems().add(purchaseOrderItemService.purchaseOrderItemToPurchaseOrderItemVo(null, item, null));
            }
            list.add(purchaseOrderWrapper);
        }

        return list;
    }

    @Transactional
    public SubmitResponse<PurchaseOrderVo> cancel(Long purchaseOrderId, AdminUser operator) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.getOne(purchaseOrderId);

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.CANSELED.getValue()) {
            SubmitResponse<PurchaseOrderVo> res = new SubmitResponse<PurchaseOrderVo>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("采购单已取消，无需再次取消。");
            res.setContent(purchaseOrderToPurchaseOrderVo(purchaseOrder));
            return res;
        }

        if (purchaseOrder.getStatus() == PurchaseOrderStatus.COMPLETED.getValue()) {
            SubmitResponse<PurchaseOrderVo> res = new SubmitResponse<PurchaseOrderVo>();
            res.setSuccess(Boolean.FALSE);
            res.setMsg("采购单已完成收货，无法取消。");
            res.setContent(purchaseOrderToPurchaseOrderVo(purchaseOrder));
            return res;
        }

        List<StockIn> stockInList = stockInService.findByPurchaseOrderId(purchaseOrderId);

        if (CollectionUtils.isEmpty(stockInList)) {//未提交审核
            cancelPurchaseOrder(purchaseOrder, operator);
            SubmitResponse<PurchaseOrderVo> res = new SubmitResponse<PurchaseOrderVo>();
            res.setSuccess(Boolean.TRUE);
            res.setMsg("采购单取消成功");
            res.setContent(purchaseOrderToPurchaseOrderVo(purchaseOrder));
            return res;
        }

        boolean hasStockIn = false;

        //已提交审核
        for (StockIn stockIn : stockInList) {
            if (stockIn.getStatus() == StockInStatus.UNACCEPTED.getValue()) {
                stockIn.setStatus(StockInStatus.CANCEL.getValue());
                stockInService.saveStockIn(stockIn);
            }

            if (stockIn.getStatus() == StockInStatus.ACCEPTED.getValue()) {
                hasStockIn = true;
            }
        }

        if (hasStockIn) {
            completePurchaseOrder(purchaseOrder, operator);
        } else {
            cancelPurchaseOrder(purchaseOrder, operator);
        }

        SubmitResponse<PurchaseOrderVo> res = new SubmitResponse<PurchaseOrderVo>();
        res.setSuccess(Boolean.TRUE);
        res.setMsg("采购单取消成功");
        res.setContent(purchaseOrderToPurchaseOrderVo(purchaseOrder));
        return res;
    }

    private void completePurchaseOrder(PurchaseOrder purchaseOrder, AdminUser operator) {
        cancelPurchaseOrderItems(purchaseOrder, purchaseOrder.getPurchaseOrderItems());
        purchaseOrder.setCanceler(operator);
        purchaseOrder.setCancelTime(new Date());
        purchaseOrder.setStatus(PurchaseOrderStatus.COMPLETED.getValue());
        save(purchaseOrder);
    }

    private void cancelPurchaseOrder(PurchaseOrder purchaseOrder, AdminUser operator) {
        cancelPurchaseOrderItems(purchaseOrder, purchaseOrder.getPurchaseOrderItems());
        purchaseOrder.setCanceler(operator);
        purchaseOrder.setCancelTime(new Date());
        purchaseOrder.setStatus(PurchaseOrderStatus.CANSELED.getValue());
        save(purchaseOrder);
    }

    private void cancelPurchaseOrderItems(PurchaseOrder purchaseOrder, List<PurchaseOrderItem> purchaseOrderItems) {
        BigDecimal sub = new BigDecimal(0);
        for (PurchaseOrderItem purchaseOrderItem : purchaseOrderItems) {
            if (purchaseOrderItem.getStatus() != PurchaseOrderItemStatus.COMPLETED.getValue()) {
                purchaseOrderItem.setStatus(PurchaseOrderItemStatus.INVALID.getValue());
                purchaseOrderItemService.save(purchaseOrderItem);
                sub = sub.add(purchaseOrderItem.getPrice().multiply(new BigDecimal(purchaseOrderItem.getPurchaseQuantity())));
            }
        }

        purchaseOrder.setTotal(purchaseOrder.getTotal().subtract(sub));
    }

    public List<PurchaseOrder> getPurchaseOrderForVendor(final Long vendorId) {
        List<PurchaseOrder> purchaseOrders = purchaseOrderRepository.findAll(new Specification<PurchaseOrder>() {
            @Override
            public Predicate toPredicate(Root<PurchaseOrder> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get(PurchaseOrder_.type), PurchaseOrderType.ACCORDING.getValue()));
                predicates.add(cb.equal(root.get(PurchaseOrder_.vendor).get(Vendor_.id), vendorId));
                predicates.add(cb.equal(root.get(PurchaseOrder_.status), PurchaseOrderStatus.EXECUTION.getValue()));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        });
        return purchaseOrders;
    }
}
