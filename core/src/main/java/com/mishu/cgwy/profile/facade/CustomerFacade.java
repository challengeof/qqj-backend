package com.mishu.cgwy.profile.facade;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.admin.service.AdminUserService;
import com.mishu.cgwy.banner.domain.Banner;
import com.mishu.cgwy.banner.domain.Push;
import com.mishu.cgwy.banner.dto.BannerUrl;
import com.mishu.cgwy.banner.dto.Message;
import com.mishu.cgwy.banner.pojo.BannerResponse;
import com.mishu.cgwy.banner.service.BannerService;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.coupon.service.ShareService;
import com.mishu.cgwy.coupon.vo.ReqShareIdVo;
import com.mishu.cgwy.error.AdminUserNotExistsException;
import com.mishu.cgwy.error.CustomerAreaOutsideException;
import com.mishu.cgwy.error.CustomerNotExistsException;
import com.mishu.cgwy.inventory.domain.DynamicSkuPrice;
import com.mishu.cgwy.inventory.service.ContextualInventoryService;
import com.mishu.cgwy.message.CouponSenderEnum;
import com.mishu.cgwy.message.PromotionMessage;
import com.mishu.cgwy.message.PromotionMessageSender;
import com.mishu.cgwy.order.facade.PermissionCheckUtils;
import com.mishu.cgwy.order.service.OrderService;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.product.domain.Sku;
import com.mishu.cgwy.product.service.ProductService;
import com.mishu.cgwy.product.wrapper.BundleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.product.wrapper.SingleDynamicSkuPriceStatusWrapper;
import com.mishu.cgwy.profile.constants.*;
import com.mishu.cgwy.profile.controller.*;
import com.mishu.cgwy.profile.controller.legacy.pojo.LegacyRegisterRequest;
import com.mishu.cgwy.profile.controller.legacy.pojo.LegacyRegisterResponse;
import com.mishu.cgwy.profile.controller.legacy.pojo.RestaurantAlarmResponse;
import com.mishu.cgwy.profile.convert.RestaurantConveter;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.dto.CustomerCenterResponse;
import com.mishu.cgwy.profile.dto.RestaurantSummary;
import com.mishu.cgwy.profile.dto.RestaurantUsableListResponseData;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.service.RestaurantService;
import com.mishu.cgwy.profile.vo.RestaurantAuditReviewVo;
import com.mishu.cgwy.profile.vo.RestaurantInfoVo;
import com.mishu.cgwy.profile.wrapper.CustomerHintWrapper;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.profile.wrapper.FavoriteWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.task.service.AsyncTaskService;
import com.mishu.cgwy.task.util.ExportExcelUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Tuple;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;

/**
 * User: xudong
 * Date: 3/3/15
 * Time: 6:58 PM
 */
@Service
public class CustomerFacade {

