package com.mishu.cgwy.stock.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.accounting.domain.AccountReceivable;
import com.mishu.cgwy.accounting.domain.Collectionment;
import com.mishu.cgwy.accounting.dto.CollectionmentData;
import com.mishu.cgwy.accounting.facade.AccountPayableFacade;
import com.mishu.cgwy.accounting.service.AccountReceivableService;
import com.mishu.cgwy.accounting.service.CollectionmentService;
import com.mishu.cgwy.accounting.service.RestaurantAccountHistoryService;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.LackInventoryException;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.message.CouponSenderEnum;
import com.mishu.cgwy.message.PromotionMessage;
import com.mishu.cgwy.message.PromotionMessageSender;
import com.mishu.cgwy.message.score.ScoreListMessage;
import com.mishu.cgwy.message.score.ScoreMessageSender;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.constants.OrderType;
import com.mishu.cgwy.order.controller.OrderGroupsSkuTotal;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.controller.SellCancelItemRequest;
import com.mishu.cgwy.order.domain.CutOrder;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.order.service.CutOrderService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.wrapper.SkuWrapper;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.purchase.domain.ReturnNote;
import com.mishu.cgwy.purchase.domain.ReturnNoteItem;
import com.mishu.cgwy.purchase.enumeration.CutOrderStatus;
import com.mishu.cgwy.purchase.service.PurchaseOrderService;
import com.mishu.cgwy.purchase.service.ReturnNoteService;
import com.mishu.cgwy.response.query.QuerySummationResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.StockOutData;
import com.mishu.cgwy.stock.dto.StockOutItemData;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.dto.StockTotalChange;
import com.mishu.cgwy.stock.service.*;
import com.mishu.cgwy.stock.wrapper.*;
import com.mishu.cgwy.vendor.service.VendorOrderItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by admin on 15/9/22.
 */
@Service
@Slf4j
public class StockOutFacade {

    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private StockService stockService;
    @Autowired
    private StockTotalService stockTotalService;
    @Autowired
    private StockInService stockInService;
    @Autowired
    private TransferService transferService;
    @Autowired
    private DepotService depotService;
    @Autowired
    private PurchaseOrderService purchaseOrderService;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ReturnNoteService returnNoteService;

    @Autowired
    private SellReturnService sellReturnService;
    @Autowired
    private AccountReceivableService accountReceivableService;
    @Autowired
    private CollectionmentService collectionmentService;
    @Autowired
    private RestaurantAccountHistoryService restaurantAccountHistoryService;
    @Autowired
    private SellCancelFacade sellCancelFacade;
    @Autowired
    private LocationService locationService;

    @Autowired
    private CutOrderService cutOrderService;

    @Autowired
    private AccountPayableFacade accountPayableFacade;
    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private PromotionMessageSender promotionMessageSender;

    @Autowired
    private ScoreMessageSender scoreMessageSender;

    @Autowired
    private VendorOrderItemService vendorOrderItemService;

    @Autowired
    private CouponService couponService;

    @Transactional(readOnly = true)
    public QuerySummationResponse<StockOutWrapper> getStockOutList(StockOutRequest stockOutRequest, AdminUser operator) {
        List<StockOutWrapper> list = new ArrayList<>();
        Page<StockOut> page = stockOutService.getStockOutList(stockOutRequest, operator);
        for (StockOut stockOut : page.getContent()) {
            list.add(new StockOutWrapper(stockOut));
        }
        QuerySummationResponse<StockOutWrapper> res = new QuerySummationResponse<>();
        res.setContent(list);
        res.setPage(stockOutRequest.getPage());
        res.setPageSize(stockOutRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal[] amounts = stockOutService.getStockOutAmounts(stockOutRequest, operator);
        res.setAmount(amounts);
        return res;
    }

    @Transactional(readOnly = true)
    public QuerySummationResponse<StockOutItemWrapper> getStockOutItemList(StockOutRequest stockOutRequest, AdminUser operator) {
        List<StockOutItemWrapper> list = new ArrayList<>();
        Page<StockOutItem> page = stockOutService.getStockOutItemList(stockOutRequest, operator);
        for (StockOutItem stockOutItem : page.getContent()) {
            list.add(new StockOutItemWrapper(stockOutItem));
        }
        QuerySummationResponse<StockOutItemWrapper> res = new QuerySummationResponse<>();
        res.setContent(list);
        res.setPage(stockOutRequest.getPage());
        res.setPageSize(stockOutRequest.getPageSize());
        res.setTotal(page.getTotalElements());
        BigDecimal[] amounts = stockOutService.getStockOutItemAmounts(stockOutRequest, operator);
        res.setAmount(amounts);
        return res;
    }

//    @Transactional(readOnly = true)
//    public QueryResponse<StockOutItemWrapper> getStockOutItemList(StockOutRequest stockOutRequest, AdminUser operator) {
//        List<StockOutItemWrapper> list = new ArrayList<>();
//        Page<StockOutItem> page = stockOutService.getStockOutItemList(stockOutRequest, operator);
//        for (StockOutItem stockOutItem : page.getContent()) {
//            list.add(new StockOutItemWrapper(stockOutItem));
//        }
//        QueryResponse<StockOutItemWrapper> res = new QueryResponse<>();
//        res.setContent(list);
//        res.setPage(stockOutRequest.getPage());
//        res.setPageSize(stockOutRequest.getPageSize());
//        res.setTotal(page.getTotalElements());
//        return res;
//    }


    @Transactional(readOnly = true)
    public StockOutWrapper getStockOut(Long stockOutId) {
        StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
        List<StockOutItemWrapper> stockOutWrappers = new ArrayList<>();
        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            stockOutWrappers.add(new StockOutItemWrapper(stockOutItem));
        }
        StockOutWrapper stockOutWrapper = new StockOutWrapper(stockOut);
        stockOutWrapper.setStockOutItems(stockOutWrappers);
        return stockOutWrapper;
    }

