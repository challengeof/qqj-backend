package com.mishu.cgwy.stock.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.OrderNotExistException;
import com.mishu.cgwy.error.OrderStatusHasChangedException;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.constants.SellCancelReason;
import com.mishu.cgwy.order.controller.SellCancelItemRequest;
import com.mishu.cgwy.order.controller.SellCancelRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.facade.LegacyOrderFacade;
import com.mishu.cgwy.order.service.OrderItemService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.CustomerCouponWrapper;
import com.mishu.cgwy.order.wrapper.PromotionWrapper;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.SellCancel;
import com.mishu.cgwy.stock.domain.SellCancelItem;
import com.mishu.cgwy.stock.domain.SellCancelType;
import com.mishu.cgwy.stock.dto.SellCancelCheckPromotionResponse;
import com.mishu.cgwy.stock.dto.SellCancelQueryRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.SellCancelService;
import com.mishu.cgwy.stock.wrapper.SellCancelItemWrapper;
import com.mishu.cgwy.stock.wrapper.SellCancelWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellCancelItemWrapper;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Created by wangwei on 15/10/15.
 */
@Service
public class SellCancelFacade {

    @Autowired
    private SellCancelService sellCancelService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private LegacyOrderFacade legacyOrderFacade;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private StockOutFacade stockOutFacade;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepotService depotService;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private CouponService couponService;

    private static final String SELLCANCEL_TEMPLATE = "/template/sellCancel-list.xls";
    private static final String SELLCANCELITEM_TEMPLATE = "/template/sellCancelItem-list.xls";


    /**
     * admin sellCancel check promotion
     * @param request
     * @return
     */
    public SellCancelCheckPromotionResponse checkSellCancelPromotionAndCoupon(@RequestBody SellCancelRequest request) {

        Order order = orderService.getOrderById(request.getOrderId());
        if (order == null) {
            throw new OrderNotExistException();
        }

        Order tempOrder = cloneTempCancelOrder(order, request);

        List<PromotionWrapper> promotionWrappers = new ArrayList<>();
        Set<Promotion> promotions = order.getPromotions();
        for (Promotion promotion : promotions) {
            boolean b = promotionService.couldOfferApplyToOrder(promotion, tempOrder, order.getCustomer().getBlock().getWarehouse(), order.getOrganization());
            if (!b) {
                promotionWrappers.add(new PromotionWrapper(promotion));
            }
        }

        List<CustomerCouponWrapper> couponWrappers = new ArrayList<>();
        for (CustomerCoupon customerCoupon : order.getCustomerCoupons()) {
            boolean b = couponService.couldOfferApplyToTempOrder(customerCoupon, tempOrder, order.getCustomer().getBlock().getWarehouse(), order.getCustomer().getBlock());
            if (!b) {
                couponWrappers.add(new CustomerCouponWrapper(customerCoupon));
            }
        }
        SellCancelCheckPromotionResponse response = new SellCancelCheckPromotionResponse();
        response.setPromotions(promotionWrappers);
        response.setCustomerCoupons(couponWrappers);
        return response;
    }

    private Order cloneTempCancelOrder(Order order, SellCancelRequest request) {
        Order tempOrder = new Order();

        for (OrderItem orderItem : order.getOrderItems()) {
            OrderItem tempOrderItem = new OrderItem();
            tempOrderItem.setSku(orderItem.getSku());
            tempOrderItem.setPrice(orderItem.getPrice());
            tempOrderItem.setBundle(orderItem.isBundle());
            if (orderItem.isBundle()) {
                tempOrderItem.setBundleQuantity((orderItem.getCountQuantity() - orderItem.getSellCancelQuantity()) / orderItem.getSku().getCapacityInBundle());
                tempOrderItem.setCountQuantity(tempOrderItem.getBundleQuantity() * orderItem.getSku().getCapacityInBundle());
            } else {
                tempOrderItem.setSingleQuantity(orderItem.getCountQuantity() - orderItem.getSellCancelQuantity());
                tempOrderItem.setCountQuantity(tempOrderItem.getSingleQuantity());
            }
            for (SellCancelItemRequest sellCancelItemRequest : request.getSellCancelItemRequest()) {
                if (orderItem.isBundle()== sellCancelItemRequest.getBundle() && orderItem.getSku().getId().equals(sellCancelItemRequest.getSkuId())) {
                    if (orderItem.isBundle()) {
                        tempOrderItem.setBundleQuantity(tempOrderItem.getBundleQuantity() - sellCancelItemRequest.getQuantity());
                        tempOrderItem.setCountQuantity(tempOrderItem.getBundleQuantity() * orderItem.getSku().getCapacityInBundle());
                    } else {
                        tempOrderItem.setSingleQuantity(tempOrderItem.getSingleQuantity() - sellCancelItemRequest.getQuantity());
                        tempOrderItem.setCountQuantity(tempOrderItem.getSingleQuantity());
                    }
                }
            }
            tempOrder.getOrderItems().add(tempOrderItem);
        }
        tempOrder.calculateSubTotal();
        tempOrder.calculateTotal();
        tempOrder.setSubmitDate(order.getSubmitDate());
        return tempOrder;
    }