    private static Logger logger = LoggerFactory.getLogger(CustomerFacade.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AdminUserService adminUserService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private ProductService productService;

    @Autowired
    private ContextualInventoryService inventoryService;

    @Autowired
    private ShareService shareService;

    @Autowired
    private BannerService bannerService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private PromotionMessageSender promotionMessageSender;

    @Autowired
    private AsyncTaskService asyncTaskService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private static final String RESTAURANT_LIST = "/template/restaurant-list.xls";

    @Transactional
    public Customer findCustomerByUsername(String username) {
        return customerService.findCustomerByUsername(username);
    }

    @Transactional
    public CustomerWrapper findCustomerWrapperByUsername(String username) {
        return new CustomerWrapper(customerService.findCustomerByUsername(username));
    }

    @Transactional
    public List<RestaurantWrapper> findRestaurantByCustomerId(Long customerId) {
        return new ArrayList<>(Collections2.transform(customerService.getRestaurantsByCustomer(customerId), new Function<Restaurant, RestaurantWrapper>() {
            @Override
            public RestaurantWrapper apply(Restaurant input) {
                return new RestaurantWrapper(input);
            }
        }));
    }


    @Transactional
    public LegacyRegisterResponse legacyRegister(LegacyRegisterRequest registerRequest) {
        Customer customer = new Customer();
        customer.setUsername(registerRequest.getTelephone());
        customer.setTelephone(registerRequest.getTelephone());
        customer.setPassword(registerRequest.getPassword());
        customer.setEnabled(true);

        customer.setCity(locationService.getZone(registerRequest.getZoneId()).getCity());
        if (registerRequest.getAdminId() != null) {
            try {
                AdminUser adminUser = adminUserService.getAdminUser(registerRequest.getAdminId());
                customer.setAdminUser(adminUser);
            } catch (Exception e) {
                logger.warn("exception caught in legacyRegister", e);
            }
        }
        customer = customerService.register(customer);
        LegacyRegisterResponse response = new LegacyRegisterResponse();
        response.setUserId(customer.getId());
        response.setUsername(customer.getUsername());
        response.setUserNumber(customer.getUserNumber());

        response.setInService(true);

        return response;
    }

    @Transactional
    public RegisterResponse register(RegisterRequest registerRequest) {

        boolean inServiceArea = customerService.checkInServiceArea(registerRequest.getLng(), registerRequest.getLat(), registerRequest.getCityId());
        if (!inServiceArea) {
            throw new CustomerAreaOutsideException();
        }

        Customer customer = new Customer();
        customer.setUsername(registerRequest.getTelephone());
        customer.setTelephone(registerRequest.getTelephone());
        customer.setPassword(registerRequest.getPassword());
//        customer.setCity(locationService.getZone(registerRequest.getZoneId()).getCity());
        customer.setCity(locationService.getCity(registerRequest.getCityId()));
        customer.setEnabled(true);
        //设置区块
        Block block = customerService.reckonBlock(registerRequest.getLng(), registerRequest.getLat(), registerRequest.getCityId());
        customer.setBlock(block);


//        Zone zone = locationService.getZone(registerRequest.getZoneId());
//        customer.setZone(zone);
        if (StringUtils.isNotBlank(registerRequest.getRecommendNumber())) {
            try {
                AdminUser adminUser = adminUserService.getAdminUser(Long.valueOf(registerRequest.getRecommendNumber()));
                // TODO any better solution
                // just visit name to ensure adminUser exists
                adminUser.getUsername();
                customer.setAdminUser(adminUser);
            } catch (Exception e) {
                logger.warn(registerRequest.getRecommendNumber() + " is not a valid recommend number", e);
            }
        }
        customer = customerService.register(customer);


        RestaurantStatus crestaurantStatus = null;

        if (registerRequest.isContainsRestaurant()) {
            Restaurant restaurant = new Restaurant();
            Address address = new Address();
            address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(registerRequest.getRestaurantAddress()));

            address.setStreetNumber(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(registerRequest.getRestaurantStreetNumber()));
            address.setWgs84Point(new Wgs84Point(registerRequest.getLng(), registerRequest.getLat()));

            restaurant.setAddress(address);
            restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(registerRequest.getReceiver()));
            restaurant.setCustomer(customer);
            restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(registerRequest.getRestaurantName()));
            restaurant.setLicense(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(registerRequest.getRestaurantLicense()));
//          restaurant.setType(registerRequest.getRestaurantType());
            restaurant.setTelephone(registerRequest.getTelephone());

//            //判断审核状态
//            crestaurantStatus = customerService.autoDefined(restaurant);
            restaurant.setStatus(RestaurantStatus.UNDEFINED.getValue());


            createRestaurant(restaurant);
        }

        ReqShareIdVo rsiV = shareService.parseReqShareId(registerRequest.getSharerId());
        if (rsiV != null) {
            shareService.saveShareInfo(customer, rsiV.shareId, rsiV.shareType);
        }

        RegisterResponse response = new RegisterResponse();
        response.setCustomerId(customer.getId());

