package com.mishu.cgwy.profile.controller;

import com.mishu.cgwy.banner.pojo.BannerResponse;
import com.mishu.cgwy.common.domain.Block;
import com.mishu.cgwy.common.service.LocationService;
import com.mishu.cgwy.error.CustomerAlreadyExistsException;
import com.mishu.cgwy.error.CustomerAreaOutsideException;
import com.mishu.cgwy.error.CustomerNotExistsException;
import com.mishu.cgwy.error.WrongCodeException;
import com.mishu.cgwy.product.constants.Constants;
import com.mishu.cgwy.product.constants.RestaurantStatus;
import com.mishu.cgwy.profile.controller.legacy.pojo.UpdateRestaurantRequest;
import com.mishu.cgwy.profile.domain.Address;
import com.mishu.cgwy.profile.domain.Customer;
import com.mishu.cgwy.profile.domain.Restaurant;
import com.mishu.cgwy.profile.domain.Wgs84Point;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.facade.RestaurantFacade;
import com.mishu.cgwy.profile.service.CodeService;
import com.mishu.cgwy.profile.service.CodeV2Service;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.wrapper.CustomerWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantTypeWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.Date;
import java.util.List;


/**
 * User: xudong
 * Date: 3/1/15
 * Time: 3:30 PM
 */
@Controller
public class ProfileController {
    private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerFacade customerFacade;

    @Autowired
    private CodeService codeService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private CodeV2Service codeV2Service;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RestaurantFacade restaurantFacade;

    @RequestMapping(value = "/api/v2/check-username", method = RequestMethod.GET)
    @ResponseBody
    public void checkUsername(@RequestParam("username") String username) {
        Customer customer = customerFacade.findCustomerByUsername(username);
        if (customer != null) {
            throw new CustomerAlreadyExistsException();
        }
    }

    @RequestMapping(value = "/api/v2/upate/customer/versioncode", method = RequestMethod.GET)
    @ResponseBody
    public void upateCustomerVersion(@RequestParam("versionCode") Integer versionCode,@RequestParam("username") String username) {
        Customer customer = customerService.findCustomerByUsername(username);
        if (customer != null) {
            customer.setVersionCode(versionCode);
            customerService.update(customer);
        }
    }



    @Secured("ROLE_USER")
    @RequestMapping(value = {"/api/legacy/customer", "/api/v2/customer"}, method = RequestMethod.GET)
    @ResponseBody
    public CustomerWrapper profile(Principal principal) {
        Customer customer = customerService.findCustomerByUsername(principal.getName());
        //更新最后登录时间 2015.10.29 by linsen
        customer.setLastLoginTime(new Date());
        customerService.update(customer);
        return new CustomerWrapper(customer);
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = {"/api/v2/restaurant"}, method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantWrapper> getRestaurants(@CurrentCustomer Customer customer) {
        return customerFacade.findRestaurantByCustomerId(customer.getId());
    }

    @RequestMapping(value = "/api/v2/restaurant/type", method = RequestMethod.GET)
    @ResponseBody
    public List<RestaurantTypeWrapper> getRestaurantType() {
        return restaurantFacade.findLastRestaurantType();
    }

    @RequestMapping(value = "/api/v2/register", method = RequestMethod.POST)
    @ResponseBody
    public RegisterResponse register(@RequestBody RegisterRequest registerRequest, HttpServletRequest request) {
        final RegisterResponse register = customerFacade.register(registerRequest);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registerRequest.getTelephone(), registerRequest.getTelephone() + registerRequest.getPassword() + "mirror");
        try {
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authenticatedUser = authenticationManager.authenticate(token);
            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            logger.warn("fail to auto login after register", e);
        }

        return register;
    }

    @RequestMapping(value = "/api/v2/banner", method = RequestMethod.GET)
    @ResponseBody
    public BannerResponse getBanner(@CurrentCustomer Customer customer, @RequestParam(value = "cityId",defaultValue = Constants.DEFAULT_CITY_ID,required = false) Long cityId) {
        return customerFacade.getBanner(customer,cityId);
    }

    @RequestMapping(value = "/api/v2/code", method = RequestMethod.GET)
    @ResponseBody
    public void sendCode(@RequestParam("telephone") String telephone) {
        if (customerService.findCustomerByUsername(telephone) == null) {
            throw new CustomerNotExistsException();
        }

        codeV2Service.sendCode(telephone);
    }

    @RequestMapping(value = "/api/v2/code", method = RequestMethod.PUT)
    @ResponseBody
    public boolean checkCode(@RequestParam("telephone") String telephone, @RequestParam("code") String code) {
        if (customerService.findCustomerByUsername(telephone) == null) {
            throw new CustomerNotExistsException();
        }

        return codeV2Service.checkCode(telephone, code);
    }

