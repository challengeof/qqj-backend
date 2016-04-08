package com.mishu.cgwy.profile.controller.legacy;


import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.mishu.cgwy.banner.pojo.BannerResponse;
import com.mishu.cgwy.common.domain.City;
import com.mishu.cgwy.common.domain.Region;
import com.mishu.cgwy.common.facade.LocationFacade;
import com.mishu.cgwy.common.service.FeedbackService;
import com.mishu.cgwy.common.service.MediaFileService;
import com.mishu.cgwy.common.wrapper.CityWrapper;
import com.mishu.cgwy.common.wrapper.SimpleRegionWrapper;
import com.mishu.cgwy.common.wrapper.ZoneWrapper;
import com.mishu.cgwy.error.ErrorCode;
import com.mishu.cgwy.error.RestError;
import com.mishu.cgwy.profile.constants.Constants;
import com.mishu.cgwy.profile.controller.CurrentCustomer;
import com.mishu.cgwy.profile.controller.legacy.pojo.*;
import com.mishu.cgwy.profile.domain.*;
import com.mishu.cgwy.profile.dto.CustomerCenterResponse;
import com.mishu.cgwy.profile.dto.FeedbackRequest;
import com.mishu.cgwy.profile.dto.FeedbackResponse;
import com.mishu.cgwy.profile.dto.RestaurantRefer;
import com.mishu.cgwy.profile.facade.CustomerFacade;
import com.mishu.cgwy.profile.facade.RestaurantFacade;
import com.mishu.cgwy.profile.service.CodeService;
import com.mishu.cgwy.profile.service.CustomerService;
import com.mishu.cgwy.profile.wrapper.FeedbackWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantTypeWrapper;
import com.mishu.cgwy.profile.wrapper.RestaurantWrapper;
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
import java.util.ArrayList;
import java.util.List;


/**
 * User: xudong
 * Date: 3/1/15
 * Time: 3:30 PM
 */
@Controller("legacyProfileController")
public class ProfileController {
    private static Logger logger = LoggerFactory.getLogger(ProfileController.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private CustomerFacade customerFacade;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CodeService codeService;
    @Autowired
    private LocationFacade locationFacade;


    @Autowired
    private FeedbackService feedbackService;

    @Autowired
    private MediaFileService mediaFileService;

    @Autowired
    private RestaurantFacade restaurantFacade;

    @RequestMapping(value = "/api/legacy/registercode/sms", method = RequestMethod.POST)
    @ResponseBody
    public RegisterSmsCodeResponse generateCode(@RequestBody RegisterSmsCodeRequest request) {
        RegisterSmsCodeResponse response = new RegisterSmsCodeResponse();
        response.setCode(codeService.sendRandomForRegister(request.getTelephone()));
        return response;
    }

    @RequestMapping(value = "/api/legacy/registercode/check", method = {RequestMethod.POST, RequestMethod.PUT})
    @ResponseBody
    public RegisterCheckCodeResponse checkCode(@RequestBody RegisterCheckCodeRequest request) {
        final boolean checkResult = codeService.checkCodeForRegister(request.getTelephone(), request.getCode(), String.valueOf(request
                .getRandom()));

        if (checkResult) {
            return new RegisterCheckCodeResponse();
        } else {
            RegisterCheckCodeResponse response = new RegisterCheckCodeResponse();
            response.setErrno(-1);
            response.setErrmsg("验证码错误");
            return response;
        }
    }

    @RequestMapping(value = "/api/legacy/register", method = RequestMethod.POST)
    @ResponseBody
    public LegacyRegisterResponse register(@RequestBody LegacyRegisterRequest registerRequest, HttpServletRequest request) {
        final LegacyRegisterResponse register = customerFacade.legacyRegister(registerRequest);
        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
                registerRequest.getTelephone(), registerRequest.getTelephone() + registerRequest.getPassword() + "mirror");
        try {
            token.setDetails(new WebAuthenticationDetails(request));
            Authentication authenticatedUser = authenticationManager
                    .authenticate(token);

            SecurityContextHolder.getContext().setAuthentication(authenticatedUser);
            request.getSession().setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());
        } catch (AuthenticationException e) {
            logger.warn("fail to auto login after register", e);
        }