//        response.setZoneActive(customer.getZone().isActive());

        return response;
    }


    @Transactional
    public Restaurant createRestaurant(Restaurant restaurant) {
        assert restaurant.getCustomer() != null
                && restaurant.getCustomer().getId() != null && restaurant.getCustomer().getId() != 0;

        return customerService.saveRestaurant(restaurant);
    }

    @Transactional
    public Restaurant updateRestaurant(Restaurant restaurant, Customer customer) {
        assert restaurant.getCustomer() != null
                && restaurant.getCustomer().getId() != null && restaurant.getCustomer().getId() != 0;

        if (restaurant.getCustomer().getId() != customer.getId()) {
            throw new IllegalAccessError("no permission");
        }

        return customerService.saveRestaurant(restaurant);
    }

    @Transactional
    public Restaurant findRestaurantById(Long id) {
        return customerService.getRestaurantById(id);
    }

    @Transactional
    public RestaurantWrapper findRestaurantWrapperById(Long id) {
        return new RestaurantWrapper(customerService.getRestaurantById(id));
    }

    @Transactional
    public boolean updatePassword(String username, String newPassword) {
        Customer customer = findCustomerByUsername(username);
        if (null != customer) {
            customerService.updateCustomerPassword(customer, newPassword);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean updatePassword(Customer customer, String oldPassword, String newPassword) {

        // 兼容旧密码格式
        String compatibleOldPassword = customer.getUsername() + oldPassword + "mirror";
        String compatibleNewPassword = customer.getUsername() + newPassword + "mirror";

        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(customer.getUsername(),
                compatibleOldPassword);

        try {
            Authentication auth = authenticationManager.authenticate(token);

            if (auth.isAuthenticated()) {
                customerService.updateCustomerPassword(customer, compatibleNewPassword);

                return true;
            }
        } catch (AuthenticationException e) {
        }
        return false;
    }

    @Transactional
    public List<RestaurantUsableListResponseData> findUsableRestaurantByCustomerId(Long customerId) {
        List<RestaurantUsableListResponseData> data = new ArrayList<RestaurantUsableListResponseData>();
        List<RestaurantWrapper> restaurants = this.findRestaurantByCustomerId(customerId);
        for (RestaurantWrapper restaurant : restaurants) {

            if (restaurant.getStatus() == RestaurantStatus.ACTIVE) {

                RestaurantUsableListResponseData r = new RestaurantUsableListResponseData();
                r.setId(restaurant.getId());
                r.setName(restaurant.getName());
                r.setAddress(restaurant.getAddress().getAddress());
                r.setRealname(restaurant.getReceiver());
                r.setTelephone(restaurant.getTelephone());
                r.setRestaurantNumber(this.generateRestaurantNumberById(restaurant.getId()));

                data.add(r);

            }
        }
        return data;
    }

    public String generateRestaurantNumberById(Long restaurantId) {
        return String.valueOf(restaurantId);
    }

    @Transactional
    public CustomerCenterResponse findCenterInfo(String username) {
        CustomerCenterResponse ccr = new CustomerCenterResponse();
        Customer customer = customerService.findCustomerByUsername(username);

        if (customer.getAdminUser() != null) {
            ccr.setAdminId(customer.getAdminUser().getId());
            ccr.setAdminName(customer.getAdminUser().getRealname());
            ccr.setTelephone(customer.getAdminUser().getTelephone());
        }
        ccr.setUsername(username);
        int restaurantCount = customerService.getRestaurantCount(customer.getId());
        ccr.setRestaurantCount(restaurantCount);
        List<CustomerHintWrapper> hintList = new ArrayList<CustomerHintWrapper>();
        List<CustomerHint> hints = customerService.getCustomerHintMsg(customer.getId());
        for (CustomerHint ch : hints) {
            CustomerHintWrapper chw = new CustomerHintWrapper(ch);
            hintList.add(chw);
        }
        ccr.setHintList(hintList);
        return ccr;

    }

    public void complaint(String name, Long adminId) {
        Customer customer = customerService.findCustomerByUsername(name);
        Long customerId = customer.getId();
        Complaint complaint = customerService.getComplaintNumber(customerId, adminId);
        if (null == complaint) {
            complaint = new Complaint();
            complaint.setAdminId(adminId);
            complaint.setCreateTime(new Date());
            complaint.setCustomerId(customerId);
            complaint.setComplaintNumber(1);
            customerService.saveComplaint(complaint);
        } else {
            complaint.setComplaintNumber(complaint.getComplaintNumber() + 1);
            customerService.saveComplaint(complaint);
        }

    }

    @Transactional(readOnly = true)
    public RestaurantAlarmResponse findAlarmRestaurants(RestaurantQueryRequest request, AdminUser operator) {
        List<Tuple> restaurantIdAndCount = customerService.findAlarmRestaurantIdAndCount(request);

        RestaurantAlarmResponse result = new RestaurantAlarmResponse();
        List<Long> restaurantIds = new ArrayList<>();

        for (Tuple tuple : restaurantIdAndCount) {
            restaurantIds.add((Long) tuple.get(0));
            result.getAlarmCount().put((Long) tuple.get(0), (Long) tuple.get(1));
        }

        if (!restaurantIds.isEmpty()) {
            List<Restaurant> restaurants = customerService.findAlarmRestaurant(restaurantIds, request, operator);

            for (Restaurant restaurant : restaurants) {
                result.getRestaurants().add(new RestaurantWrapper(restaurant));
            }
        }

        return result;
    }


    @Transactional(readOnly = true)
    public RestaurantQueryResponse findRestaurants(RestaurantQueryRequest request, AdminUser operator) {
        Page<Restaurant> page = customerService.findRestaurants(request, operator);

        RestaurantQueryResponse result = new RestaurantQueryResponse();
        result.setTotal(page.getTotalElements());
        result.setPage(request.getPage());
        result.setPageSize(request.getPageSize());

        List<RestaurantWrapper> restaurantWrappers = new ArrayList<>();
        for (Restaurant restaurant : page.getContent()) {
            restaurantWrappers.add(new RestaurantWrapper(restaurant));
        }

        List<Long> restaurantIds = new ArrayList<>();
        for (Restaurant restaurant : page.getContent()) {
            restaurantIds.add(restaurant.getId());
        }

        final List<Object[]> restaurantConsumption = orderService.getRestaurantConsumption(restaurantIds);
        for (Object[] i : restaurantConsumption) {
            result.getConsumption().put(((Restaurant) (i[0])).getId(), (BigDecimal) i[1]);
        }
//        RestaurantSummary restaurantSummary = getRestaurantSummary(request, operator);
//        result.setRestaurantSummary(restaurantSummary);
        result.setRestaurants(restaurantWrappers);
        return result;
    }

//    /**
//     * 获取客户公海列表
//     * @param request
//     * @param operater
//     * @return
//     */
//    public QueryResponse<RestaurantWrapper> getCustomerSeaList(RestaurantQueryRequest request, AdminUser operater) {
//        QueryResponse<RestaurantWrapper> response = new QueryResponse<>();
//        Page<Restaurant> restaurantPage =customerService.findAllBySeaReqeust(request,operater);
//
//        Collection<RestaurantWrapper> restaurants = Collections2.transform(restaurantPage.getContent(), new Function<Restaurant, RestaurantWrapper>() {
//            @Override
//            public RestaurantWrapper apply(Restaurant input) {
//                return new RestaurantWrapper(input);
//            }
//        });
//        response.setContent(new ArrayList<>(restaurants));
//        response.setPage(request.getPage());
//        response.setPageSize(request.getPageSize());
//        response.setTotal(restaurantPage.getTotalElements());
//        return response;
//    }

    public HttpEntity<byte[]> generateRestaurantExcel(RestaurantQueryRequest request, AdminUser operator) throws Exception {

        request.setPage(0);

        request.setPageSize(Integer.MAX_VALUE);

        Page<Restaurant> page = customerService.findRestaurants(request, operator);

        List<RestaurantWrapper> restaurantWrappers = new ArrayList<>();
        for (Restaurant restaurant : page.getContent()) {
            restaurantWrappers.add(new RestaurantWrapper(restaurant));
        }

        List<Long> restaurantIds = new ArrayList<>();
        for (Restaurant restaurant : page.getContent()) {
            restaurantIds.add(restaurant.getId());
        }

        Map<Long, BigDecimal> sumMap = new HashMap<>();
        for (Object[] i : orderService.getRestaurantConsumption(restaurantIds)) {
            sumMap.put(((Restaurant) (i[0])).getId(), (BigDecimal) i[1]);
        }

        Map<String, Object> beans = new HashMap<>();
        beans.put("list", restaurantWrappers);
        beans.put("sumMap", sumMap);
        return ExportExcelUtils.generateExcelBytes(beans, "商户列表.xls", RESTAURANT_LIST);
    }

    @Transactional
    public void updateRestaurantAdminUserBatch(Long oldAdminUserId, Long newAdminUserId) {
        AdminUser newAdminUser = null;

        if (newAdminUserId != null) {
            newAdminUser = adminUserService.getAdminUser(newAdminUserId);
        }
        if (oldAdminUserId == null || newAdminUser == null) {
            throw new AdminUserNotExistsException();
        }

        RestaurantQueryRequest request = new RestaurantQueryRequest();
        request.setPageSize(Integer.MAX_VALUE);
        if (oldAdminUserId.equals(0L)) {
            request.setAdminUserIdIsNull(true);
        } else {
            request.setAdminUserId(oldAdminUserId);
        }
        Page<Restaurant> page = customerService.findRestaurants(request, null);
        for (Restaurant restaurant : page) {
            restaurant.getCustomer().setAdminUser(newAdminUser);
            customerService.saveRestaurant(restaurant);
        }

    }

    public void updateRestaurantByAdminUser(Long restaurantId, RestaurantUpdateRequest request, AdminUser operator) {
        Restaurant restaurant = updateRestaurant(restaurantId, request, operator);
        if (RestaurantStatus.ACTIVE.getValue().equals(restaurant.getStatus())) {
            PromotionMessage promotionMessage = new PromotionMessage(CouponSenderEnum.REGISTER_SEND);
            promotionMessage.setRestaurantId(restaurant.getId());
            promotionMessageSender.sendMessage(promotionMessage);
        }
    }

    @Transactional
    public Restaurant updateRestaurant(Long restaurantId, RestaurantUpdateRequest request, AdminUser operator) {
        Restaurant restaurant = customerService.getRestaurantById(restaurantId);

        PermissionCheckUtils.checkCustomerUpdatePermission(restaurant.getCustomer(), operator);

        restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getName()));
        Address address = restaurant.getAddress();
        if (null == address) {
            address = new Address();
        }
        address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getAddress()));
        address.setStreetNumber(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getStreeNumer()));
        address.setWgs84Point(Wgs84Point.fromString(request.getWgs84Point()));

        restaurant.setAddress(address);

        restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getContact()));
        restaurant.setTelephone(request.getTelephone());


        RestaurantStatus restaurantStatus = RestaurantStatus.fromInt(request.getStatus());

        if(restaurant.getStatus()!=restaurantStatus.getValue() || (request.getRestaurantReason()!=null && restaurant.getRestaurantReason()!=RestaurantReason.fromInt(request.getRestaurantReason()).getValue().intValue())){
            restaurant.setStatusLastOperater(operator);
            restaurant.setStatusLastOperateTime(new Date());
        }
        restaurant.setStatus(restaurantStatus.getValue());

        if (restaurantStatus.getValue().equals(RestaurantStatus.INACTIVE.getValue())) {
            restaurant.setRestaurantReason(RestaurantReason.fromInt(request.getRestaurantReason()).getValue());
        }
        if (null != request.getType2()) {
            restaurant.setType(restaurantService.findOne(request.getType2()));
        }

        if (restaurant.getAuditTime() == null && RestaurantStatus.ACTIVE.getValue().equals(restaurant.getStatus())) {
            restaurant.setAuditTime(new Date());
        }

        restaurant = customerService.saveRestaurant(restaurant);

        Customer customer = restaurant.getCustomer();
        Block block = locationService.getBlockById(request.getBlockId());
        if (null != block) {
            customer.setBlock(block);
            customer.setCity(block.getCity());
        }

        logger.warn("customer message modify：" + "id:" + customer.getId() + ", blockId:" + request.getBlockId() + ", zoneId:" + request.getCustomerZoneId() + ", adminUserId:" + operator.getId());

        customerService.update(customer);

        return restaurant;
    }

    @Transactional
    public void assignRestaurantToAdminUser(Long restaurantId, Long adminUserId, AdminUser operator) {

        Restaurant restaurant = customerService.getRestaurantById(restaurantId);
        Customer customer = restaurant.getCustomer();
        AdminUser adminUser = adminUserService.getAdminUser(adminUserId);

        PermissionCheckUtils.checkCustomerUpdatePermission(customer, operator);

        customer.setAdminUser(adminUser);
        customerService.update(customer);
    }

    @Transactional
    public void assignCustomerToAdminUser(Long customerId, Long adminUserId, AdminUser operator) {

        Customer customer = customerService.getCustomerById(customerId);
        AdminUser adminUser = adminUserService.getAdminUser(adminUserId);

        PermissionCheckUtils.checkCustomerUpdatePermission(customer, operator);

        customer.setAdminUser(adminUser);
        customerService.update(customer);
    }

    public void updateRestaurantStatus(Long restaurantId, Integer status, AdminUser operator) {
        Restaurant restaurant = customerService.getRestaurantById(restaurantId);
        Customer customer = restaurant.getCustomer();

        PermissionCheckUtils.checkCustomerUpdatePermission(customer, operator);

        if(restaurant.getStatus()!=status){
            restaurant.setStatusLastOperater(operator);
            restaurant.setStatusLastOperateTime(new Date());
        }

        restaurant.setStatus(status);
        customerService.saveRestaurant(restaurant);
    }

    @Transactional(readOnly = true)
    public RestaurantSummary getRestaurantSummary(RestaurantQueryRequest request, AdminUser adminUser) {

        RestaurantSummary restaurantSummary = new RestaurantSummary();
        BigDecimal total = orderService.getUnCancelRestaurantTotal(request, adminUser);
        Long aliveCustomer = orderService.getAliveCustomers(request, adminUser);
        Date start = DateUtils.truncate(new Date(), Calendar.MONTH);
        Date today = DateUtils.truncate(new Date(), Calendar.DATE);
        Date end = DateUtils.addDays(today, 1);
        request.setStart(start);
        request.setEnd(end);
        BigDecimal monthlyTotal = orderService.getUnCancelRestaurantTotalBetween(request, adminUser);
        restaurantSummary.setAliveCustomer(aliveCustomer == null ? 0 : aliveCustomer);
        restaurantSummary.setMonthlyConsumption(monthlyTotal == null ? BigDecimal.ZERO : monthlyTotal);
        restaurantSummary.setTotalConsumption(total == null ? BigDecimal.ONE.ZERO : total);
        return restaurantSummary;
    }

    @Transactional
    public FavoriteWrapper addFavorite(Customer customer, Long skuId) {
        Sku sku = productService.getSku(skuId);
        return new FavoriteWrapper(customerService.addFavorite(customer, sku));
    }

    @Transactional
    public void deleteFavorite(Customer customer, List<Long> skuIds) {

        for (Long skuId : skuIds) {
            Sku sku = productService.getSku(skuId);
            customerService.deleteFavorite(customer, sku);
        }
    }

    /**
     * web
     * @param customer
     * @return
     */
    @Transactional
    public List<FavoriteWrapper> getFavorites(Customer customer) {
        final ArrayList<FavoriteWrapper> favorites = new ArrayList<>(Collections2.transform(customerService
                .getFavorites(customer), new Function<Favorite,
                FavoriteWrapper>() {
            @Override
            public FavoriteWrapper apply(Favorite input) {
                return new FavoriteWrapper(input);
            }
        }));

        Long warehouseId = Constants.DEFAULT_WAREHOUSE;
        if (customer.getBlock() != null && customer.getBlock().getWarehouse() != null) {
            warehouseId = customer.getBlock().getWarehouse().getId();
        }

        final ArrayList<Long> skuIds = new ArrayList<Long>(Collections2.transform(favorites, new
                Function<FavoriteWrapper, Long>
                        () {
                    @Override
                    public Long apply(FavoriteWrapper input) {
                        return input.getSku().getId();
                    }
                }));
        final Map<Long, DynamicSkuPrice> dynamicSkuPriceMap = inventoryService.getDynamicSkuPrices(skuIds, warehouseId);

        final Map<Long, Tuple> boughtCountAndQuantity = orderService.getSkuBoughtCountAndQuantity(customer);

        for (FavoriteWrapper favorite : favorites) {
            final DynamicSkuPrice dynamicSkuPrice = dynamicSkuPriceMap.get(favorite.getSku().getId());
            if (dynamicSkuPrice != null) {
                favorite.getSku().setBundlePrice(new BundleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getBundlePriceStatus()));
                favorite.getSku().setSinglePrice(new SingleDynamicSkuPriceStatusWrapper(dynamicSkuPrice.getSinglePriceStatus()));
            }

            if (boughtCountAndQuantity.containsKey(favorite.getSku().getId())) {
                Tuple tuple = boughtCountAndQuantity.get(favorite.getSku().getId());
                favorite.setBundleCount((long) tuple.get(1));
                favorite.setBundleQuantity((long) tuple.get(2));
                favorite.setSingleCount((long) tuple.get(3));
                favorite.setSingleQuantity((long) tuple.get(4));
            }
        }

        return favorites;
    }

    @Transactional(readOnly = true)
    public BannerResponse getBanner(Customer customer, Long cityId) {
        final Date current = new Date();
        BannerResponse response = new BannerResponse();
        Long warehouseId = Constants.BANNER_DEFAULT_WAREHOUSE;
        if (customer != null && customer.getBlock() != null) {
            warehouseId = customer.getBlock().getWarehouse().getId();
            cityId = customer.getBlock().getWarehouse().getCity().getId();
        }
        List<Banner> banners = bannerService.getApplicableBanner(current, warehouseId, cityId);

        List<Push> pushs = bannerService.getApplicablePush(current, warehouseId, cityId);

        for (Push push : pushs) {
            if (StringUtils.isNotBlank(push.getWelcomeMessage())) {
                try {
                    Message message = objectMapper.readValue(push.getWelcomeMessage(), Message.class);
                    response.setWelcomeContent(message);
                } catch (IOException e) {
                    logger.warn("catch exception", e);
                }
            }
            if (StringUtils.isNotBlank(push.getShoppingTip())) {
                response.setShoppingTip(push.getShoppingTip());
            }
        }
        for (Banner banner : banners) {
            if (StringUtils.isNotBlank(banner.getContent())) {
                try {
                    BannerUrl bannerUrl = objectMapper.readValue(banner.getContent(), BannerUrl.class);
                    response.getBanner().add(bannerUrl);
                } catch (IOException e) {
                    logger.warn("catch exception", e);
                }
            }
        }
        return response;

    }


    /************客户管理************/

    private static final int followUpDays=30;


    /**
     * 审核餐馆
     * @param request
     * @param operator
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public void auditRestaurant(RestaurantInfoRequest request, AdminUser operator) {
        Restaurant restaurant = this.updateRestaurant(request,operator);

        if (RestaurantStatus.ACTIVE == RestaurantStatus.fromInt(request.getRestaurantStatus()) ) {
            PromotionMessage promotionMessage = new PromotionMessage(CouponSenderEnum.REGISTER_SEND);
            promotionMessage.setRestaurantId(restaurant.getId());
            promotionMessageSender.sendMessage(promotionMessage);
        }
    }


    /**
     * 更新餐馆信息
     * @param request
     * @param operater
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public Restaurant updateRestaurant(RestaurantInfoRequest request, AdminUser operater){
        Date now =new Date();

        if(request.getRestaurantId()==null){
            throw new CustomerNotExistsException();
        }
        Restaurant restaurant = restaurantService.getOne(request.getRestaurantId());
        Customer customer = restaurant.getCustomer();
        customer.setBlock(locationService.getBlockById(request.getBlockId()));
        customer.setCity(locationService.getCity(request.getCityId()));
//        customer.setPassword(request.getPassword());
//        customer.setTelephone(request.getTelephone());
//        customer.setUsername(request.getTelephone());
//        restaurant.setStatus(RestaurantStatus.UNDEFINED.getValue());

        Address address = new Address();
        address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantAddress()));
        address.setStreetNumber(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantStreetNumber()));
        address.setWgs84Point(new Wgs84Point(Double.valueOf(request.getLng()), Double.valueOf(request.getLat())));
        restaurant.setAddress(address);

        restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getReceiver()));
        restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantName()));
        restaurant.setLicense(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantLicense()));
        restaurant.setTelephone(request.getTelephone());
        restaurant.setType(restaurantService.getRestaurantType(request.getRestaurantType()));

        restaurant.setCooperatingState(RestaurantCooperatingState.fromInt(request.getCooperatingState()).val);
        restaurant.setConcern(request.getConcern());
        restaurant.setOpponent(request.getOpponent());
        restaurant.setSpecialReq(request.getSpecialReq());
        restaurant.setStockRate(request.getStockRate());
        restaurant.setReceiver2(request.getReceiver2());
        restaurant.setTelephone2(request.getTelephone2());
        restaurant.setReceiver3(request.getReceiver3());
        restaurant.setTelephone3(request.getTelephone3());
//      restaurant.setCustomer(customer);

        if(request.getRestaurantStatus()!=null){
            RestaurantStatus rstatus = RestaurantStatus.fromInt(request.getRestaurantStatus());
            restaurant.setStatus(rstatus.getValue());
            if( RestaurantStatus.ACTIVE == rstatus){
                // crm审核通过的客户变为  成交客户
                restaurant.setActiveType(RestaurantActiveType.deal.val);
//                customer.setActiveType();
            }

            if(rstatus != RestaurantStatus.ACTIVE){
                restaurant.setRestaurantReason(request.getRestaurantReason());
            }
            //设置首次审核时间
            if (restaurant.getAuditTime() == null && RestaurantStatus.ACTIVE==rstatus) {
                restaurant.setAuditTime(now);
            }
            restaurant.setStatusLastOperater(operater);
            restaurant.setStatusLastOperateTime(now);
        }

        restaurant.setLastOperater(operater);
        restaurant.setLastOperateTime(now);

        restaurantService.save(restaurant);
        return restaurant;
    }

    /**
     * 增加客户
     * @param request
     * @param operater
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public void addRestaurant(RestaurantInfoRequest request, AdminUser operater){
        Date now = new Date();
        Customer customer = new Customer();

        Block block= locationService.getBlockById(request.getBlockId());
        customer.setBlock(block);
        customer.setCity(block.getCity());
        customer.setPassword(request.getPassword());
        customer.setTelephone(request.getTelephone());
        customer.setUsername(request.getTelephone());
//        customer.setCreateMode(CustomerCreateModeEnum.backAdd.val);

        RestaurantAddType rAddType = RestaurantAddType.find(request.getAddType());
        if(rAddType==RestaurantAddType.my){
            customer.setAdminUser(operater);  //如果是在我的客户 页面增加的客户，维护人员直接指定为此人
        }
        customer.setDevUser(operater);  //操作者为开发人员


        customer.setEnabled(true);
        customerService.register(customer);

        Restaurant restaurant = new Restaurant();
        restaurant.setStatus(RestaurantStatus.UNDEFINED.getValue());

        Address address = new Address();
        address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantAddress()));
        address.setStreetNumber(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantStreetNumber()));
        address.setWgs84Point(new Wgs84Point(Double.valueOf(request.getLng()), Double.valueOf(request.getLat())));
        restaurant.setAddress(address);

        restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getReceiver()));
        restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantName()));
        restaurant.setLicense(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(request.getRestaurantLicense()));
        restaurant.setTelephone(request.getTelephone());
        restaurant.setType(restaurantService.getRestaurantType(request.getRestaurantType()));

        restaurant.setCooperatingState(RestaurantCooperatingState.fromInt(request.getCooperatingState()).val);
        restaurant.setConcern(request.getConcern());
        restaurant.setOpponent(request.getOpponent());
        restaurant.setSpecialReq(request.getSpecialReq());
        restaurant.setStockRate(request.getStockRate());
        restaurant.setReceiver2(request.getReceiver2());
        restaurant.setTelephone2(request.getTelephone2());
        restaurant.setReceiver3(request.getReceiver3());
        restaurant.setTelephone3(request.getTelephone3());
        restaurant.setCustomer(customer);
        restaurant.setCreateTime(now);

        restaurant.setCreateOperater(operater);

        restaurant.setActiveType(RestaurantActiveType.potential.val);


        restaurantService.save(restaurant);

    }

    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public RestaurantInfoVo getRestaurantInfo(Long restaurantId) {
        Restaurant restaurant = restaurantService.getOne(restaurantId);
        return RestaurantConveter.toRestaurantInfoVo(restaurant);
    }

    /**
     * 客户分配
     * @param request
     * @param adminUser
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public void customerAllot(CustomerSellerChangeRequest request, AdminUser adminUser) {

        restaurantService.customerAllot(request, adminUser);

    }

    /**
     * 客户认领
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public void sellerClaim(CustomerSellerChangeRequest request, AdminUser adminUser) {
        restaurantService.addAuditReview(adminUser, RestaurantAuditReviewType.claim,request.getRestaurantId());
    }

    /**
     * 投放公海
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public void ontoSea(CustomerSellerChangeRequest request, AdminUser adminUser) {
        restaurantService.addAuditReview(adminUser, RestaurantAuditReviewType.seaBack,request.getRestaurantId());
    }

    /**
     * 申请审核餐馆
     */
    @Transactional(propagation = Propagation.REQUIRED,rollbackFor=Exception.class)
    public void reqAudit(Long restaurantId, AdminUser adminUser) {
        restaurantService.addAuditReview(adminUser,RestaurantAuditReviewType.restaurantInfo , restaurantId);
    }

