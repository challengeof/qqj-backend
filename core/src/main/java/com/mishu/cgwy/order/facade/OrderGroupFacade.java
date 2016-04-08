package com.mishu.cgwy.order.facade;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.car.domain.Car;
import com.mishu.cgwy.car.service.CarService;
import com.mishu.cgwy.car.vo.CarVo;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.order.controller.*;
import com.mishu.cgwy.order.domain.Order;
import com.mishu.cgwy.order.domain.OrderGroup;
import com.mishu.cgwy.order.service.OrderGroupService;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.order.wrapper.*;
import com.mishu.cgwy.organization.service.OrganizationService;
import com.mishu.cgwy.product.controller.OrderGroupQueryRequest;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.domain.StockOut;
import com.mishu.cgwy.stock.domain.StockOutStatus;
import com.mishu.cgwy.stock.domain.StockOutType;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.stock.service.DepotService;
import com.mishu.cgwy.stock.service.StockOutService;
import com.mishu.cgwy.utils.DateUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * User: xudong
 * Date: 4/15/15
 * Time: 5:11 PM
 */
@Service
public class OrderGroupFacade {
    @Autowired
    private OrderService orderService;

    @Autowired
    private AdminUserService adminUserService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private OrganizationService organizationService;

    @Autowired
    private StockOutService stockOutService;

    @Autowired
    private OrderGroupService orderGroupService;

    @Autowired
    private DepotService depotService;
    @Autowired
    private CarService carService;

    @Transactional
    public SimpleOrderGroupWrapper groupOrders(OrderGroupRequest request,
                                         AdminUser operator) {
        return new SimpleOrderGroupWrapper(groupOrders(request, new OrderGroup(),operator));
    }

    @Transactional
    public OrderGroupWrapper updateOrderGroup(Long id,
                                              OrderGroupRequest request, AdminUser operator) {
        OrderGroup orderGroup = orderService.getOrderGroupById(id);

        return new OrderGroupWrapper(groupOrders(request, orderGroup,operator));
    }

