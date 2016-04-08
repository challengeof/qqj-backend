package com.mishu.cgwy.crm.customerInfo.controller;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.admin.controller.CurrentAdminUser;
import com.mishu.cgwy.admin.domain.AdminUser;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.constants.CustomerQueryType;
import com.mishu.cgwy.profile.constants.RestaurantGrade;
import com.mishu.cgwy.profile.controller.RestaurantQueryRequest;
import com.mishu.cgwy.profile.controller.RestaurantQueryResponse;
import com.mishu.cgwy.profile.controller.RestaurantUpdateRequest;
import com.mishu.cgwy.profile.controller.legacy.pojo.RestaurantAlarmResponse;
import com.mishu.cgwy.profile.domain.RestaurantReason;
import com.mishu.cgwy.profile.dto.RestaurantSummary;
import com.mishu.cgwy.profile.dto.RestaurantTypeRequest;
import com.mishu.cgwy.profile.dto.RestaurantTypeStatus;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.facade.RestaurantFacade;
import com.mishu.cgwy.profile.vo.RestaurantTypeVo;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import com.mishu.cgwy.profile.wrapper.SimpleRestaurantWrapper;
import com.mishu.cgwy.response.query.QueryResponse;
import com.mishu.cgwy.response.query.QueryValueResponse;
import com.mishu.cgwy.stock.dto.StockOutRequest;
import com.mishu.cgwy.utils.TreeJsonHasChild;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

//    @Autowired
//    private StringRedisTemplate redisTemplate;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        binder.registerCustomEditor(Date.class, new CustomDateEditor(dateFormat, false));
    }


    /**
     * 客户公海列表
     */
    @RequestMapping(value = "/api/restaurant/sea", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantQueryResponse seaList(RestaurantQueryRequest request, @CurrentAdminUser AdminUser adminUser) {

        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }
        request.setQueryType(CustomerQueryType.sea.val);
        RestaurantQueryResponse response = customerFacade.findRestaurants(request, adminUser);

        //QueryResponse<RestaurantWrapper> response = customerFacade.getCustomerSeaList(request, adminUser);
        return response;
    }

    /**
     * 我的客户
     */
    @RequestMapping(value = "/api/restaurant/my", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantQueryResponse myList(RestaurantQueryRequest request, @CurrentAdminUser AdminUser adminUser) {
        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }
        request.setQueryType(CustomerQueryType.My.val);
        RestaurantQueryResponse response = customerFacade.findRestaurants(request, adminUser);
        return response;
    }


    @RequestMapping(value = "/api/restaurant", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantQueryResponse list(RestaurantQueryRequest request, @CurrentAdminUser AdminUser operator) {
        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }
        return customerFacade.findRestaurants(request, operator);
    }

    @RequestMapping(value = "/api/restaurant/summary", method = RequestMethod.GET)
    @ResponseBody
    public QueryValueResponse<RestaurantSummary> summary(RestaurantQueryRequest request, @CurrentAdminUser AdminUser operator) {
        if (request.getAdminUserId() != null && request.getAdminUserId().equals(0l)) {
            request.setAdminUserIdIsNull(true);
        }
        RestaurantSummary rSummary = customerFacade.getRestaurantSummary(request, operator);

        return new QueryValueResponse<>(rSummary,true);
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

    @RequestMapping(value = "/api/restaurant/type", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeVo> listRestaurantType() {
        return restaurantFacade.findRestaurantType();
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


    @RequestMapping(value = "/api/restaurant/candidates", method = RequestMethod.GET)
    @ResponseBody
    public List<SimpleRestaurantWrapper> getRestaurantCandidates(RestaurantQueryRequest request) {
        return restaurantFacade.getRestaurantCandidates(request);
    }

    @RequestMapping(value = "/api/restaurantType/treeJson", method = RequestMethod.GET)
    @ResponseBody
    public List<TreeJsonHasChild> getRestaurantTypeTree(@RequestParam(value = "status", required = false) Integer status) {
        return restaurantFacade.getRestaurantTypeTree(0l,status);
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

    @RequestMapping(value = "/api/restaurant/username/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantWrapper getRestaurantByUsername(@PathVariable("id") String username, @CurrentAdminUser AdminUser operator) {
        return customerFacade.findRestaurantByUsername(username);
    }

}


