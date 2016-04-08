package com.mishu.cgwy.order.facade;

import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.domain.Warehouse;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.order.constants.OrderHistoryConstants;
import com.mishu.cgwy.order.constants.OrderRefundStatus;
import com.mishu.cgwy.order.constants.OrderReturnDetailType;
import com.mishu.cgwy.order.constants.OrderStatus;
import com.mishu.cgwy.order.controller.OrderQueryRequest;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderItem;
import com.mishu.cgwy.order.domain.Refund;
import com.mishu.cgwy.order.dto.*;
import com.mishu.cgwy.order.service.DateProcessorHandle;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.OrderWrapper;
import com.mishu.cgwy.organization.domain.Organization;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.profile.constants.PromotionConstants;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.promotion.domain.Promotion;
import com.mishu.cgwy.promotion.service.PromotionService;
import com.mishu.cgwy.utils.LegacyOrderUtils;
import com.mishu.cgwy.utils.OrderUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 4/30/15
 * Time: 3:44 PM
 */
@Service
public class LegacyOrderFacade {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private OrderService orderService;
    @Autowired
    private ProductService productService;
    @Autowired
    private ContextualInventoryService contextualInventoryService;
    @Autowired
    private RestaurantService restaurantService;
    @Autowired
    private LocationService locationService;
    @Autowired
    private PromotionService promotionService;

    private Logger logger = LoggerFactory.getLogger(OrderFacade.class);

    @Transactional
    public OrderWrapper addCart(CartAddRequest shoppingRequest, String name) {
        Order cart = getCart(name);
        final Customer customer = customerService.findCustomerByUsername(name);

        for (CartAddData data : shoppingRequest.getCartList()) {
            OrderItem match = null;

            Sku sku = productService.getSku(Long.valueOf(data.getProductNumber()));

            for (OrderItem orderItem : cart.getOrderItems()) {
                if (orderItem.getSku().getId().equals(sku.getId())) {
                    match = orderItem;
                    break;
                }
            }

            if (match == null) {
                match = new OrderItem();
                match.setOrder(cart);
                match.setSku(sku);
                cart.getOrderItems().add(match);
            }
            /*match.setQuantity(match.getQuantity()
                    + data.getNumber());*/
        }

        if (customer.getBlock() != null && customer.getBlock().getWarehouse() != null) {
            contextualInventoryService.updateSalePrice(cart, customer.getBlock().getWarehouse());
        }

        cart = orderService.saveOrder(cart);

        return new OrderWrapper(cart);

    }

    @Transactional
    public Order getCart(String customerName) {
        final Customer customer = customerService.findCustomerByUsername(customerName);
        Order cart = orderService.getCartByCustomer(customer);

        final Block block = customer.getBlock();
        if (block != null && block.getWarehouse() != null) {
            contextualInventoryService.updateSalePrice(cart, block.getWarehouse());
        }

        return cart;
    }

    @Transactional
    public OrderWrapper updateCart(CartUpdateRequest shoppingUpdateRequest,
                                   String customerName) {
        Order order = getCart(customerName);
        List<CartUpdateData> datas = shoppingUpdateRequest
                .getCartList();
        Iterator<OrderItem> it = order.getOrderItems().iterator();
        while (it.hasNext()) {
            OrderItem ot = it.next();
            for (CartUpdateData sud : datas) {
                if (sud.getId().equals(ot.getId())) {
                    if (sud.getNumber() <= 0) {
                        it.remove();
                    } else {
//                        ot.setQuantity(sud.getNumber());
                    }
                }
            }
        }


        return new OrderWrapper(orderService.saveOrder(order));
    }

