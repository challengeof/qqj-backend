package com.mishu.cgwy.order.facade;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminRole;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.admin.vo.AdminRoleVo;
import com.mishu.cgwy.admin.vo.AdminUserVo;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.conf.domain.ConfEnum;
import com.mishu.cgwy.conf.service.ConfService;
import com.mishu.cgwy.coupon.constant.CouponConstant;
import com.mishu.cgwy.coupon.constant.PromotionConstant;
import com.mishu.cgwy.coupon.domain.CouponStatus;
import com.mishu.cgwy.coupon.domain.CustomerCoupon;
import com.mishu.cgwy.coupon.service.CouponService;
import com.mishu.cgwy.error.ActiveRestaurantNotExistsException;
import com.mishu.cgwy.error.ErrorCode;
import com.mishu.cgwy.error.OrderLimitException;
import com.mishu.cgwy.error.PurchaseQuantityExcessException;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.message.CouponSenderEnum;
import com.mishu.cgwy.message.PromotionMessage;
import com.mishu.cgwy.message.PromotionMessageSender;
import com.mishu.cgwy.operating.skipe.domain.SpikeItem;
import com.mishu.cgwy.operating.skipe.facade.SpikeFacade;
import com.mishu.cgwy.operating.skipe.service.SpikeCacheService;
import com.mishu.cgwy.operating.skipe.service.SpikeService;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeItemWrapper;
import com.mishu.cgwy.operating.skipe.wrapper.SpikeWrapper;
import com.mishu.cgwy.order.constants.*;
import com.mishu.cgwy.order.controller.*;
import com.mishu.cgwy.order.controller.OrderRequest;
import com.mishu.cgwy.order.domain.*;
import com.mishu.cgwy.order.dto.*;
import com.mishu.cgwy.order.exception.OutOfStockException;
import com.mishu.cgwy.order.exception.SpikeOutOfStockException;
import com.mishu.cgwy.order.service.CutOrderService;
import com.mishu.cgwy.order.service.DateProcessorHandle;
import com.mishu.cgwy.order.service.OrderItemService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.*;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.product.controller.SkuTagQueryRequest;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.domain.SkuStatus;
import com.mishu.cgwy.product.facade.SkuTagFacade;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.service.SkuService;
import com.mishu.cgwy.product.wrapper.*;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.*;
import com.mishu.cgwy.stock.facade.SellCancelFacade;
import com.mishu.cgwy.stock.service.SellCancelService;
import com.mishu.cgwy.stock.service.SellReturnService;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.stock.wrapper.SellCancelItemWrapper;
import com.mishu.cgwy.stock.wrapper.SellCancelWrapper;
import com.mishu.cgwy.stock.wrapper.SellReturnWrapper;
import com.mishu.cgwy.stock.wrapper.SimpleSellReturnItemWrapper;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import com.mishu.cgwy.utils.LegacyOrderUtils;
import com.mishu.cgwy.utils.OrderUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang.time.DateUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.Tuple;
import javax.persistence.TypedQuery;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author chengzheng
 */
@Service
public class OrderFacade {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ContextualInventoryService contextualInventoryService;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private CouponService couponService;
    @Autowired
    private PromotionMessageSender promotionMessageSender;
    @Autowired
    private OrganizationService organizationService;
    @Autowired
    private SellCancelFacade sellCancelFacade;
    @Autowired
    private ConfService confService;
    @Autowired
    private LegacyOrderFacade legacyOrderFacade;
    @Autowired
    private CutOrderService cutOrderService;
    @Autowired
    private StockOutService stockOutService;
    @Autowired
    private SellReturnService sellReturnService;
    @Autowired
    private SellCancelService sellCancelService;
    @Autowired
    private PromotionService promotionService;
    @Autowired
    private SkuTagFacade skuTagFacade;
    @Autowired
    private SkuService skuService;

    @Autowired
    private OrderItemService orderItemService;

    @Autowired
    private SpikeService spikeService;
    @Autowired
    private SpikeCacheService spikeCacheService;

    @Autowired
    private SpikeFacade spikeFacade;

    private Logger logger = LoggerFactory.getLogger(OrderFacade.class);

    public EvaluateWrapper getEvaluateByOrder(Long orderId) {

        Evaluate evaluate = orderService.getEvaluateByOrder(orderId);
        EvaluateWrapper evaluateWrapper = new EvaluateWrapper();
        if (evaluate != null) {
            evaluateWrapper = new EvaluateWrapper(evaluate);
        }
        return evaluateWrapper;
    }

    enum OrderEvaluateExcelHeader {

        ORDER_ID("订单 ID"), SUBMIT_DATE("下单 时间"), ADMIN_NAME("客服 名称"), TRACKER_NAME("司机 名称"), PRODUCT_QUALITY("商品 质量"), DELIVERY_SPEED("送货 速度"), DELIVERY_EVALUATE("配送评价"), OTHER_INFO("其他信息");

        private String name;

        OrderEvaluateExcelHeader(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }

        public static OrderEvaluateExcelHeader indexOf(String name) {
            for (int i = 0; i < values().length; i++) {
                if (values()[i].getName().equals(name)) {
                    return values()[i];
                }
            }
            return null;
        }
    }