    @Transactional(readOnly = true)
    public StockOutWrapper getDistributedStockOut(Long stockOutId) {
        StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
        List<StockOutItemWrapper> stockOutWrappers = new ArrayList<>();
        List<StockOutItem> unDistributeItems = new ArrayList<>();
        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                stockOutWrappers.add(new StockOutItemWrapper(stockOutItem));
            } else {
                unDistributeItems.add(stockOutItem);
            }
        }
        for (StockOutItem unItem : unDistributeItems) {
            stockOutWrappers.add(new StockOutItemWrapper(unItem));
        }
        StockOutWrapper stockOutWrapper = new StockOutWrapper(stockOut);
        stockOutWrapper.setStockOutItems(stockOutWrappers);
        return stockOutWrapper;
    }

    @Transactional
    public void createStockOut(Transfer transfer) {

        if (transfer == null || transfer.getTransferItems() == null || transfer.getTransferItems().size() == 0)
            return;

        StockOut stockOut = new StockOut();
        stockOut.setStatus(StockOutStatus.IN_STOCK.getValue());
        stockOut.setDepot(transfer.getSourceDepot());
        stockOut.setCreateDate(new Date());
        stockOut.setTransfer(transfer);
        stockOut.setType(StockOutType.TRANSFER.getValue());
        stockOutService.saveStockOut(stockOut);

        List<StockOutItem> stockOutItems = new ArrayList<>();

        for (TransferItem transferItem : transfer.getTransferItems()) {
            Boolean isHave = false;
            for (StockOutItem soi : stockOutItems) {
                if (transferItem.getSku().getId().equals(soi.getSku().getId())) {
                    isHave = true;
                    soi.setExpectedQuantity(soi.getExpectedQuantity() + transferItem.getQuantity());
                    soi.setRealQuantity(soi.getExpectedQuantity());

                    break;
                }
            }

            if (!isHave) {
                StockOutItem soi = new StockOutItem();
                soi.setRealQuantity(transferItem.getQuantity());
                soi.setExpectedQuantity(transferItem.getQuantity());
                soi.setReceiveQuantity(transferItem.getQuantity());
                soi.setSku(transferItem.getSku());
                soi.setStatus(StockOutItemStatus.UNDISTRIBUTED.getValue());
                soi.setBundle(false);
                stockOutItems.add(soi);
            }
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (StockOutItem stockOutItem : stockOutItems) {

            int matchQuantity = stockService.occupySockQuantity(stockOut, stockOutItem.getExpectedQuantity(), stockOut.getDepot().getId(), stockOutItem.getSku().getId(), null);
            if (matchQuantity > 0) {
                throw new LackInventoryException(stockOutItem.getSku().getName());
            }
            StockTotal stockTotal = stockTotalService.findStockTotal(transfer.getSourceDepot().getCity().getId(), stockOutItem.getSku().getId());
            stockOutItem.setPrice(stockTotal.getAvgCost());
            stockOutItem.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());

            amount = amount.add(stockOutItem.getPrice().multiply(new BigDecimal(stockOutItem.getExpectedQuantity())));
        }

        stockOut.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        stockOut.setReceiveAmount(stockOut.getAmount());
        stockOut.getStockOutItems().clear();
        stockOut.getStockOutItems().addAll(stockOutItems);
        stockOutService.saveStockOut(stockOut);
    }

    @Transactional
    public void createStockOut(ReturnNote returnNote) {

        if (returnNote == null || returnNote.getReturnNoteItems() == null || returnNote.getReturnNoteItems().size() == 0)
            return;

        StockOut stockOut = new StockOut();
        stockOut.setStatus(StockOutStatus.IN_STOCK.getValue());
        stockOut.setDepot(returnNote.getDepot());
        stockOut.setCreateDate(new Date());
        stockOut.setReturnNote(returnNote);
        stockOut.setType(StockOutType.PURCHASERETURN.getValue());
        stockOutService.saveStockOut(stockOut);

        List<StockOutItem> stockOutItems = new ArrayList<>();

        for (ReturnNoteItem returnNoteItem : returnNote.getReturnNoteItems()) {
            Boolean isHave = false;
            for (StockOutItem soi : stockOutItems) {
                if (returnNoteItem.getPurchaseOrderItem().getSku().getId().equals(soi.getSku().getId())
                        && returnNoteItem.getReturnPrice().compareTo(soi.getPrice()) == 0
                        && returnNoteItem.getPurchaseOrderItem().getRate().compareTo(soi.getTaxRate()) == 0) {
                    isHave = true;
                    soi.setExpectedQuantity(soi.getExpectedQuantity() + returnNoteItem.getReturnQuantity());
                    soi.setRealQuantity(soi.getExpectedQuantity());

                    break;
                }
            }

            if (!isHave) {
                if (returnNoteItem.getReturnQuantity().intValue() > 0) {
                    StockOutItem soi = new StockOutItem();
                    soi.setRealQuantity(returnNoteItem.getReturnQuantity());
                    soi.setExpectedQuantity(returnNoteItem.getReturnQuantity());
                    soi.setReceiveQuantity(returnNoteItem.getReturnQuantity());
                    soi.setSku(returnNoteItem.getPurchaseOrderItem().getSku());
                    soi.setStatus(StockOutItemStatus.UNDISTRIBUTED.getValue());
                    soi.setTaxRate(returnNoteItem.getPurchaseOrderItem().getRate());
                    soi.setPrice(returnNoteItem.getReturnPrice());
                    soi.setAvgCost(returnNoteItem.getReturnPrice());
                    soi.setPurchasePrice(returnNoteItem.getPurchaseOrderItem().getPrice());
                    soi.setBundle(false);
                    stockOutItems.add(soi);
                }
            }
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (StockOutItem stockOutItem : stockOutItems) {

            int matchQuantity = stockService.occupySockQuantity(stockOut, stockOutItem.getExpectedQuantity(), stockOut.getDepot().getId(), stockOutItem.getSku().getId(), stockOutItem.getTaxRate());
            if (matchQuantity > 0) {
                throw new LackInventoryException(stockOutItem.getSku().getName());
            }
            stockOutItem.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());

            amount = amount.add(stockOutItem.getPrice().multiply(new BigDecimal(stockOutItem.getExpectedQuantity())));
        }

        stockOut.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        stockOut.setReceiveAmount(stockOut.getAmount());
        stockOut.getStockOutItems().clear();
        stockOut.getStockOutItems().addAll(stockOutItems);
        stockOutService.saveStockOut(stockOut);
    }

    @Transactional
    public void createStockOut(OrderQueryRequest request, AdminUser adminUser) {

        if (request.getDepotId() == null) {
            throw new UserDefinedException("请选择仓库");
        }

        Map<Long, List<StockOutItem>> depotStockOutItemMap = new HashMap<>();
        CutOrder cutOrder = new CutOrder();
        cutOrder.setCutDate(new Date());
        cutOrder.setCity(locationService.getCity(request.getCityId()));
        cutOrder.setDepot(depotService.findOne(request.getDepotId()));
        cutOrder.setStatus(CutOrderStatus.NOTSTARTED.getValue());
        cutOrder.setOperator(adminUser);

        if (request.getEnd() == null) {
            request.setEnd(new Date());
        }
        request.setPage(0);
        request.setStatus(OrderStatus.COMMITTED.getValue());
        Organization organization = organizationService.getDefaultOrganization();
        request.setOrganizationId(organization.getId());

        Map<Long, List<StockOutItem>> willTransferItemMap = new HashMap<>();

        while (true) {
            Page<Order> page = orderService.findOrders(request, adminUser);
            if (page.getTotalElements() <= 0) {
                break;
            }
            for (Order order : page) {
                StockOut stockOut = new StockOut();
                stockOut.setStatus(StockOutStatus.IN_STOCK.getValue());
                stockOut.setDepot(order.getCustomer().getBlock().getWarehouse().getDepot());
                stockOut.setCreateDate(new Date());
                stockOut.setOrder(order);
                stockOut.setType(StockOutType.ORDER.getValue());
                stockOutService.saveStockOut(stockOut);

                List<StockOutItem> stockOutItems = new ArrayList<>();
                List<OrderItem> orderItems = new ArrayList<>();
                orderItems.addAll(order.getOrderItems());
                Set<Promotion> promotions = order.getPromotions();
                if (promotions != null && promotions.size() > 0) {
                    for (Promotion promotion : promotions) {
                        if (promotion.getPromotableItems() != null && promotion.getPromotableItems().getSku() != null) {
                            OrderItem promotionOrderItem = new OrderItem();
                            promotionOrderItem.setPrice(BigDecimal.ZERO);
                            promotionOrderItem.setCountQuantity(promotion.getPromotableItems().isBundle() ? promotion.getPromotableItems().getQuantity() * promotion.getPromotableItems().getSku().getCapacityInBundle() : promotion.getPromotableItems().getQuantity());
                            promotionOrderItem.setSellCancelQuantity(0);
                            promotionOrderItem.setBundle(promotion.getPromotableItems().isBundle());
                            promotionOrderItem.setSku(promotion.getPromotableItems().getSku());
                            orderItems.add(promotionOrderItem);
                        }
                    }
                }

                BigDecimal stockOutAmount = BigDecimal.ZERO;
                for (OrderItem orderItem : orderItems) {

                    if (orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() <= 0) {
                        continue;
                    }

                    StockOutItem soi = new StockOutItem();
                    soi.setExpectedQuantity(orderItem.getCountQuantity() - orderItem.getSellCancelQuantity());
                    soi.setReceiveQuantity(soi.getExpectedQuantity());
                    soi.setRealQuantity(soi.getExpectedQuantity());
                    soi.setPrice(orderItem.isBundle() ? orderItem.getPrice().divide(new BigDecimal(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP) : orderItem.getPrice());
                    soi.setSku(orderItem.getSku());
                    soi.setStatus(StockOutItemStatus.UNDISTRIBUTED.getValue());
                    soi.setBundle(orderItem.isBundle());

                    stockOutAmount = stockOutAmount.add(soi.getPrice().multiply(new BigDecimal(soi.getExpectedQuantity())));

                    int matchQuantity = stockService.occupySockQuantity(stockOut, soi.getExpectedQuantity(), stockOut.getDepot().getId(), soi.getSku().getId(), null);
                    if (matchQuantity > 0) {//not match

                        if (matchQuantity - (orderItem.getCountQuantity() - orderItem.getSellCancelQuantity()) < 0) {

                            StockOutItem newSoi = new StockOutItem();
                            newSoi.setExpectedQuantity(matchQuantity);
                            newSoi.setReceiveQuantity(newSoi.getExpectedQuantity());
                            newSoi.setRealQuantity(newSoi.getExpectedQuantity());
                            newSoi.setPrice(orderItem.isBundle() ? orderItem.getPrice().divide(new BigDecimal(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP) : orderItem.getPrice());
                            newSoi.setSku(orderItem.getSku());
                            newSoi.setStatus(StockOutItemStatus.UNDISTRIBUTED.getValue());
                            newSoi.setBundle(orderItem.isBundle());
                            stockOutItems.add(newSoi);

                            soi.setExpectedQuantity(soi.getExpectedQuantity() - matchQuantity);
                            soi.setReceiveQuantity(soi.getExpectedQuantity());
                            soi.setRealQuantity(soi.getExpectedQuantity());
                            soi.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());

                            if (depotStockOutItemMap.containsKey(stockOut.getDepot().getId())) {

                                List<StockOutItem> depotStockOutItems = depotStockOutItemMap.get(stockOut.getDepot().getId());
                                boolean isHave = false;

                                for (StockOutItem depotStockOutItem : depotStockOutItems) {

                                    if (depotStockOutItem.getSku().getId().equals(newSoi.getSku().getId())) {

                                        depotStockOutItem.setExpectedQuantity(depotStockOutItem.getExpectedQuantity() + newSoi.getExpectedQuantity());
                                        isHave = true;
                                        break;
                                    }
                                }
                                if (!isHave) {
                                    depotStockOutItems.add(newSoi.clone());
                                }

                            } else {

                                List<StockOutItem> depotStockOutItems = new ArrayList<>();
                                depotStockOutItems.add(newSoi.clone());
                                depotStockOutItemMap.put(stockOut.getDepot().getId(), depotStockOutItems);
                            }
                        } else {

                            if (depotStockOutItemMap.containsKey(stockOut.getDepot().getId())) {

                                List<StockOutItem> depotStockOutItems = depotStockOutItemMap.get(stockOut.getDepot().getId());
                                boolean isHave = false;

                                for (StockOutItem depotStockOutItem : depotStockOutItems) {

                                    if (depotStockOutItem.getSku().getId().equals(soi.getSku().getId())) {

                                        depotStockOutItem.setExpectedQuantity(depotStockOutItem.getExpectedQuantity() + soi.getExpectedQuantity());
                                        isHave = true;
                                        break;
                                    }
                                }
                                if (!isHave) {
                                    depotStockOutItems.add(soi.clone());
                                }

                            } else {

                                List<StockOutItem> depotStockOutItems = new ArrayList<>();
                                depotStockOutItems.add(soi.clone());
                                depotStockOutItemMap.put(stockOut.getDepot().getId(), depotStockOutItems);
                            }
                        }
                    } else {

                        soi.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());
                    }
                    stockOutItems.add(soi);
                }

                stockOutAmount = stockOutAmount.subtract(order.getSubTotal().subtract(order.getTotal())).setScale(2, BigDecimal.ROUND_HALF_UP);
                stockOut.setAmount(stockOutAmount.compareTo(BigDecimal.ZERO) >= 0 ? stockOutAmount : BigDecimal.ZERO);
                stockOut.setReceiveAmount(stockOut.getAmount());
                stockOut.getStockOutItems().clear();
                stockOut.getStockOutItems().addAll(stockOutItems);
                stockOut = stockOutService.saveStockOut(stockOut);

                order.setStatus(OrderStatus.DEALING.getValue());
                orderService.save(order);
                cutOrder.getOrders().add(order);

                for (StockOutItem willItem : stockOut.getStockOutItems()) {
                    if (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(willItem.getStatus())) {
                        willItem.setStockOut(stockOut);
                        if (willTransferItemMap.containsKey(stockOut.getDepot().getId())) {
                            willTransferItemMap.get(stockOut.getDepot().getId()).add(willItem);
                        } else {
                            List<StockOutItem> willTransferItems = new ArrayList<>();
                            willTransferItems.add(willItem);
                            willTransferItemMap.put(stockOut.getDepot().getId(), willTransferItems);
                        }
                    }
                }
            }
        }
        if (cutOrder.getOrders().isEmpty()) {
            cutOrder.setStatus(CutOrderStatus.COMMITED.getValue());
            return;
        }

        cutOrderService.saveCutOrder(cutOrder);

        Iterator<Map.Entry<Long, List<StockOutItem>>> depotStockOutItemIterator = depotStockOutItemMap.entrySet().iterator();

        if (depotStockOutItemMap.isEmpty()) {
            cutOrder.setStatus(CutOrderStatus.COMMITED.getValue());
            cutOrderService.saveCutOrder(cutOrder);
        }

        while (depotStockOutItemIterator.hasNext()) {

            Map.Entry<Long, List<StockOutItem>> entry = depotStockOutItemIterator.next();
            Long depotId = entry.getKey();
            List<StockOutItem> notMatchItems = entry.getValue();
            Depot depot = depotService.findOne(depotId);
            if (depot != null && !depot.getIsMain()) {
                List<Depot> cityDepots = depotService.findDepotsByCityId(depot.getCity().getId());
                Depot mainDepot = null;
                for (Depot cityDepot : cityDepots) {
                    if (cityDepot.getIsMain() != null && cityDepot.getIsMain()) {
                        mainDepot = cityDepot;
                        break;
                    }
                }

                if (mainDepot != null) {

                    Transfer transfer = new Transfer();
                    transfer.setCreator(adminUser);
                    transfer.setCreateDate(new Date());
                    transfer.setSourceDepot(mainDepot);
                    transfer.setTargetDepot(depot);
                    transfer.setStatus(TransferStatus.EXECUTION.getValue());
                    transfer.setAuditDate(transfer.getCreateDate());
                    transfer.setAuditor(adminUser);
                    transferService.save(transfer);

                    StockOut transferStockOut = new StockOut();
                    transferStockOut.setStatus(StockOutStatus.IN_STOCK.getValue());
                    transferStockOut.setDepot(transfer.getSourceDepot());
                    transferStockOut.setCreateDate(transfer.getCreateDate());
                    transferStockOut.setTransfer(transfer);
                    transferStockOut.setType(StockOutType.TRANSFER.getValue());
                    stockOutService.saveStockOut(transferStockOut);

                    Iterator<StockOutItem> notMatchIter = notMatchItems.iterator();
                    List<TransferItem> transferItems = new ArrayList<>();
                    List<StockOutItem> transferOutItems = new ArrayList<>();
                    BigDecimal transferOutAmount = BigDecimal.ZERO;

                    while (notMatchIter.hasNext()) {

                        StockOutItem notMatchItem = notMatchIter.next();
                        StockTotal stockTotal = stockTotalService.findStockTotal(mainDepot.getCity().getId(), notMatchItem.getSku().getId());
                        int matchQuantity = stockService.occupySockQuantity(transferStockOut, notMatchItem.getExpectedQuantity(), transfer.getSourceDepot().getId(), notMatchItem.getSku().getId(), null);
                        int transferQuantity = 0;

                        if (matchQuantity > 0 && matchQuantity - notMatchItem.getExpectedQuantity() < 0) {

                            TransferItem transferItem = new TransferItem();
                            transferItem.setSku(notMatchItem.getSku());
                            transferItem.setQuantity(notMatchItem.getExpectedQuantity() - matchQuantity);
                            transferItems.add(transferItem);

                            StockOutItem transferSoi = new StockOutItem();
                            transferSoi.setReceiveQuantity(transferItem.getQuantity());
                            transferSoi.setRealQuantity(transferItem.getQuantity());
                            transferSoi.setExpectedQuantity(transferItem.getQuantity());
                            transferSoi.setSku(transferItem.getSku());
                            transferSoi.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());
                            transferSoi.setBundle(false);
                            transferSoi.setPrice(stockTotal.getAvgCost());
                            transferOutItems.add(transferSoi);

                            transferOutAmount = transferOutAmount.add(transferSoi.getPrice().multiply(new BigDecimal(transferSoi.getExpectedQuantity())));

                            notMatchItem.setExpectedQuantity(matchQuantity);

                            transferQuantity = transferItem.getQuantity();

                        } else if (matchQuantity <= 0) {

                            TransferItem transferItem = new TransferItem();
                            transferItem.setSku(notMatchItem.getSku());
                            transferItem.setQuantity(notMatchItem.getExpectedQuantity());
                            transferItems.add(transferItem);

                            StockOutItem transferSoi = new StockOutItem();
                            transferSoi.setReceiveQuantity(transferItem.getQuantity());
                            transferSoi.setRealQuantity(transferItem.getQuantity());
                            transferSoi.setExpectedQuantity(transferItem.getQuantity());
                            transferSoi.setSku(transferItem.getSku());
                            transferSoi.setStatus(StockOutItemStatus.DISTRIBUTED.getValue());
                            transferSoi.setBundle(false);
                            transferSoi.setPrice(stockTotal.getAvgCost());
                            transferOutItems.add(transferSoi);

                            transferOutAmount = transferOutAmount.add(transferSoi.getPrice().multiply(new BigDecimal(transferSoi.getExpectedQuantity())));

                            notMatchIter.remove();

                            transferQuantity = transferItem.getQuantity();
                        }

                        if (transferQuantity > 0 && willTransferItemMap.get(depotId) != null) {//区分未配货的哪些是调拨
                            for (StockOutItem willTransferItem : willTransferItemMap.get(depotId)) {
                                if (willTransferItem.getSku().getId().equals(notMatchItem.getSku().getId())
                                        && willTransferItem.getTransferOut() == null) {

                                    if (transferQuantity <= 0) {
                                        break;
                                    }
                                    if (willTransferItem.getExpectedQuantity() <= transferQuantity) {

                                        willTransferItem.setTransferOut(transferStockOut);
                                        stockOutService.saveStockOutItem(willTransferItem);
                                        transferQuantity = transferQuantity - willTransferItem.getExpectedQuantity();
                                    } else {
                                        StockOutItem newWillTransferItem = stockOutService.split(willTransferItem, transferQuantity);
                                        newWillTransferItem.setStockOut(willTransferItem.getStockOut());
                                        newWillTransferItem.setTransferOut(transferStockOut);
                                        stockOutService.saveStockOutItem(newWillTransferItem);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (transferItems.size() > 0) {

                        transfer.getTransferItems().clear();
                        transfer.getTransferItems().addAll(transferItems);
                        transferService.save(transfer);

                        transferStockOut.setAmount(transferOutAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
                        transferStockOut.setReceiveAmount(transferStockOut.getAmount());
                        transferStockOut.getStockOutItems().clear();
                        transferStockOut.getStockOutItems().addAll(transferOutItems);
                        stockOutService.saveStockOut(transferStockOut);

                        cutOrder.getTransfers().add(transfer);
                        cutOrderService.saveCutOrder(cutOrder);
                    } else {

                        stockOutService.delete(transferStockOut);
                        transferService.delete(transfer);
                    }
                }

                if (notMatchItems.size() == 0) {
                    cutOrder.setStatus(CutOrderStatus.COMMITED.getValue());
                    cutOrderService.saveCutOrder(cutOrder);
                    depotStockOutItemIterator.remove();
                } else {

                    purchaseOrderService.generatePurchaseOrder(notMatchItems, depot, cutOrder);
                }
            } else {

                purchaseOrderService.generatePurchaseOrder(notMatchItems, depot, cutOrder);
            }
        }

        vendorOrderItemService.generateVendorOrderItems(cutOrder);
    }

    @Transactional
    public void stockOutConfirmOut(StockOutData stockOutData, AdminUser adminUser) {

        StockOut stockOut = stockOutService.getOneStockOut(stockOutData.getStockOutId());
        if (stockOut == null) {
            throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "不存在");
        }

        if (!StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())) {
            throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "状态已改变");
        }

        if (StockOutType.PURCHASERETURN.getValue().equals(stockOut.getType())) {

            this.returnStockOutConfirmOut(stockOut, stockOutData, adminUser);
            return;
        }

        Boolean allZero = true;
        List<StockOutItem> newStockOutItems = new ArrayList<>();
        List<Stock> onRoadStocks = new ArrayList<>();

        List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());
        //取消订单
        List<SellCancelItemRequest> sellCancelItemRequests = new ArrayList<>();
        //订单出库成本
        Map<Long, BigDecimal> skuAvgCostMap = new HashMap<>();

        for (StockOutItemData stockOutItemData : stockOutData.getStockOutItems()) {
            Long stockOutItemId = stockOutItemData.getStockOutItemId();
            StockOutItem stockOutItem = stockOutService.getOneStockOutItem(stockOutItemId);

            int realQuantity = stockOutItemData.getRealQuantity();
            if (realQuantity > stockOutItem.getExpectedQuantity()) {
                throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "实出数量大于应出数量");
            } else if (realQuantity < 0) {
                throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "实出数量小于0");
            } else if (realQuantity != 0) {
                allZero = false;
            }

            stockOutItem.setRealQuantity(realQuantity);

            StockTotal stockTotal = stockTotalService.findStockTotal(stockOut.getDepot().getCity().getId(), stockOutItem.getSku().getId());
            if (!skuAvgCostMap.containsKey(stockOutItem.getSku().getId())) {
                skuAvgCostMap.put(stockOutItem.getSku().getId(), stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
            }

            Iterator<Stock> stockIterator = occupiedStocks.iterator();

            while (stockIterator.hasNext()) {

                if (realQuantity <= 0) {
                    break;
                }

                Stock occStock = stockIterator.next();
                if (stockOutItem.getSku().getId().equals(occStock.getSku().getId())) {

                    if (realQuantity > occStock.getStock()) {

                        StockOutItem newStockOutItem = stockOutService.split(stockOutItem, occStock.getStock());
                        newStockOutItem.setTaxRate(occStock.getTaxRate());
                        newStockOutItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                        newStockOutItems.add(newStockOutItem);
                        realQuantity -= occStock.getStock();

                        onRoadStocks.add(occStock);
                        stockIterator.remove();
                    } else if (realQuantity - occStock.getStock() == 0) {
                        stockOutItem.setTaxRate(occStock.getTaxRate());
                        realQuantity = 0;

                        onRoadStocks.add(occStock);
                        stockIterator.remove();

                        break;
                    } else {
                        stockOutItem.setTaxRate(occStock.getTaxRate());

                        Stock newOccStock = stockService.split(occStock, realQuantity);
                        newOccStock.setStockOut(occStock.getStockOut());
                        onRoadStocks.add(newOccStock);

                        realQuantity = 0;
                        break;
                    }
                }
            }

            stockOutItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());

            if (stockOutItem.getRealQuantity() == 0) {

                stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
            }

            if (StockOutType.ORDER.getValue().equals(stockOut.getType()) && stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity() > 0) {

                boolean isHave = false;
                for (SellCancelItemRequest sci : sellCancelItemRequests) {

                    if (stockOutItem.getSku().getId().equals(sci.getSkuId()) && stockOutItem.isBundle() == sci.getBundle().booleanValue()) {

                        sci.setQuantity(sci.getQuantity() + (stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity()));
                        isHave = true;
                        break;
                    }
                }
                if (!isHave) {
                    SellCancelItemRequest sci = new SellCancelItemRequest();
                    sci.setQuantity(stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity());
                    sci.setBundle(stockOutItem.isBundle());
                    sci.setSkuId(stockOutItem.getSku().getId());
                    sellCancelItemRequests.add(sci);
                }
            }
        }

        //剩下的表示出库时没出那么多，合并库存
        Iterator<Stock> stockIterator = occupiedStocks.iterator();
        while (stockIterator.hasNext()) {
            Stock willMergeStock = stockIterator.next();
            Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
            Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                    , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

            if (mergeStock != null) {
                mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                stockService.save(mergeStock);
                stockService.delete(willMergeStock);

            } else {
                willMergeStock.setStockOut(null);
                stockService.save(willMergeStock);
            }
        }
        stockOut.setSender(adminUser);
        stockOut.setFinishDate(new Date());

        if (allZero) {
            stockOut.setStatus(StockOutStatus.CANCEL.getValue());

            if (StockOutType.TRANSFER.getValue().equals(stockOut.getType())) {
                transferService.cancel(stockOut);
            }
        } else {
            stockOut.setStatus(StockOutStatus.HAVE_OUTBOUND.getValue());
        }

        if (newStockOutItems.size() > 0) {

            Iterator<StockOutItem> newItemIter = newStockOutItems.iterator();
            while (newItemIter.hasNext()) {

                StockOutItem newItem = newItemIter.next();
                for (StockOutItem originItem : stockOut.getStockOutItems()) {

                    if (StockOutItemStatus.DISTRIBUTED.getValue().equals(originItem.getStatus())
                            && originItem.getSku().getId().equals(newItem.getSku().getId())
                            && originItem.getTaxRate().compareTo(newItem.getTaxRate()) == 0
                            && originItem.isBundle() == originItem.isBundle()
                            && ((originItem.getPrice() != null && newItem.getPrice() != null && originItem.getPrice().compareTo(newItem.getPrice()) == 0) || (originItem.getPrice() == null && newItem.getPrice() == null))) {

                        originItem.setExpectedQuantity(originItem.getExpectedQuantity() + newItem.getExpectedQuantity());
                        originItem.setRealQuantity(originItem.getRealQuantity() + newItem.getRealQuantity());
                        stockOutService.deleteItem(newItem);
                        newItemIter.remove();
                        break;
                    }
                }

            }

        }
        if (newStockOutItems.size() > 0) {
            stockOut.getStockOutItems().addAll(newStockOutItems);
        }

        BigDecimal amount = BigDecimal.ZERO;
        for (StockOutItem soi : stockOut.getStockOutItems()) {

            soi.setReceiveQuantity(soi.getRealQuantity());

            if (StockOutType.TRANSFER.getValue().equals(stockOut.getType())) {
                soi.setPrice(soi.getAvgCost());
            }
            if (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(soi.getStatus())) {

                if (StockOutType.ORDER.getValue().equals(stockOut.getType())) {

                    boolean isHave = false;
                    for (SellCancelItemRequest sci : sellCancelItemRequests) {

                        if (soi.getSku().getId().equals(sci.getSkuId()) && soi.isBundle() == sci.getBundle().booleanValue()) {

                            sci.setQuantity(sci.getQuantity() + soi.getExpectedQuantity());
                            isHave = true;
                            break;
                        }
                    }
                    if (!isHave) {
                        SellCancelItemRequest sci = new SellCancelItemRequest();
                        sci.setQuantity(soi.getExpectedQuantity());
                        sci.setBundle(soi.isBundle());
                        sci.setSkuId(soi.getSku().getId());
                        sellCancelItemRequests.add(sci);
                    }
                }

                soi.setStatus(StockOutItemStatus.CANCEL.getValue());
            } else if (StockOutItemStatus.DISTRIBUTED.getValue().equals(soi.getStatus())) {
                amount = amount.add(soi.getPrice().multiply(new BigDecimal(soi.getRealQuantity())));
            }
        }

        if (StockOutType.ORDER.getValue().equals(stockOut.getType())) {
            amount = amount.subtract(stockOut.getOrder().getSubTotal().subtract(stockOut.getOrder().getTotal())).setScale(2, BigDecimal.ROUND_HALF_UP);
            stockOut.setAmount(amount.compareTo(BigDecimal.ZERO) >= 0 ? amount : BigDecimal.ZERO);
        } else {
            stockOut.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        }
        stockOut.setReceiveAmount(stockOut.getAmount());
        stockOutService.saveStockOut(stockOut);

        if (StockOutType.TRANSFER.getValue().equals(stockOut.getType())) {

            StockIn stockIn = stockInService.createStockIn(stockOut);

            for (Stock orStock : onRoadStocks) {
                orStock.setStockOut(null);
                orStock.setStockIn(stockIn);
                stockService.save(orStock);
            }
        } else if (StockOutType.ORDER.getValue().equals(stockOut.getType())) {

            StockTotalChange stockTotalChange = new StockTotalChange();
            Iterator<Stock> onRoadIter = onRoadStocks.iterator();
            while (onRoadIter.hasNext()) {
                Stock orStock = onRoadIter.next();
                stockTotalChange.add(orStock.getDepot().getCity(), orStock.getSku(), null, orStock.getStock() * (-1));

                stockService.delete(orStock);
            }
            stockTotalService.saveStockTotal(stockTotalChange);

            if (sellCancelItemRequests.size() > 0) {
                sellCancelFacade.createDepotSellCancel(stockOut.getOrder(), sellCancelItemRequests, adminUser);
            }
            Order order = orderService.getOrderById(stockOut.getOrder().getId());
            for (OrderItem orderItem : order.getOrderItems()) {
                if (skuAvgCostMap.containsKey(orderItem.getSku().getId())) {
                    orderItem.setAvgCost(skuAvgCostMap.get(orderItem.getSku().getId()));
                } else {
                    StockTotal stockTotal = stockTotalService.findStockTotal(stockOut.getDepot().getCity().getId(), orderItem.getSku().getId());
                    orderItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                    skuAvgCostMap.put(orderItem.getSku().getId(), stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                }
            }
            if (!OrderStatus.CANCEL.getValue().equals(order.getStatus())) {
                order.setRealTotal(stockOut.getAmount());
                order.setStatus(OrderStatus.SHIPPING.getValue());
            }
            orderService.save(order);

            if (stockOut.getOrderGroup() != null) {
                Set<Long> groupIds = new HashSet<>();
                groupIds.add(stockOut.getOrderGroup().getId());
                updateOrderGroupCheckResult(groupIds);
            }
        }
    }

    @Transactional
    public void returnStockOutConfirmOut(StockOut stockOut, StockOutData stockOutData, AdminUser adminUser) {

        Boolean allZero = true;
        List<StockOutItem> newStockOutItems = new ArrayList<>();
        List<Stock> onRoadStocks = new ArrayList<>();

        List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());

        for (StockOutItemData stockOutItemData : stockOutData.getStockOutItems()) {
            Long stockOutItemId = stockOutItemData.getStockOutItemId();
            StockOutItem stockOutItem = stockOutService.getOneStockOutItem(stockOutItemId);

            int realQuantity = stockOutItemData.getRealQuantity();
            if (realQuantity > stockOutItem.getExpectedQuantity()) {
                throw new UserDefinedException("出库单" + stockOut.getId() + "实出数量大于应出数量");
            } else if (realQuantity < 0) {
                throw new UserDefinedException("出库单" + stockOut.getId() + "实出数量小于0");
            } else if (realQuantity != 0) {
                allZero = false;
            }

            stockOutItem.setRealQuantity(realQuantity);

            Iterator<Stock> stockIterator = occupiedStocks.iterator();

            while (stockIterator.hasNext()) {

                if (realQuantity <= 0) {
                    break;
                }

                Stock occStock = stockIterator.next();
                if (stockOutItem.getSku().getId().equals(occStock.getSku().getId()) && stockOutItem.getTaxRate().compareTo(occStock.getTaxRate()) == 0) {

                    if (realQuantity > occStock.getStock()) {

                        StockOutItem newStockOutItem = stockOutService.split(stockOutItem, occStock.getStock());
                        newStockOutItems.add(newStockOutItem);
                        realQuantity -= occStock.getStock();

                        onRoadStocks.add(occStock);
                        stockIterator.remove();
                    } else if (realQuantity - occStock.getStock() == 0) {

                        realQuantity = 0;

                        onRoadStocks.add(occStock);
                        stockIterator.remove();

                        break;
                    } else {

                        Stock newOccStock = stockService.split(occStock, realQuantity);
                        newOccStock.setStockOut(occStock.getStockOut());
                        onRoadStocks.add(newOccStock);

                        realQuantity = 0;
                        break;
                    }
                }
            }

            if (stockOutItem.getRealQuantity() == 0) {

                stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
            }
        }

        //剩下的表示出库时没出那么多，合并库存
        Iterator<Stock> stockIterator = occupiedStocks.iterator();
        while (stockIterator.hasNext()) {
            Stock willMergeStock = stockIterator.next();
            Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
            Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                    , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

            if (mergeStock != null) {
                mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                stockService.save(mergeStock);
                stockService.delete(willMergeStock);

            } else {
                willMergeStock.setStockOut(null);
                stockService.save(willMergeStock);
            }
        }

        stockOut.setSender(adminUser);
        stockOut.setFinishDate(new Date());

        if (allZero) {
            stockOut.setStatus(StockOutStatus.CANCEL.getValue());
            returnNoteService.cancel(stockOut);
        } else {
            stockOut.setStatus(StockOutStatus.FINISHED.getValue());
            returnNoteService.complete(stockOut);
        }

        if (newStockOutItems.size() > 0) {

            Iterator<StockOutItem> newItemIter = newStockOutItems.iterator();
            while (newItemIter.hasNext()) {

                StockOutItem newItem = newItemIter.next();
                for (StockOutItem originItem : stockOut.getStockOutItems()) {

                    if (StockOutItemStatus.DISTRIBUTED.getValue().equals(originItem.getStatus())
                            && originItem.getSku().getId().equals(newItem.getSku().getId())
                            && originItem.getTaxRate().compareTo(newItem.getTaxRate()) == 0
                            && ((originItem.getPrice() != null && newItem.getPrice() != null && originItem.getPrice().compareTo(newItem.getPrice()) == 0) || (originItem.getPrice() == null && newItem.getPrice() == null))) {

                        originItem.setExpectedQuantity(originItem.getExpectedQuantity() + newItem.getExpectedQuantity());
                        originItem.setRealQuantity(originItem.getRealQuantity() + newItem.getRealQuantity());
                        stockOutService.deleteItem(newItem);
                        newItemIter.remove();
                        break;
                    }
                }

            }

        }
        if (newStockOutItems.size() > 0) {
            stockOut.getStockOutItems().addAll(newStockOutItems);
        }

        BigDecimal amount = BigDecimal.ZERO;
        StockTotalChange stockTotalChange = new StockTotalChange();
        for (StockOutItem soi : stockOut.getStockOutItems()) {

            soi.setReceiveQuantity(soi.getRealQuantity());
            if (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(soi.getStatus())) {

                soi.setStatus(StockOutItemStatus.CANCEL.getValue());
            } else if (StockOutItemStatus.DISTRIBUTED.getValue().equals(soi.getStatus())) {
                amount = amount.add(soi.getPrice().multiply(new BigDecimal(soi.getRealQuantity())));
                stockTotalChange.add(stockOut.getDepot().getCity(), soi.getSku(), soi.getAvgCost(), soi.getRealQuantity() * (-1));
            }
        }

        stockOut.setAmount(amount.setScale(2, BigDecimal.ROUND_HALF_UP));
        stockOut.setReceiveAmount(stockOut.getAmount());
        stockOutService.saveStockOut(stockOut);
        stockTotalService.saveStockTotal(stockTotalChange);
        Iterator<Stock> onRoadIter = onRoadStocks.iterator();
        while (onRoadIter.hasNext()) {
            Stock orStock = onRoadIter.next();
            stockService.delete(orStock);
            onRoadIter.remove();
        }
        accountPayableFacade.saveAccountPayable(stockOut);
    }

    @Transactional
    public void stockOutConfirmOutAll(StockOutData stockOutData, AdminUser adminUser) {
        for (Long stockOutId : stockOutData.getStockOutIds()) {
            StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
            if (stockOut == null || !StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())) {
                continue;
            }

            List<StockOutItemData> stockOutItemDatas = new ArrayList<>();
            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                    StockOutItemData stockOutItemData = new StockOutItemData();
                    stockOutItemData.setStockOutItemId(stockOutItem.getId());
                    stockOutItemData.setRealQuantity(stockOutItem.getExpectedQuantity());
                    stockOutItemDatas.add(stockOutItemData);
                }
            }
            StockOutData outData = new StockOutData();
            outData.setStockOutId(stockOut.getId());
            outData.setStockOutItems(stockOutItemDatas);
            this.stockOutConfirmOut(outData, adminUser);
        }
    }


    public void sendCouponAndScoreMessage(StockOutData stockOutData,StockOut... stockOuts) {
        //从收货事务方法里拆出来， 避免message服务接收到消息处理时 这边的事务还未完毕的情况
        if(stockOuts==null || stockOuts.length==0){
            return;
        }
        List<Long> stockOutIds = new ArrayList<>();
        for(StockOut so : stockOuts){
            stockOutIds.add(so.getId());
        }
        try {
            PromotionMessage message = new PromotionMessage(CouponSenderEnum.GATHERING_COMPLETED_SEND);
            message.setStockOutIds(stockOutIds);
            promotionMessageSender.sendMessage(message);
        }catch (Exception ex){
            log.error("promotion message send error",ex);
        }
        if(stockOutData==null || stockOutData.getType()==0) {
            try {
                //派发积分请求
                scoreMessageSender.send(new ScoreListMessage(stockOutIds));
            } catch (Exception ex) {
                log.error("score message send error", ex);
            }
        }

    }

