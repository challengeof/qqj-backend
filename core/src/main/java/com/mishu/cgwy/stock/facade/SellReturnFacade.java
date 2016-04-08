package com.mishu.cgwy.stock.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.OrderNotExistException;
import com.mishu.cgwy.error.UserDefinedException;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.SellReturnItemRequest;
import com.mishu.cgwy.order.controller.SellReturnRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.service.OrderItemService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.dto.SellReturnQueryRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.SellReturnService;
import com.mishu.cgwy.stock.service.StockInService;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.stock.wrapper.SellReturnReasonWrapper;
import com.mishu.cgwy.stock.wrapper.SellReturnWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnItemWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnWrapper;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by wangwei on 15/10/12.
 */
@Service
public class SellReturnFacade {

    @Autowired
    private SellReturnService sellReturnService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private StockInService stockInService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private DepotService depotService;

    @Autowired
    private CouponService couponService;

    private static final String SELLRETURN_TEMPLATE = "/template/sellReturn-list.xls";
    private static final String SELLRETURNITEM_TEMPLATE = "/template/sellReturnItem-list.xls";

    @Transactional(readOnly = true)
    public SellReturnWrapper getSellReturn(Long id) {
        return new SellReturnWrapper(sellReturnService.getSellReturn(id));
    }

    @Transactional
    public void updateSellReturn(Long id, Integer status, String auditOpinion, AdminUser operator) {
        SellReturn sellReturn = sellReturnService.getSellReturn(id);
        if (!SellReturnStatus.PENDINGAUDIT.getValue().equals(sellReturn.getStatus())) {
            throw new UserDefinedException("退货单" + sellReturn.getId() + "状态已改变");
        }
        sellReturn.setStatus(SellReturnStatus.fromInt(status).getValue());
        sellReturn.setAuditor(operator);
        sellReturn.setAuditDate(new Date());
        sellReturn.setAuditOpinion(auditOpinion);
        sellReturnService.saveSellReturn(sellReturn);

        if (SellReturnStatus.EXECUTION.getValue().equals(sellReturn.getStatus())) {

            stockInService.createStockIn(sellReturn);

            Map<String, SellReturnItem> sellReturnItemMap = new HashMap<>();
            for (SellReturnItem sellReturnItem : sellReturn.getSellReturnItems()) {
                String key = new StringBuffer(sellReturnItem.getSku().getId().toString()).append("_").append(sellReturnItem.isBundle()).toString();
                sellReturnItemMap.put(key, sellReturnItem);
            }

            Order order = sellReturn.getOrder();
            if (!sellReturnItemMap.isEmpty()) {
                int statusCount = 0;
                for (OrderItem orderItem : order.getOrderItems()) {
                    String key = new StringBuffer(orderItem.getSku().getId().toString()).append("_").append(orderItem.isBundle()).toString();
                    if (sellReturnItemMap.containsKey(key)) {
                        SellReturnItem sellReturnItem = sellReturnItemMap.get(key);
                        orderItem.setSellReturnQuantity(sellReturnItem.getQuantity() + orderItem.getSellReturnQuantity());
                    }
                    if (orderItem.getCountQuantity() - orderItem.getSellCancelQuantity() - orderItem.getSellReturnQuantity() == 0) {
                        statusCount ++;
                    }
                }

                if (statusCount == order.getOrderItems().size()) {
                    order.setStatus(OrderStatus.RETURNED.getValue());
                }
                order.setRealTotal(order.getRealTotal().subtract(sellReturn.getAmount()));
                if (order.getRealTotal().compareTo(BigDecimal.ZERO) < 0) {
                    order.setRealTotal(BigDecimal.ZERO);
                    //往期退货小于优惠券金额时候，自动将优惠券设为作废
                    if (!order.getCustomerCoupons().isEmpty()) {
                        Set<CustomerCoupon> coupons = order.getCustomerCoupons();
                        for (CustomerCoupon coupon : coupons) {
                            coupon.setStatus(CouponStatus.INVALID.getValue());
                            couponService.saveCustomerCoupon(coupon);
                        }
                    }
                }
                orderService.saveOrder(order);
            }
        }
    }

    @Transactional(readOnly = true)
    public List<SellReturnReasonWrapper> getSellReturnReasons() {
        List<SellReturnReasonWrapper> list = new ArrayList<>();
        for (SellReturnReason sellReturnReason : sellReturnService.getSellReturnReasonList()) {
            list.add(new SellReturnReasonWrapper(sellReturnReason));
        }
        return list;
    }