    @Deprecated
    @Transactional
    public CartListResponse listCart(Customer customer) {
        Order order = getCart(customer.getUsername());

        List<CartListData> data = new ArrayList<>();
        Warehouse warehouse = locationService.getWarehouse(Constants.DEFAULT_WAREHOUSE);
        if (customer.getBlock() != null) {
            warehouse = customer.getBlock().getWarehouse();
        }

        for (OrderItem oi : order.getOrderItems()) {
            BigDecimal price = BigDecimal.ZERO;
            int maxBuy = 0;

            DynamicSkuPrice dynamicSkuPrice = contextualInventoryService.getDynamicSkuPrice(oi.getSku().getId(),
                    warehouse.getId());

            if (dynamicSkuPrice == null) {
                price = BigDecimal.ZERO;
                maxBuy = 0;
            } else if (!checkAvailable(warehouse, oi)) {
                price = dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice();
                maxBuy = 0;
            } else {
                price = dynamicSkuPrice.getSinglePriceStatus().getSingleSalePrice();
//                maxBuy = Math.min(Constants.MAX_BUY, dynamicSkuPrice.getStock());
            }

            oi.setPrice(price);
//            oi.setTotalPrice(oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity())));

            CartListData cld = new CartListData(oi);
            cld.setMaxBuy(maxBuy);
            data.add(cld);
        }

        CartListResponse clr = new CartListResponse();
        clr.setCartList(data);
        clr.setShippingFee(0);
        clr.setShippingFeeLimit(1);
        return clr;
    }


    @Transactional
    public CreateOrderResponse createOrder(CreateOrderRequest createOrderRequest, String name) {

        Customer customer = customerService.findCustomerByUsername(name);

        Warehouse warehouse = null;
        if (customer.getBlock() != null) {
            warehouse = customer.getBlock().getWarehouse();
        } else {
            warehouse = locationService.getWarehouse(Constants.DEFAULT_WAREHOUSE);
        }


        Restaurant restaurant = customerService.getRestaurantById(createOrderRequest.getRestaurantId());
        if (restaurant.getStatus() != RestaurantStatus.ACTIVE.getValue()) {
            CreateOrderResponse cor = new CreateOrderResponse();
            cor.setErrno(-1);
            return cor;
        }

        List<String> stockOut = new ArrayList<>();

        final Date current = new Date();
        Map<Long, Order> organizationOrderMap = new HashMap<>();

        for (CartAddData item : createOrderRequest.getProductList()) {
            String productNumber = item.getProductNumber();
            Sku sku = productService.getSku(Long.valueOf(productNumber));
            //暂时使用单品
            final boolean available = contextualInventoryService.isAvailable(sku, warehouse, false);

            Organization organization = sku.getProduct().getOrganization();

            if (!available) {
                stockOut.add(productNumber);
            } else {
                OrderItem orderItem = new OrderItem();
                orderItem.setSku(sku);
//            	orderItem.setQuantity(item.getNumber());

                Order order = organizationOrderMap.get(organization.getId());
                if (order == null) {
                    order = new Order();
                    List<OrderItem> items = new ArrayList<>();
                    order.setCustomer(customer);
                    order.setAdminUser(customer.getAdminUser());
                    order.setSubmitDate(current);
                    order.setExpectedArrivedDate(OrderUtils.getExpectedArrivedDate(current));
                    order.setRestaurant(restaurant);
                    order.setStatus(OrderStatus.COMMITTED.getValue());
                    items.add(orderItem);
                    orderItem.setOrder(order);
                    order.setOrderItems(items);
                    order.setOrganization(organization);
                    organizationOrderMap.put(organization.getId(), order);
                } else {
                    List<OrderItem> items = order.getOrderItems();
                    orderItem.setOrder(order);
                    items.add(orderItem);
                    order.setOrderItems(items);
                }
            }
        }

        if (!stockOut.isEmpty()) {
            CreateOrderResponse cor = new CreateOrderResponse();
            cor.setNotEnough(stockOut);
            return cor;
        } else {
            long orderCount = orderService.getOrderCountByRestaurant(restaurant);

            if (!organizationOrderMap.isEmpty()) {
                Iterator<Long> iterator = organizationOrderMap.keySet().iterator();
                while (iterator.hasNext()) {
                    Order order = organizationOrderMap.get(iterator.next());
                    order.setSequence(++orderCount);
                    contextualInventoryService.updateSalePrice(order, warehouse);
                    order.setPromotions(new HashSet<>(findApplicablePromotion(order, new Date(), warehouse, order.getOrganization())));
                    order.calculateSubTotal();
                    order.calculateTotal();
                }
            }

            Order cart = orderService.getCartByCustomer(customer);
            Iterator<OrderItem> iterator = cart.getOrderItems().iterator();
            while (iterator.hasNext()) {

                OrderItem orderItemInCart = iterator.next();
                boolean found = false;
                Organization cartOrganization = orderItemInCart.getSku().getProduct().getOrganization();
                Order order = organizationOrderMap.get(cartOrganization.getId());
                if (order != null) {
                    for (OrderItem orderItemInOrder : order.getOrderItems()) {
                        if (orderItemInCart.getSku().getId().equals(orderItemInOrder.getSku().getId())) {
                            found = true;
                            break;
                        }
                    }
                }
                if (found) {
                    iterator.remove();
                }
            }

            if (!organizationOrderMap.isEmpty()) {
                Iterator<Long> iterator2 = organizationOrderMap.keySet().iterator();
                while (iterator2.hasNext()) {
                    Order order = organizationOrderMap.get(iterator2.next());
                    order = orderService.saveOrder(order);
                    restaurant.setLastPurchaseTime(order.getSubmitDate());
                    restaurantService.save(restaurant);
                    for (OrderItem orderItem : order.getOrderItems()) {
                        customerService.addFavorite(customer, orderItem.getSku());
                    }
                }
            }

            cart = orderService.saveOrder(cart);

            CreateOrderResponse cor = new CreateOrderResponse();

            if (!organizationOrderMap.isEmpty()) {
                cor.setOrderNumber(String.valueOf(organizationOrderMap.get(organizationOrderMap.keySet().iterator().next()).getId()));
            }
            return cor;
        }
    }