    @RequestMapping(value = "/api/v2/{username}/reset-password", method = RequestMethod.PUT)
    @ResponseBody
    public void resetPassword(@PathVariable("username") String telephone,
                                 @RequestParam("code") String code,
                                 @RequestParam("password") String password) {
        final Customer customer = customerService.findCustomerByUsername(telephone);
        if (customer == null) {
            throw new CustomerNotExistsException();
        }

        if (codeV2Service.checkCode(telephone, code)) {
            customerService.updateCustomerPassword(customer, password);
        } else {
            throw new WrongCodeException();
        }
    }

    @RequestMapping(value = "/api/v2/restaurant/updatePassword", method = RequestMethod.POST)
    @ResponseBody
    public int updateCustomerPassword(@RequestParam("username") String username, @RequestParam("password") String password,@RequestParam("newpassword") String newPassword) {
        final Customer customer = customerService.findCustomerByUsername(username);
        String oldPassword = customerService.getReformedPassword(username, password);

        if(customer != null && customer.getPassword().equals(oldPassword)){
            return customerFacade.updatePassword(username, newPassword) ? 1 : 2;
        }else {
            return 3;
        }
    }

    @RequestMapping(value = "/api/v2/restaurant/{id}", method = RequestMethod.PUT)
    @ResponseBody
    public RestaurantWrapper updateRestaurant(@RequestBody UpdateRestaurantRequest updateRestaurantRequest,
                                        @CurrentCustomer Customer
            customer) {
        Restaurant restaurant = null;
        if (updateRestaurantRequest.getId() != null) {
            restaurant = customerFacade.findRestaurantById(updateRestaurantRequest.getId());
        } else {
            restaurant = new Restaurant();
        }

        //增加是否在服务区块内判断
        boolean inServiceArea =customerService.checkInServiceArea(updateRestaurantRequest.getLng(), updateRestaurantRequest.getLat(), customer.getCity().getId());
        if(!inServiceArea){
            throw new CustomerAreaOutsideException();
        }
        //修改所在区块
        Block block = customerService.reckonBlock(updateRestaurantRequest.getLng(), updateRestaurantRequest.getLat(), customer.getCity().getId());
        if(block!=null) {
            restaurant.getCustomer().setBlock(block);
        }

        logger.warn("customer self modify restaurant_detail:id:" + updateRestaurantRequest.getId() + ",blockId:" + block.getId());

        restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getName()));
        restaurant.setTelephone(updateRestaurantRequest.getTelephone());
        restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRealname()));
        //增加修改地址和坐标 by linsen 2015.10.12
        Address address = new Address();

        if (StringUtils.isNotBlank(updateRestaurantRequest.getRestaurantAddress())) {
            address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRestaurantAddress()));
        }
        if (StringUtils.isNotBlank(updateRestaurantRequest.getRestaurantStreetNumber())) {
            address.setStreetNumber(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRestaurantStreetNumber()));
        }

        if (updateRestaurantRequest.getLng() != null && updateRestaurantRequest.getLat() != null) {
            address.setWgs84Point(new Wgs84Point(updateRestaurantRequest.getLng(), updateRestaurantRequest.getLat()));
        }

        restaurant.setAddress(address);

        return new RestaurantWrapper(customerFacade.updateRestaurant(restaurant, customer));
    }

    @RequestMapping(value = "/api/v2/restaurant", method = RequestMethod.POST)
    @ResponseBody
    public RestaurantWrapper createRestaurant(@RequestBody CreateRestaurantRequest updateRestaurantRequest,
                                              @CurrentCustomer Customer
                                                      customer) {
        final List<RestaurantWrapper> restaurants = customerFacade.findRestaurantByCustomerId(customer.getId());
        if (!restaurants.isEmpty()) {
            return null;
        }

        Restaurant restaurant = new Restaurant();

        restaurant.setName(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRestaurantName()));
        restaurant.setTelephone(customer.getTelephone());
        restaurant.setReceiver(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getReceiver()));
        Address address = new Address();
        address.setAddress(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRestaurantAddress()));
        restaurant.setAddress(address);
//        restaurant.setType(updateRestaurantRequest.getRestaurantType());
        restaurant.setCustomer(customer);
        restaurant.setLicense(com.mishu.cgwy.utils.StringUtils.skipSpecialCharacters(updateRestaurantRequest.getRestaurantLicense()));
        restaurant.setStatus(RestaurantStatus.UNDEFINED.getValue());

        return new RestaurantWrapper(customerFacade.updateRestaurant(restaurant, customer));
    }

    @RequestMapping(value = "/api/v2/available",
            method = {
                    RequestMethod.GET,
                    RequestMethod.HEAD,
                    RequestMethod.POST,
                    RequestMethod.PUT,
                    RequestMethod.PATCH,
                    RequestMethod.DELETE,
                    RequestMethod.OPTIONS,
                    RequestMethod.TRACE,
            })
    @ResponseBody
    public void webAvilable() {}
}