//    /**
//     *  客户认领审核
//     */
//    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
//    public void customerClaimAudit(CustomerSellerChangeRequest request, AdminUser adminUser) {
//
//        RestaurantSellerOperaterType operaterType = RestaurantSellerOperaterType.fromInt(request.getAuditType());
//        if(operaterType==null){
//            throw new RequestNotCorrectException();
//        }
//
//        restaurantService.customerClaimAudit(request.getSellerLogId(), operaterType, adminUser);
//
//    }

    /**
     * 查询 申请审核 列表
     */
    @Transactional(readOnly = true,propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public QueryResponse<RestaurantAuditReviewVo> auditList(RestaurantAuditInfoQueryRequest request, AdminUser adminUser) {

        Page<RestaurantAuditReview> auditReviews =  restaurantService.getAuditReviewList(request, adminUser);
        Collection<RestaurantAuditReviewVo> reviewVos = Collections2.transform(auditReviews.getContent(), new Function<RestaurantAuditReview, RestaurantAuditReviewVo>() {
            @Override
            public RestaurantAuditReviewVo apply(RestaurantAuditReview input) {
                return RestaurantConveter.toAuditInfoVo(input);
            }
        });
        QueryResponse<RestaurantAuditReviewVo> response = new QueryResponse<>();
        response.setContent(new ArrayList<RestaurantAuditReviewVo>(reviewVos));
        response.setPage(request.getPage());
        response.setPageSize(request.getPageSize());
        response.setTotal(auditReviews.getTotalElements());
        return response;
    }

    /**
     * 认领审核 , 投放公海审核， 客户审核
     */
    @Transactional(propagation= Propagation.REQUIRED,rollbackFor=Exception.class)
    public List<RestaurantAuditReviewVo> claimAudit(RestaurantAuditRequest request, AdminUser operater) {

        RestaurantReviewStatus reviewStatus = RestaurantReviewStatus.fromInt(request.getReviewStatus());
        RestaurantAuditReviewType reviewType = RestaurantAuditReviewType.fromInt(request.getReviewType());

        Date now = new Date();
        List<RestaurantAuditReview> auditReview = restaurantService.getRestaurantAuditReview(request.getAuditReviewId());

        for (RestaurantAuditReview auditR : auditReview) {
            //认领
            if (reviewType == RestaurantAuditReviewType.claim && reviewStatus == RestaurantReviewStatus.PASS) {
                Customer customer = auditR.getRestaurant().getCustomer();
                customer.setAdminUser(auditR.getCreateUser());
                customer.setAdminUserFollowBegin(now);
                customer.setAdminUserFollowEnd(DateUtils.addDays(now, followUpDays));
                customerService.update(customer);
            }

            //投放公海
            if (reviewType == RestaurantAuditReviewType.seaBack && reviewStatus == RestaurantReviewStatus.PASS) {
                Customer customer = auditR.getRestaurant().getCustomer();
                customer.setAdminUser(null);
                customer.setAdminUserFollowBegin(null);
                customer.setAdminUserFollowEnd(null);
                customerService.update(customer);
            }

            //餐馆审核
            if(reviewType == RestaurantAuditReviewType.restaurantInfo && reviewStatus == RestaurantReviewStatus.PASS ){
                RestaurantStatus restaurantStatus = RestaurantStatus.fromInt(request.getRestaurantStatus());
                Restaurant restaurant = auditR.getRestaurant();
                restaurant.setStatus(restaurantStatus.getValue());

                if(request.getRestaurantReason()!=null) {
                    RestaurantReason restaurantReason = RestaurantReason.fromInt(request.getRestaurantReason());
                    restaurant.setRestaurantReason(restaurantReason.getValue());
                }
                restaurantService.save(restaurant);
            }
        }

        List<RestaurantAuditReview> auditReviews = restaurantService.auditReviewChange(request.getAuditReviewId(), reviewStatus, reviewType, operater);

        return RestaurantConveter.toAuditInfoVo(auditReviews);

    }


    public Block reckonBlock(Double lng, Double lat, Long cityId) {
        return customerService.reckonBlock(lng,lat,cityId);
    }


    public RestaurantWrapper findRestaurantByUsername(String username) {

        Customer customer = customerService.findCustomerByUsername(username);
        Restaurant restaurant = null;
        if (customer != null) {

            List<Restaurant> restaurants = customer.getRestaurant();
            if (restaurants != null && !restaurants.isEmpty()) {

                restaurant = restaurants.get(0);
            }
        }

        return new RestaurantWrapper(restaurant);
    }

}