    public List<Promotion> findApplicablePromotion(final Order order, Date current, Warehouse warehouse, Organization organization) {
        List<Promotion> promotions = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        Date start = DateUtils.truncate(current, Calendar.DAY_OF_MONTH);
        Date end = DateUtils.addDays(start, +1);

        for (Promotion promotion : promotionService.findApplicablePromotion(order, current, warehouse, organization)) {
            if (!anyOrderBefore(order.getCustomer(), current, null) && promotion
                    .getPromotionConstants().equals(PromotionConstants.RESTAURANT_FIRST_SINGLE)) {
                promotions.add(promotion);
            } else if (promotion.getPromotionConstants().equals(PromotionConstants.TODAY_FIRST_SINGLE)) {
                // 每天的首单
                final List<Order> orders = orderService.getOrderByCustomerAndSubmitDate(order.getCustomer(), start, end);
                boolean discountAlreadyUsed = false;
                for (Order o : orders) {
                    if (o.getStatus() != OrderStatus.CANCEL.getValue()) {
                        if (!o.getPromotions().isEmpty()) {
                            discountAlreadyUsed = true;
                        }
                    }
                }
                if (!discountAlreadyUsed) {
                    promotions.add(promotion);
                }
            } else if (promotion.getPromotionConstants().equals(PromotionConstants.ORDER_WITH_A_GIFT_SEND)) {
                promotions.add(promotion);
            }
        }


        return promotions;
    }

    private boolean anyOrderBefore(Customer customer, Date end, AdminUser adminUser) {
        OrderQueryRequest request = new OrderQueryRequest();
        request.setEnd(end);
        request.setPage(0);
        request.setPageSize(1);
        request.setCustomerId(customer.getId());
        request.setPromotionTag(true);

        Page<Order> page = orderService.findOrders(request, adminUser);

        return page.getTotalElements() > 0;
    }

    public Order getOrder(String name, int orderStatus) {
        Customer customer = customerService.findCustomerByUsername(name);
        List<Order> orders = orderService.getOrderByCustomer(customer);
        for (Order order : orders) {
            if (order.getStatus() == (orderStatus)) {
                return order;
            }
        }
        return null;
    }