//    @Transactional
//    public StockOut finishStockOutAndSendCouponMessage(StockOutData stockOutData, AdminUser adminUser) {
//        StockOut stockOut = stockOutFinish(stockOutData, adminUser);
//        return stockOut;
//    }

    @Transactional
    public StockOut stockOutFinish(StockOutData stockOutData, AdminUser adminUser) {

        StockOut stockOut = stockOutService.getOneStockOut(stockOutData.getStockOutId());
        if (stockOut == null) {
            throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "不存在");
        }

        if (!StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus())) {
            throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "状态已改变");
        }

        List<StockOutItemData> stockOutDatas = stockOutData.getStockOutItems();
        List<SellReturnItem> sellReturnItems = new ArrayList<>();
        List<CollectionmentData> collectionmentDatas = stockOutData.getCollectionments();
        if (stockOutData.getReceiveAmount().compareTo(BigDecimal.ZERO) <= 0) {
            stockOutData.setSettle(true);
            if (collectionmentDatas != null && !collectionmentDatas.isEmpty()) {
                collectionmentDatas.clear();
            }
        }

        for (StockOutItemData stockOutItemData : stockOutDatas) {

            StockOutItem stockOutItem = stockOutService.getOneStockOutItem(stockOutItemData.getStockOutItemId());
            if (!StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                stockOutItem.setReceiveQuantity(0);
            } else {
                int returndQuantity = stockOutItemData.getReturnQuantity();
                if (returndQuantity > stockOutItem.getRealQuantity()) {
                    throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "退货数量不能大于应收数量");
                }
                if (returndQuantity < 0) {
                    throw new UserDefinedException("出库单" + stockOutData.getStockOutId() + "数量不能小于0");
                }
                stockOutItem.setReceiveQuantity(stockOutItem.getRealQuantity() - returndQuantity);
                if (returndQuantity != 0) {

                    SellReturnItem sellReturnItem = new SellReturnItem();
                    sellReturnItem.setBundle(stockOutItem.isBundle());
                    sellReturnItem.setAvgCost(stockOutItem.getAvgCost());
                    sellReturnItem.setPrice(stockOutItem.getPrice());
                    sellReturnItem.setQuantity(returndQuantity);
                    sellReturnItem.setSku(stockOutItem.getSku());
                    sellReturnItem.setTaxRate(stockOutItem.getTaxRate());
                    if (null != stockOutItemData.getSellReturnReasonId()) {
                        sellReturnItem.setSellReturnReason(sellReturnService.getSellReturnReason(stockOutItemData.getSellReturnReasonId()));
                    }
                    sellReturnItems.add(sellReturnItem);
                }
            }
        }

        stockOut.setReceiveDate(new Date());
        stockOut.setSettle(stockOutData.isSettle());
        if (stockOutData.isSettle()) {
            stockOut.setSettleDate(stockOut.getReceiveDate());
        }
        stockOut.setReceiveAmount(stockOutData.getReceiveAmount());
        stockOut.setReceiver(adminUser);
        stockOut.setStatus(StockOutStatus.FINISHED.getValue());
        stockOutService.saveStockOut(stockOut);

        Order order = stockOut.getOrder();
        if (!sellReturnItems.isEmpty()) {
            SellReturn sellReturn = new SellReturn();
            sellReturn.setAmount(stockOut.getAmount().subtract(stockOutData.getReceiveAmount()).setScale(2, BigDecimal.ROUND_HALF_UP));
            sellReturn.setCreateDate(new Date());
            sellReturn.setCreator(adminUser);
            sellReturn.setOrder(stockOut.getOrder());
            sellReturn.setType(SellReturnType.CURRENT.getValue());
            sellReturn.setStatus(SellReturnStatus.EXECUTION.getValue());
            sellReturn.setDepot(stockOut.getDepot());
            sellReturn.setSellReturnItems(sellReturnItems);
            sellReturn = sellReturnService.saveSellReturn(sellReturn);

            stockInService.createStockIn(sellReturn);

            List<SellReturnItem> returnItems = new ArrayList<>();
            returnItems.addAll(sellReturnItems);
            Iterator<SellReturnItem> sellReturnItemIterator = returnItems.iterator();
            while (sellReturnItemIterator.hasNext()) {

                SellReturnItem sri = sellReturnItemIterator.next();
                for (OrderItem orderItem : order.getOrderItems()) {
                    if (orderItem.getSku().getId().equals(sri.getSku().getId()) && orderItem.isBundle() == sri.isBundle()) {
                        orderItem.setSellReturnQuantity(orderItem.getSellReturnQuantity() + sri.getQuantity());
                        sellReturnItemIterator.remove();
                        break;
                    }
                }
            }

            boolean allReturn = true;
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity() > 0) {
                    allReturn = false;
                    break;
                }
            }

            if (allReturn) {
                order.setStatus(OrderStatus.RETURNED.getValue());
                //全部退货，优惠券作废
                if (!order.getCustomerCoupons().isEmpty()) {
                    Set<CustomerCoupon> coupons = order.getCustomerCoupons();
                    for (CustomerCoupon coupon : coupons) {
                        coupon.setStatus(CouponStatus.INVALID.getValue());
                        couponService.saveCustomerCoupon(coupon);
                    }
                }
            } else {
                order.setStatus(OrderStatus.COMPLETED.getValue());
            }

        } else {
            order.setStatus(OrderStatus.COMPLETED.getValue());
        }
        order.setRealTotal(stockOut.getReceiveAmount());
        order.setCompleteDate(new Date());
        orderService.save(order);

        //应收
        AccountReceivable accountReceivable = accountReceivableService.generateAccountReceivableByStockOut(stockOut);
        if (accountReceivable != null && accountReceivable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
            restaurantAccountHistoryService.createRestaurantAccountHistory(BigDecimal.ZERO, accountReceivable.getAmount(), accountReceivable.getCreateDate(), accountReceivable.getRestaurant(), accountReceivable, null, null);
        }
        //收款
        if (collectionmentDatas != null && collectionmentDatas.size() > 0) {

            List<Collectionment> collectionments = collectionmentService.generateCollectionment(stockOut, collectionmentDatas);
            if (collectionments != null && collectionments.size() > 0) {
                for (Collectionment collectionment : collectionments) {
                    restaurantAccountHistoryService.createRestaurantAccountHistory(collectionment.getAmount(), BigDecimal.ZERO, collectionment.getCreateDate(), collectionment.getRestaurant(), null, collectionment, null);
                }
            }
        }

        return stockOut;
    }

    @Transactional
    public void stockOutCancelByOrder(Order order, List<SellCancelItem> sellCancelItems) {

        if (order != null && sellCancelItems != null && sellCancelItems.size() > 0) {

            StockOut stockOut = stockOutService.getOneStockOutByType(StockOutType.ORDER.getValue(), order.getId());
            if (stockOut != null) {

                List<StockOutItem> newStockOutItems = new ArrayList<>();
                List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());
                List<Stock> unMatchStocks = new ArrayList<>();

                for (SellCancelItem sellCancelItem : sellCancelItems) {

                    int cancelQuantity = sellCancelItem.getQuantity();

                    //先取消没配货的再取消配货的
                    for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {

                        if (cancelQuantity <= 0) {
                            break;
                        }
                        if (sellCancelItem.getSku().getId().equals(stockOutItem.getSku().getId())
                                && StockOutItemStatus.UNDISTRIBUTED.getValue().equals(stockOutItem.getStatus())
                                && sellCancelItem.isBundle() == stockOutItem.isBundle()
                                && sellCancelItem.getPrice().compareTo(stockOutItem.getPrice()) == 0) {

                            if (stockOutItem.getExpectedQuantity() <= cancelQuantity) {

                                stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
                                cancelQuantity -= stockOutItem.getExpectedQuantity();
                            } else {

                                StockOutItem newSoi = stockOutService.split(stockOutItem, cancelQuantity);
                                newSoi.setStatus(StockOutItemStatus.CANCEL.getValue());
                                newStockOutItems.add(newSoi);
                                cancelQuantity = 0;
                                break;

                            }
                        }
                    }

                    if (cancelQuantity > 0) {

                        for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {

                            if (cancelQuantity <= 0) {
                                break;
                            }
                            if (sellCancelItem.getSku().getId().equals(stockOutItem.getSku().getId())
                                    && StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())
                                    && sellCancelItem.isBundle() == stockOutItem.isBundle()
                                    && sellCancelItem.getPrice().compareTo(stockOutItem.getPrice()) == 0) {

                                if (stockOutItem.getExpectedQuantity() <= cancelQuantity) {

                                    //释放库存
                                    int unMatchQuantity = stockOutItem.getExpectedQuantity();
                                    Iterator<Stock> stockIterator = occupiedStocks.iterator();
                                    while (stockIterator.hasNext()) {

                                        if (unMatchQuantity <= 0) {
                                            break;
                                        }

                                        Stock occStock = stockIterator.next();
                                        if (stockOutItem.getSku().getId().equals(occStock.getSku().getId())) {

                                            if (unMatchQuantity >= occStock.getStock()) {

                                                unMatchQuantity -= occStock.getStock();
                                                unMatchStocks.add(occStock);
                                                stockIterator.remove();
                                            } else {

                                                Stock newOccStock = stockService.split(occStock, unMatchQuantity);
                                                newOccStock.setStockOut(occStock.getStockOut());
                                                unMatchStocks.add(newOccStock);

                                                unMatchQuantity = 0;
                                                break;
                                            }
                                        }
                                    }

                                    stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
                                    cancelQuantity -= stockOutItem.getExpectedQuantity();
                                } else {

                                    //释放库存
                                    int unMatchQuantity = cancelQuantity;
                                    Iterator<Stock> stockIterator = occupiedStocks.iterator();
                                    while (stockIterator.hasNext()) {

                                        if (unMatchQuantity <= 0) {
                                            break;
                                        }

                                        Stock occStock = stockIterator.next();
                                        if (stockOutItem.getSku().getId().equals(occStock.getSku().getId())) {

                                            if (unMatchQuantity >= occStock.getStock()) {

                                                unMatchQuantity -= occStock.getStock();
                                                unMatchStocks.add(occStock);
                                                stockIterator.remove();
                                            } else {

                                                Stock newOccStock = stockService.split(occStock, unMatchQuantity);
                                                newOccStock.setStockOut(occStock.getStockOut());
                                                unMatchStocks.add(newOccStock);

                                                unMatchQuantity = 0;
                                                break;
                                            }
                                        }
                                    }
                                    StockOutItem newSoi = stockOutService.split(stockOutItem, cancelQuantity);
                                    newSoi.setStatus(StockOutItemStatus.CANCEL.getValue());
                                    newStockOutItems.add(newSoi);
                                    cancelQuantity = 0;
                                    break;

                                }
                            }
                        }
                    }
                }
                Set<Promotion> promotions = order.getPromotions();
                if (!OrderType.GIFT.getVal().equals(order.getType()) && (promotions == null || (promotions != null && promotions.size() == 0))) {
                    for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                        if (stockOutItem.getPrice().compareTo(BigDecimal.ZERO) == 0
                                && !StockOutItemStatus.CANCEL.getValue().equals(stockOutItem.getStatus())) {
                            if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                                //释放库存
                                int unMatchQuantity = stockOutItem.getExpectedQuantity();
                                Iterator<Stock> stockIterator = occupiedStocks.iterator();
                                while (stockIterator.hasNext()) {

                                    if (unMatchQuantity <= 0) {
                                        break;
                                    }

                                    Stock occStock = stockIterator.next();
                                    if (stockOutItem.getSku().getId().equals(occStock.getSku().getId())) {

                                        if (unMatchQuantity >= occStock.getStock()) {

                                            unMatchQuantity -= occStock.getStock();
                                            unMatchStocks.add(occStock);
                                            stockIterator.remove();
                                        } else {

                                            Stock newOccStock = stockService.split(occStock, unMatchQuantity);
                                            newOccStock.setStockOut(occStock.getStockOut());
                                            unMatchStocks.add(newOccStock);

                                            unMatchQuantity = 0;
                                            break;
                                        }
                                    }
                                }
                            }
                            stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
                        }
                    }
                }

                if (newStockOutItems.size() > 0) {

                    Iterator<StockOutItem> newItemIter = newStockOutItems.iterator();
                    while (newItemIter.hasNext()) {

                        StockOutItem newItem = newItemIter.next();
                        for (StockOutItem originItem : stockOut.getStockOutItems()) {

                            if (StockOutItemStatus.CANCEL.getValue().equals(originItem.getStatus())
                                    && originItem.getSku().getId().equals(newItem.getSku().getId())
                                    && originItem.isBundle() == newItem.isBundle()
                                    && ((originItem.getPrice() != null && newItem.getPrice() != null && originItem.getPrice().compareTo(newItem.getPrice()) == 0) || (originItem.getPrice() == null && newItem.getPrice() == null))) {

                                originItem.setExpectedQuantity(originItem.getExpectedQuantity() + newItem.getExpectedQuantity());
                                originItem.setRealQuantity(originItem.getExpectedQuantity());
                                originItem.setReceiveQuantity(originItem.getExpectedQuantity());
                                stockOutService.deleteItem(newItem);
                                newItemIter.remove();
                                break;
                            }
                        }

                    }

                }
                if (newStockOutItems.size() > 0) {
                    stockOut.getStockOutItems().addAll(newStockOutItems);
                }

                boolean allCancel = true;
                BigDecimal stockOutAmount = BigDecimal.ZERO;
                for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                    if (!StockOutItemStatus.CANCEL.getValue().equals(stockOutItem.getStatus())) {
                        allCancel = false;
                        stockOutAmount = stockOutAmount.add(stockOutItem.getPrice().multiply(new BigDecimal(stockOutItem.getExpectedQuantity())));
                    }
                }
                stockOutAmount = stockOutAmount.subtract(order.getSubTotal().subtract(order.getTotal())).setScale(2, BigDecimal.ROUND_HALF_UP);
                stockOut.setAmount(stockOutAmount.compareTo(BigDecimal.ZERO) >= 0 ? stockOutAmount : BigDecimal.ZERO);
                if (allCancel) {
                    stockOut.setStatus(StockOutStatus.CANCEL.getValue());
                    stockOut.setAmount(BigDecimal.ZERO);
                }
                stockOut.setReceiveAmount(stockOut.getAmount());
                stockOutService.saveStockOut(stockOut);

                if (unMatchStocks.size() > 0) {
                    //释放库存后，合并库存
                    Iterator<Stock> unMatchIterator = unMatchStocks.iterator();
                    while (unMatchIterator.hasNext()) {
                        Stock willMergeStock = unMatchIterator.next();
                        Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
                        Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                                , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

                        if (mergeStock != null) {
                            mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                            stockService.save(mergeStock);
                            stockService.delete(willMergeStock);

                        } else {
                            willMergeStock.setStockOut(null);
                            stockService.save(willMergeStock);
                        }
                    }
                }
            }
        }

    }