    @Transactional(readOnly = true)
    public QueryResponse<SimpleSellReturnWrapper> getSellReturn(SellReturnQueryRequest request, AdminUser adminUser) {
        Page<SellReturn> page = sellReturnService.getSellReturn(request, adminUser);
        QueryResponse<SimpleSellReturnWrapper> response = new QueryResponse<>();
        List<SimpleSellReturnWrapper> list = new ArrayList<>();
        for (SellReturn sellReturn : page) {
            list.add(new SimpleSellReturnWrapper(sellReturn));
        }
        response.setContent(list);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional(readOnly = true)
    public QueryResponse<SimpleSellReturnItemWrapper> getSellReturnItem(SellReturnQueryRequest request, AdminUser adminUser) {
        Page<SellReturnItem> page = sellReturnService.getSellReturnItem(request, adminUser);
        QueryResponse<SimpleSellReturnItemWrapper> response = new QueryResponse<>();
        List<SimpleSellReturnItemWrapper> list = new ArrayList<>();
        for (SellReturnItem sellReturnItem : page.getContent()) {
            list.add(new SimpleSellReturnItemWrapper(sellReturnItem));
        }
        response.setContent(list);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    /**
     * 往期退货
     * @param request
     * @param adminUser
     */
    @Transactional
    public void createOrderSellReturn(SellReturnRequest request, AdminUser adminUser) {
        Order order = orderService.getOrderById(request.getOrderId());
        if (order == null) {
            throw new OrderNotExistException();
        }

        Boolean hasUnFinishedSellReturn = false;
        for (SellReturn sellReturn : order.getSellReturns()) {
            if (sellReturn.getStatus() == SellReturnStatus.PENDINGAUDIT.getValue()) {
                hasUnFinishedSellReturn = true;
                break;
            }
        }
        if (!OrderStatus.COMPLETED.getValue().equals(order.getStatus()) || hasUnFinishedSellReturn) {
            throw new UserDefinedException("订单" + order.getId() + "状态已改变");
        }

        SellReturn sellReturn = new SellReturn();
        sellReturn.setCreator(adminUser);
        sellReturn.setOrder(order);
        sellReturn.setType(SellReturnType.PAST.getValue());
        sellReturn.setStatus(SellReturnStatus.PENDINGAUDIT.getValue());
        sellReturn.setCreateDate(new Date());

        List<SellReturnItem> sellReturnItems = new ArrayList<>();
        BigDecimal amount = BigDecimal.ZERO;
        for (SellReturnItemRequest returnItem : request.getSellReturnItemRequests()) {
            OrderItem orderItem = orderItemService.getOrderItem(returnItem.getOrderItemId());
            int availableQuantity = orderItem.getCountQuantity() - orderItem.getSellReturnQuantity() - orderItem.getSellCancelQuantity();
            if (returnItem.getQuantity() > availableQuantity) {
                returnItem.setQuantity(availableQuantity);
            }
            if (returnItem.getQuantity() == 0) {
                break;
            }
            SellReturnItem sellReturnItem = new SellReturnItem();
            sellReturnItem.setSku(orderItem.getSku());
            if (returnItem.getReasonId() != null) {
                sellReturnItem.setSellReturnReason(sellReturnService.getSellReturnReason(returnItem.getReasonId()));
            }
            if (returnItem.getMemo() != null) {
                sellReturnItem.setMemo(returnItem.getMemo());
            }
            sellReturnItem.setQuantity(returnItem.getQuantity());
            sellReturnItem.setBundle(orderItem.isBundle());
            if (orderItem.isBundle()) {
                sellReturnItem.setPrice(orderItem.getPrice().divide(BigDecimal.valueOf(orderItem.getSku().getCapacityInBundle()), 6, BigDecimal.ROUND_HALF_UP));
            } else {
                sellReturnItem.setPrice(orderItem.getPrice());
            }
            sellReturnItem.setAvgCost(orderItem.getAvgCost());
            sellReturnItem.setTaxRate(getStockOutItemTaxRate(orderItem));
            sellReturnItem.setSellReturn(sellReturn);
            sellReturnItems.add(sellReturnItem);
            amount = sellReturnItem.getPrice().multiply(BigDecimal.valueOf(sellReturnItem.getQuantity())).add(amount);
        }

        if (!sellReturnItems.isEmpty()) {
            List<SellReturn> oldSellReturns = order.getSellReturns();
            BigDecimal availableCount = order.getRealTotal();
            if (null != oldSellReturns && !oldSellReturns.isEmpty()) {
                for (SellReturn temp : oldSellReturns) {
                    if (temp.getStatus() == SellReturnStatus.EXECUTION.getValue() && temp.getType() == SellReturnType.PAST.getValue()) {
                        availableCount = availableCount.subtract(temp.getAmount());
                    }
                }
            }
            availableCount =  availableCount.compareTo(BigDecimal.ZERO) > 0 ? availableCount : BigDecimal.ZERO;
            sellReturn.setSellReturnItems(sellReturnItems);
            amount = availableCount.compareTo(amount) > 0 ? amount : availableCount;
            sellReturn.setAmount(amount);
            sellReturn.setDepot(order.getCustomer().getBlock().getWarehouse().getDepot());
            sellReturnService.saveSellReturn(sellReturn);
            order.getSellReturns().add(sellReturn);
        }
    }

    //TODO 这里去出库单上税率，可能不准
    private BigDecimal getStockOutItemTaxRate(OrderItem orderItem) {
        StockOut stockOuts = stockOutService.getStockOutByOrderId(orderItem.getOrder().getId());
        if (stockOuts != null) {
            for (StockOutItem stockOutItem : stockOuts.getStockOutItems()) {
                if (stockOutItem.getSku().getId().equals(orderItem.getSku().getId()) && stockOutItem.isBundle() == orderItem.isBundle()) {
                    return stockOutItem.getTaxRate();
                }
            }
        }
        return BigDecimal.ZERO;
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSellReturnList(SellReturnQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<SellReturn> page = sellReturnService.getSellReturn(request, operator);
        List<SellReturnWrapper> list = new ArrayList<>();
        for (SellReturn sellReturn : page) {
            list.add(new SellReturnWrapper(sellReturn));
        }
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : locationService.getCity(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotService.findOne(request.getDepotId()).getName());
        beans.put("sellReturnType", request.getType() == null ? "全部" : SellReturnType.fromInt(request.getType()).getName());
        beans.put("sellReturnStatus", request.getStatus() == null ? "全部" : SellReturnStatus.fromInt(request.getStatus()).getName());
        beans.put("orderStartDate", request.getStartDate() == null ? "全部" : DateFormatUtils.format(request.getStartDate(), "yyyy-MM-dd"));
        beans.put("orderEndDate", request.getEndDate() == null ? "全部" : DateFormatUtils.format(request.getEndDate(), "yyyy-MM-dd"));
        beans.put("list", list);
        beans.put("now", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        beans.put("operator", operator);
        String fileName = String.format("stockReturnList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SELLRETURN_TEMPLATE);
    }

    @Transactional(readOnly = true)
    public HttpEntity<byte[]> exportSellReturnItemList(SellReturnQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);
        request.setPageSize(Integer.MAX_VALUE);
        Page<SellReturnItem> page = sellReturnService.getSellReturnItem(request, operator);
        List<SimpleSellReturnItemWrapper> list = new ArrayList<>();
        for (SellReturnItem sellReturnItem : page) {
            list.add(new SimpleSellReturnItemWrapper(sellReturnItem));
        }
        Map<String, Object> beans = new HashMap<>();
        beans.put("city", request.getCityId() == null ? "全部" : locationService.getCity(request.getCityId()).getName());
        beans.put("depot", request.getDepotId() == null ? "全部" : depotService.findOne(request.getDepotId()).getName());
        beans.put("sellReturnType", request.getType() == null ? "全部" : SellReturnType.fromInt(request.getType()).getName());
        beans.put("sellReturnStatus", request.getStatus() == null ? "全部" : SellReturnStatus.fromInt(request.getStatus()).getName());
        beans.put("orderStartDate", request.getStartDate() == null ? "全部" : DateFormatUtils.format(request.getStartDate(), "yyyy-MM-dd"));
        beans.put("orderEndDate", request.getEndDate() == null ? "全部" : DateFormatUtils.format(request.getEndDate(), "yyyy-MM-dd"));
        beans.put("list", list);
        beans.put("now", DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        beans.put("operator", operator);
        String fileName = String.format("stockReturnItemList-%s.xls", DateFormatUtils.format(new Date(), "yyyy-MM-dd"));

        return ExportExcelUtils.generateExcelBytes(beans, fileName, SELLRETURNITEM_TEMPLATE);
    }
}