    public OrderListResponse listOrder(Long restaurantId, int status,
                                       String name) {
        Customer customer = customerService.findCustomerByUsername(name);
        List<Order> temp = orderService.getOrderByCustomer(customer);
        List<Order> orders = new ArrayList<Order>();
        if (restaurantId == null || restaurantId == 0) {//全部餐馆
            for (Order order : temp) {
                if (status == 0) {
                    if (order.getStatus() != OrderStatus.UNCOMMITTED.getValue())
                        orders.add(order);
                } else {
                    if (order.getStatus() == status)
                        orders.add(order);
                }
            }
        } else {
            //TODO
        }

        OrderListResponse olr = new OrderListResponse();
        List<OrderListData> old = new ArrayList<OrderListData>();
        for (Order order : orders) {
            Restaurant restaurant2 = order.getRestaurant();
            OrderListData data = new OrderListData();

            data.setAddress(restaurant2.getAddress().getAddress());
            data.setCreateTime(DateProcessorHandle.format(order.getSubmitDate()));
            data.setName(restaurant2.getName());
            data.setOrderNumber(order.getId() + "");
            data.setPrice(order.getTotal().floatValue());
            data.setRealname(restaurant2.getReceiver());
            data.setRestaurantNumber(restaurant2.getId() + "");
            data.setTelephone(restaurant2.getTelephone());
            data.setStatus(order.getStatus());
            List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();
            List<OrderItem> items = order.getOrderItems();

            for (OrderItem ot : items) {
                OrderDetail od = new OrderDetail();
                od.setName(ot.getSku().getProduct().getName());
                od.setPrice(ot.getPrice().floatValue());
                od.setProductNumber(String.valueOf(ot.getSku().getId()));
//                final MediaFile mediaFile = ot.getSku().getProduct().getMediaFile();
                /*if (mediaFile != null) {
                    od.setUrl(mediaFile.getUrl());
                }*/
//                od.setNumber(ot.getQuantity());
                orderDetails.add(od);
            }

            data.setOrderDetail(orderDetails);
            data.setTelephone(restaurant2.getTelephone());
            old.add(data);
        }
        olr.setRows(old);
        olr.setTotal(orders.size());
        return olr;
    }

    @Deprecated
    @Transactional
    public OrderDetailResponse getOrderDetail(String orderNumber) {
        Order order = orderService.getOrderById(Long.valueOf(orderNumber));
        OrderDetailResponse od = new OrderDetailResponse();
        Restaurant restaurant = order.getRestaurant();
        od.setAddress(restaurant.getAddress().getAddress());
        od.setCreateTime(DateProcessorHandle.format(order.getSubmitDate()));
        od.setName(restaurant.getName());
        List<OrderDetail> orderDetails = LegacyOrderUtils.getSubItems(order);
        od.setOrderDetail(orderDetails);
        od.setOrderNumber(order.getId() + "");
        List<Refund> refunds = orderService.findRefundByOrder(order);
        List<OrderReturnDetail> list = getReturnMsg(od, refunds);
        od.setOrderReturnDetail(list);
        od.setPayTime(DateProcessorHandle.format(order.getSubmitDate()));// TODO
        od.setPrice(order.getTotal().floatValue());
        od.setRealname(restaurant.getReceiver());
        od.setRestaurantNumber(restaurant.getId() + "");
        od.setSendTime("");// TODO send time single
        od.setShippingFee(0);
        od.setStatus(order.getStatus());
        od.setTelephone(restaurant.getTelephone());
        //od.setTraceInfos(null);// TODO the trace information
        return od;
    }

    public List<OrderReturnDetail> getReturnMsg(OrderDetailResponse od,
                                                List<Refund> refunds) {
        if (refunds != null && refunds.size() > 0) {
            od.setOrderReturnStatus(OrderRefundStatus.HAS_REVIEW);// TODO
        }
        List<OrderReturnDetail> list = new ArrayList<OrderReturnDetail>();
        for (Refund refund : refunds) {
            OrderReturnDetail ord = new OrderReturnDetail();
//            ord.setNumber(refund.getQuantity());
            ord.setPrice(refund.getPrice().floatValue());
            ord.setProductNumber(String.valueOf(refund.getSku().getId()));
            /*final MediaFile mediaFile = refund.getSku().getProduct().getMediaFile();
            if (mediaFile != null) {
                ord.setUrl(mediaFile.getUrl());
            }*/
            ord.setType(OrderReturnDetailType.EXCHANGE_GOODS);// TODO
            list.add(ord);
        }
        return list;
    }