    /**
     * admin sellCancel 主动取消
     *  接口处取消数量 打包按照打包，单品按照单品最小单位，内部统一通过单品总数计算
     * @param request
     * @param adminUser
     */
    @Transactional
    public void createSellCancel(SellCancelRequest request, AdminUser adminUser) {
        Order order = orderService.getOrderById(request.getOrderId());
        Integer orderStatus = order.getStatus();
        if (order == null) {
            throw new OrderNotExistException();
        }

        if (order.getStatus() != OrderStatus.COMMITTED.getValue() && order.getStatus() != OrderStatus.DEALING.getValue()) {
            throw new UserDefinedException("订单" + order.getId() + "状态已改变");
        }
        BigDecimal promotionMoney = order.getSubTotal().subtract(order.getTotal());
        Order tempOrder = cloneTempCancelOrder(order, request);

        SellCancel sellCancel = new SellCancel();
        sellCancel.setOrder(order);
        sellCancel.setCreateDate(new Date());
        sellCancel.setCreator(adminUser);
        sellCancel.setType(SellCancelType.CUSTOMER_CANCEL.getValue());


        List<SellCancelItem> sellCancelItems = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (SellCancelItemRequest cancelItem : request.getSellCancelItemRequest()) {
            OrderItem orderItem = orderItemService.getOrderItem(cancelItem.getOrderItemId());
            int availableQuantity = orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity();
            if (orderItem.isBundle()) {
                cancelItem.setQuantity(cancelItem.getQuantity() * orderItem.getSku().getCapacityInBundle());
            }
            if (cancelItem.getQuantity() > availableQuantity) {
                cancelItem.setQuantity(availableQuantity);
            }
            if (cancelItem.getQuantity() == 0) {
                continue;
            }
            SellCancelItem sellCancelItem = new SellCancelItem();
            sellCancelItem.setQuantity(cancelItem.getQuantity());
            sellCancelItem.setSku(orderItem.getSku());
            sellCancelItem.setBundle(orderItem.isBundle());
            sellCancelItem.setMemo(cancelItem.getMemo());
            sellCancelItem.setReason(cancelItem.getReasonId());
            if (orderItem.isBundle()) {
                sellCancelItem.setPrice(orderItem.getPrice().divide(BigDecimal.valueOf(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP));
            } else {
                sellCancelItem.setPrice(orderItem.getPrice());
            }
            sellCancelItem.setSellCancel(sellCancel);
            sellCancelItems.add(sellCancelItem);

            amount = sellCancelItem.getPrice().multiply(BigDecimal.valueOf(sellCancelItem.getQuantity())).add(amount);
            orderItem.setSellCancelQuantity(orderItem.getSellCancelQuantity() + cancelItem.getQuantity());
            orderItem.setOrder(order);
        }

        if (!sellCancelItems.isEmpty()) {
            sellCancel.setAmount(amount);
            sellCancel.setSellCancelItems(sellCancelItems);
            sellCancel.setType(request.getType());
            sellCancelService.saveSellCancel(sellCancel);

            int allcanceldItem = 0;
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getCountQuantity() == orderItem.getSellCancelQuantity()) {
                    allcanceldItem++;
                }
            }
            if (allcanceldItem == order.getOrderItems().size()) {
                order.setCancelDate(new Date());
                order.setStatus(OrderStatus.CANCEL.getValue());
            }

            //检查优惠
            Set<Promotion> promotions = order.getPromotions();
            for (Promotion promotion : promotions) {
                boolean b = promotionService.couldOfferApplyToOrder(promotion, tempOrder, order.getCustomer().getBlock().getWarehouse(), order.getOrganization());
                if (!b) {
                    promotions.remove(promotion);
                }
            }
            Set<CustomerCoupon> coupons = order.getCustomerCoupons();
            for (CustomerCoupon customerCoupon : coupons) {
                boolean b = couponService.couldOfferApplyToTempOrder(customerCoupon, tempOrder, order.getCustomer().getBlock().getWarehouse(), order.getCustomer().getBlock());
                if (!b) {
                    coupons.remove(customerCoupon);
                    customerCoupon.setStatus(CouponStatus.UNUSED.getValue());
                    customerCoupon.setUseDate(null);
                    couponService.saveCustomerCoupon(customerCoupon);
                }
            }
            order.calculateTotal();
            order.calculateSubTotal();
            order.calculateRealTotal();
            order = orderService.saveOrder(order);

            if (orderStatus.equals(OrderStatus.DEALING.getValue()) && SellCancelType.CUSTOMER_CANCEL.getValue().equals(request.getType())) {
                stockOutFacade.stockOutCancelByOrder(order, sellCancelItems);
            }
        }
    }


    /**
     * admin sellCancel 缺货取消
     *  接口处，内部 取消数量 统一通过单品总数计算，不重新计算订单
     * @param request
     * @param adminUser
     */
    @Transactional
    public void createDepotSellCancel(SellCancelRequest request, AdminUser adminUser) {
        Order order = orderService.getOrderById(request.getOrderId());
        Integer orderStatus = order.getStatus();
        if (order == null) {
            throw new OrderNotExistException();
        }

        if (order.getStatus() != OrderStatus.COMMITTED.getValue() && order.getStatus() != OrderStatus.DEALING.getValue()) {
            throw new UserDefinedException("订单" + order.getId() + "状态已改变");
        }

        SellCancel sellCancel = new SellCancel();
        sellCancel.setOrder(order);
        sellCancel.setCreateDate(new Date());
        sellCancel.setCreator(adminUser);
        sellCancel.setType(SellCancelType.CUSTOMER_CANCEL.getValue());


        List<SellCancelItem> sellCancelItems = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (SellCancelItemRequest cancelItem : request.getSellCancelItemRequest()) {
            OrderItem orderItem = orderItemService.getOrderItem(cancelItem.getOrderItemId());
            int availableQuantity = orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity();
            cancelItem.setQuantity(cancelItem.getQuantity());
            if (cancelItem.getQuantity() > availableQuantity) {
                cancelItem.setQuantity(availableQuantity);
            }
            if (cancelItem.getQuantity() == 0) {
                continue;
            }
            SellCancelItem sellCancelItem = new SellCancelItem();
            sellCancelItem.setQuantity(cancelItem.getQuantity());
            sellCancelItem.setSku(orderItem.getSku());
            sellCancelItem.setBundle(orderItem.isBundle());
            sellCancelItem.setMemo(cancelItem.getMemo());
            sellCancelItem.setReason(cancelItem.getReasonId());
            if (orderItem.isBundle()) {
                sellCancelItem.setPrice(orderItem.getPrice().divide(BigDecimal.valueOf(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP));
            } else {
                sellCancelItem.setPrice(orderItem.getPrice());
            }
            sellCancelItem.setSellCancel(sellCancel);
            sellCancelItems.add(sellCancelItem);

            amount = sellCancelItem.getPrice().multiply(BigDecimal.valueOf(sellCancelItem.getQuantity())).add(amount);
            orderItem.setSellCancelQuantity(orderItem.getSellCancelQuantity() + cancelItem.getQuantity());
            orderItem.setOrder(order);
        }

        if (!sellCancelItems.isEmpty()) {
            sellCancel.setAmount(amount);
            sellCancel.setSellCancelItems(sellCancelItems);
            sellCancel.setType(request.getType());
            sellCancelService.saveSellCancel(sellCancel);

            int allcanceldItem = 0;
            for (OrderItem orderItem : order.getOrderItems()) {
                if (orderItem.getCountQuantity() == orderItem.getSellCancelQuantity()) {
                    allcanceldItem++;
                }
            }
            if (allcanceldItem == order.getOrderItems().size()) {
                order.setCancelDate(new Date());
                order.setStatus(OrderStatus.CANCEL.getValue());
            }

            order.calculateTotal();
            order.calculateSubTotal();
            order.calculateRealTotal();
            //全部缺货取消，优惠券变为可用
            if (order.getRealTotal().equals(BigDecimal.ZERO) && !order.getCustomerCoupons().isEmpty()) {
                Set<CustomerCoupon> coupons = order.getCustomerCoupons();
                for (CustomerCoupon coupon : coupons) {
                    coupons.remove(coupon);
                    coupon.setStatus(CouponStatus.UNUSED.getValue());
                    coupon.setUseDate(null);
                    couponService.saveCustomerCoupon(coupon);
                }
            }
            order = orderService.saveOrder(order);

            if (orderStatus.equals(OrderStatus.DEALING.getValue()) && SellCancelType.CUSTOMER_CANCEL.getValue().equals(request.getType())) {
                stockOutFacade.stockOutCancelByOrder(order, sellCancelItems);
            }
        }
    }


    /**
     * web sellCancel
     * @param orderId
     * @param customer
     */
    @Transactional
    public void createSellCancel(Long orderId, String deviceId, Customer customer) {
        Order order = orderService.getOrderById(orderId);
        Integer orderStatus = order.getStatus();
        if (order == null) {
            throw new OrderNotExistException();
        }

        if (order.getStatus() != OrderStatus.COMMITTED.getValue() && order.getStatus() != OrderStatus.DEALING.getValue()) {
            throw new OrderStatusHasChangedException();
        }

        SellCancel sellCancel = new SellCancel();
        sellCancel.setOrder(order);
        sellCancel.setCreateDate(new Date());
        sellCancel.setCustomer(customer);
        sellCancel.setType(SellCancelType.CUSTOMER_SELF_CANCEL.getValue());

        List<SellCancelItem> sellCancelItems = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;

        for (OrderItem orderItem : order.getOrderItems()) {
            SellCancelItem item = new SellCancelItem();
            int availableQuantity = orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity();
            item.setQuantity(availableQuantity);
            if (item.getQuantity() == 0) {
                continue;
            }
            item.setSku(orderItem.getSku());
            if (orderItem.isBundle()) {
                item.setPrice(orderItem.getPrice().divide(BigDecimal.valueOf(item.getSku().getCapacityInBundle()), 6, RoundingMode.HALF_UP));
            } else {
                item.setPrice(orderItem.getPrice());
            }
            item.setBundle(orderItem.isBundle());
            item.setReason(SellCancelReason.NOT_WANT_TO_BUY.getValue());
            item.setSellCancel(sellCancel);
            sellCancelItems.add(item);
            amount = item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())).add(amount);
            orderItem.setSellCancelQuantity(orderItem.getCountQuantity());
            orderItem.setOrder(order);
        }

        if (!sellCancelItems.isEmpty()) {
            sellCancel.setAmount(amount);
            sellCancel.setSellCancelItems(sellCancelItems);
            sellCancelService.saveSellCancel(sellCancel);
        }

        order.setCancelDate(new Date());
        order.setCancelDeviceId(deviceId);
        order.setStatus(OrderStatus.CANCEL.getValue());

        //取消优惠
        Set<Promotion> promotions = order.getPromotions();
        for (Promotion promotion : promotions) {
            promotions.remove(promotion);
        }
        Set<CustomerCoupon> coupons = order.getCustomerCoupons();
        for (CustomerCoupon customerCoupon : coupons) {
            coupons.remove(customerCoupon);
            customerCoupon.setStatus(CouponStatus.UNUSED.getValue());
            customerCoupon.setUseDate(null);
            couponService.saveCustomerCoupon(customerCoupon);
        }
        order.calculateTotal();
        order.calculateSubTotal();
        order.calculateRealTotal();
        order = orderService.saveOrder(order);

        if (orderStatus.equals(OrderStatus.DEALING.getValue())) {
            stockOutFacade.stockOutCancelByOrder(order, sellCancelItems);
        }

    }

    @Transactional
    public void createDepotSellCancel(Order order, List<SellCancelItemRequest> cancelItems, AdminUser operator) {
        SellCancelRequest request = new SellCancelRequest();
        request.setOrderId(order.getId());
        request.setType(SellCancelType.DEPOT_CANCEL.getValue());
        if (!cancelItems.isEmpty()) {
            for (OrderItem orderItem : order.getOrderItems()) {
                for (SellCancelItemRequest item : cancelItems) {
                    if (orderItem.getSku().getId().equals(item.getSkuId()) && orderItem.isBundle() == item.getBundle().booleanValue()) {
                        item.setOrderItemId(orderItem.getId());
                        request.getSellCancelItemRequest().add(item);
                        cancelItems.remove(item);
                        break;
                    }
                }
            }
        }
        createDepotSellCancel(request, operator);
    }

    @Transactional(readOnly = true)
    public QueryResponse<SellCancelWrapper> getSellCancelList(SellCancelQueryRequest request, AdminUser operator) {
        Page<SellCancel> page = sellCancelService.getSellCancelList(request, operator);
        List<SellCancelWrapper> list = new ArrayList<>();
        for (SellCancel sellCancel : page.getContent()) {
            list.add(new SellCancelWrapper(sellCancel));
        }
        QueryResponse<SellCancelWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public QueryResponse<SimpleSellCancelItemWrapper> getSellCancelItemList(SellCancelQueryRequest request, AdminUser operator) {
        Page<SellCancelItem> page = sellCancelService.getSellCancelItemList(request, operator);
        List<SimpleSellCancelItemWrapper> list = new ArrayList<>();
        for (SellCancelItem sellCancelItem : page.getContent()) {
            list.add(new SimpleSellCancelItemWrapper(sellCancelItem));
        }
        QueryResponse<SimpleSellCancelItemWrapper> res = new QueryResponse<>();
        res.setContent(list);
        res.setPage(request.getPage());
        res.setPageSize(request.getPageSize());
        res.setTotal(page.getTotalElements());
        return res;
    }

    @Transactional(readOnly = true)
    public SellCancelWrapper getSellCancelById(Long id) {
        SellCancel sellCancel = sellCancelService.getSellCancel(id);
        SellCancelWrapper sellCancelWrapper = new SellCancelWrapper(sellCancel);
        List<SellCancelItemWrapper> itemWrappers = new ArrayList<>();
        for (SellCancelItem item : sellCancel.getSellCancelItems()) {
            itemWrappers.add(new SellCancelItemWrapper(item));
        }
        sellCancelWrapper.setSellCancelItems(itemWrappers);
        return sellCancelWrapper;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSellCancelList(SellCancelQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<SellCancel> page = sellCancelService.getSellCancelList(request, operator);
        List<SellCancelWrapper> list = new ArrayList<>();
        for (SellCancel sellReturn : page) {
            list.add(new SellCancelWrapper(sellReturn));
        }
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : locationService.getCity(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotService.findOne(request.getDepotId()).getName());
        beans.put("sellCancelType", request.getType() == null ? "全部" : SellCancelType.fromInt(request.getType()).getName());
        beans.put("orderStartDate", request.getStartDate() == null ? "全部" : DateFormatUtils.format(request.getStartDate(), "yyyy-MM-dd"));
        beans.put("orderEndDate", request.getEndDate() == null ? "全部" : DateFormatUtils.format(request.getEndDate(), "yyyy-MM-dd"));
        beans.put("list", list);
        beans.put("now", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        beans.put("operator", operator);
        String fileName = String.format("stockCancelList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SELLCANCEL_TEMPLATE);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSellCancelItemList(SellCancelQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<SellCancelItem> page = sellCancelService.getSellCancelItemList(request, operator);
        List<SimpleSellCancelItemWrapper> list = new ArrayList<>();
        for (SellCancelItem sellCancelItem : page) {
            list.add(new SimpleSellCancelItemWrapper(sellCancelItem));
        }
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : locationService.getCity(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotService.findOne(request.getDepotId()).getName());
        beans.put("sellCancelType", request.getType() == null ? "全部" : SellCancelType.fromInt(request.getType()).getName());
        beans.put("orderStartDate", request.getStartDate() == null ? "全部" : DateFormatUtils.format(request.getStartDate(), "yyyy-MM-dd"));
        beans.put("orderEndDate", request.getEndDate() == null ? "全部" : DateFormatUtils.format(request.getEndDate(), "yyyy-MM-dd"));
        beans.put("list", list);
        beans.put("now", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        beans.put("operator", operator);
        String fileName = String.format("stockCancelItemList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SELLCANCELITEM_TEMPLATE);
    }
}