    //获取最大的订单
    @Transactional(readOnly = true)
    public List<PcOrderResponse> findMaxOrders() {

        List<PcOrderResponse> list = new ArrayList<>();
        OrderQueryRequest request = new OrderQueryRequest();
        request.setPageSize(50);
        request.setStart(DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), -2));
        request.setEnd(DateUtils.addDays(DateUtils.truncate(new Date(), Calendar.DAY_OF_MONTH), -1));
        Page<Order> page = orderService.findMaxOrders(request);

        for (Order order : page) {
            PcOrderResponse response = new PcOrderResponse();
            Restaurant restaurant = order.getRestaurant();
            response.setId(order.getId());
            response.setName(restaurant.getName());
            response.setPhone(restaurant.getTelephone());
            response.setTotalMoney(order.getSubTotal());
            list.add(response);
        }
        return list;
    }


    @Transactional(readOnly = true)
    public OrderQueryResponse findOrders(OrderQueryRequest request, AdminUser operator) {

        //TODO 仓库暂时全部为自营，如果仓库有值，全部为自营
        if (request.getDepotId() != null) {
            request.setOrganizationId(organizationService.getDefaultOrganization().getId());
        }
        Page<Order> page = orderService.findOrders(request, operator);
        List<SimpleOrderWrapper> simpleOrderWrappers = new ArrayList<>();
        for (Order order : page) {
            simpleOrderWrappers.add(new SimpleOrderWrapper(order));
        }

        OrderQueryResponse result = new OrderQueryResponse();
        OrderStatistics orderStatistics = getOrderStatistics(request, operator);
        result.setOrderStatistics(orderStatistics);
        result.setOrders(simpleOrderWrappers);
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(page.getTotalElements());

        return result;
    }

    @Transactional(readOnly = true)
    public OrderQueryResponse findOrdersByCustomer(OrderQueryRequest request, Customer customer) {

        OrderQueryResponse response = new OrderQueryResponse();
        Page<Order> page = orderService.findOrdersByCustomer(request, customer);
        List<SimpleOrderWrapper> simpleOrderWrappers = new ArrayList<>();
        for (Order order : page) {
            simpleOrderWrappers.add(new SimpleOrderWrapper(order));
        }
        response.setOrders(simpleOrderWrappers);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    @Transactional
    public void updateOrder(Long id, OrderUpdateRequest request, AdminUser operator) {

        Order order = orderService.getOrderById(id);
        PermissionCheckUtils.checkOrderUpdatePermission(order, operator);
        if (request.getStatus() != null) {
            order.setStatus(request.getStatus());
            if (request.getStatus().equals(OrderStatus.CANCEL.getValue())) {
                logger.warn(operator.getRealname() + "更新订单取消订单");
            }
        }
        if (StringUtils.isNotBlank(request.getMemo()) && StringUtils.isNotBlank(request.getNewMemo())) {
            order.setMemo(order.getMemo() + "\n" + request.getNewMemo());
        } else if (StringUtils.isBlank(request.getMemo()) && StringUtils.isNotBlank(request.getNewMemo())) {
            order.setMemo(request.getNewMemo());
        }
        orderService.saveOrder(order);
    }

    @Transactional
    public void updateFulfillment(Long id, FulfillmentRequest request, AdminUser operator) {

        Order order = orderService.getOrderById(id);
        PermissionCheckUtils.checkFulfillmentUpdatePermission(order, operator);
        // 目前只有减免运费，暂不支持增加运费
        if (Boolean.TRUE.equals(request.getFreeShipping())) {
            order.setShipping(BigDecimal.ZERO);
            order.calculateSubTotal();
        }
        // 目前只有立刻配送，暂不支持延迟配送
        if (Boolean.TRUE.equals(request.getScheduleNow())) {
            order.setExpectedArrivedDate(new Date());
        }
        // TODO log
        orderService.saveOrder(order);
    }



    @Transactional(readOnly = true)
    public HttpEntity<byte[]> itemExport(final OrderItemQueryRequest request, final AdminUser adminUser) throws Exception {


        Map<String, Object> params = new HashMap<>();
        params.put("now", new Date());

        return ExportExcelUtils.generateExcelBytes(new ExportExcelUtils.ExportIterator<ExportExcelUtils.ExportDataVo<Map>>() {

            private int page=0;
            private int pageSize=20000;
            @Override
            protected ExportExcelUtils.ExportDataVo<Map> getNextVal() {

                request.setPage(this.page);
                request.setPageSize(pageSize);
                OrderItemQueryResponse result = OrderFacade.this.findOrderItems(request, adminUser);
                page++;

                Map<String, Object> pageVal = null;
                if(result.getOrderItems()!=null && result.getOrderItems().size()!=0){
                    pageVal = new HashMap<String, Object>();
                    pageVal.put("list",result.getOrderItems());
                }
                return new ExportExcelUtils.ExportDataVo<Map>( "page"+page, pageVal);
            }
        },"orderItem",params,"order-item.xls", ExportExcelUtils.ORDER_ITEM_LIST_TEMPLATE,true);


    }


    @Transactional(readOnly = true)
    public OrderItemQueryResponse findOrderItems(OrderItemQueryRequest request, AdminUser operator) {

        Page<OrderItem> page = orderService.findOrderItems(request, operator);
        List<OrderItemWrapper> orderItemWrappers = new ArrayList<>();
        for (OrderItem orderItem : page) {
            orderItemWrappers.add(new OrderItemWrapper(orderItem));
        }
        OrderItemQueryResponse result = new OrderItemQueryResponse();
        result.setOrderItems(orderItemWrappers);
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());
        result.setTotal(page.getTotalElements());
        return result;
    }

    /**
     * admin
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public OrderWrapper adminGetOrderById(Long id) {

        final Order order = orderService.getOrderById(id);
        final OrderWrapper orderWrapper = new OrderWrapper(order);
        /*List<SimpleOrderItemWrapper> orderItemWrappers = orderWrapper.getOrderItems();
        List<Long> skuIds = new ArrayList<>(Collections2.transform(orderItemWrappers, new Function<SimpleOrderItemWrapper, Long>() {
            @Override
            public Long apply(SimpleOrderItemWrapper input) {
                return input.getSku().getId();
            }
        }));*/

        //sellCancelItem合并
        Map<String, SellCancelItemWrapper> sellCancelItemMap = new HashMap<>();
        for (SellCancel sellCancel : order.getSellCancels()) {
            for (SellCancelItem sellCancelItem : sellCancel.getSellCancelItems()) {
                StringBuffer key = new StringBuffer(sellCancelItem.getSku().getId().toString()).append("_").append(sellCancelItem.isBundle()).append("_").append(sellCancel.getType());
                if (sellCancelItemMap.containsKey(key.toString())) {
                    SellCancelItemWrapper sciw = sellCancelItemMap.get(key.toString());
                    sciw.setQuantity(sciw.getQuantity() + sellCancelItem.getQuantity());
                    sellCancelItemMap.put(key.toString(), sciw);
                } else {
                    sellCancelItemMap.put(key.toString(), new SellCancelItemWrapper(sellCancelItem));
                }
            }
        }
        orderWrapper.setSellCancelItems(new ArrayList<>(sellCancelItemMap.values()));

        //sellReturnItem合并
        Map<String, SimpleSellReturnItemWrapper> sellReturnItemMap = new HashMap<>();
        boolean havaUnFinishedSellCancel = false;
        for (SellReturn sellReturn : order.getSellReturns()) {
            if (SellReturnStatus.REFUSED.getValue().equals(sellReturn.getStatus())) {
                continue;
            }
            for (SellReturnItem sellReturnItem : sellReturn.getSellReturnItems()) {
                StringBuffer key = new StringBuffer(sellReturnItem.getSku().getId().toString()).append("_").append(sellReturnItem.isBundle()).append("_").append(sellReturn.getStatus());
                if (null != sellReturnItem.getSellReturnReason()) {
                    key.append("_").append(sellReturnItem.getId());
                }
                if (sellReturnItemMap.containsKey(key.toString())) {
                    SimpleSellReturnItemWrapper newSsri = sellReturnItemMap.get(key.toString());
                    newSsri.setQuantity(newSsri.getQuantity() + sellReturnItem.getQuantity());
                    sellReturnItemMap.put(key.toString(), newSsri);
                } else {
                    sellReturnItemMap.put(key.toString(), new SimpleSellReturnItemWrapper(sellReturnItem));
                }
            }
            if (SellReturnStatus.PENDINGAUDIT.getValue().equals(sellReturn.getStatus())) {
                havaUnFinishedSellCancel = true;
            }
            if (SellReturnStatus.PENDINGAUDIT.getValue().equals(sellReturn.getStatus())) {
                orderWrapper.setSellCancelId(sellReturn.getId());
            }
        }
        orderWrapper.setSellReturnItems(new ArrayList<>(sellReturnItemMap.values()));
        orderWrapper.setHavaUnFinishedSellCancel(havaUnFinishedSellCancel);

        final OrderGroup orderGroup = orderService.getOrderGroupByOrder(order);
        if (orderGroup != null) {
            Fulfillment fulfillment = new Fulfillment();
            fulfillment.setOrderGroupId(orderGroup.getId());
            if (null != orderGroup.getTracker()) {
                AdminUser tracker = orderGroup.getTracker();
                AdminUserVo adminUserVo = new AdminUserVo();
                adminUserVo.setId(tracker.getId());
                adminUserVo.setUsername(tracker.getUsername());
                adminUserVo.setTelephone(tracker.getTelephone());
                adminUserVo.setEnabled(tracker.isEnabled());
                adminUserVo.setRealname(tracker.getRealname());
                adminUserVo.setGlobalAdmin(tracker.isGlobalAdmin());

                if (!adminUserVo.isGlobalAdmin()) {
                    adminUserVo.setOrganizationId(tracker.getOrganizations().iterator().next().getId());
                }

                for (AdminRole role : tracker.getAdminRoles()) {
                    AdminRoleVo adminRoleVo = new AdminRoleVo();
                    adminRoleVo.setId(role.getId());
                    adminRoleVo.setName(role.getName());
                    adminRoleVo.setDisplayName(role.getDisplayName());
                    adminRoleVo.setOrganizationRole(role.isOrganizationRole());
                    adminUserVo.getAdminRoles().add(adminRoleVo);
                }

                fulfillment.setTracker(adminUserVo);
            }
            orderWrapper.setFulfillment(fulfillment);
        }
        return orderWrapper;
    }

    /**
     * web
     *
     * @param id
     * @return
     */
    @Transactional(readOnly = true)
    public OrderWrapper webGetOrderById(Long id) {

        final Order order = orderService.getOrderById(id);
        final OrderWrapper orderWrapper = new OrderWrapper(order);
        List<SimpleOrderItemWrapper> orderItemWrappers = orderWrapper.getOrderItems();
        if (!orderItemWrappers.isEmpty()) {
            List<Long> skuIds = new ArrayList<>(Collections2.transform(orderItemWrappers, new Function<SimpleOrderItemWrapper, Long>() {
                @Override
                public Long apply(SimpleOrderItemWrapper input) {
                    return input.getSku().getId();
                }
            }));
            Map<Long, DynamicSkuPrice> map = contextualInventoryService.getDynamicSkuPrices(skuIds, order.getCustomer().getBlock().getWarehouse().getId());
            for (SimpleOrderItemWrapper orderItemWrapper : orderItemWrappers) {
                SkuWrapper sku = orderItemWrapper.getSku();
                DynamicSkuPrice price = map.get(sku.getId());
                sku.setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(price.getSinglePriceStatus()));
                sku.setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(price.getBundlePriceStatus()));
            }
        }


        /*//sellCancelItem合并
        Map<String, SellCancelItemWrapper> sellCancelItemMap = new HashMap<>();
        for (SellCancel sellCancel : order.getSellCancels()) {
            for (SellCancelItem sellCancelItem : sellCancel.getSellCancelItems()) {
                StringBuffer key = new StringBuffer(sellCancelItem.getSku().getId().toString()).append("_").append(sellCancelItem.isBundle()).append("_").append(sellCancel.getType());
                if (sellCancelItemMap.containsKey(key.toString())) {
                    SellCancelItemWrapper sciw = sellCancelItemMap.get(key.toString());
                    sciw.setQuantity(sciw.getQuantity() + sellCancelItem.getQuantity());
                    sellCancelItemMap.put(key.toString(), sciw);
                } else {
                    sellCancelItemMap.put(key.toString(), new SellCancelItemWrapper(sellCancelItem));
                }
            }
        }
        orderWrapper.setSellCancelItems(new ArrayList<>(sellCancelItemMap.values()));

        //sellReturnItem合并
        Map<String, SimpleSellReturnItemWrapper> sellReturnItemMap = new HashMap<>();
        boolean havaUnFinishedSellCancel = false;
        for (SellReturn sellReturn : order.getSellReturns()) {
            if (SellReturnStatus.REFUSED.getValue().equals(sellReturn.getStatus())) {
                continue;
            }
            for (SellReturnItem sellReturnItem : sellReturn.getSellReturnItems()) {
                StringBuffer key = new StringBuffer(sellReturnItem.getSku().getId().toString()).append("_").append(sellReturnItem.isBundle()).append("_").append(sellReturn.getStatus());
                if (null != sellReturnItem.getSellReturnReason()) {
                    key.append("_").append(sellReturnItem.getId());
                }
                if (sellReturnItemMap.containsKey(key.toString())) {
                    SimpleSellReturnItemWrapper newSsri = sellReturnItemMap.get(key.toString());
                    newSsri.setQuantity(newSsri.getQuantity() + sellReturnItem.getQuantity());
                    sellReturnItemMap.put(key.toString(), newSsri);
                } else {
                    sellReturnItemMap.put(key.toString(), new SimpleSellReturnItemWrapper(sellReturnItem));
                }
            }
            if (SellReturnStatus.PENDINGAUDIT.getValue().equals(sellReturn.getStatus())) {
                havaUnFinishedSellCancel = true;
            }
            if (SellReturnStatus.PENDINGAUDIT.getValue().equals(sellReturn.getStatus())) {
                orderWrapper.setSellCancelId(sellReturn.getId());
            }
        }
        orderWrapper.setSellReturnItems(new ArrayList<>(sellReturnItemMap.values()));
        orderWrapper.setHavaUnFinishedSellCancel(havaUnFinishedSellCancel);

        final OrderGroup orderGroup = orderService.getOrderGroupByOrder(order);
        if (orderGroup != null) {
            Fulfillment fulfillment = new Fulfillment();
            fulfillment.setOrderGroupId(orderGroup.getId());
            if (null != orderGroup.getTracker()) {
                AdminUser tracker = orderGroup.getTracker();
                AdminUserVo adminUserVo = new AdminUserVo();
                adminUserVo.setId(tracker.getId());
                adminUserVo.setUsername(tracker.getUsername());
                adminUserVo.setTelephone(tracker.getTelephone());
                adminUserVo.setEnabled(tracker.isEnabled());
                adminUserVo.setRealname(tracker.getRealname());
                adminUserVo.setGlobalAdmin(tracker.isGlobalAdmin());

                if (!adminUserVo.isGlobalAdmin()) {
                    adminUserVo.setOrganizationId(tracker.getOrganizations().iterator().next().getId());
                }

                for (AdminRole role : tracker.getAdminRoles()) {
                    AdminRoleVo adminRoleVo = new AdminRoleVo();
                    adminRoleVo.setId(role.getId());
                    adminRoleVo.setName(role.getName());
                    adminRoleVo.setDisplayName(role.getDisplayName());
                    adminRoleVo.setOrganizationRole(role.isOrganizationRole());
                    adminUserVo.getAdminRoles().add(adminRoleVo);
                }

                fulfillment.setTracker(adminUserVo);
            }
            orderWrapper.setFulfillment(fulfillment);
        }*/
        return orderWrapper;
    }

    @Transactional(readOnly = true)
    public OrderInfoWrapper getOrderInfoById(Long orderId) {

        OrderInfoWrapper orderInfo = new OrderInfoWrapper(orderService.getOrderById(orderId));
        CutOrder cutOrder = cutOrderService.getCutOrderByOrderId(orderId);
        if (cutOrder != null) {
            orderInfo.setCutDate(cutOrder.getCutDate());
            orderInfo.setCutOperator(cutOrder.getOperator().getRealname());
        }
        StockOut stockOut = stockOutService.getStockOutByOrderId(orderId);
        if (stockOut != null) {
            orderInfo.setStockOutId(stockOut.getId());
            orderInfo.setStockOutDate(stockOut.getFinishDate());
            orderInfo.setStockOutOperator(stockOut.getSender() != null ? stockOut.getSender().getRealname() : "");
            orderInfo.setReceiveDate(stockOut.getReceiveDate());
            orderInfo.setReceiveOperator(stockOut.getReceiver() != null ? stockOut.getReceiver().getRealname() : "");
        }
        List<SellCancelWrapper> sellCancels = new ArrayList<>();
        for (SellCancel sellCancel : sellCancelService.getSellCancelByOrderId(orderId)) {
            sellCancels.add(new SellCancelWrapper(sellCancel));
        }
        orderInfo.setSellCancels(sellCancels);
        List<SellReturnWrapper> sellReturns = new ArrayList<>();
        for (SellReturn sellReturn : sellReturnService.getSellReturnByOrderId(orderId)) {
            sellReturns.add(new SellReturnWrapper(sellReturn));
        }
        orderInfo.setSellReturns(sellReturns);

        return orderInfo;
    }

    @Transactional(readOnly = true)
    public OrderListResponse getAdminOrderListById(AdminUser operator) {

        OrderListResponse orderListResponse = new OrderListResponse();
        OrderQueryRequest request = new OrderQueryRequest();
        request.setExpectedArrivedDate(DateUtils.truncate(new Date(), Calendar.DATE));
        request.setPageSize(Integer.MAX_VALUE);
        if (!operator.hasRole(AdminRole.CustomerServiceSupervisor)) {
            request.setAdminId(operator.getId());
        }
        List<Order> orders = orderService.findOrders(request, operator).getContent();
        List<OrderListData> orderListDatas = getOrderListDatas(orders, operator);
        orderListResponse.setRows(orderListDatas);
        return orderListResponse;
    }

    @Transactional(readOnly = true)
    public OrderListResponse getTodayNewOrders(AdminUser operator) {

        OrderListResponse orderListResponse = new OrderListResponse();
        OrderQueryRequest request = new OrderQueryRequest();
        request.setStart(getDateList().get(0));
        request.setEnd(getDateList().get(1));
        request.setPageSize(Integer.MAX_VALUE);
        if (!operator.hasRole(AdminRole.CustomerServiceSupervisor)) {
            request.setAdminId(operator.getId());
        }
        request.setStatus(OrderStatus.COMMITTED.getValue());
        List<Order> orders = orderService.findOrders(request, operator).getContent();
        List<OrderListData> orderListDatas = getOrderListDatas(orders, operator);
        orderListResponse.setTotal(orders.size());
        orderListResponse.setRows(orderListDatas);
        return orderListResponse;
    }

    @Transactional
    public OrderListResponse successOrder(AdminUser operator, OrderListRequest orderListRequest) {
        completeOrder(Long.valueOf(orderListRequest.getOrderNumber()), operator);
        return new OrderListResponse();
    }

    @Transactional
    public OrderListResponse handleOrder(OrderListRequest orderListRequest, AdminUser operator) {
        Order order = orderService.getOrderById(orderListRequest.getOrderId());
        //status等于2是处理,等于3是不处理
        if (orderListRequest.getStatus() == 2) {
            List<Refund> list = new ArrayList<>();
            for (ReturnDetail returnDetail : orderListRequest.getReturnDetail()) {
                // 生成一个退货订单
                Refund refund = new Refund();
                refund.setPrice(returnDetail.getPrice());
                //refund.setQuantity(returnDetail.getNumber());
                //refund.setTotalPrice(returnDetail.getPrice().multiply(new BigDecimal(returnDetail.getNumber())));
                refund.setTotalPrice(returnDetail.getPrice().multiply(new BigDecimal(returnDetail.getNumber())));
                refund.setOrder(order);
                refund.setSku(productService.getSku(returnDetail.getProductId()));
                if (returnDetail.getReCreate() == 1) {
                    list.add(refund);
                }
                orderService.saveRefund(refund);
            }
            if (!list.isEmpty()) {//reCreate :0是退货,1是换货.
                // 生成一个新的订单
                Order newOrder = new Order();
                copyOrder(order, newOrder, list);
                orderService.saveOrder(newOrder);
            }
        }
        completeOrder(orderListRequest.getOrderId(), operator);
        return new OrderListResponse();
    }

    @Transactional
    public OrderListResponse noExceptionOrder(Long orderId, AdminUser operator) {
        completeOrder(orderId, operator);
        return new OrderListResponse();
    }

    @Transactional
    public void copyOrder(Order oldOrder, Order newOrder, List<Refund> list) {

        newOrder.setCustomer(oldOrder.getCustomer());
        newOrder.setAdminUser(oldOrder.getCustomer().getAdminUser());
        newOrder.setMemo(oldOrder.getMemo());
        newOrder.setRestaurant(oldOrder.getRestaurant());
        newOrder.setSubmitDate(new Date());
        newOrder.setShipping(new BigDecimal(0));
        newOrder.setStatus(3);
        BigDecimal total = new BigDecimal(0);
        List<OrderItem> orderItems = new ArrayList<>();

        for (Refund refund : list) {
            OrderItem orderItem = new OrderItem();
            orderItem.setPrice(refund.getPrice());
            orderItem.setOrder(refund.getOrder());
            //orderItem.setQuantity(refund.getQuantity());
            orderItem.setSku(refund.getSku());
            orderItem.setTotalPrice(refund.getTotalPrice());
            orderItems.add(orderItem);
            total = total.add(refund.getTotalPrice());
        }
        newOrder.setTotal(total);
        newOrder.setSubTotal(total);
        newOrder.setOrderItems(orderItems);
    }

    public List<Date> getDateList() {

        List<Date> list = new ArrayList<>();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date1 = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, +1);
        Date date = calendar.getTime();
        try {
            list.add(simpleDateFormat.parse(simpleDateFormat.format(date1)));
            list.add(simpleDateFormat.parse(simpleDateFormat.format(date)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Deprecated
    @Transactional(readOnly = true)
    public OrderDetailResp getOrderDetailById(Long orderId) {

        Order order = orderService.getOrderById(orderId);
        OrderDetailResp orderDetailResp = new OrderDetailResp();
        orderDetailResp.setAdminName(order.getCustomer().getAdminUser().getRealname());
        orderDetailResp.setAdminTelephone(order.getCustomer().getAdminUser().getTelephone());
        if (orderService.getOrderGroupByOrder(order) != null) {
            orderDetailResp.setDriverTelephone(orderService.getOrderGroupByOrder(order).getTracker().getTelephone());
            //司机电话
            orderDetailResp.setCarNo(orderService.getOrderGroupByOrder(order).getTracker().getRealname());//车号
        } else {
            orderDetailResp.setDriverTelephone("");
            orderDetailResp.setCarNo("");
        }
        orderDetailResp.setOrderNumber(String.valueOf(order.getId()));
        orderDetailResp.setPayType("货到付款");
        orderDetailResp.setPrice(order.getTotal());
        orderDetailResp.setRealname(order.getRestaurant().getReceiver());
        orderDetailResp.setRestaurantNumber(String.valueOf(order.getRestaurant().getId()));
        if (order.getExpectedArrivedDate() != null) {
            orderDetailResp.setSendTime(DateProcessorHandle.format(order.getExpectedArrivedDate()));
        } else {
            orderDetailResp.setSendTime("");
        }
        orderDetailResp.setShippingFee(order.getShipping());
        orderDetailResp.setStatus(order.getStatus());
        orderDetailResp.setTelephone(order.getRestaurant().getTelephone());
        orderDetailResp.setCreateTime(DateProcessorHandle.format(order.getSubmitDate()));
        orderDetailResp.setName(order.getRestaurant().getName());
        orderDetailResp.setAddress(order.getRestaurant().getAddress().getAddress());
        orderDetailResp.setTraceInfo(new ArrayList<TraceInfo>());
        orderDetailResp.setOrderDetailList(LegacyOrderUtils.getSubItems(order));
        //退货不一定换货
        if (!order.getRefunds().isEmpty()) {
            List<ReturnList> returnLists = new ArrayList<>();
            for (Refund refund : order.getRefunds()) {
                ReturnList returnList = new ReturnList();
                returnList.setName(refund.getSku().getName());
                //returnList.setNumber(refund.getQuantity());
                returnList.setPrice(refund.getPrice());
                returnList.setProductNumber(String.valueOf(refund.getSku().getId()));
                returnList.setProductId(refund.getSku().getId());
//                final MediaFile mediaFile = refund.getSku().getProduct().getMediaFile();
                /*if (mediaFile != null) {
                    returnList.setUrl(mediaFile.getUrl());
                }*/
                returnLists.add(returnList);
            }
            orderDetailResp.setReturnList(returnLists);
        } else {
            orderDetailResp.setReturnList(new ArrayList<ReturnList>());
        }
        orderDetailResp.setChangeList(new ArrayList<ReturnList>());

        return orderDetailResp;
    }

    @Transactional
    public List<OrderListData> getOrderListDatas(List<Order> orders, AdminUser operator) {

        List<OrderListData> result = new ArrayList<>();
        for (Order order : orders) {
            OrderListData orderListData = new OrderListData();
            orderListData.setOrderNumber(String.valueOf(order.getId()));//订单编号
            orderListData.setCreateTime(DateProcessorHandle.format(order.getSubmitDate()));//创建时间
            orderListData.setRestaurantName(order.getRestaurant().getName());//餐厅名称
            orderListData.setStatus(order.getStatus());//订单状态
            orderListData.setAddress(order.getRestaurant().getAddress().getAddress());//餐厅地址
            orderListData.setRealname(order.getRestaurant().getReceiver());//接收人姓名
            orderListData.setTelephone(order.getRestaurant().getTelephone());//接收人电话
            orderListData.setAdminTelephone(order.getCustomer().getAdminUser().getTelephone());//客服电话
            orderListData.setAdminName(order.getCustomer().getAdminUser().getRealname());//客服姓名
            if (orderService.getOrderGroupByOrder(order) != null) {
                final AdminUser tracker = orderService.getOrderGroupByOrder(order).getTracker();
                if (tracker != null) {
                    orderListData.setDriverTelephone(tracker.getTelephone());//司机电话
                    orderListData.setCarNo(tracker.getRealname());//车号
                }
            } else {
                orderListData.setDriverTelephone("");
                orderListData.setCarNo("");
            }
            orderListData.setOrderId(order.getId());//订单ID
            orderListData.setRestaurantNumber(String.valueOf(order.getRestaurant().getId()));//餐厅编号
            orderListData.setPrice(Float.parseFloat(order.getTotal().toString()));//订单金额
            orderListData.setUserId(operator.getId());//用户id
            orderListData.setShipping(order.getShipping());//运费
            orderListData.setPayType("货到付款");//支付方式
            result.add(orderListData);
        }
        return result;
    }

    @Transactional(readOnly = true)
    public OrderListResponse getDriverOrderListById(AdminUser operator) {

        OrderListResponse orderListResponse = new OrderListResponse();
        List<OrderListData> orderListDatas = new ArrayList<>();
        List<OrderGroup> list = orderService.findOrderGroupsByTracker(new Date(), operator);
        for (OrderGroup orderGroup : list) {
            orderListDatas.addAll(getOrderListDatas(orderGroup.getMembers(), operator));
        }
        orderListResponse.setRows(orderListDatas);
        return orderListResponse;
    }

    @Transactional
    public void deliverOrder(Long id, AdminUser operator) {

        Order order = orderService.getOrderById(id);
        PermissionCheckUtils.checkOrderUpdatePermission(order, operator);
        if (order.getStatus() == OrderStatus.COMMITTED.getValue()) {
            order.setStatus(OrderStatus.SHIPPING.getValue());
            order.setExpectedArrivedDate(DateUtils.truncate(new Date(), Calendar.DATE));
        }
        orderService.saveOrder(order);
    }

    @Transactional(readOnly = true)
    public OrderGroupWrapper getOrderGroupByOrderId(Long orderId) {

        Order order = orderService.getOrderById(orderId);
        final OrderGroup orderGroupByOrder = orderService.getOrderGroupByOrder(order);
        if (orderGroupByOrder != null) {
            return new OrderGroupWrapper(orderGroupByOrder);
        } else {
            return null;
        }
    }

    @Transactional(readOnly = true)
    public OrderStatistics getOrderStatistics(OrderQueryRequest orderQueryRequest, AdminUser adminUser) {

        OrderStatistics orderStatistics = new OrderStatistics();
        if (orderService.sumOrderRealTotal(orderQueryRequest, adminUser) != null) {
            orderStatistics.setTotal(orderService.sumOrderRealTotal(orderQueryRequest, adminUser));
        } else {
            orderStatistics.setTotal(BigDecimal.ZERO);
        }
        orderStatistics.setFirstOrderCount(orderService.firstOrderCount(orderQueryRequest, adminUser));
        orderStatistics.setRestaurantCount(orderService.restaurantCount(orderQueryRequest, adminUser));
        return orderStatistics;
    }

    @Transactional
    public void cancelOrder(Long orderId, AdminUser operator) {

        Order order = orderService.getOrderById(orderId);
        SellCancelRequest request = new SellCancelRequest();
        request.setOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItems()) {
            SellCancelItemRequest cancelItem = new SellCancelItemRequest();
            cancelItem.setOrderItemId(orderItem.getId());
            cancelItem.setQuantity(orderItem.getCountQuantity());
            request.getSellCancelItemRequest().add(cancelItem);
        }
        sellCancelFacade.createSellCancel(request, operator);
    }

    @Transactional
    public void cancelOrder(CancelOrderReasonRequest reasonRequest, AdminUser operator) {

        Order order = orderService.getOrderById(reasonRequest.getOrderId());
        SellCancelRequest request = new SellCancelRequest();
        request.setOrderId(order.getId());
        for (OrderItem orderItem : order.getOrderItems()) {
            SellCancelItemRequest cancelItem = new SellCancelItemRequest();
            cancelItem.setOrderItemId(orderItem.getId());
            cancelItem.setQuantity(orderItem.getCountQuantity());
            cancelItem.setMemo(reasonRequest.getMemo());
            cancelItem.setReasonId(reasonRequest.getReasonId());
            cancelItem.setBundle(orderItem.isBundle());
            request.getSellCancelItemRequest().add(cancelItem);
        }
        sellCancelFacade.createSellCancel(request, operator);

        logger.info("operator " + operator.getId() + " try to cancel order " + reasonRequest.getOrderId());
        //Order order = orderService.getOrderById(reasonRequest.getOrderId());
        //PermissionCheckUtils.checkOrderUpdatePermission(order, operator);

        //if (order.getStatus() != OrderStatus.CANCEL.getValue()) {
        //    logger.warn("operator " + operator.getId() + " cancel order " + reasonRequest.getOrderId());
        //
        //    order.setStatus(OrderStatus.CANCEL.getValue());
        //    order.setCancelDate(new Date());
        //    if (reasonRequest.getReasonId() != null) {
        //
        //        order.setReason(reasonRequest.getReasonId());
        //    }
        //    if (reasonRequest.getMemo() != null) {
        //
        //        order.setMemo(reasonRequest.getMemo());
        //    }
        //}

        logger.info(operator.getRealname() + "admin取消订单");
        //orderService.saveOrder(order);
    }

    @Transactional
    public void completeOrder(Long id, AdminUser operator) {

        Order order = orderService.getOrderById(id);
        PermissionCheckUtils.checkOrderUpdatePermission(order, operator);
        if (order.getStatus() == OrderStatus.SHIPPING.getValue()) {
            order.setStatus(OrderStatus.COMPLETED.getValue());
            order.setCompleteDate(new Date());
        }
        orderService.saveOrder(order);
        PromotionMessage message = new PromotionMessage(CouponSenderEnum.COMPLETE_ORDER_SEND);
        message.setOrderId(order.getId());
        promotionMessageSender.sendMessage(message);
    }

    @Transactional(readOnly = true)
    public List<OrderWrapper> myOrderToday(Customer customer) {
        return new ArrayList<>(Collections2.transform(orderService.getOrderByCustomerAndSubmitDateAfter(customer, new Date()), new Function<Order, OrderWrapper>() {
            @Override
            public OrderWrapper apply(Order input) {
                OrderWrapper orderWrapper = new OrderWrapper(input);
                OrderGroup orderGroup = orderService.getOrderGroupByOrder(input);
                if (orderGroup != null) {
                    AdminUser tracker = orderGroup.getTracker();
                    AdminUserVo adminUserVo = new AdminUserVo();
                    adminUserVo.setId(tracker.getId());
                    adminUserVo.setUsername(tracker.getUsername());
                    adminUserVo.setTelephone(tracker.getTelephone());
                    adminUserVo.setEnabled(tracker.isEnabled());
                    adminUserVo.setRealname(tracker.getRealname());
                    adminUserVo.setGlobalAdmin(tracker.isGlobalAdmin());

                    if (!adminUserVo.isGlobalAdmin()) {
                        adminUserVo.setOrganizationId(tracker.getOrganizations().iterator().next().getId());
                    }

                    for (AdminRole role : tracker.getAdminRoles()) {
                        AdminRoleVo adminRoleVo = new AdminRoleVo();
                        adminRoleVo.setId(role.getId());
                        adminRoleVo.setName(role.getName());
                        adminRoleVo.setDisplayName(role.getDisplayName());
                        adminRoleVo.setOrganizationRole(role.isOrganizationRole());
                        adminUserVo.getAdminRoles().add(adminRoleVo);
                    }
                    orderWrapper.setTracker(adminUserVo);
                }
                return orderWrapper;
            }
        }));
    }

    @Transactional
    public OrderWrapper addSkuToCart(Long customerId, List<CartRequest> cartRequest, boolean append) {

        Customer customer = customerService.getCustomerById(customerId);
        Order cart = orderService.getCartByCustomer(customer);
        final Block block = customer.getBlock();
        Warehouse warehouse = null;
        if (block != null && block.getWarehouse() != null) {
            warehouse = block.getWarehouse();
        } else {
            warehouse = locationService.getDefaultWarehouse(customer.getCity().getId());
        }

        for (CartRequest r : cartRequest) {
            Integer quantity = r.getQuantity();
            Boolean bundle = null;
            Sku sku = null;
            SpikeItem spikeItem = null;
            if (null != r.getCartSkuType() && r.getCartSkuType() == CartSkuType.spike.val) {

                //秒杀活动的商品
                spikeItem = spikeService.getSpikeItem(r.getSpikeItemId());
                sku = spikeItem.getSku();
                bundle = spikeItem.isBundle();
            } else {
                sku = productService.getSku(r.getSkuId());
                bundle = r.isBundle();
            }

            OrderItem orderItem = null;
            for (OrderItem o : cart.getOrderItems()) {
                if (o.getSku().getId().equals(sku.getId()) && o.isBundle() == bundle &&
                        (   ( spikeItem!=null && o.getSpikeItem()!=null && o.getSpikeItem().getId().equals(spikeItem.getId()) )
                            || (spikeItem==null && o.getSpikeItem()==null)
                        )
                    ) {
                    orderItem = o;
                    break;
                }
            }
            if (orderItem != null) {
                if (append) {
                    if (orderItem.isBundle()) {
                        orderItem.setBundleQuantity(orderItem.getBundleQuantity() + quantity);
                        orderItem.setCountQuantity(orderItem.getBundleQuantity() * sku.getCapacityInBundle());
                    } else {
                        orderItem.setSingleQuantity(orderItem.getSingleQuantity() + quantity);
                        orderItem.setCountQuantity(orderItem.getSingleQuantity());
                    }
                } else {
                    if (orderItem.isBundle()) {
                        orderItem.setBundleQuantity(quantity);
                        orderItem.setCountQuantity(quantity * sku.getCapacityInBundle());
                    } else {
                        orderItem.setSingleQuantity(quantity);
                        orderItem.setCountQuantity(quantity);
                    }
                }
            } else {
                orderItem = new OrderItem();
                orderItem.setSku(sku);

                orderItem.setSpikeItem(spikeItem);
                if (bundle) {
                    orderItem.setBundleQuantity(orderItem.getBundleQuantity() + quantity);
                    orderItem.setCountQuantity(orderItem.getBundleQuantity() * sku.getCapacityInBundle());
                } else {
                    orderItem.setSingleQuantity(orderItem.getSingleQuantity() + quantity);
                    orderItem.setCountQuantity(orderItem.getSingleQuantity());
                }
                orderItem.setBundle(bundle);
                orderItem.setOrder(cart);
                cart.getOrderItems().add(orderItem);
            }
        }

        Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
        while (iterator.hasNext()) {
            OrderItem orderItem = iterator.next();
            if (orderItem.getSpikeItem() != null) {
                orderItem.setPrice(orderItem.getSpikeItem().getPrice());
            } else {
                DynamicSkuPrice dynamicSkuPrice = contextualInventoryService.getDynamicSkuPrice(orderItem.getSku().getId(), warehouse.getId());
                if (orderItem.getSku().getStatus() == SkuStatus.ACTIVE.getValue()) {
                    if (!orderItem.isBundle() && dynamicSkuPrice != null && dynamicSkuPrice.getSinglePriceStatus().isSingleInSale() && dynamicSkuPrice.getSinglePriceStatus().isSingleAvailable() && !dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice().equals(BigDecimal.ZERO)) {
                        orderItem.setPrice(dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice());
                    } else if (orderItem.isBundle() && dynamicSkuPrice != null && dynamicSkuPrice.getBundlePriceStatus().isBundleInSale() && dynamicSkuPrice.getBundlePriceStatus().isBundleAvailable() && !dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice().equals(BigDecimal.ZERO)) {
                        orderItem.setPrice(dynamicSkuPrice.getBundlePriceStatus().getBundleSalePrice());
                    } else {
                        iterator.remove();
                    }
                } else {
                    iterator.remove();
                }
            }
        }
        cart.calculateSubTotal();
        cart.calculateTotal();
        orderService.saveOrder(cart);
        return getCart(customer);
    }

    @Transactional
    public OrderWrapper removeSkuFromCart(Customer customer, List<Long> itemIds) {

        Order cart = orderService.getCartByCustomer(customer);
        Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
        while (iterator.hasNext()) {
            final OrderItem orderItem = iterator.next();
            if (itemIds.contains(orderItem.getId())) {
                iterator.remove();
            }
        }
        cart.calculateSubTotal();
        cart.calculateTotal();
        orderService.saveOrder(cart);
        return getCart(customer);
    }

    @Transactional
    public OrderWrapper getCart(Customer customer) {

        Order cart = orderService.getCartByCustomer(customer);
        cart.calculateSubTotal();
        // TODO bad
        BigDecimal subTotal = cart.getSubTotal();
        cart.setSubTotal(subTotal);
        cart.calculateTotal();
        orderService.saveOrder(cart);
        return new OrderWrapper(cart);
    }

    @Transactional
    public List<OrderWrapper> createOrderCoupon(Customer customer, CartAndCouponRequest cartAndCouponRequest) {
        return createOrder(customer, cartAndCouponRequest.getCartRequestList(), cartAndCouponRequest.getCouponId(), cartAndCouponRequest.getDeviceId());
    }

    public void incrSpikeLimitByCustomer(Customer customer, List<CartRequest> cartRequests) {
        //增量更新缓存中该用户参与秒杀商品的量
        for (CartRequest request : cartRequests) {
            if (request.getCartSkuType() != null && request.getCartSkuType() == CartSkuType.spike.val) {
                spikeCacheService.incrLimitByCustomer(customer, request.getSpikeItemId(), request.getQuantity());
            }
        }
    }

    public CartSkuStockOutWrapper cartRequestSpikeItemCheck(Customer customer, CartRequest request) {
        if (request.getCartSkuType() != null && request.getCartSkuType() == CartSkuType.spike.val) {
            //判断地域限制
            boolean inArea = spikeFacade.spikeInArea(customer, request.getSpikeItemId());
            // 检查 秒杀活动是否在进行中
            boolean isProcess = spikeFacade.spikeIsProcess(request.getSpikeItemId());
            // 判断秒杀商品剩余库存是否满足
            boolean hasNum = spikeCacheService.checkItemSurplus(request.getQuantity(), request.getSpikeItemId());
            // 判断秒杀活动按用户限量是否满足
            boolean hasCnum = spikeCacheService.checkItemLimitByCustomer(customer, request.getQuantity(), request.getSpikeItemId());

            if (!inArea || !isProcess || !hasNum || !hasCnum) {
                SpikeWrapper spike = spikeCacheService.getSpikeByItemId(request.getSpikeItemId());
                SpikeItemWrapper spikeItem = spikeCacheService.getSpikeItem(request.getSpikeItemId());
                Integer customerTakeNum = spikeCacheService.getCustomerSpikeTakeNum(customer, request.getSpikeItemId());

                CartSkuStockOutWrapper stockOutWrapper = CartSkuStockOutWrapper.createCartSkuStockOutWrapper(request.getQuantity(), customerTakeNum, spikeItem, spike, null);
                return stockOutWrapper;
            }
        }
        return null;
    }

    public void spikeItemNumCheck(Customer customer, CartRequest... cartRequests) {
        List<CartSkuStockOutWrapper> spikeStockOut = new ArrayList<>();
        for (CartRequest request : cartRequests) {
            CartSkuStockOutWrapper skuStockOutWrapper = this.cartRequestSpikeItemCheck(customer, request);
            if (skuStockOutWrapper != null) {
                spikeStockOut.add(skuStockOutWrapper);
            }
        }
        if (spikeStockOut.size() != 0) {
            throw new SpikeOutOfStockException(ErrorCode.SpikeSkuNotEnough, spikeStockOut); //秒杀商品库存不足
        }

    }


    private void checkOrderLimit(Customer customer, Order order) {

        // 如果存在已下单状态的订单，则可继续下单
        if (orderService.existsOrderCommitted(customer)) {
            return;
        }

        // 不存在已下单状态的订单，订单金额需要满足限制才能下单
        Map<String, String> confMap = null;
        try {
            confMap = confService.getConfMap(ConfEnum.ORDER_LIMIT.getName());
        } catch (Exception e) {
            confMap = new HashMap<>();
            logger.error(e.getMessage(), e);
        }

        City city = order.getCustomer().getCity();
        String limit = confMap.get(String.valueOf(city.getId()));
        BigDecimal limitValue = limit == null ? BigDecimal.ZERO : new BigDecimal(limit);
        if (order.getSubTotal().compareTo(limitValue) < 0) {
            throw new OrderLimitException(String.format("当日首单满%s元才能免费配送哦（当日后续下单无限制）", limitValue));
        }
    }

    @Transactional
    public List<OrderWrapper> createOrder(Customer customer, List<CartRequest> cartRequests, Long couponId, String deviceId) {

        checkSkuPurchaseQuantity(cartRequests, customer.getCity().getId(), customer);

        CustomerCoupon coupon = null;
        if (couponId != null) {
            coupon = couponService.getCustomerCoupon(couponId);
        }
        List<Order> orders = previewOrder(customer, cartRequests, coupon);
        for (Order order : orders) {
            if (order.getOrganization().getId() == 1) {
                checkOrderLimit(customer, order);
            }
        }
        for (Order order : orders) {
            order.setDeviceId(deviceId);
        }
        if (coupon != null) {
            coupon.setStatus(CouponStatus.USED.getValue());
            coupon.setUseDate(new Date());
        }

        Order cart = orderService.getCartByCustomer(customer);
        // update cart
        Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
        while (iterator.hasNext()) {
            OrderItem orderItem = iterator.next();
            for (CartRequest r : cartRequests) {
                if (r.getCartSkuType() != null && r.getCartSkuType() == CartSkuType.spike.val) {
                    if (orderItem.getSpikeItem() != null && orderItem.getSpikeItem().getId() == r.getSpikeItemId()) {
                        iterator.remove();
                        break;
                    }
                } else {
                    if (r.getSkuId().equals(orderItem.getSku().getId()) && r.isBundle() == orderItem.isBundle()) {
                        iterator.remove();
                        break;
                    }
                }
            }
        }
        List<CartSkuStockOutWrapper> spikeStockOut = new ArrayList<>();
        for (Order order : orders) {
            for (OrderItem item : order.getOrderItems()) {
                if (item.getSpikeItem() != null) {
                    int quantity = item.isBundle() ? item.getBundleQuantity() : item.getSingleQuantity();
                    int resultCnt = spikeFacade.increaseTakeNum(item.getSpikeItem().getId(), quantity);
                    if (resultCnt == 0) {

                        SpikeWrapper spike = spikeCacheService.getSpikeByItemId(item.getSpikeItem().getId());
                        SpikeItemWrapper spikeItem = spikeCacheService.getSpikeItem(item.getSpikeItem().getId());
                        Integer customerTakeNum = spikeCacheService.getCustomerSpikeTakeNum(customer, item.getSpikeItem().getId());
                        CartSkuStockOutWrapper cssoWrapper = CartSkuStockOutWrapper.createCartSkuStockOutWrapper(quantity, customerTakeNum, spikeItem, spike, null);
//                                new CartSkuStockOutWrapper(CartSkuType.spike.val, item.getSpikeItem().getId(),item.getSpikeItem().getNum(),item.getSpikeItem().getTakeNum(), sku);
                        spikeStockOut.add(cssoWrapper);
                    }
                }
            }
        }
        if (spikeStockOut.size() != 0) {
            throw new SpikeOutOfStockException(ErrorCode.SpikeSkuNotEnough, spikeStockOut); //秒杀商品不足
        }

        String replacer = "";
        if (RequestContextHolder.getRequestAttributes() != null) {
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            replacer = String.valueOf(request.getSession().getAttribute("AdminUserId"));
        }
        List<OrderWrapper> result = new ArrayList<>();
        for (Order order : orders) {
            reduceLimited(order.getPromotions());
            result.add(new OrderWrapper(orderService.saveOrder(order)));
            Restaurant restaurant = order.getRestaurant();
            restaurant.setLastPurchaseTime(order.getSubmitDate());
            restaurantService.save(restaurant);
            if (replacer.equals("null")) {
                order.setCustomerOperator(customer);
                logger.info("客户自己下单:" + order.getId());
            } else {
                String operatorId = replacer.substring(replacer.indexOf("x") + 1);
                AdminUser operator = adminUserService.getOne(Long.valueOf(operatorId));
                order.setAdminOperator(operator);
                logger.info("工作人员下单:" + order.getId() + "---工作人员Id:" + operatorId);
            }
        }

        cart = orderService.saveOrder(cart);
        for (Order order : orders) {
            for (OrderItem orderItem : order.getOrderItems()) {
                customerService.addFavorite(customer, orderItem.getSku());
            }
        }
        logger.info("Statistics_createOrder");
        return result;
    }

    @Transactional(readOnly = true)
    public List<CartSimpleSkuWrapper> checkStockOut(Customer customer, List<CartRequest> cartRequests) {

        final Block block = customer.getBlock();
        final City city = customer.getCity();
        Warehouse warehouse = null;
        if (block != null) {
            warehouse = block.getWarehouse();
        } else {
            warehouse = locationService.getDefaultWarehouse(city.getId());
        }
        List<CartSimpleSkuWrapper> stockOutSku = new ArrayList<>();
        for (CartRequest cartRequest : cartRequests) {
            Sku sku = productService.getSku(cartRequest.getSkuId());
            if (cartRequest.getCartSkuType() != null && cartRequest.getCartSkuType() == CartSkuType.spike.val) {
                CartSkuStockOutWrapper stockOutWrapper = this.cartRequestSpikeItemCheck(customer, cartRequest);
                if (null != stockOutWrapper) {
                    CartSimpleSkuWrapper simpleSkuWrapper = new CartSimpleSkuWrapper(sku, stockOutWrapper);
                    stockOutSku.add(simpleSkuWrapper);
                }
            } else {
                //暂时使用单品
                final boolean available = contextualInventoryService.isAvailable(sku, warehouse, cartRequest.isBundle());
                if (!available) {
                    //final int stock = contextualInventoryService.getStock(sku, warehouse);
                    //final SimpleSkuWrapper simpleSkuWrapper = new SimpleSkuWrapper(sku);
                    //simpleSkuWrapper.setStock(stock);
                    stockOutSku.add(new CartSimpleSkuWrapper(sku));
                }
            }
        }
        return stockOutSku;
    }

    @Transactional
    public List<OrderWrapper> previewOrderWrapper(Customer customer, List<CartRequest> cartRequests) {

        List<Order> orders = previewOrder(customer, cartRequests, null);
        return new ArrayList<>(Collections2.transform(orders, new Function<Order,
                OrderWrapper>() {
            @Override
            public OrderWrapper apply(Order input) {
                return new OrderWrapper(input);
            }
        }));
    }

    @Transactional(readOnly = true)
    public List<CustomerCoupon> findAvailableCoupons(Customer customer, List<CartRequest> cartRequests) {

        List<Order> orders = previewOrder(customer, cartRequests, null);
        List<CustomerCoupon> customerCoupons = new ArrayList<>();
        for (Order order : orders) {
            customerCoupons.addAll(couponService.findAvailableCoupons(customer, order));
        }
        return customerCoupons;
    }

    private List<Order> previewOrder(Customer customer, List<CartRequest> cartRequests, CustomerCoupon coupon) {

        List<Order> result = new ArrayList<>();
        Block block = customer.getBlock();
        if (block == null) {
            throw new ActiveRestaurantNotExistsException();
        }
        Warehouse warehouse = block.getWarehouse();
        final List<Restaurant> restaurantsByCustomer = customerService.getRestaurantsByCustomer(customer.getId());
        Restaurant restaurant = null;
        for (Restaurant r : restaurantsByCustomer) {
            if (r.getStatus() == RestaurantStatus.ACTIVE.getValue()) {
                restaurant = r;
                break;
            }
        }
        if (restaurant == null) {
            logger.warn("no active restaurant for customer " + customer.getId());
            throw new ActiveRestaurantNotExistsException();
        }
        Map<Long, Order> organizationOrderMap = new HashMap<>();
        List<CartSkuStockOutWrapper> stockOut = new ArrayList<>();
        for (CartRequest request : cartRequests) {
            Boolean bundle = null;
            Sku sku = null;
            SpikeItem spikeItem = null;
            if (null != request.getCartSkuType() && request.getCartSkuType() == CartSkuType.spike.val) {
                //秒杀活动的商品
                spikeItem = spikeService.getSpikeItem(request.getSpikeItemId());
                sku = spikeItem.getSku();
                bundle = spikeItem.isBundle();

            } else {
                bundle = request.isBundle();
                sku = productService.getSku(request.getSkuId());
                if (!contextualInventoryService.isAvailable(sku, customer.getBlock().getWarehouse(), bundle)) {
                    stockOut.add(new CartSkuStockOutWrapper(sku));
                }
            }

            Organization organization = sku.getProduct().getOrganization();
            Order order = organizationOrderMap.get(organization.getId());
            OrderItem ot = new OrderItem();
            ot.setSpikeItem(spikeItem);
            ot.setSku(sku);
            ot.setBundle(bundle);
            if (bundle) {
                ot.setBundleQuantity(request.getQuantity());
                ot.setCountQuantity(ot.getBundleQuantity() * sku.getCapacityInBundle());
            } else {
                ot.setSingleQuantity(request.getQuantity());
                ot.setCountQuantity(ot.getSingleQuantity());
            }
            if (order == null) {
                order = new Order();
                List<OrderItem> items = new ArrayList<>();
                order.setCustomer(customer);
                order.setAdminUser(customer.getAdminUser());
                final Date current = new Date();
                order.setSubmitDate(current);
                order.setExpectedArrivedDate(OrderUtils.getExpectedArrivedDate(current));
                order.setRestaurant(restaurant);
                order.setStatus(OrderStatus.COMMITTED.getValue());
                order.setSequence(orderService.getOrderCountByRestaurant(restaurant) + 1);
                order.setType(OrderType.APP_ORDER.getVal());
                items.add(ot);
                ot.setOrder(order);
                order.setOrderItems(items);
            } else {
                List<OrderItem> items = order.getOrderItems();
                ot.setOrder(order);
                items.add(ot);
                order.setOrderItems(items);
            }
            order.setOrganization(organization);
            organizationOrderMap.put(organization.getId(), order);
        }

        if (!stockOut.isEmpty()) {
            throw new OutOfStockException(stockOut);
        } else {
            long orderCount = orderService.getOrderCountByRestaurant(restaurant);
            if (!organizationOrderMap.isEmpty()) {
                Iterator<Long> iterator = organizationOrderMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Order order = organizationOrderMap.get(iterator.next());
                    order.setSequence(++orderCount);
                    contextualInventoryService.updateSalePrice(order, warehouse);
                    if (coupon != null && couponService.couldOfferApplyToOrder(coupon, order, warehouse, block)) {
                        order.getCustomerCoupons().add(coupon);
                    } else {
                        order.setPromotions(new HashSet<>(legacyOrderFacade.findApplicablePromotion(order, new Date(), warehouse, order.getOrganization())));
                    }
                    order.calculateSubTotal();
                    order.calculateTotal();
                    order.setRealTotal(order.getTotal());
                }
            }
            for (Order organizationOrder : organizationOrderMap.values()) {
                result.add(organizationOrder);
            }
        }
        return result;
    }

    @Transactional(readOnly = true)
    public OrderSearchSkusResponse getOrderSkus(OrderGroupQueryRequest request, AdminUser operator) {

        List<Order> orders = orderService.findByExpectedArrivedDateAndWarehouseId(request, operator);
        List<SimpleSkuWrapper> skus = new ArrayList<SimpleSkuWrapper>();
        OrderSearchSkusResponse response = new OrderSearchSkusResponse();
        List<Long> skuIds = new ArrayList<Long>();
        List<Long> stockOutRefunds = new ArrayList<Long>();
        for (Order order : orders) {
            List<Refund> refunds = order.getRefunds();
            for (Refund refund : refunds) {
                if (RefundType.STOCKLESS.equals(refund.getType())) {
                    stockOutRefunds.add(refund.getSku().getId());
                }
            }
        }

        for (Order order : orders) {
            List<OrderItem> items = order.getOrderItems();
            if (request.getSkuId() != null) {
                for (OrderItem ot : items) {
                    if (ot.getSku().getId().equals(request.getSkuId()) && !stockOutRefunds.contains(ot.getSku().getId())) {
                        skus.add(new SimpleSkuWrapper(ot.getSku()));
                        response.setSkus(skus);
                        return response;
                    }
                }
            } else {
                for (OrderItem ot : items) {
                    if (!skuIds.contains(ot.getSku().getId()) && !stockOutRefunds.contains(ot.getSku().getId())) {
                        skuIds.add(ot.getSku().getId());
                        skus.add(new SimpleSkuWrapper(ot.getSku()));
                    }
                }
            }
        }
        response.setSkus(skus);
        return response;
    }

    @Transactional(readOnly = true)
    public SkuSaleResponse findSkuSales(final SkuSalesRequest request, AdminUser operator) {

        SkuSaleResponse response = new SkuSaleResponse();
        TypedQuery<Tuple> listQuery = orderService.findSkuSaleStatistics(request, operator);
        response.setTotal(listQuery.getResultList().size());
        listQuery.setFirstResult(request.getPage() * request.getPageSize());
        listQuery.setMaxResults(request.getPage() * request.getPageSize() + request.getPageSize());
        List<Tuple> pages = listQuery.getResultList();
        List<SkuSaleWrapper> skuSale = new ArrayList<SkuSaleWrapper>();
        for (Tuple tuple : pages) {
            SkuSaleWrapper ssw = new SkuSaleWrapper();
            Sku sku = (Sku) tuple.get(0);
            ssw.setSkuId(sku.getId());
            ssw.setSingleSale((Long) tuple.get(1));
            ssw.setBundleSale((Long) tuple.get(2));
            ssw.setCountSale((Long) tuple.get(3));
            ssw.setSellCancel((Long) tuple.get(4));
            ssw.setSellReturn((Long) tuple.get(5));
            ssw.setSkuName(sku.getName());
            ssw.setCapacityInBundle(sku.getCapacityInBundle());
            skuSale.add(ssw);
        }
        response.setSkuSales(skuSale);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        return response;
    }

    @Transactional
    public EvaluateWrapper addEvaluate(Customer customer, Long orderId, EvaluateWrapper evaluate) {

        Evaluate data = new Evaluate();
        data.setMsg(evaluate.getMsg());
        data.setProductQualityScore(evaluate.getProductQualityScore());
        data.setTrackerServiceScore(evaluate.getTrackerServiceScore());
        data.setDeliverySpeedScore(evaluate.getDeliverySpeedScore());
        Order order = orderService.getOrderById(orderId);
        if (order != null) {
            order.setHasEvaluated(true);
            data.setCustomer(order.getCustomer());
            data.setAdminUser(order.getAdminUser());
        }
        data.setOrder(order);
        OrderGroup group = orderService.getOrderGroupByOrder(order);
        if (group != null) {
            data.setTracker(group.getTracker());
        }
        return new EvaluateWrapper(orderService.saveEvaluate(data));
    }

    @Transactional
    public OrderEvaluateResponse getEvaluates(OrderEvaluateSearchRequest request, AdminUser adminUser) throws Exception {

        OrderEvaluateResponse response = new OrderEvaluateResponse();
        List<OrderEvaluateWrapper> data = new ArrayList<>();
        Page<Evaluate> page = orderService.getEvaluate(request, adminUser);
        for (Evaluate evaluate : page) {


            data.add(new OrderEvaluateWrapper(evaluate));
        }
        response.setOrderEvaluates(data);
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());
        return response;
    }

    public File evaluateOrderExcelExport(OrderEvaluateSearchRequest request, AdminUser adminUser, String fileDir, String fileName) throws Exception {

        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        OrderEvaluateResponse response = getEvaluates(request, adminUser);
        List<OrderEvaluateWrapper> evaluates = response.getOrderEvaluates();
        Workbook wb = new HSSFWorkbook();
        Sheet sheet = wb.createSheet();
        Row firstRow = sheet.createRow(1);
        Map<OrderEvaluateExcelHeader, Integer> inputIndex = new HashMap<OrderEvaluateExcelHeader, Integer>();
        for (int i = 0; i < OrderEvaluateExcelHeader.values().length; i++) {
            inputIndex.put(OrderEvaluateExcelHeader.values()[i], i);
            Cell cell = firstRow.createCell(i);
            cell.setCellValue(OrderEvaluateExcelHeader.values()[i].name);
        }
        int rowIndex = 2;
        while (evaluates.size() != 0) {
            OrderEvaluateWrapper orderEvaluateWrapper = evaluates.get(0);
            Row row = sheet.createRow(rowIndex);
            rowIndex++;
            evaluates.remove(orderEvaluateWrapper);
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.ORDER_ID)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.ORDER_ID));
                cell.setCellValue(orderEvaluateWrapper.getOrderId());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.SUBMIT_DATE)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.SUBMIT_DATE));
                SimpleDateFormat formate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                cell.setCellValue(formate.format(orderEvaluateWrapper.getSubmitDate()));
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.ADMIN_NAME)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.ADMIN_NAME));
                cell.setCellValue(orderEvaluateWrapper.getAdminUserName());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.TRACKER_NAME)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.TRACKER_NAME));
                cell.setCellValue(orderEvaluateWrapper.getTrackerName());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.PRODUCT_QUALITY)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.PRODUCT_QUALITY));
                cell.setCellValue(orderEvaluateWrapper.getProductQualityScore());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.DELIVERY_SPEED)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.DELIVERY_SPEED));
                cell.setCellValue(orderEvaluateWrapper.getTrackerServiceScore());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.DELIVERY_EVALUATE)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.DELIVERY_EVALUATE));
                cell.setCellValue(orderEvaluateWrapper.getTrackerServiceScore());
            }
            if (inputIndex.containsKey(OrderEvaluateExcelHeader.OTHER_INFO)) {
                Cell cell = row.createCell(inputIndex.get(OrderEvaluateExcelHeader.OTHER_INFO));
                cell.setCellValue(orderEvaluateWrapper.getMsg());
            }
        }
        try {
            wb.write(out);
            if (null != out) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Transactional
    public List<Refund> findRefunds(Date submitDate, Warehouse warehouse) {

        List<Refund> refunds = new ArrayList<>();
        for (Refund refund : orderService.findRefundBySubmitDate(submitDate, warehouse)) {
            Refund ref = null;
            for (Refund refund1 : refunds) {
                if (refund.getSku().equals(refund1.getSku())) {
                    ref = refund1;
                    break;
                }
            }
            if (ref == null) {
                ref = new Refund();
                ref.setSku(refund.getSku());
                ref.setOrder(refund.getOrder());
                refunds.add(ref);
            }
            ref.setBundleQuantity(ref.getBundleQuantity() + refund.getBundleQuantity());
            ref.setSingleQuantity(ref.getSingleQuantity() + refund.getSingleQuantity());
        }
        return refunds;
    }

    enum OrderHeader {

        ORDER_ID("订单ID"), RESTAURANT_ID("餐馆ID"), RESTAURANT_NAME("餐馆名称"), SEQUENCE("第几单"), ORGANIZATION_NAME("店面"), TOTAL_PRICE("金额"), CREATE_TIME("下单时间"), STATUS("状态"), TELEPHONE("联系方式"), SERVICE_NAME("客服");

        private String name;

        OrderHeader(String name) {
            this.name = name;
        }

        String getName() {
            return name;
        }
    }

    @Transactional(readOnly = true)
    public File ordersExcelExport(OrderQueryRequest request, AdminUser operator, String fileDir, String fileName) {

        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, fileName);
        request.setPageSize(Integer.MAX_VALUE);
        Page<Order> page = orderService.findOrders(request, operator);
        List<Order> list = new ArrayList<>();
        list.addAll(page.getContent());
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Workbook wb = new HSSFWorkbook();
        Map<OrderHeader, Integer> mapIndex = new HashMap<>();
        int rowIndex = 0;
        int sheetIndex = 1;
        while (!list.isEmpty()) {
            if (rowIndex++ > 60000) {
                rowIndex = 0;
                sheetIndex++;
                continue;
            }
            Sheet sheet = wb.getSheet("订单" + sheetIndex);
            if (sheet == null) {
                sheet = wb.createSheet("订单" + sheetIndex);
                Row firstRow = sheet.createRow(0);
                for (int i = 0; i < OrderHeader.values().length; i++) {
                    mapIndex.put(OrderHeader.values()[i], i);
                    Cell cell = firstRow.createCell(i);
                    cell.setCellValue(OrderHeader.values()[i].name);
                }
            }
            Order order = list.get(0);
            Row row = sheet.createRow(rowIndex);
            list.remove(order);
            if (mapIndex.containsKey(OrderHeader.ORDER_ID)) {
                row.createCell(mapIndex.get(OrderHeader.ORDER_ID)).setCellValue(order.getId());
            }
            if (mapIndex.containsKey(OrderHeader.RESTAURANT_ID)) {
                row.createCell(mapIndex.get(OrderHeader.RESTAURANT_ID)).setCellValue(order.getRestaurant().getId());
            }
            if (mapIndex.containsKey(OrderHeader.RESTAURANT_NAME)) {
                row.createCell(mapIndex.get(OrderHeader.RESTAURANT_NAME)).setCellValue(order.getRestaurant().getName());
            }
            if (mapIndex.containsKey(OrderHeader.SEQUENCE)) {
                row.createCell(mapIndex.get(OrderHeader.SEQUENCE)).setCellValue(order.getSequence());
            }
            if (mapIndex.containsKey(OrderHeader.ORGANIZATION_NAME)) {
                row.createCell(mapIndex.get(OrderHeader.ORGANIZATION_NAME)).setCellValue(order.getOrganization().getName());
            }
            if (mapIndex.containsKey(OrderHeader.TOTAL_PRICE)) {
                row.createCell(mapIndex.get(OrderHeader.TOTAL_PRICE)).setCellValue(String.valueOf(order.getTotal()));
            }
            if (mapIndex.containsKey(OrderHeader.CREATE_TIME)) {
                row.createCell(mapIndex.get(OrderHeader.CREATE_TIME)).setCellValue(DateFormatUtils.format(order.getSubmitDate(), "yyyy-MM-dd HH:mm:ss"));
            }
            if (mapIndex.containsKey(OrderHeader.STATUS)) {
                row.createCell(mapIndex.get(OrderHeader.STATUS)).setCellValue(OrderStatus.fromInt(order.getStatus()).getName());
            }
            if (mapIndex.containsKey(OrderHeader.TELEPHONE)) {
                row.createCell(mapIndex.get(OrderHeader.TELEPHONE)).setCellValue(order.getRestaurant().getTelephone());
            }
            if (mapIndex.containsKey(OrderHeader.SERVICE_NAME)) {
                row.createCell(mapIndex.get(OrderHeader.SERVICE_NAME)).setCellValue(order.getCustomer().getAdminUser() != null ? order.getCustomer().getAdminUser().getRealname() : null);
            }
        }

        try {
            wb.write(out);
            if (null != out) {
                out.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    @Transactional
    public EvaluateResponse getEvaluateByTracker(AdminUser operator) {

        List<Evaluate> evaluates = orderService.getEvaluateByTracker(operator);
        DecimalFormat decimalFormat = new DecimalFormat("0.0");
        double deliverySpeedScore = 0;
        double trackerServiceScore = 0;
        for (Evaluate evaluate : evaluates) {
            deliverySpeedScore += evaluate.getDeliverySpeedScore();
            trackerServiceScore += evaluate.getTrackerServiceScore();
        }
        EvaluateResponse evaluateResponse = new EvaluateResponse();
        if (evaluates.size() != 0) {
            evaluateResponse.setDeliverySpeedScore(decimalFormat.format(deliverySpeedScore / evaluates.size()));
            evaluateResponse.setTrackerServiceScore(decimalFormat.format(trackerServiceScore / evaluates.size()));
        } else {
            evaluateResponse.setDeliverySpeedScore("0");
            evaluateResponse.setTrackerServiceScore("0");
        }
        return evaluateResponse;
    }

    @Transactional
    public List<OrderWrapper> createOrder(AdminUser operator, OrderCreateRequest orderCreateRequest) {

        Customer customer = customerService.getRestaurantById(orderCreateRequest.getRestaurantId()).getCustomer();
        List<OrderWrapper> result = new ArrayList<>();
        Block block = customer.getBlock();
        if (block == null) {
            throw new ActiveRestaurantNotExistsException();
        }
        Warehouse warehouse = block.getWarehouse();
        final List<Restaurant> restaurantsByCustomer = customerService.getRestaurantsByCustomer(customer.getId());
        Restaurant restaurant = null;
        for (Restaurant r : restaurantsByCustomer) {
            if (r.getStatus() == RestaurantStatus.ACTIVE.getValue()) {
                restaurant = r;
                break;
            }
        }
        if (restaurant == null) {
            logger.warn("no active restaurant for customer " + customer.getId());
            throw new ActiveRestaurantNotExistsException();
        }
        Map<Long, Order> organizationOrderMap = new HashMap<>();
        //List<SkuWrapper> stockOut = new ArrayList<>();
        for (OrderRequest request : orderCreateRequest.getRequests()) {
            Sku sku = productService.getSku(request.getSkuId());
            //TODO..
            //if (!contextualInventoryService.isAvailable(sku, customer.getBlock().getWarehouse(), request.isBundle())) {
            //    stockOut.add(new SkuWrapper(sku));
            //}
            Organization organization = sku.getProduct().getOrganization();
            Order order = organizationOrderMap.get(organization.getId());
            OrderItem ot = new OrderItem();
            if (orderCreateRequest.getType().equals(OrderType.GIFT.getVal())) {
                ot.setPrice(BigDecimal.ZERO);
                logger.info("donation for sku " + request.getSkuId());
            }
            ot.setSku(sku);
            ot.setBundle(request.isBundle());
            if (request.isBundle()) {
                ot.setBundleQuantity(request.getQuantity());
                ot.setCountQuantity(ot.getBundleQuantity() * sku.getCapacityInBundle());
            } else {
                ot.setSingleQuantity(request.getQuantity());
                ot.setCountQuantity(ot.getSingleQuantity());
            }
            if (order == null) {
                order = new Order();
                List<OrderItem> items = new ArrayList<>();
                order.setCustomer(customer);
                order.setAdminUser(customer.getAdminUser());
                final Date current = new Date();
                order.setSubmitDate(current);
                order.setExpectedArrivedDate(OrderUtils.getExpectedArrivedDate(current));
                order.setRestaurant(restaurant);
                order.setStatus(OrderStatus.COMMITTED.getValue());
                order.setSequence(orderService.getOrderCountByRestaurant(restaurant) + 1);
                order.setType(OrderType.find(orderCreateRequest.getType(), OrderType.NOMAL).getVal());
                items.add(ot);
                ot.setOrder(order);
                order.setOrderItems(items);
            } else {
                List<OrderItem> items = order.getOrderItems();
                ot.setOrder(order);
                items.add(ot);
                order.setOrderItems(items);
            }
            order.setOrganization(organization);
            organizationOrderMap.put(organization.getId(), order);
        }

        //if (!stockOut.isEmpty()) {
        //    throw new OutOfStockException(stockOut);
        //} else {
        long orderCount = orderService.getOrderCountByRestaurant(restaurant);
        if (!organizationOrderMap.isEmpty()) {
            Iterator<Long> iterator = organizationOrderMap.keySet().iterator();
            while (iterator.hasNext()) {
                Order order = organizationOrderMap.get(iterator.next());
                order.setSequence(++orderCount);
                if (orderCreateRequest.getType().equals(OrderType.NOMAL.getVal()) || orderCreateRequest.getType().equals(OrderType.OUTOFSTOCK.getVal())) {
                    contextualInventoryService.updateOrderItemPrice(order, warehouse);
                }
                order.setMemo(orderCreateRequest.getRemark());
                order.calculateSubTotal();
                order.calculateTotal();
                order.setRealTotal(order.getTotal());
            }
        }
        for (Order organizationOrder : organizationOrderMap.values()) {
            result.add(new OrderWrapper(organizationOrder));
            organizationOrder.setAdminOperator(operator);
            Order successOrder = orderService.save(organizationOrder);
            restaurant.setLastPurchaseTime(successOrder.getSubmitDate());
            restaurantService.save(restaurant);
            logger.info("background user for order" + successOrder.getId());
        }
        //}
        return result;
    }


    @Transactional
    public void reduceLimited(Set<Promotion> promotions) {

        for (Promotion promotion : promotions) {

            if (!promotion.getType().equals(PromotionConstant.FULL_MINUS.getType()) && promotion.getLimitedQuantity() != null) {

                int quantitySold = promotionService.reduceLimited(promotion.getId(), promotion.getPromotableItems().getQuantity());

                if (quantitySold <= 0) {
                    promotionService.invalidatePromotion(promotion.getId());
                    promotions.remove(promotion);
//                    throw new ExceedLimitedException();
                }
            }
        }
    }

    public void checkSkuPurchaseQuantity(List<CartRequest> requests, Long cityId, Customer customer) {

        Calendar currentDate = Calendar.getInstance();
        currentDate.set(Calendar.HOUR_OF_DAY, 0);
        currentDate.set(Calendar.MINUTE, 0);
        currentDate.set(Calendar.SECOND, 0);

        SkuTagQueryRequest queryRequest = new SkuTagQueryRequest();

        queryRequest.setTagCityId(cityId);

        QueryResponse<SkuTagWrapper> response = skuTagFacade.getSkuTag(queryRequest);

        for (CartRequest request : requests) {

            for (SkuTagWrapper skuTag : response.getContent()) {

                if (request.getSkuId().equals(skuTag.getSku().getId())) {

                    Long quantity = orderItemService.getQuantityByCustomerAndSku(request.getSkuId(), currentDate.getTime(), customer);

                    if (skuTag.getLimitedQuantity() != null && (quantity + request.getQuantity()) > skuTag.getLimitedQuantity()) {

                        throw new PurchaseQuantityExcessException(String.format("商品%s已经超过限定购买数量.", skuTag.getSku().getName()));
                    }
                }
            }
        }
    }
}