    @Transactional
    public void cancel(String orderNumber) {
        Order order = orderService.getOrderById(Long.valueOf(orderNumber));
        order.setStatus(OrderStatus.CANCEL.getValue());
        logger.warn("app1.0订单取消订单");
    }

    @Deprecated
    @Transactional
    public List<OrderHistoryItem> buildSortedOrderHistory(Customer customer, Long restaurantId, String sort, String order,
                                                          Integer page, Integer rows) {

        List<Order> orderList = new ArrayList<>();
        if (restaurantId == null || restaurantId == 0) {
            //default情况
            orderList = orderService.getOrderByCustomer(customer);
        } else {
            orderList = orderService.findByRestaurantId(restaurantId);
        }

        List<Order> validOrderList = new ArrayList<Order>();
        for (Order o : orderList) {
            if (o.getStatus() == OrderStatus.COMPLETED.getValue()) {
                validOrderList.add(o);
            }
        }


        //TODO: sort, order的其他方式,现在只按时间降序排列
        if (sort == OrderHistoryConstants.SORT_CREATE_TIME && order == OrderHistoryConstants.ORDER_DESC) {
            validOrderList = orderService.sortOrder(validOrderList);
        }

        List<OrderHistoryItem> orderHistoryItems = new ArrayList<OrderHistoryItem>();
        for (Order o : validOrderList) {
            List<OrderItem> orderItems = o.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                OrderHistoryItem orderHistoryItem = new OrderHistoryItem(orderItem);
                orderHistoryItems.add(orderHistoryItem);

            }
        }
        //合并
        List<OrderHistoryItem> finalOrderHistoryItems = new ArrayList<OrderHistoryItem>();
        for (OrderHistoryItem orderHistoryItem : orderHistoryItems) {
            boolean find = false;
            for (OrderHistoryItem item : finalOrderHistoryItems) {
                //同种sku
                if (item.getProductNumber().equals(orderHistoryItem.getProductNumber())) {
                    item.setTotal(item.getTotal() + orderHistoryItem.getTotal());
                    item.setCount(item.getCount() + 1);
                    find = true;
                    break;
                }
            }
            if (!find) {
                finalOrderHistoryItems.add(orderHistoryItem);
            }

        }
        //page, rows
        if (sort != null) {
            //finalOrderHistoryItems = orderService.sortOrderHistoryItems(finalOrderHistoryItems, sort, order);
        }
        if (page == OrderHistoryConstants.PAGE) {
            if (rows != null && rows < finalOrderHistoryItems.size()) {
                finalOrderHistoryItems = finalOrderHistoryItems.subList(0, rows);
            } else if (OrderHistoryConstants.ROWS < finalOrderHistoryItems.size()) {
                finalOrderHistoryItems = finalOrderHistoryItems.subList(0, OrderHistoryConstants.ROWS);
            }
        }
        return finalOrderHistoryItems;


    }

    private boolean checkAvailable(Warehouse warehouse, OrderItem oi) {
        //暂时按照单品走
        return contextualInventoryService.isAvailable(oi.getSku(), warehouse, false);
    }

    public void addCustomerEvaluate(Long orderId,
                                    CustomerEvaluateRequest request, Customer customer) {
        Order order = orderService.getOrderById(orderId);
        String evaluateStr = request.isSatisfied() == true ? "满意" : "不满意" + "," + request.getEvaluateStr();
        String memo = order.getMemo();
        String newMemo = "";
        if (StringUtils.isNotBlank(memo))
            newMemo = memo + "\n" + customer.getUsername() + ":" + evaluateStr;
        else
            newMemo = customer.getUsername() + ":" + evaluateStr;
        order.setMemo(newMemo);
        orderService.saveOrder(order);
    }
}