    private OrderGroup groupOrders(OrderGroupRequest request,
                                   OrderGroup orderGroup,AdminUser adminUser) {
        List<Order> orders = new ArrayList<>();
        List<StockOut> stockOuts = new ArrayList<>();

        if (request.getDepotId() != null) {
            orderGroup.setDepot(depotService.findOne(request.getDepotId()));
        } else {
            orderGroup.setDepot(null);
        }

        if (StringUtils.isNotBlank(request.getName())) {
            orderGroup.setName(request.getName());
        } else {
            orderGroup.setName(DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm"));
        }

        //TODO  暂时全部为自营店
        orderGroup.setOrganization(organizationService.getDefaultOrganization());

        if(request.getCityId() != null) {
            orderGroup.setCity(locationService.getCity(request.getCityId()));
        }

        for (Long orderId : request.getOrderIds()) {
            StockOut stockOut = stockOutService.getStockOutByOrderId(orderId);
            stockOuts.add(stockOut);
            Order order = stockOut.getOrder();
            stockOut.setOrderGroup(orderGroup);
            orders.add(order);
        }

        if (request.getTrackerId() != null) {
            orderGroup.setTracker(adminUserService.getAdminUser(request.getTrackerId()));
        } else {
            orderGroup.setTracker(null);
        }

        orderGroup.setMembers(orders);
        orderGroup.setStockOuts(stockOuts);
        orderGroup.setCreateDate(new Date()); //设置创建时间,更新数据也更新此字段

        return orderService.saveOrderGroup(orderGroup);
    }

    @Transactional(readOnly = true)
    public QueryResponse<SimpleOrderGroupWrapper> findOrderGroups(OrderGroupQueryRequest request,AdminUser adminUser) {
        QueryResponse<SimpleOrderGroupWrapper> response = new QueryResponse<>();
        Page<OrderGroup> page = orderGroupService.getOrderGroup(request, adminUser);

        for (OrderGroup orderGroup : page) {
            SimpleOrderGroupWrapper sOrderGroup = new SimpleOrderGroupWrapper(orderGroup);
            Car car = carService.getCarByAdminUserId(orderGroup.getTracker().getId());
            if(car != null) {
                sOrderGroup.setCarExpenses(car.getExpenses());
                sOrderGroup.setCarSource(car.getSource());
                sOrderGroup.setCarTaxingPoint(car.getTaxingPoint());
            }
            response.getContent().add(sOrderGroup);
        }
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(page.getTotalElements());

        return response;
    }


    @Transactional(readOnly = true)
    public OrderGroupWrapper getOrderGroup(Long id) {
        OrderGroup orderGroup = orderService.getOrderGroupById(id);
        return new OrderGroupWrapper(orderGroup);
    }

    @Transactional(readOnly = true)
    public QueryResponse<SimpleOrderWrapper> findUnGroupedOrders(final StockOutRequest request,AdminUser adminUser) {
        QueryResponse<SimpleOrderWrapper> response = new QueryResponse<>();

        request.setStockOutType(StockOutType.ORDER.getValue());
        request.setStockOutStatus(StockOutStatus.IN_STOCK.getValue());
        request.setPageSize(Integer.MAX_VALUE);
        request.setOrderGroupIsNull(true);
        request.setTrackerId(null);
        Page<StockOut> page = stockOutService.getStockOutList(request, adminUser);

        for (StockOut o : page) {

            if(request.getBlockIds().size() != 0){
                for(Long blockId : request.getBlockIds()){
                    if(blockId.longValue() == o.getOrder().getCustomer().getBlock().getId().longValue()) {
                        response.getContent().add(new SimpleOrderWrapper(o.getOrder()));
                        break;
                    }
                }
            }else{
                response.getContent().add(new SimpleOrderWrapper(o.getOrder()));
            }
        }

        response.setPageSize(request.getPageSize());
        response.setPage(request.getPage());
        response.setTotal(page.getTotalElements());
        return response;
    }

    /*@Transactional(readOnly = true)
    public List<OrderWrapper> findOrderGroupsByTracker(AdminUser tracker, Date expectedArrivedDate) {

        List<OrderGroup> orderGroups = orderService.findOrderGroupsByTracker(expectedArrivedDate, tracker);
        List<OrderWrapper> orders = new ArrayList<>();
        for (OrderGroup orderGroup : orderGroups) {
            for (Order order : orderGroup.getMembers()) {
                orders.add(new OrderWrapper(order));
            }
        }
        return orders;
    }

    @Transactional(readOnly = true)
    public List<SimpleOrderGroupResponse> findOrderGroupsByOperator(AdminUser tracker, Date expectedArrivedDate) {

        List<OrderGroup> orderGroups = orderService.findOrderGroupsByOperator(expectedArrivedDate, tracker);
        List<SimpleOrderGroupResponse> orderGroupList = new ArrayList<>();
        for (OrderGroup orderGroup : orderGroups) {
            orderGroupList.add(new SimpleOrderGroupResponse(orderGroup));
        }

        return orderGroupList;
    }

    public OrderGroupsSku getOrderGroupById(Long id) {
        OrderGroupWrapper orderGroup = getOrderGroup(id);
        List<OrderGroupsSkuTotal> skus = new ArrayList<>();
        Map<Long, OrderGroupsSkuTotal> map = new HashMap<>();
        for (OrderWrapper order : orderGroup.getMembers()) {
            for (SimpleOrderItemWrapper orderItem : order.getOrderItems()) {
                if (map.containsKey(orderItem.getSku().getId())) {
                    OrderGroupsSkuTotal skuTotal = map.get(orderItem.getSku().getId());
                    skuTotal.setQuantity(skuTotal.getQuantity() + orderItem.getQuantity());
                } else {
                    OrderGroupsSkuTotal orderGroupsSkuTotal = new OrderGroupsSkuTotal();
                    orderGroupsSkuTotal.setSku(orderItem.getSku());
                    orderGroupsSkuTotal.setQuantity(orderItem.getQuantity());
                    orderGroupsSkuTotal.setPrice(orderItem.getPrice());
                    map.put(orderItem.getSku().getId(), orderGroupsSkuTotal);
                }
            }

            for (RefundWrapper refund : order.getRefunds()) {
                if (map.containsKey(refund.getSku().getId())) {
                    map.remove(refund.getSku().getId());
                }
            }

        }
        for (Map.Entry<Long, OrderGroupsSkuTotal> entry : map.entrySet()) {
            skus.add(entry.getValue());
        }
        OrderGroupsSku orderGroupsSku = new OrderGroupsSku();
        orderGroupsSku.setOrderGroupsSkuTotals(skus);
        orderGroupsSku.setOrderGroupWrapper(orderGroup);

        return orderGroupsSku;
    }

    @Transactional
    public SimpleOrderGroupWrapper updateOrderGroupCheckResult(Long id, boolean checkResult) {

        OrderGroup orderGroup = orderService.getOrderGroupById(id);
        orderGroup.setCheckResult(checkResult);
        orderService.saveOrderGroup(orderGroup);
        return new SimpleOrderGroupWrapper(orderGroup);
    }*/
}