//    @Transactional
//    public List<StockOut> finishAllStockOutsAndSendCouponMessage(StockOutData stockOutData, AdminUser adminUser) {
//        List<Long> stockOutIds = new ArrayList<>();
//        List<StockOut> stockOuts = stockOutFinishAll(stockOutData, adminUser);
//
//        return stockOuts;
//        if (CollectionUtils.isNotEmpty(stockOuts)) {
//            for (StockOut stockOut : stockOuts) {
//                stockOutIds.add(stockOut.getId());
//            }
//        }
//
//        PromotionMessage message = new PromotionMessage(CouponSenderEnum.GATHERING_COMPLETED_SEND);
//        message.setStockOutIds(stockOutIds);
//        promotionMessageSender.sendMessage(message);
//
//
//        //收货时派送积分
//        if (stockOutData.getType() == 0) {
//
//            scoreMessageSender.send(new ScoreListMessage(stockOutIds));
//
//            for (StockOut stockOut : stockOuts) {
//                //增加 派发积分
//                scoreMessageSender.send(new ScoreMessage(stockOut.getId()));
//            }
//        }
//    }

    @Transactional
    public List<StockOut> stockOutFinishAll(StockOutData stockOutData, AdminUser adminUser) {
        List<CollectionmentData> collectionmentDatas = stockOutData.getCollectionments();
        if (stockOutData.getReceiveAmount().compareTo(BigDecimal.ZERO) <= 0) {
            stockOutData.setSettle(true);
            if (collectionmentDatas != null) {
                collectionmentDatas.clear();
            } else {
                collectionmentDatas = new ArrayList<>();
            }
        }

        List<StockOut> stockOuts = new ArrayList<>();
        for (Long stockOutId : stockOutData.getStockOutIds()) {

            StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
            stockOuts.add(stockOut);
            if (stockOut == null) {
                throw new UserDefinedException("出库单" + stockOutId + "不存在");
            }

            if (stockOutData.getType() == 0 && !StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus())) {
                throw new UserDefinedException("出库单" + stockOutId + "状态不是已出库");
            }

            if (stockOutData.getType() != 0 && !StockOutStatus.FINISHED.getValue().equals(stockOut.getStatus())) {
                throw new UserDefinedException("出库单" + stockOutId + "状态不是已完成");
            }

            if (stockOutData.getType() == 0) {
                stockOut.setReceiveDate(new Date());
                stockOut.setSettle(stockOutData.isSettle());
                if (stockOutData.isSettle()) {
                    stockOut.setSettleDate(stockOut.getReceiveDate());
                }
                stockOut.setReceiveAmount(stockOut.getAmount());
                stockOut.setReceiver(adminUser);
                stockOut.setStatus(StockOutStatus.FINISHED.getValue());

                Order order = stockOut.getOrder();
                order.setStatus(OrderStatus.COMPLETED.getValue());
                order.setRealTotal(stockOut.getReceiveAmount());
                order.setCompleteDate(new Date());
                orderService.save(order);
            } else {
                stockOut.setSettle(true);
                stockOut.setSettleDate(new Date());
            }
            stockOutService.saveStockOut(stockOut);

            if (stockOutData.getType() == 0) {
                //应收
                AccountReceivable accountReceivable = accountReceivableService.generateAccountReceivableByStockOut(stockOut);
                if (accountReceivable != null && accountReceivable.getAmount().compareTo(BigDecimal.ZERO) != 0) {
                    restaurantAccountHistoryService.createRestaurantAccountHistory(BigDecimal.ZERO, accountReceivable.getAmount(), accountReceivable.getCreateDate(), accountReceivable.getRestaurant(), accountReceivable, null, null);
                }
            }

            //收款
            BigDecimal receiveAmount = stockOut.getReceiveAmount();
            if (receiveAmount.compareTo(BigDecimal.ZERO) != 0) {
                List<CollectionmentData> paramCollectionDatas = new ArrayList<>();
                Iterator<CollectionmentData> collectionmentDataIterator = collectionmentDatas.iterator();
                while (collectionmentDataIterator.hasNext()) {
                    if (receiveAmount.compareTo(BigDecimal.ZERO) <= 0) {
                        break;
                    }
                    CollectionmentData collectionmentData = collectionmentDataIterator.next();
                    if (collectionmentData.getAmount().compareTo(receiveAmount) > 0) {
                        CollectionmentData paramCollectionData = new CollectionmentData();
                        paramCollectionData.setAmount(receiveAmount);
                        paramCollectionData.setCollectionPaymentMethodId(collectionmentData.getCollectionPaymentMethodId());
                        paramCollectionDatas.add(paramCollectionData);

                        collectionmentData.setAmount(collectionmentData.getAmount().subtract(receiveAmount));
                        receiveAmount = BigDecimal.ZERO;
                        break;
                    } else {
                        paramCollectionDatas.add(collectionmentData);
                        receiveAmount = receiveAmount.subtract(collectionmentData.getAmount());
                        collectionmentDataIterator.remove();
                    }
                }
                if (paramCollectionDatas.size() > 0) {
                    List<Collectionment> collectionments = collectionmentService.generateCollectionment(stockOut, paramCollectionDatas);
                    if (collectionments != null && collectionments.size() > 0) {
                        for (Collectionment collectionment : collectionments) {
                            restaurantAccountHistoryService.createRestaurantAccountHistory(collectionment.getAmount(), BigDecimal.ZERO, collectionment.getCreateDate(), collectionment.getRestaurant(), null, collectionment, null);
                        }
                    }
                }
            }
        }

        return stockOuts;
    }

    //验货App查看订单包
    @Transactional(readOnly = true)
    public List<SimpleStockOutGroupWrapper> findStockOutGroupsByOperator(AdminUser tracker, String trackerName) {

        List<OrderGroup> orderGroups = this.findOrderGroupsByOperator(tracker, 0, trackerName);
        List<SimpleStockOutGroupWrapper> stockOutGroupList = new ArrayList<>();
        for (OrderGroup orderGroup : orderGroups) {
            SimpleStockOutGroupWrapper stockOutGroupWrapper = new SimpleStockOutGroupWrapper(orderGroup);
            BigDecimal total = BigDecimal.ZERO;
            boolean checkResult = false;
            for (StockOut stockOut : orderGroup.getStockOuts()) {
                if (StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())
                        || StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus())) {
                    total = total.add(stockOut.getAmount());
                }
                if (!checkResult && StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus())) {
                    checkResult = true;
                }
            }
            if (!stockOutGroupWrapper.isCheckResult() && checkResult) {
                stockOutGroupWrapper.setCheckResult(checkResult);
            }
            stockOutGroupWrapper.setSumOfTotal(total);
            stockOutGroupList.add(stockOutGroupWrapper);
        }

        return stockOutGroupList;
    }

    //验货App查看订单包
    @Transactional(readOnly = true)
    public StockOutGroupsSku getStockOutGroupById(Long id) {

        OrderGroup orderGroup = orderService.getOrderGroupById(id);
        List<OrderGroupsSkuTotal> skus = new ArrayList<>();
        Map<Long, OrderGroupsSkuTotal> map = new HashMap<>();
        SimpleStockOutGroupWrapper stockOutGroupWrapper = new SimpleStockOutGroupWrapper(orderGroup);
        BigDecimal total = BigDecimal.ZERO;
        List<GroupStockOutWrapper> stockOutWrappers = new ArrayList<>();
        for (StockOut stockOut : orderGroup.getStockOuts()) {
            if (StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())) {
                total = total.add(stockOut.getAmount());
                stockOutWrappers.add(new GroupStockOutWrapper(stockOut));

                for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {

                    if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                        if (map.containsKey(stockOutItem.getSku().getId())) {
                            OrderGroupsSkuTotal skuTotal = map.get(stockOutItem.getSku().getId());
                            skuTotal.setQuantity(skuTotal.getQuantity() + stockOutItem.getExpectedQuantity());
                        } else {
                            OrderGroupsSkuTotal orderGroupsSkuTotal = new OrderGroupsSkuTotal();
                            orderGroupsSkuTotal.setSku(new SkuWrapper(stockOutItem.getSku()));
                            orderGroupsSkuTotal.setQuantity(stockOutItem.getExpectedQuantity());
                            orderGroupsSkuTotal.setPrice(stockOutItem.getPrice());
                            map.put(stockOutItem.getSku().getId(), orderGroupsSkuTotal);
                        }
                    }
                }
            }
        }

        stockOutGroupWrapper.setSumOfTotal(total);
        stockOutGroupWrapper.setMembers(stockOutWrappers);
        for (Map.Entry<Long, OrderGroupsSkuTotal> entry : map.entrySet()) {
            skus.add(entry.getValue());
        }
        StockOutGroupsSku stockOutGroupsSku = new StockOutGroupsSku();
        stockOutGroupsSku.setOrderGroupsSkuTotals(skus);
        stockOutGroupsSku.setStockOutGroupWrapper(stockOutGroupWrapper);

        return stockOutGroupsSku;
    }

    @Transactional(readOnly = true)
    public List<OrderGroup> findOrderGroupsByOperator(final AdminUser operator, final int type, final String tracker) {
        assert operator != null;
        if (PermissionCheckUtils.canViewAllTracker(operator)) {

            Set<Depot> depots = adminUserService.getAdminUserAllDepot(operator);
            List<Long> depotIds = new ArrayList<>(Collections2.transform(depots, new Function<Depot, Long>() {
                @Override
                public Long apply(Depot input) {
                    return input.getId();
                }

            }));
            if (depotIds.size() == 0) {
                depotIds.add(-1L);
            }

            return stockOutService.findStockOutGroups(type, depotIds, tracker, null);

        } else {

            return stockOutService.findStockOutGroups(type, null, tracker, operator.getId());
        }
    }

    //App验货出库
    @Transactional
    public void orderStockOutConfirmOut(Long groupId, StockOutData stockOutData, AdminUser adminUser) {

        Set<Long> stockOutIds = stockOutData.getStockOutIds();
        List<StockOutItemData> skus = stockOutData.getStockOutItems();
        for (Long id : stockOutIds) {

            StockOut stockOut = stockOutService.getOneStockOut(id);
            if (stockOut == null) {
                throw new UserDefinedException("出库单" + id + "不存在");
            }

            if (!StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())) {
                throw new UserDefinedException("出库单" + id + "状态已改变");
            }

            Boolean allZero = true;
            List<StockOutItem> newStockOutItems = new ArrayList<>();
            List<Stock> onRoadStocks = new ArrayList<>();

            List<Stock> occupiedStocks = stockService.findOccupiedSocks(stockOut.getId());
            //取消订单
            List<SellCancelItemRequest> sellCancelItemRequests = new ArrayList<>();
            //订单出库成本
            Map<Long, BigDecimal> skuAvgCostMap = new HashMap<>();

            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {

                if (StockOutItemStatus.DISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {

                    int realQuantity = 0;
                    Iterator<StockOutItemData> skuIterator = skus.iterator();
                    while (skuIterator.hasNext()) {

                        StockOutItemData skuTotal = skuIterator.next();
                        if (skuTotal.getSkuId().equals(stockOutItem.getSku().getId())) {

                            if (stockOutItem.getExpectedQuantity() > skuTotal.getRealQuantity()) {

                                realQuantity = skuTotal.getRealQuantity();
                                skuTotal.setRealQuantity(0);
                            } else {
                                realQuantity = stockOutItem.getExpectedQuantity();
                                skuTotal.setRealQuantity(skuTotal.getRealQuantity() - stockOutItem.getExpectedQuantity());
                            }

                            if (skuTotal.getRealQuantity() <= 0) {
                                skuIterator.remove();
                            }
                            break;
                        }
                    }

                    if (realQuantity != 0) {
                        allZero = false;
                    }

                    stockOutItem.setRealQuantity(realQuantity);

                    StockTotal stockTotal = stockTotalService.findStockTotal(stockOut.getDepot().getCity().getId(), stockOutItem.getSku().getId());
                    if (!skuAvgCostMap.containsKey(stockOutItem.getSku().getId())) {
                        skuAvgCostMap.put(stockOutItem.getSku().getId(), stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                    }

                    Iterator<Stock> stockIterator = occupiedStocks.iterator();

                    while (stockIterator.hasNext()) {

                        if (realQuantity <= 0) {
                            break;
                        }

                        Stock occStock = stockIterator.next();
                        if (stockOutItem.getSku().getId().equals(occStock.getSku().getId())) {

                            if (realQuantity > occStock.getStock()) {

                                StockOutItem newStockOutItem = stockOutService.split(stockOutItem, occStock.getStock());
                                newStockOutItem.setTaxRate(occStock.getTaxRate());
                                newStockOutItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                                newStockOutItems.add(newStockOutItem);
                                realQuantity -= occStock.getStock();

                                onRoadStocks.add(occStock);
                                stockIterator.remove();
                            } else if (realQuantity - occStock.getStock() == 0) {
                                stockOutItem.setTaxRate(occStock.getTaxRate());
                                realQuantity = 0;

                                onRoadStocks.add(occStock);
                                stockIterator.remove();

                                break;
                            } else {
                                stockOutItem.setTaxRate(occStock.getTaxRate());

                                Stock newOccStock = stockService.split(occStock, realQuantity);
                                newOccStock.setStockOut(occStock.getStockOut());
                                onRoadStocks.add(newOccStock);

                                realQuantity = 0;
                                break;
                            }
                        }
                    }

                    stockOutItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());

                    if (stockOutItem.getRealQuantity() == 0) {

                        stockOutItem.setStatus(StockOutItemStatus.CANCEL.getValue());
                    }

                    if (stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity() > 0) {

                        boolean isHave = false;
                        for (SellCancelItemRequest sci : sellCancelItemRequests) {

                            if (stockOutItem.getSku().getId().equals(sci.getSkuId()) && stockOutItem.isBundle() == sci.getBundle().booleanValue()) {

                                sci.setQuantity(sci.getQuantity() + (stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity()));
                                isHave = true;
                                break;
                            }
                        }
                        if (!isHave) {
                            SellCancelItemRequest sci = new SellCancelItemRequest();
                            sci.setQuantity(stockOutItem.getExpectedQuantity() - stockOutItem.getRealQuantity());
                            sci.setBundle(stockOutItem.isBundle());
                            sci.setSkuId(stockOutItem.getSku().getId());
                            sellCancelItemRequests.add(sci);
                        }
                    }
                }
            }

            //剩下的表示出库时没出那么多，合并库存
            Iterator<Stock> stockIterator = occupiedStocks.iterator();
            while (stockIterator.hasNext()) {
                Stock willMergeStock = stockIterator.next();
                Long shelfId = willMergeStock.getShelf() != null ? willMergeStock.getShelf().getId() : null;
                Stock mergeStock = stockService.findMergeStock(willMergeStock.getDepot().getId(), willMergeStock.getSku().getId()
                        , willMergeStock.getTaxRate(), shelfId, willMergeStock.getExpirationDate(), willMergeStock.getId());

                if (mergeStock != null) {
                    mergeStock.setStock(mergeStock.getStock() + willMergeStock.getStock());
                    stockService.save(mergeStock);
                    stockService.delete(willMergeStock);

                } else {
                    willMergeStock.setStockOut(null);
                    stockService.save(willMergeStock);
                }
            }
            stockOut.setSender(adminUser);
            stockOut.setFinishDate(new Date());

            if (allZero) {
                stockOut.setStatus(StockOutStatus.CANCEL.getValue());
            } else {
                stockOut.setStatus(StockOutStatus.HAVE_OUTBOUND.getValue());
            }

            if (newStockOutItems.size() > 0) {

                Iterator<StockOutItem> newItemIter = newStockOutItems.iterator();
                while (newItemIter.hasNext()) {

                    StockOutItem newItem = newItemIter.next();
                    for (StockOutItem originItem : stockOut.getStockOutItems()) {

                        if (StockOutItemStatus.DISTRIBUTED.getValue().equals(originItem.getStatus())
                                && originItem.getSku().getId().equals(newItem.getSku().getId())
                                && originItem.getTaxRate().compareTo(newItem.getTaxRate()) == 0
                                && originItem.isBundle() == originItem.isBundle()
                                && ((originItem.getPrice() != null && newItem.getPrice() != null && originItem.getPrice().compareTo(newItem.getPrice()) == 0) || (originItem.getPrice() == null && newItem.getPrice() == null))) {

                            originItem.setExpectedQuantity(originItem.getExpectedQuantity() + newItem.getExpectedQuantity());
                            originItem.setRealQuantity(originItem.getRealQuantity() + newItem.getRealQuantity());
                            stockOutService.deleteItem(newItem);
                            newItemIter.remove();
                            break;
                        }
                    }

                }

            }
            if (newStockOutItems.size() > 0) {
                stockOut.getStockOutItems().addAll(newStockOutItems);
            }

            BigDecimal amount = BigDecimal.ZERO;
            for (StockOutItem soi : stockOut.getStockOutItems()) {

                soi.setReceiveQuantity(soi.getRealQuantity());
                if (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(soi.getStatus())) {

                    boolean isHave = false;
                    for (SellCancelItemRequest sci : sellCancelItemRequests) {

                        if (soi.getSku().getId().equals(sci.getSkuId()) && soi.isBundle() == sci.getBundle().booleanValue()) {

                            sci.setQuantity(sci.getQuantity() + soi.getExpectedQuantity());
                            isHave = true;
                            break;
                        }
                    }
                    if (!isHave) {
                        SellCancelItemRequest sci = new SellCancelItemRequest();
                        sci.setQuantity(soi.getExpectedQuantity());
                        sci.setBundle(soi.isBundle());
                        sci.setSkuId(soi.getSku().getId());
                        sellCancelItemRequests.add(sci);
                    }
                    soi.setStatus(StockOutItemStatus.CANCEL.getValue());
                } else if (StockOutItemStatus.DISTRIBUTED.getValue().equals(soi.getStatus())) {
                    amount = amount.add(soi.getPrice().multiply(new BigDecimal(soi.getRealQuantity())));
                }
            }

            amount = amount.subtract(stockOut.getOrder().getSubTotal().subtract(stockOut.getOrder().getTotal())).setScale(2, BigDecimal.ROUND_HALF_UP);
            stockOut.setAmount(amount.compareTo(BigDecimal.ZERO) >= 0 ? amount : BigDecimal.ZERO);
            stockOut.setReceiveAmount(stockOut.getAmount());
            stockOutService.saveStockOut(stockOut);

            StockTotalChange stockTotalChange = new StockTotalChange();
            Iterator<Stock> onRoadIter = onRoadStocks.iterator();
            while (onRoadIter.hasNext()) {
                Stock orStock = onRoadIter.next();
                stockTotalChange.add(orStock.getDepot().getCity(), orStock.getSku(), null, orStock.getStock() * (-1));

                stockService.delete(orStock);
            }
            stockTotalService.saveStockTotal(stockTotalChange);

            if (sellCancelItemRequests.size() > 0) {
                sellCancelFacade.createDepotSellCancel(stockOut.getOrder(), sellCancelItemRequests, adminUser);
            }
            Order order = orderService.getOrderById(stockOut.getOrder().getId());
            for (OrderItem orderItem : order.getOrderItems()) {
                if (skuAvgCostMap.containsKey(orderItem.getSku().getId())) {
                    orderItem.setAvgCost(skuAvgCostMap.get(orderItem.getSku().getId()));
                } else {
                    StockTotal stockTotal = stockTotalService.findStockTotal(stockOut.getDepot().getCity().getId(), orderItem.getSku().getId());
                    orderItem.setAvgCost(stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                    skuAvgCostMap.put(orderItem.getSku().getId(), stockTotal == null ? BigDecimal.ZERO : stockTotal.getAvgCost());
                }
            }
            if (!OrderStatus.CANCEL.getValue().equals(order.getStatus())) {
                order.setStatus(OrderStatus.SHIPPING.getValue());
            }
            orderService.save(order);
        }

        Set<Long> groupIds = new HashSet<>();
        groupIds.add(groupId);
        updateOrderGroupCheckResult(groupIds);
    }

    @Transactional
    public void updateOrderGroupCheckResult(Set<Long> groupIds) {

        for (Long groupId : groupIds) {
            OrderGroup orderGroup = orderService.getOrderGroupById(groupId);
            if (!orderGroup.isCheckResult()) {
                orderGroup.setCheckResult(true);
                orderService.saveOrderGroup(orderGroup);
            }
        }
    }

    //配送App仓库经纬度
    @Transactional(readOnly = true)
    public DepotWrapper findOrderGroupPointByTracker(AdminUser tracker) {

        DepotWrapper depotWrapper = new DepotWrapper();
        List<OrderGroup> orderGroups = this.findOrderGroupsByOperator(tracker, 1, null);

        for (OrderGroup orderGroup : orderGroups) {
            for (StockOut stockOut : orderGroup.getStockOuts()) {
                if (StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus())) {
                    Depot depot = stockOut.getDepot();
                    if (depot != null) {
                        depotWrapper.setId(depot.getId());
                        depotWrapper.setName(depot.getName());
                        depotWrapper.setLatitude(depot.getWgs84Point() != null ? depot.getWgs84Point().getLatitude() : null);
                        depotWrapper.setLongitude(depot.getWgs84Point() != null ? depot.getWgs84Point().getLongitude() : null);

                        return depotWrapper;
                    }
                }
            }
        }
        if (depotWrapper.getId() == null) {
            for (OrderGroup orderGroup : orderGroups) {
                for (StockOut stockOut : orderGroup.getStockOuts()) {
                    if (StockOutStatus.FINISHED.getValue().equals(stockOut.getStatus())) {
                        Depot depot = stockOut.getDepot();
                        if (depot != null) {
                            depotWrapper.setId(depot.getId());
                            depotWrapper.setName(depot.getName());
                            depotWrapper.setLatitude(depot.getWgs84Point() != null ? depot.getWgs84Point().getLatitude() : null);
                            depotWrapper.setLongitude(depot.getWgs84Point() != null ? depot.getWgs84Point().getLongitude() : null);

                            return depotWrapper;
                        }
                    }
                }
            }
        }
        if (depotWrapper.getId() == null) {
            Set<Depot> depots = tracker.getDepots();
            for (Depot depot : depots) {
                if (depot != null && depot.getWgs84Point() != null) {
                    depotWrapper.setId(depot.getId());
                    depotWrapper.setName(depot.getName());
                    depotWrapper.setLatitude(depot.getWgs84Point().getLatitude());
                    depotWrapper.setLongitude(depot.getWgs84Point().getLongitude());

                    return depotWrapper;
                }
            }
        }
        return depotWrapper;
    }
    //配送App
    @Transactional(readOnly = true)
    public List<StockOutOrderWrapper> findStockOutOrdersByTracker(AdminUser tracker) {

        List<OrderGroup> orderGroups = this.findOrderGroupsByOperator(tracker, 1, null);
        List<StockOutOrderWrapper> orders = new ArrayList<>();

        for (OrderGroup orderGroup : orderGroups) {
            for (StockOut stockOut : orderGroup.getStockOuts()) {
                if (StockOutStatus.HAVE_OUTBOUND.getValue().equals(stockOut.getStatus()) || StockOutStatus.FINISHED.getValue().equals(stockOut.getStatus())) {
                    orders.add(new StockOutOrderWrapper(stockOut));
                }
            }
        }
        return orders;
    }

    //配送App详情
    @Transactional(readOnly = true)
    public StockOutOrderWrapper getStockOutOrderById(Long id) {

        StockOut stockOut = stockOutService.getOneStockOut(id);
        return new StockOutOrderWrapper(stockOut);
    }

    //出库前判断未配货
    @Transactional
    public Set<String> beforeConfirmOutAll(StockOutData stockOutData) {

        Set<String> skuNames = new HashSet<>();
        for (Long stockOutId : stockOutData.getStockOutIds()) {
            StockOut stockOut = stockOutService.getOneStockOut(stockOutId);
            if (stockOut == null || !StockOutStatus.IN_STOCK.getValue().equals(stockOut.getStatus())) {
                continue;
            }

            for (StockOutItem stockOutItem : stockOut.getStockOutItems()) {
                if (StockOutItemStatus.UNDISTRIBUTED.getValue().equals(stockOutItem.getStatus())) {
                    skuNames.add(stockOutItem.getSku().getName());
                }
            }
        }
        return skuNames;
    }
}
