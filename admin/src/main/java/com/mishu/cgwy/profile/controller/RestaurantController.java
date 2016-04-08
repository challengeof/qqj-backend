package com.mishu.cgwy.profile.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.app.dto.*;
import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.constants.RestaurantGrade;
import com.mishu.cgwy.profile.controller.legacy.pojo.RestaurantAlarmResponse;
import com.mishu.cgwy.profile.domain.RestaurantReason;
import com.mishu.cgwy.profile.dto.RestaurantTypeRequest;
import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.facade.RestaurantFacade;
import com.mishu.cgwy.profile.vo.RestaurantTypeVo;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.http.impl.cookie.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * User: xudong
 * Date: 4/1/15
 * Time: 11:14 AM
 */
@Controller
public class RestaurantController {
    @Autowired
    private CustomerFacade customerFacade;

    @Autowired
    private RestaurantFacade restaurantFacade;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }

    @RequestMapping(value = "/api/restaurant", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantQueryResponse list(RestaurantQueryRequest request, @CurrentAdminUser AdminUser operator) {
        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }

        return customerFacade.findRestaurants(request, operator);
    }

    @RequestMapping(value = "/api/restaurant/export", method = RequestMethod.GET)
    @ResponseBody
    public HttpEntity<byte[]> generateRestaurantExcel(RestaurantQueryRequest request, @CurrentAdminUser AdminUser operator) throws Exception {
        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }
       return  customerFacade.generateRestaurantExcel(request, operator);
    }

    @RequestMapping(value = "/api/restaurant/changeAdminUserBatch")
    @ResponseBody
    public void changeAdminUser(Long oldAdminUserId, Long newAdminUserId, @CurrentAdminUser AdminUser operator) {
        customerFacade.updateRestaurantAdminUserBatch(oldAdminUserId, newAdminUserId);
    }

    @RequestMapping(value = "/api/restaurant/grades", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantGrade[] listRestaurantGrades() {
        return RestaurantGrade.values();
    }

    @RequestMapping(value = "/api/restaurant/status", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantStatus[] listRestaurantStatus() {
        return RestaurantStatus.values();
    }

    @RequestMapping(value = "/api/restaurant/reasons", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantReason[] listRestaurantReasons() {
        return RestaurantReason.values();
    }

    @RequestMapping(value = "/api/restaurant/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public void update(@PathVariable("id") Long restaurantId, @RequestBody RestaurantUpdateRequest
            request, @CurrentAdminUser AdminUser adminUser) {
        customerFacade.updateRestaurantByAdminUser(restaurantId, request, adminUser);
    }

    @RequestMapping(value = "/api/restaurant/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantWrapper view(@PathVariable("id") Long restaurantId, @CurrentAdminUser AdminUser operator) {
        return customerFacade.findRestaurantWrapperById(restaurantId);
    }

    @RequestMapping(value = "/api/restaurant/batch", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantWrapper> batchView(@RequestParam("restaurantId") List<Long> restaurantIds) {
        List<RestaurantWrapper> result = new ArrayList<>();
        if (restaurantIds != null) {
            for (Long id : restaurantIds) {
                result.add(customerFacade.findRestaurantWrapperById(id));
            }
        }

        return result;
    }

    @RequestMapping(value = "/api/restaurant/delivery", method = RequestMethod.GET)
    @ResponseBody
    public QueryResponse<SimpleRestaurantWrapper> findRestaurantsDelivery(StockOutRequest request, @CurrentAdminUser AdminUser adminUser) {
        return restaurantFacade.findRestaurantsDelivery(request, adminUser);
    }

    @RequestMapping(value = "/api/restaurant/map", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantWrapper> batchView(@RequestParam("restaurantId") List<Long> restaurantIds, @RequestParam("hash") String hash) {
        StringBuffer buffer = new StringBuffer();
        if (restaurantIds != null && !restaurantIds.isEmpty()) {
            List<RestaurantWrapper> result = new ArrayList<>();

            for (Long id : restaurantIds) {
                buffer.append(id).append(",");
            }
            buffer = buffer.length() > 0 ? buffer.deleteCharAt(buffer.length() - 1) : buffer;
            String md5Hex = DigestUtils.md5Hex(buffer.append("canguanwuyou").toString());
            if (md5Hex.equals(hash)) {
                for (Long id : restaurantIds) {
                    result.add(customerFacade.findRestaurantWrapperById(id));
                }
            }
            return result;
        }
        return null;
    }


    @RequestMapping(value = "/api/restaurant/{id}/admin-user", method = RequestMethod.PUT)
    @ResponseBody
    public void assignAdminUser(@PathVariable("id") Long restaurantId,
                                @RequestParam("adminUserId") Long adminUserId,
                                @CurrentAdminUser AdminUser operator) {
        customerFacade.assignRestaurantToAdminUser(restaurantId, adminUserId, operator);
    }

    @RequestMapping(value = "/api/restaurant/{id}/status", method = RequestMethod.PUT)
    @ResponseBody
    public void updateStatus(@PathVariable("id") Long restaurantId,
                             @RequestParam("status") Integer status,
                             @CurrentAdminUser AdminUser operator) {
        customerFacade.updateRestaurantStatus(restaurantId, status, operator);
    }

    @RequestMapping(value = "/api/restaurant/uncheckList", method = RequestMethod.POST)
    @ResponseBody
    public UncheckRestaurantResponse getUserUncheckList(@CurrentAdminUser AdminUser operator) {
        UncheckRestaurantResponse response = new UncheckRestaurantResponse();
        List<RestaurantResponse> restList = new ArrayList<RestaurantResponse>();
        List<UserResponse> userList = new ArrayList<UserResponse>();


        RestaurantQueryRequest restaurantQuery = new RestaurantQueryRequest();
        restaurantQuery.setStatus(RestaurantStatus.UNDEFINED.getValue()); //未审核通过
        RestaurantQueryResponse restaurants = customerFacade.findRestaurants(restaurantQuery, operator);
        List<RestaurantWrapper> list = restaurants.getRestaurants();
        RestaurantResponse restaurantResponse = null;
        for (RestaurantWrapper rest : list) {
            restaurantResponse = new RestaurantResponse();
            if (rest.getType() != null) {
                restaurantResponse.setResType(rest.getType().getName());
            }
            restaurantResponse.setUsername(rest.getTelephone());
            restaurantResponse.setName(rest.getName());
            if (null != rest.getAddress()) {
                restaurantResponse.setAddress(rest.getAddress().getAddress());
            }
            restaurantResponse.setRealname(rest.getReceiver());
            restaurantResponse.setTelephone(rest.getTelephone());
            restaurantResponse.setLicense(rest.getLicense());
            restaurantResponse.setShare1("");
            restaurantResponse.setShare2("");
            if (null != rest.getCustomer() && null != rest.getCustomer().getAdminUser()) {
                restaurantResponse.setAdminName(rest.getCustomer().getAdminUser().getRealname());
            }
            restaurantResponse.setCreateTime(DateUtils.formatDate(rest.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            restaurantResponse.setRestaurantId(rest.getId());
            restaurantResponse.setRestaurantNumber(rest.getId().toString());
            restaurantResponse.setUserId(rest.getCustomer().getId());
            restList.add(restaurantResponse);
        }
        response.setNewRestaurant(restList);
        response.setNewUser(userList);
        return response;
    }

    @Deprecated
    @RequestMapping(value = "/api/restaurant/checkPass", method = RequestMethod.POST)
    @ResponseBody
    public RestError checkPass(@RequestBody UserCheckRestaurantRequest request, @CurrentAdminUser AdminUser operator) {
        RestError error = new RestError();
        //生效
//    	customerFacade.updateRestaurantStatus(request.getRestaurantId(), RestaurantStatus.ACTIVE.getValue(), operator);
        error.setErrno(-1);
        error.setErrmsg("区块未分配，请到后台审核!");
        return error;
    }

    @Deprecated
    @RequestMapping(value = "/api/restaurant/checkRefuse", method = RequestMethod.POST)
    @ResponseBody
    public RestError checkRefuse(@RequestBody UserCheckRestaurantRequest request, @CurrentAdminUser AdminUser operator) {
        RestError error = new RestError();
        //实效
        customerFacade.updateRestaurantStatus(request.getRestaurantId(), RestaurantStatus.INACTIVE.getValue(), operator);
        error.setErrno(0);
        return error;
    }

    @RequestMapping(value = "/api/restaurant/getCollections", method = RequestMethod.GET)
    @ResponseBody
    public CollcationResponse getCollocations(@CurrentAdminUser AdminUser operator) {
        CollcationResponse response = new CollcationResponse();
        List<Collcation> rows = new ArrayList<Collcation>();

        RestaurantQueryRequest request = new RestaurantQueryRequest();
        request.setAdminUserIdIsNull(true);
        RestaurantQueryResponse restaurantPage = customerFacade.findRestaurants(request, operator);
        Collcation coll = null;
        for (RestaurantWrapper restaurant : restaurantPage.getRestaurants()) {
            coll = new Collcation();
            coll.setCreateTime(DateUtils.formatDate(restaurant.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
            coll.setRAddress(restaurant.getAddress().getAddress());
            coll.setRLicense(restaurant.getLicense());
            coll.setRName(restaurant.getName());
            coll.setRRealname(restaurant.getCustomer().getUsername());
            coll.setRTelephone(restaurant.getTelephone());
            if (restaurant.getType() != null) {
                coll.setRType(restaurant.getType().getName());
            }
            if (restaurant.getWarehouse() != null) {
                coll.setRegion(restaurant.getWarehouse().getDisplayName());
                coll.setRegionId(restaurant.getWarehouse().getId());
            }
            coll.setShare1("");
            coll.setShare2("");
            coll.setTelephone(restaurant.getTelephone());
            coll.setUserId(restaurant.getCustomer().getId());
            rows.add(coll);
        }
        response.setRows(rows);
        return response;
    }

    @RequestMapping(value = "/api/restaurant/setUser", method = RequestMethod.POST)
    @ResponseBody
    public RestError setAdminUser(@RequestBody CollcatingRequest request, @CurrentAdminUser AdminUser operator) {
        RestError error = new RestError();
        customerFacade.assignCustomerToAdminUser(request.getUserId(), request.getAdminId(), operator);
        return error;
    }

    @RequestMapping(value = "/api/restaurant/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public boolean updateCustomerPassword(@RequestParam("username") String username, @RequestParam("password") String password) {
        return customerFacade.updatePassword(username, password);
    }

    @RequestMapping(value = "/api/restaurant/alarm", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantAlarmResponse restaurantAlarm(RestaurantQueryRequest request, @CurrentAdminUser AdminUser operator) {
        return customerFacade.findAlarmRestaurants(request, operator);
    }

    @RequestMapping(value = "/api/customer/token/{username}")
    @ResponseBody
    public String getCustomerToken(@CurrentAdminUser AdminUser operator, @PathVariable("username") String username) {

        redisTemplate.delete(username);
        String token = String.format("%06d", new java.util.Random().nextInt(1000000)) + "x" + operator.getId();
        final BoundHashOperations<String, String, Object> ops = redisTemplate
                .boundHashOps(username);
        ops.put("token", token);
        redisTemplate.expire(username, 30 * 60, TimeUnit.SECONDS);

        Token responseToken = new Token();
        responseToken.setToken(token);

        ObjectMapper objectMapper = new ObjectMapper();
        String response = null;
        try {
            response = objectMapper.writeValueAsString(responseToken);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return response;

    }

    @RequestMapping(value = "/api/restaurant/candidates", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleRestaurantWrapper> getRestaurantCandidates(RestaurantQueryRequest request) {
        return restaurantFacade.getRestaurantCandidates(request);
    }

    @RequestMapping(value = "/api/restaurantType/treeJson", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeJsonHasChild> getRestaurantTypeTree(@RequestParam(value = "status", required = false) Integer status) {
        return restaurantFacade.getRestaurantTypeTree(0l, status);
    }

    @RequestMapping(value = "/api/restaurantType/{id}/changeCity", method = RequestMethod.PUT)
    @ResponseBody
    public void setRestaurantTypeCity(@PathVariable(value = "id") Long restaurantType, @RequestParam(value = "cityId") Long cityId, @RequestParam(value = "active") Boolean active) {
        restaurantFacade.setRestaurantTypeCity(restaurantType, cityId, active);
    }

    @RequestMapping(value = "/api/restaurantType", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeVo> listAllCategories(
            @RequestParam(value = "status", required = false) final Integer status) {


        final List<RestaurantTypeVo> all = restaurantFacade.listAllRestaurantTypes();

        if (status == null) {
            return all;
        } else {
            return new ArrayList<>(Collections2.filter(all, new Predicate<RestaurantTypeVo>() {
                @Override
                public boolean apply(RestaurantTypeVo input) {
                    return input.getStatus().getValue().equals(status);
                }
            }));
        }
    }

    @RequestMapping(value = "/api/restaurantType/status", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantTypeStatus[] listRestaurantTypeStatus() {
        return RestaurantTypeStatus.values();
    }

    @RequestMapping(value = "/api/restaurantType", method = RequestMethod.POST)
    @ResponseBody
    public RestaurantTypeVo createCategory(@RequestBody RestaurantTypeRequest restaurantTypeRequest) {
        if (Long.valueOf(0).equals(restaurantTypeRequest.getParentRestaurantTypeId())) {
            restaurantTypeRequest.setParentRestaurantTypeId(null);
        }

        return restaurantFacade.createRestaurantType(restaurantTypeRequest);
    }

    @RequestMapping(value = "/api/restaurantType/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public RestaurantTypeVo updateCategory(@PathVariable("id") Long id, @RequestBody RestaurantTypeRequest request) {
        if (Long.valueOf(0).equals(request.getParentRestaurantTypeId())) {
            request.setParentRestaurantTypeId(null);
        }

        return restaurantFacade.updateRestaurantType(id, request);
    }

    @RequestMapping(value = "/api/restaurantType/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantTypeVo getRestaurantType(@PathVariable("id") Long id) {
        return restaurantFacade.getRestaurantType(id);
    }

    //有效餐馆类型
    @RequestMapping(value = "/api/restaurant/type", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeVo> listRestaurantType() {
        return restaurantFacade.findRestaurantType();
    }


    @RequestMapping(value = "/api/restaurantType/parent", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeVo> listActiveParentRestaurantType() {
        return restaurantFacade.getRestaurantTypeParent(RestaurantTypeStatus.ACTIVE.getValue());
    }

    @RequestMapping(value = "/api/restaurantType/{id}/child", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeVo> listActiveParentRestaurantType(@PathVariable("id") Long id, Integer status) {
        return restaurantFacade.getRestaurantTypeChild(id, RestaurantTypeStatus.ACTIVE.getValue());
    }



}