        return register;
    }

    @RequestMapping(value = "/api/legacy/forget/password", method = RequestMethod.POST)
    @ResponseBody
    public ForgetPasswordResponse forgetPassword(@RequestBody ForgetPasswordRequest forgetPasswordRequest) {
        final boolean checked = codeService.checkCodeForRegister(forgetPasswordRequest.getTelephone(),
                forgetPasswordRequest.getCode(), String.valueOf(forgetPasswordRequest.getRandom()));
        if (checked) {
            Customer customer = customerFacade.findCustomerByUsername(forgetPasswordRequest.getTelephone());
            customerService.updateCustomerPassword(customer, forgetPasswordRequest.getPassword());
            return new ForgetPasswordResponse();
        } else {
            ForgetPasswordResponse response = new ForgetPasswordResponse();
            response.setErrno(-1);
            response.setErrmsg("验证码错误");
            return response;
        }
    }

    @Secured("ROLE_CUSTOMER")
    @RequestMapping(value = "/api/legacy/center", method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public CustomerCenterResponse customerCenter(Principal principal) {
        return customerFacade.findCenterInfo(principal.getName());
    }

    @RequestMapping(value = "/api/legacy/customer/complain/{adminId}", method = RequestMethod.GET)
    @ResponseBody
    public ComplainResponse complain(@PathVariable("adminId") Long adminId, Principal principal) {
        customerFacade.complaint(principal.getName(), adminId);
        return new ComplainResponse();
    }

    @RequestMapping(value = "/api/legacy/modify/password", method = RequestMethod.POST)
    @ResponseBody
    public ModifyPasswordResponse modifyPassword(@RequestBody ModifyPasswordRequest modifyPasswordRequest,
                                                 Principal principal) {
        ModifyPasswordResponse modifyPasswordResponse = new ModifyPasswordResponse();

        final Customer customer = customerFacade.findCustomerByUsername(principal.getName());
        if (!customerFacade.updatePassword(customer, modifyPasswordRequest.getOldPassword(),
                modifyPasswordRequest.getNewPassword())) {
            modifyPasswordResponse.setErrno(-1);
            modifyPasswordResponse.setErrmsg("密码不正确");
        }

        return modifyPasswordResponse;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/restaurant/list", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantListResponse restaurantList(Principal principal) {
        RestaurantListResponse rls = new RestaurantListResponse();
        Customer customer = customerFacade.findCustomerByUsername(principal.getName());
        List<RestaurantListResponseData> data = new ArrayList<RestaurantListResponseData>();
        List<RestaurantWrapper> restaurants = customerFacade.findRestaurantByCustomerId(customer.getId());
        for (RestaurantWrapper restaurant : restaurants) {
            RestaurantListResponseData rrd = new RestaurantListResponseData();
            rrd.setId(restaurant.getId());
            rrd.setAddress(restaurant.getAddress().getAddress());
            rrd.setLicense(restaurant.getLicense());
            rrd.setName(restaurant.getName());
            rrd.setRealname(restaurant.getReceiver());
            rrd.setStatus(restaurant.getStatus().getValue());
            rrd.setTelephone(restaurant.getTelephone());
            if (restaurant.getType() != null) {
                rrd.setType(restaurant.getType().getId());
                rrd.setTypeMessage(restaurant.getType().getName());
            }
//
//            if (restaurant.getCustomer().getZone() != null) {
//                rrd.setZone(restaurant.getCustomer().getZone());
//            }
            rrd.setRestaurantNumber("" + restaurant.getId());
            data.add(rrd);
        }
        rls.setRestaurantList(data);
        return rls;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/restaurant/create", method = RequestMethod.POST)
    @ResponseBody
    public RestaurantResponse createRestaurant(@RequestBody CreateRestaurantRequest createRestaurantRequest,
                                               @CurrentCustomer Customer customer) {
        Restaurant restaurant = new Restaurant();
        Address address = new Address();
        address.setAddress(createRestaurantRequest.getAddress());
        Region region = locationFacade.getRegionById(createRestaurantRequest.getRegionId());

        restaurant.setAddress(address);
        restaurant.setReceiver(createRestaurantRequest.getRealname());
        restaurant.setCustomer(customer);
        restaurant.setName(createRestaurantRequest.getName());
        restaurant.setLicense(createRestaurantRequest.getLicense());
        restaurant.setTelephone(createRestaurantRequest.getTelephone());
        //新创建的餐馆状态为未审核
        restaurant.setStatus(Constants.NOT_CHECKED_RESTAURANT);
        restaurant = customerFacade.createRestaurant(restaurant);
        RestaurantResponse response = new RestaurantResponse();
        response.setName(createRestaurantRequest.getName());
        response.setId(restaurant.getId());
        return response;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/restaurant/update", method = RequestMethod.POST)
    @ResponseBody
    public RestaurantResponse updateRestaurant(@RequestBody UpdateRestaurantRequest updateRestaurantRequest, @CurrentCustomer Customer customer) {
        Restaurant restaurant = customerFacade.findRestaurantById(updateRestaurantRequest.getId());

        restaurant.setName(updateRestaurantRequest.getName());
        restaurant.setTelephone(updateRestaurantRequest.getTelephone());
        restaurant.setReceiver(updateRestaurantRequest.getRealname());

        restaurant = customerFacade.updateRestaurant(restaurant, customer);
        RestaurantResponse response = new RestaurantResponse();
        response.setName(updateRestaurantRequest.getName());
        response.setId(restaurant.getId());
        return response;
    }


    @RequestMapping(value = "/api/legacy/zone/{regionId}", method = RequestMethod.GET)
    @ResponseBody
    public ZoneResponse getRegionZone(@PathVariable("regionId") Long regionId) {
        ZoneResponse zoneListResponse = new ZoneResponse();
        List<ZoneWrapper> zoneWrappers = locationFacade.getZoneWrapper(regionId);
        zoneListResponse.setZoneList(zoneWrappers);
        return zoneListResponse;
    }


    @RequestMapping(value = "/api/legacy/zones/{regionId}", method = RequestMethod.GET)
    @ResponseBody
    public ZoneListResponse getRegionZones(@PathVariable("regionId") Long regionId) {
        ZoneListResponse zoneListResponse = new ZoneListResponse();
        List<ZoneWrapper> zoneWrappers = locationFacade.getZoneWrapper(regionId);
        zoneListResponse.setZones(zoneWrappers);
        return zoneListResponse;
    }

    /**
     * 对应原接口 http://root_path/region/d1
     *
     * @return
     */
    @RequestMapping(value = "/api/legacy/cities", method = RequestMethod.GET)
    @ResponseBody
    public CityResponse getAllCites() {
        List<City> cities = locationFacade.getAllCities();
        CityResponse cityResponse = new CityResponse();
        cityResponse.setRegions(new ArrayList<CityWrapper>(Collections2.transform(cities, new Function<City, CityWrapper>() {
            @Override
            public CityWrapper apply(City input) {
                return new CityWrapper(input);
            }
        })));
        return cityResponse;
    }

    /**
     * 对应原接口 http://root_path/region/d2/{regionId}
     *
     * @param cityId
     * @return
     */
    @RequestMapping(value = "/api/legacy/regions/{cityId}", method = RequestMethod.GET)
    @ResponseBody
    public RegionListResponse getCityRegions(@PathVariable("cityId") Long cityId) {
        RegionListResponse regionListResponse = new RegionListResponse();
        List<SimpleRegionWrapper> regionWrappers = locationFacade.getRegionWrapper(cityId);
        regionListResponse.setRegions(regionWrappers);
        return regionListResponse;
    }


    @RequestMapping(value = "/api/legacy/restaurant/get/{id}", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantGetResponse getRestaurant(@PathVariable("id") Long id) {
        RestaurantWrapper restaurant = customerFacade.findRestaurantWrapperById(id);
        RestaurantRefer rr = new RestaurantRefer(restaurant);
        if (restaurant.getType() != null) {
            rr.setTypeMessage(restaurant.getType().getName());
        }
        RestaurantGetResponse response = new RestaurantGetResponse();
        response.setRestaurant(rr);
        return response;
    }

    @RequestMapping(value = "/api/legacy/user/check/username", method = RequestMethod.POST)
    @ResponseBody
    public RestError checkUsername(@RequestBody CheckUsernameRequest request) {

        Customer customer = customerFacade.findCustomerByUsername(request.getUsername());
        RestError response = new RestError();
        if (customer != null) {
            response.setErrno(ErrorCode.CustomerAlreadyExists.getError());
            response.setErrmsg(ErrorCode.CustomerAlreadyExists.getErrorMessage());
        }
        return response;

    }

    @RequestMapping(value = "/api/legacy/restaurant/json", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantUsableListResponse getUsableRestaurants(@CurrentCustomer Customer customer) {

        RestaurantUsableListResponse response = new RestaurantUsableListResponse();
        response.setRestaurantList(customerFacade.findUsableRestaurantByCustomerId(customer.getId()));
        return response;
    }

    @RequestMapping(value = "/api/legacy/dict/key/4", method = RequestMethod.GET)
    @ResponseBody
    public RestaurantTypeResponse getRestaurantType() {
        RestaurantTypeResponse response = new RestaurantTypeResponse();

        List<RestaurantTypeDetailResponse> list = new ArrayList<>();
        for (RestaurantTypeWrapper restaurantTypeWrapper : restaurantFacade.findLastRestaurantType()) {
            RestaurantTypeDetailResponse detailResponse = new RestaurantTypeDetailResponse();
            detailResponse.setId(restaurantTypeWrapper.getId());
            detailResponse.setShowValue(restaurantTypeWrapper.getName());
            list.add(detailResponse);
        }
        response.setValues(list);
        return response;

    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/restaurant/full/update", method = RequestMethod.POST)
    @ResponseBody
    public RestError fullUpdateRestaurant(@RequestBody FullUpdateRestaurantRequest request, @CurrentCustomer Customer customer) {
        Restaurant restaurant = customerFacade.findRestaurantById(request.getId());

        restaurant.setName(request.getName());
        restaurant.setTelephone(request.getTelephone());
        restaurant.setReceiver(request.getRealname());
        request.setLicense(request.getLicense());

        Address address = restaurant.getAddress();
        if (address == null) {
            address = new Address();
        }

        address.setAddress(request.getAddress());
        restaurant.setAddress(address);
        //Status? 需要不需要从 invalid 变为 valid??

        customerFacade.updateRestaurant(restaurant, customer);

        RestError response = new RestError();
        return response;
    }


    //积分相关
    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/score")
    @ResponseBody
    public ScoreResponse getScore(@CurrentCustomer Customer customer) {
        ScoreResponse response = new ScoreResponse();
        return response;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/score/come")
    @ResponseBody
    public ScoreComeResponse getScoreCome(@CurrentCustomer Customer customer) {
        ScoreComeResponse response = new ScoreComeResponse();
        return response;
    }

    @Secured("ROLE_USER")
    @RequestMapping(value = "/api/legacy/score/back")
    @ResponseBody
    public ScoreBackResponse getScoreBack(@CurrentCustomer Customer customer) {
        ScoreBackResponse response = new ScoreBackResponse();
        return response;
    }

    @RequestMapping(value = "/api/legacy/banner", method = RequestMethod.GET)
    @ResponseBody
    public BannerResponse getBanner(@CurrentCustomer Customer customer,@RequestParam(value = "cityId",defaultValue = com.mishu.cgwy.product.constants.Constants.DEFAULT_CITY_ID,required = false) Long cityId) {

        return customerFacade.getBanner(customer,cityId);
    }

    @RequestMapping(value = "api/legacy/feedback",method = RequestMethod.POST)
    @ResponseBody
    public FeedbackResponse submitFeedback(@CurrentCustomer Customer customer,@RequestBody FeedbackRequest feedbackRequest) {

        Feedback feedback = new Feedback();
        feedback.setFeedbackDescription(feedbackRequest.getFeedbackDescription());
        feedback.setCustomer(customer);
        feedback.setType(FeedbackType.CUSTOMER.getValue());
        if (feedbackRequest.getMediaFileId() != null) {
            feedback.setFile(mediaFileService.getMediaFile(feedbackRequest.getMediaFileId()));
        }
        FeedbackResponse feedbackResponse = new FeedbackResponse();
        feedbackResponse.setFeedback(new FeedbackWrapper(feedbackService.saveFeedback(feedback)));
        return feedbackResponse;
    }
}
